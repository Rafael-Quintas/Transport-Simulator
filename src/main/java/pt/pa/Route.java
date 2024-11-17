package pt.pa;

/**
 * Representa uma rota especídfica que descreve o tranporte entre duas paragens.
 * Contém informações sobre o tipo de transporte, distância, duração e custo.
 */
public class Route {
    private TransportType transportType;
    private double distance;
    private int duration;
    private double cost;

    /**
     * Construtor para inicializar uma instância de {@code Route}.
     *
     * @param type Tipo de transporte utilizado nesta rota ({@link TransportType}).
     * @param distance Distância da rota em Kms
     * @param duration Duração da rota em minutos.
     * @param cost Custo da rota em unidades monetárias.
     */
    public Route(TransportType type, double distance, int duration, double cost) {
        this.transportType = type;
        this.distance = distance;
        this.duration = duration;
        this.cost = cost;
    }
}
