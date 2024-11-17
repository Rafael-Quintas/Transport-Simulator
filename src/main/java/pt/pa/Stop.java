package pt.pa;

/**
 * Representa uma paragem com um código único, nome e coordenadas.
 */
public class Stop {
    private String stopCode;
    private String stopName;
    private double latitude;
    private double longitude;

    /**
     * Construtor para inicializar uma instância de {@code Stop}.
     *
     * @param stopCode Código único da paragem.
     * @param stopName Nome da paragem.
     * @param latitude Latitude da paragem.
     * @param longitude Longitude da paragem.
     */
    public Stop(String stopCode, String stopName, double latitude, double longitude) {
        this.stopCode = stopCode;
        this.stopName = stopName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Obrém o código único da paragem.
     *
     * @return Código da paragem.
     */
    public String getStopCode() {
        return this.stopCode;
    }

    /**
     * Retorna uma representação em String da paragem, que é o seu nome.
     *
     * @return Nome da paragem.
     */
    public String toString() {
        return stopName;
    }
}
