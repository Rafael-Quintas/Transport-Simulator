package pt.pa;

/**
 * Representa uma Route específica que descreve o tranporte entre duas Stops.
 * Contém informações sobre o tipo de transporte, distância, duração e custo.
 *  @author Rafael Quintas, Rafael Pato, Guilherme Pereira
 */
public class Route {
    private TransportType transportType;
    private double distance;
    private int duration;
    private double sustainability;

    /**
     * Construtor para inicializar uma instância de {@code Route}.
     *
     * @param type Tipo de transporte utilizado nesta Route ({@link TransportType}).
     * @param distance Distância da Route em Kms
     * @param duration Duração da Route em minutos.
     * @param sustainability Custo da Route em unidades monetárias.
     */
    public Route(TransportType type, double distance, int duration, double sustainability) {
        if (distance < 0 || duration < 0) { throw new IllegalArgumentException("Distance and Duration cannot be negative."); }
        this.transportType = type;
        this.distance = distance;
        this.duration = duration;
        this.sustainability = sustainability;
    }

    public TransportType getTransportType(){
        return this.transportType;
    }

    public double getDistance(){
        return this.distance;
    }

    public int getDuration(){
        return this.duration;
    }

    public double getSustainability(){
        return this.sustainability;
    }
}
