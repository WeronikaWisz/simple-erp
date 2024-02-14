# Simple ERP

Uproszczony system klasy ERP dedykowany dla małych przedsiębiorstw głównie w branży e-commerce. W celu optymalizacji powierzchni magazynowych, wprowadzono do niego prognozowanie popytu na produkty.

Do prognozowania zapotrzebowania na produkty wykorzystano algorytm DeepAR. Jest to model uczenia nadzorowanego, który przy użyciu rekurencyjnych sieci neuronowych (RNN) przewiduje jednowymiarowe szeregi czasowe. Dzięki takiemu rozwiązaniu prognoza zapotrzebowania na wiele produktów jest możliwa przy pomocy jednego modelu, co w przypadku klasycznych metod prognozowania jak ARIMA czy ETS nie byłoby możliwe - dla każdego produktu konieczny byłby osobny model. Takie podejście pozwala dodatkowo na wykrycie zależności między sprzedażą różnych produktów.

### Prezentacja działania

https://github.com/WeronikaWisz/simple-erp/assets/32033773/26b2f59e-76e8-402d-acb6-0c188e8f8e89

