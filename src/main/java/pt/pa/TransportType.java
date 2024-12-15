package pt.pa;

/**
 * Enumeração que representa os diferentes tipos de transporte disponíveis no sistema.
 * Cada tipo de transporte possui uma cor associada, que pode ser usada para identificação visual.
 *
 * Os tipos de transporte disponíveis são:
 * <ul>
 *     <li>{@link #BUS}: Representa o transporte de autocarro, identificado pela cor amarela.</li>
 *     <li>{@link #TRAIN}: Representa o transporte ferroviário, identificado pela cor azul.</li>
 *     <li>{@link #BOAT}: Representa o transporte marítimo, identificado pela cor verde.</li>
 *     <li>{@link #WALK}: Representa o deslocamento a pé, identificado pela cor azul-esverdeado.</li>
 *     <li>{@link #BICYCLE}: Representa o transporte de bicicleta, identificado pela cor magenta.</li>
 * </ul>
 *
 * @author
 * Rafael Quintas, Rafael Pato, Guilherme Pereira
 */
public enum TransportType {
    BUS("Yellow"),
    TRAIN("Blue"),
    BOAT("Green"),
    WALK("Teal"),
    BICYCLE("Magenta");

    private final String color;

    /**
     * Construtor que associa uma cor ao tipo de transporte.
     *
     * @param color a cor associada ao tipo de transporte.
     */
    TransportType(String color) {
        this.color = color;
    }

    /**
     * Retorna a cor associada ao tipo de transporte.
     *
     * @return String que representa a cor associada.
     */
    public String getColor() {
        return color;
    }

    /**
     * Retorna uma representação em String do tipo de transporte.
     *
     * @return String formatada que representa o tipo de transporte a respetiva cor.
     */
    @Override
    public String toString() {
        String name = name().charAt(0) + name().substring(1).toLowerCase();
        return name + " - " + color + " Line";
    }
}

