package pt.pa;

/**
 * Representa uma Stop com um código único, nome e coordenadas.
 */
public class Stop {
    private String stopCode;
    private String stopName;
    private double latitude;
    private double longitude;

    /**
     * Construtor para inicializar uma instância de {@code Stop}.
     *
     * @param stopCode Código único da Stop.
     * @param stopName Nome da Stop.
     * @param latitude Latitude da Stop.
     * @param longitude Longitude da Stop.
     */
    public Stop(String stopCode, String stopName, double latitude, double longitude) {
        if (stopCode == null || stopName == null) { throw new IllegalArgumentException("Name and Code cannot be null."); }
        this.stopCode = stopCode;
        this.stopName = stopName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Obtém o código único da Stop.
     *
     * @return Código da Stop.
     */
    public String getStopCode() {
        return this.stopCode;
    }

    /**
     * Retorna uma representação em String da Stop, que é o seu nome.
     *
     * @return Nome da Stop.
     */
    public String toString() {
        return stopName;
    }
}
