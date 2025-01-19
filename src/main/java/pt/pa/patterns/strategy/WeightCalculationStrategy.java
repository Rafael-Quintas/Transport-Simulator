package pt.pa.patterns.strategy;

import pt.pa.Route;

/**
 * A interface {@code WeightCalculationStrategy} define um contrato para a implementação de estratégias
 * de cálculo de peso em rotas. As estratégias podem basear-se em diferentes critérios, como distância,
 * duração ou sustentabilidade.
 *
 * Esta interface faz parte do padrão de design Strategy, permitindo que diferentes algoritmos de cálculo
 * sejam aplicados de forma intercambiável.
 *
 * @author Rafael Quintas
 */
public interface WeightCalculationStrategy {

    /**
     * Calcula o peso de uma rota com base no critério implementado pela estratégia concreta.
     *
     * @param route A rota para a qual o peso será calculado.
     * @return O peso calculado para a rota, representado como um valor {@code double}.
     */
    double calculateWeight(Route route);
}
