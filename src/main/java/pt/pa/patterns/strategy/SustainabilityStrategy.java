package pt.pa.patterns.strategy;

import pt.pa.Route;

/**
 * A classe {@code SustainabilityStrategy} implementa a interface {@link WeightCalculationStrategy}.
 * Esta estratégia calcula o peso de uma rota com base na sua sustentabilidade, adicionando um valor de desvio
 * definido pela constante {@code OFFSET}.
 *
 * O objetivo é utilizar a métrica de sustentabilidade como critério principal, aplicando uma penalização
 * ou ajuste padrão.
 *
 * @author Rafael Quintas
 */
public class SustainabilityStrategy implements WeightCalculationStrategy {

    /**
     * Constante que define o valor de desvio a ser adicionado à sustentabilidade da rota.
     */
    public static final int OFFSET = 10000;

    /**
     * Calcula o peso de uma rota utilizando a sustentabilidade como critério, adicionando o valor de {@code OFFSET}.
     *
     * @param route A rota cuja sustentabilidade será utilizada para calcular o peso.
     * @return O valor da sustentabilidade da rota acrescido de {@code OFFSET}.
     */
    @Override
    public double calculateWeight(Route route) {
        return route.getSustainability() + OFFSET;
    }
}
