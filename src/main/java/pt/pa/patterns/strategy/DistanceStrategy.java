package pt.pa.patterns.strategy;

import pt.pa.Route;

/**
 * A classe {@code DistanceStrategy} implementa a interface {@link WeightCalculationStrategy}.
 * Esta estratégia calcula o peso de uma rota com base na sua distância.
 *
 * @author Rafael Quintas
 */
public class DistanceStrategy implements WeightCalculationStrategy {

    /**
     * Calcula o peso de uma rota utilizando a distância como critério.
     *
     * @param route A rota cuja distância será utilizada para calcular o peso.
     * @return O valor da distância da rota.
     */
    @Override
    public double calculateWeight(Route route) {
        return route.getDistance();
    }
}
