package pt.pa;

import java.util.List;

public class GenericRoute {
    private Stop stopStart;
    private Stop stopEnd;
    private List<Route> routes;

    public GenericRoute(Stop start, Stop end, List<Route> routes) {
        this.stopStart = start;
        this.stopEnd = end;
        this.routes = routes;
    }

    public Stop getStopStart() {
        return this.stopStart;
    }

    public Stop getStopEnd() {
        return this.stopEnd;
    }

    public List<Route> getRoutes() {
        return this.routes;
    }
}

