package pt.pa;

import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import java.util.List;

public class TransportMap extends GraphEdgeList<Stop, List<Route>> {
    List<Stop> stopList;
    List<GenericRoute> genericRouteList;

    public TransportMap() {
        this.stopList = DataImporter.loadStops();
        this.genericRouteList = DataImporter.loadRoutes();
    }


    public Graph<Stop, List<Route>> loadToGraph() {
        Graph<Stop, List<Route>> graph = new GraphEdgeList<>();

        for (Stop s : stopList) {
            graph.insertVertex(s);
        }

        for (GenericRoute gr : genericRouteList) {
            graph.insertEdge(getStopByDesignation(gr.getStopStart()), getStopByDesignation(gr.getStopEnd()), gr.getRoutes());
        }

        return graph;
    }

    private Stop getStopByDesignation(String code) {
        for (Stop s : stopList) {
            if (s.getStopCode().equals(code)) {
                return s;
            }
        }
        return null;
    }
}
