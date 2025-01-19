package pt.pa.patterns.strategy;

import pt.pa.Route;

/**
 * A classe {@code DurationStrategy} implementa a interface {@link WeightCalculationStrategy}.
 * Esta estratégia calcula o peso de uma rota com base na sua duração.
 *
 * @author Rafael Quintas
 */
public class DurationStrategy implements WeightCalculationStrategy {

    /**
     * Calcula o peso de uma rota utilizando a duração como critério.
     *
     * @param route A rota cuja duração será utilizada para calcular o peso.
     * @return O valor da duração da rota.
     */
    @Override
    public double calculateWeight(Route route) {
        return route.getDuration();
    }
}
