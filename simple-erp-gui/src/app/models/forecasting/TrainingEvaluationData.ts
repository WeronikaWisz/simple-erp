import {ChartData} from "../trade/ChartData";

export interface TrainingEvaluationData{
  real: ChartData[];
  forecast: ChartData[];
}
