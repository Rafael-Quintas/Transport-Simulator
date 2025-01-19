package pt.pa;

/**
 * Representa uma Route específica que descreve o tranporte entre dois Stops.
 * Contém informações sobre o tipo de transporte, distância, duração e custo.
 *  @author Rafael Quintas, Rafael Pato, Guilherme Pereira
 */
public class Route {
    private TransportType transportType;
    private double distance;
    private int duration;
    private double sustainability;
    private boolean state;

    /**
     * Construtor para inicializar uma instância de {@code Route}.
     *
     * @param type Tipo de transporte utilizado nesta Route ({@link TransportType}).
     * @param distance Distância da Route em Kms
     * @param duration Duração da Route em minutos.
     * @param sustainability Custo da Route em termos de sustentabilidade.
     */
    public Route(TransportType type, double distance, int duration, double sustainability) {
        if (distance < 0 || duration < 0) { throw new IllegalArgumentException("Distance and Duration cannot be negative."); }
        this.transportType = type;
        this.distance = distance;
        this.duration = duration;
        this.sustainability = sustainability;
        this.state = true;
    }

    public Route(TransportType type, double distance, int duration, double sustainability, boolean state) {
        if (distance < 0 || duration < 0) { throw new IllegalArgumentException("Distance and Duration cannot be negative."); }
        this.transportType = type;
        this.distance = distance;
        this.duration = duration;
        this.sustainability = sustainability;
        this.state = state;
    }

    /**
     * Retorna o tipo de transporte para esta route.
     *
     * @return o tipo de transporte {@code TransportType}.
     */
    public TransportType getTransportType(){
        return this.transportType;
    }

    /**
     * Retorna a distância para esta route.
     *
     * @return a distância como um {@code double}.
     */
    public double getDistance(){
        return this.distance;
    }

    /**
     * Retorna a duração para esta route.
     *
     * @return a duração como um {@code int}.
     */
    public int getDuration(){
        return this.duration;
    }

    /**
     * Retorna a sustentabilidade associada ao transporte.
     *
     * @return a sustentabilidade como um {@code double}.
     */
    public double getSustainability(){
        return this.sustainability;
    }

    /**
     * Define a duração da Route.
     *
     * @param duration nova duração da Route, em minutos.
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Define o estado da Route.
     *
     * @param state o novo estado da Route; {@code true} para ativo e {@code false} para inativo.
     */
    public void setState(boolean state) {
        this.state = state;
    }

    /**
     * Retorna o estado atual da Route.
     *
     * @return {@code true} se a Route estiver ativa, {@code false} caso contrário.
     */
    public boolean getState() {
        return this.state;
    }

    /**
     * Cria uma cópia desta Route.
     *
     * @return uma nova instância de {@code Route} com os mesmos valores desta Route.
     */
    public Route copyRoute() {
        return new Route(this.transportType, this.distance, this.duration, this.sustainability, this.state);
    }

    /**
     * Retorna uma representação textual da Route.
     *
     * @return uma string que descreve o tipo de transporte, distância, duração,
     *         sustentabilidade e estado da Route.
     */
    public String toString() {
        return this.transportType.toString() + "\n+" + this.distance + "\n" + this.duration + "\n" + this.sustainability + "\n" + this.state;
    }
}
