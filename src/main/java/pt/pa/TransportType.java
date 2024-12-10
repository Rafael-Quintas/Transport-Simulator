package pt.pa;

/**
 * Enumeração que representa os diferentes tipos de transporte disponíveis.
 *  @author Rafael Quintas, Rafael Pato, Guilherme Pereira
 */
public enum TransportType {
    BUS("Blue"),
    TRAIN("Red"),
    BOAT("Orange"),
    WALK("Violet"),
    BICYCLE("Green");

    private final String color;

    TransportType(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        String name = name().charAt(0) + name().substring(1).toLowerCase();
        return name + " - " + color + " Line";
    }
}

