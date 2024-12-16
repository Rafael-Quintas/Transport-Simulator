package pt.pa.patterns.strategy;

import pt.pa.Route;

public class  DurationStrategy implements WeightCalculationStrategy {

    @Override
    public double calculateWeight(Route route) {
        return route.getDuration();
    }
}
