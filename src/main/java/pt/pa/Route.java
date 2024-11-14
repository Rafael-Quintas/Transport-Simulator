package pt.pa;

public class Route {
    private TransportType transportType;
    private double distance;
    private int duration;
    private double cost;

    public Route(TransportType type, double distance, int duration, double cost) {
        this.transportType = type;
        this.distance = distance;
        this.duration = duration;
        this.cost = cost;
    }
}
