package pt.pa.patterns.strategy;

import pt.pa.Route;

public interface WeightCalculationStrategy {
    double calculateWeight(Route route);
}
