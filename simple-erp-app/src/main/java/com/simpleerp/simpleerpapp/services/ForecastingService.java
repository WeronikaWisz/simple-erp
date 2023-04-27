package com.simpleerp.simpleerpapp.services;

import ai.djl.Model;
import ai.djl.ModelException;
import ai.djl.basicdataset.BasicDatasets;
import ai.djl.basicdataset.tabular.utils.Feature;
import ai.djl.engine.Engine;
import ai.djl.inference.Predictor;
import ai.djl.metric.Metrics;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.Parameter;
import ai.djl.repository.Repository;
import ai.djl.timeseries.Forecast;
import ai.djl.timeseries.TimeSeriesData;
import ai.djl.timeseries.dataset.FieldName;
import ai.djl.timeseries.dataset.M5Forecast;
import ai.djl.timeseries.dataset.TimeFeaturizers;
import ai.djl.timeseries.distribution.DistributionLoss;
import ai.djl.timeseries.distribution.output.DistributionOutput;
import ai.djl.timeseries.distribution.output.NegativeBinomialOutput;
import ai.djl.timeseries.evaluator.Rmsse;
import ai.djl.timeseries.model.deepar.DeepARNetwork;
import ai.djl.timeseries.timefeature.TimeFeature;
import ai.djl.timeseries.transform.TimeSeriesTransform;
import ai.djl.timeseries.translator.DeepARTranslator;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.EasyTrain;
import ai.djl.training.Trainer;
import ai.djl.training.TrainingResult;
import ai.djl.training.dataset.Batch;
import ai.djl.training.dataset.Dataset;
import ai.djl.training.dataset.Record;
import ai.djl.training.initializer.XavierInitializer;
import ai.djl.training.listener.SaveModelTrainingListener;
import ai.djl.training.listener.TrainingListener;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;
import ai.djl.util.Progress;
import com.simpleerp.simpleerpapp.dtos.forecasting.ForecastingActive;
import com.simpleerp.simpleerpapp.dtos.forecasting.ForecastingTrainingData;
import com.simpleerp.simpleerpapp.dtos.forecasting.ForecastingTrainingElement;
import com.simpleerp.simpleerpapp.exception.ApiExpectationFailedException;
import com.simpleerp.simpleerpapp.exception.ApiNotFoundException;
import com.simpleerp.simpleerpapp.forecasting.Evaluator;
import com.simpleerp.simpleerpapp.forecasting.ExcelHelper;
import com.simpleerp.simpleerpapp.models.ForecastingProperties;
import com.simpleerp.simpleerpapp.models.Product;
import com.simpleerp.simpleerpapp.repositories.ForecastingPropertiesRepository;
import com.simpleerp.simpleerpapp.repositories.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ForecastingService {

    private static final String FREQ = "D";
//    private static final Integer PREDICTION_LENGTH = 4;
    private static final String MODEL_PATH = "/Users/Weronika/Inf_semestr10/simple-erp/simple-erp-app/src/main/resources/forecasting";

    private static final Logger LOGGER = LoggerFactory.getLogger(ForecastingService.class);

    private final ProductRepository productRepository;
    private final ForecastingPropertiesRepository forecastingPropertiesRepository;

    @Autowired
    public ForecastingService(ProductRepository productRepository,
                              ForecastingPropertiesRepository forecastingPropertiesRepository) {
        this.productRepository = productRepository;
        this.forecastingPropertiesRepository = forecastingPropertiesRepository;
    }

    //    public ForecastingService() throws TranslateException, IOException, ModelException {
//        trainModel();
//        Map<String, Float> metrics = predict(LocalDateTime.now());
//        for (Map.Entry<String, Float> entry : metrics.entrySet()) {
//            LOGGER.info(String.format("metric: %s:\t%.2f", entry.getKey(), entry.getValue()));
//        }
//    }

    public TrainingResult trainModel(Integer predictionLength, Integer itemCardinality,
                                     LocalDateTime startTime, int maxDays) throws IOException, TranslateException {

        try (Model model = Model.newInstance("deepar")) {
            DistributionOutput distributionOutput = new NegativeBinomialOutput();
            DefaultTrainingConfig config = setupTrainingConfig(distributionOutput);

            NDManager manager = model.getNDManager();
            DeepARNetwork trainingNetwork = getDeepARModel(distributionOutput, true,
                    itemCardinality, predictionLength);
            model.setBlock(trainingNetwork);

            List<TimeSeriesTransform> trainingTransformation =
                    trainingNetwork.createTrainingTransformation(manager);
            int contextLength = trainingNetwork.getContextLength();

            M5Forecast trainSet =
                    getDataset(trainingTransformation, contextLength, Dataset.Usage.TRAIN,
                            startTime, maxDays);

            Record record = trainSet.get(manager, 0);

            try (Trainer trainer = model.newTrainer(config)) {
                trainer.setMetrics(new Metrics());

                int historyLength = trainingNetwork.getHistoryLength();
                Shape[] inputShapes = new Shape[9];
                // (N, num_cardinality)
                inputShapes[0] = new Shape(1, 1);
                // (N, num_real) if use_feat_stat_real else (N, 1)
                inputShapes[1] = new Shape(1, 1);
                // (N, history_length, num_time_feat + num_age_feat)
                inputShapes[2] =
                        new Shape(
                                1,
                                historyLength,
                                TimeFeature.timeFeaturesFromFreqStr(FREQ).size() + 1);
                inputShapes[3] = new Shape(1, historyLength);
                inputShapes[4] = new Shape(1, historyLength);
                inputShapes[5] = new Shape(1, historyLength);
                inputShapes[6] =
                        new Shape(
                                1,
                                predictionLength,
                                TimeFeature.timeFeaturesFromFreqStr(FREQ).size() + 1);
                inputShapes[7] = new Shape(1, predictionLength);
                inputShapes[8] = new Shape(1, predictionLength);
                trainer.initialize(inputShapes);

                EasyTrain.fit(trainer, 10, trainSet, null);
                return trainer.getTrainingResult();
            }
        }
    }

//    public static Map<String, Float> predict(LocalDateTime startTime)
//            throws IOException, TranslateException, ModelException {
//        try (Model model = Model.newInstance("deepar")) {
//            DeepARNetwork predictionNetwork = getDeepARModel(new NegativeBinomialOutput(), false);
//            model.setBlock(predictionNetwork);
//            model.load(Paths.get(MODEL_PATH));
//
//            M5Forecast testSet =
//                    getDataset(
//                            new ArrayList<>(),
//                            predictionNetwork.getContextLength(),
//                            Dataset.Usage.TEST);
//
//            Map<String, Object> arguments = new ConcurrentHashMap<>();
//            arguments.put("prediction_length", PREDICTION_LENGTH);
//            arguments.put("freq", FREQ);
//            arguments.put("use_" + FieldName.FEAT_DYNAMIC_REAL.name().toLowerCase(), false);
//            arguments.put("use_" + FieldName.FEAT_STATIC_CAT.name().toLowerCase(), true);
//            arguments.put("use_" + FieldName.FEAT_STATIC_REAL.name().toLowerCase(), false);
//            DeepARTranslator translator = DeepARTranslator.builder(arguments).build();
//
//            Evaluator evaluator =
//                    new Evaluator(0.5f, 0.67f, 0.95f, 0.99f);
//            Progress progress = new ProgressBar();
//            progress.reset("Inferring", testSet.size());
//            try (Predictor<TimeSeriesData, Forecast> predictor = model.newPredictor(translator)) {
//                for (Batch batch : testSet.getData(model.getNDManager().newSubManager())) {
//                    NDList data = batch.getData();
//                    NDArray target = data.head();
//                    NDArray featStaticCat = data.get(1);
//
//                    NDArray gt = target.get(":, {}:", -PREDICTION_LENGTH);
//                    NDArray pastTarget = target.get(":, :{}", -PREDICTION_LENGTH);
//
//                    NDList gtSplit = gt.split(batch.getSize());
//                    NDList pastTargetSplit = pastTarget.split(batch.getSize());
//                    NDList featStaticCatSplit = featStaticCat.split(batch.getSize());
//
//                    List<TimeSeriesData> batchInput = new ArrayList<>(batch.getSize());
//                    for (int i = 0; i < batch.getSize(); i++) {
//                        TimeSeriesData input = new TimeSeriesData(10);
//                        input.setStartTime(startTime);
//                        input.setField(FieldName.TARGET, pastTargetSplit.get(i).squeeze(0));
//                        input.setField(
//                                FieldName.FEAT_STATIC_CAT, featStaticCatSplit.get(i).squeeze(0));
//                        batchInput.add(input);
//                    }
//                    List<Forecast> forecasts = predictor.batchPredict(batchInput);
////                    NDArray samples = ((SampleForecast) forecasts.get(0)).getSortedSamples();
////                    samples.setName("samples");
////                    saveNDArray(samples);
//                    for (int i = 0; i < forecasts.size(); i++) {
//                        LOGGER.info("Forecast mean: "+forecasts.get(i).mean());
////                        LOGGER.info("Size: "+((SampleForecast) forecasts.get(0)).getSortedSamples().size());
////                        LOGGER.info("Mean: "+((SampleForecast) forecasts.get(0)).getSortedSamples().mean(new int[]{0}));
//                        evaluator.aggregateMetrics(
//                                evaluator.getMetricsPerTs(
//                                        gtSplit.get(i).squeeze(0),
//                                        pastTargetSplit.get(i).squeeze(0),
//                                        forecasts.get(i)));
//                    }
//                    progress.increment(batch.getSize());
//                    batch.close();
//                }
//                return evaluator.computeTotalMetrics();
//            }
//        }
//    }

//    private static void saveNDArray(NDArray array) throws IOException {
//        Path path = Paths.get(MODEL_PATH).resolve(array.getName() + ".npz");
//        try (OutputStream os = Files.newOutputStream(path)) {
//            new NDList(new NDList(array)).encode(os, true);
//        }
//    }

    private static DefaultTrainingConfig setupTrainingConfig(DistributionOutput distributionOutput) {
        SaveModelTrainingListener listener = new SaveModelTrainingListener(MODEL_PATH);
        listener.setSaveModelCallback(
                trainer -> {
                    TrainingResult result = trainer.getTrainingResult();
                    Model model = trainer.getModel();
                    float rmsse = result.getValidateEvaluation("RMSSE");
                    model.setProperty("RMSSE", String.format("%.5f", rmsse));
                    model.setProperty("Loss", String.format("%.5f", result.getValidateLoss()));
                });

        return new DefaultTrainingConfig(new DistributionLoss("Loss", distributionOutput))
                .addEvaluator(new Rmsse(distributionOutput))
                .optDevices(Engine.getInstance().getDevices(1))
                .optInitializer(new XavierInitializer(), Parameter.Type.WEIGHT)
                .addTrainingListeners(TrainingListener.Defaults.logging(MODEL_PATH))
                .addTrainingListeners(listener);
    }

    /**
     * Create the deepar model with specified distribution output.
     *
     * @param distributionOutput the distribution output
     * @param training if training create trainingNetwork else predictionNetwork
     * @return deepar model
     */
    private static DeepARNetwork getDeepARModel(DistributionOutput distributionOutput, boolean training,
                                                Integer itemCardinality, Integer predictionLength) {
        List<Integer> cardinality = new ArrayList<>();
        cardinality.add(itemCardinality);

        DeepARNetwork.Builder builder =
                DeepARNetwork.builder()
                        .setCardinality(cardinality)
                        .setFreq(FREQ)
                        .setPredictionLength(predictionLength)
                        .optDistrOutput(distributionOutput)
                        .optUseFeatStaticCat(true);
        return training ? builder.buildTrainingNetwork() : builder.buildPredictionNetwork();
    }

    private static M5Forecast getDataset(List<TimeSeriesTransform> transformation, int contextLength, Dataset.Usage usage,
                                         LocalDateTime startTime, int maxDays)
            throws IOException {

        Repository repository = Repository.newInstance("local_dataset",
                Paths.get("/Users/Weronika/Inf_semestr10/simple-erp/simple-erp-app/src/main/resources/forecasting/"));

        M5Forecast.Builder builder =
                M5Forecast.builder()
                        .optUsage(usage)
                        .optRepository(repository)
                        .optGroupId(BasicDatasets.GROUP_ID)
                        .optArtifactId("m5forecast-unittest")
                        .setTransformation(transformation)
                        .setContextLength(contextLength)
                        .setSampling(32, usage == Dataset.Usage.TRAIN);

//        int maxDays = usage == Dataset.Usage.TRAIN ? 10 : 14;
        for (int i = 1; i <= maxDays; i++) {
            builder.addFeature("d_" + i, FieldName.TARGET);
        }

        M5Forecast m5Forecast =
                builder
                        .addFeature("item_id", FieldName.FEAT_STATIC_CAT)
                        .addFieldFeature(
                                FieldName.START,
                                new Feature(
                                        "date",
                                        TimeFeaturizers.getConstantTimeFeaturizer(startTime)))
                        .build();
        m5Forecast.prepare(new ProgressBar());

        return m5Forecast;
    }

    public ForecastingActive checkForecastingState() {
        ForecastingProperties forecastingProperties = forecastingPropertiesRepository.findByCodeAndIsValid("FORECASTING_ACTIVE", true)
                .orElseThrow( () -> new ApiNotFoundException("exception.forecastingPropertyNotFound"));
        ForecastingActive forecastingActive = new ForecastingActive();
        forecastingActive.setActive(!forecastingProperties.getValue().equals("NO"));
        return forecastingActive;
    }

    @Transactional
    public void train(InputStreamSource file) {
        try {
            ForecastingTrainingData forecastingTrainingData = ExcelHelper.createTrainingFile(file.getInputStream());
            LOGGER.info(forecastingTrainingData.getForecastingTrainingElementList().toString());

            List<List<String>> trainingFileLines = new ArrayList<>();
            List<String> headerRecord = new ArrayList<>();
            headerRecord.add("id");
            headerRecord.add("item_id");
            long duration = Duration.between(forecastingTrainingData.getStartDate(), forecastingTrainingData.getEndDate()).toDays();
            for (long i = 1; i <= duration; i++) {
                headerRecord.add("d_" + i);
            }
            trainingFileLines.add(headerRecord);
            for (ForecastingTrainingElement forecastingTrainingElement: forecastingTrainingData.getForecastingTrainingElementList()){
                List<String> record = new ArrayList<>();
                record.add(forecastingTrainingElement.getId().toString());
                record.add(getProductCategoryMapping(forecastingTrainingElement.getItemId()));
                record.addAll(forecastingTrainingElement.getDayQuantity());
                trainingFileLines.add(record);
            }

            File csvTrainingFile = new File(MODEL_PATH + "/weekly_sales_train_validation.csv");
            try(PrintWriter pw = new PrintWriter(csvTrainingFile)) {
                trainingFileLines.stream()
                        .map(this::convertToCSV)
                        .forEach(pw::println);
            }

            long maxDays = Duration.between(forecastingTrainingData.getStartDate(), forecastingTrainingData.getEndDate()).toDays();

            try {
                this.trainModel(4,
                        forecastingTrainingData.getForecastingTrainingElementList().size(),
                        forecastingTrainingData.getStartDate(), (int) maxDays);
            } catch (IOException | TranslateException exception){
                throw new ApiExpectationFailedException("exception.forecastingTraining");
            }

            changeForecastingActiveProperty(true);
        } catch (IOException e){
            throw new ApiExpectationFailedException("exception.wrongFileFormat");
        }
    }

    private String getProductCategoryMapping(String productCode){
        Optional<Product> product = productRepository.findByCode(productCode);
        if(product.isEmpty()) {
            throw new ApiExpectationFailedException("exception.forecastingProductCode");
        }
        if(product.get().getForecastingMapping() != null && !product.get().getForecastingMapping().isEmpty()){
            return product.get().getForecastingMapping();
        } else {
            String mappingPrefix = "FOODS_1_";
            ForecastingProperties forecastingProperties = forecastingPropertiesRepository.findByCodeAndIsValid("CATEGORY_SEQUENCE_NUMBER", true)
                    .orElseThrow( () -> new ApiNotFoundException("exception.forecastingPropertyNotFound"));
            StringBuilder mappingSuffix = new StringBuilder(forecastingProperties.getValue());
            while(mappingSuffix.length() < 3){
                mappingSuffix.insert(0, "0");
            }
            String mapping = mappingPrefix + mappingSuffix;
            product.get().setForecastingMapping(mapping);
            productRepository.save(product.get());
            String newSequenceValue = String.valueOf(Integer.parseInt(forecastingProperties.getValue()) + 1);
            forecastingPropertiesRepository.changeValue(forecastingProperties.getCode(), newSequenceValue);
            return mapping;
        }
    }

    private void changeForecastingActiveProperty(boolean value){
        ForecastingProperties forecastingProperties = forecastingPropertiesRepository.findByCodeAndIsValid("FORECASTING_ACTIVE", true)
                .orElseThrow( () -> new ApiNotFoundException("exception.forecastingPropertyNotFound"));
        if(forecastingProperties.getValue().equals("NO") && value){
            forecastingPropertiesRepository.changeValue(forecastingProperties.getCode(), "YES");
        } else if (forecastingProperties.getValue().equals("YES") && !value){
            forecastingPropertiesRepository.changeValue(forecastingProperties.getCode(), "NO");
        }
    }

    public String convertToCSV(List<String> data) {
        return data.stream()
                .map(this::escapeSpecialCharacters)
                .collect(Collectors.joining(","));
    }

    public String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}
