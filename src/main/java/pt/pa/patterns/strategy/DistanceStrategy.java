package pt.pa.patterns.strategy;

import pt.pa.Route;

public class DistanceStrategy implements WeightCalculationStrategy {

    @Override
    public double calculateWeight(Route route) {
        return route.getDistance();
    }
}
