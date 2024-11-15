package pt.pa;

import java.util.List;

public class GenericRoute {
    private String stopStart;
    private String stopEnd;
    private List<Route> routes;

    public GenericRoute(String start, String end, List<Route> routes) {
        this.stopStart = start;
        this.stopEnd = end;
        this.routes = routes;
    }

    public String getStopStart() {
        return this.stopStart;
    }

    public String getStopEnd() {
        return this.stopEnd;
    }

    public List<Route> getRoutes() {
        return this.routes;
    }
}

