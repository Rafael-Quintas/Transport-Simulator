package pt.pa.patterns.strategy;

import pt.pa.Route;

public class SustainabilityStrategy  implements WeightCalculationStrategy {
    public static final int OFFSET = 10000;

    @Override
    public double calculateWeight(Route route) {
        return route.getSustainability() + OFFSET;
    }
}