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
}

