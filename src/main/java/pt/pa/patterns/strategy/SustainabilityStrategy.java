package pt.pa.patterns.strategy;

import pt.pa.Route;

public class SustainabilityStrategy  implements WeightCalculationStrategy {

    @Override
    public double calculateWeight(Route route) {
        return route.getSustainability();
    }
}