package pt.pa;

import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;

import java.util.List;

public class TransportMap {
    private Graph<Stop, List<Route>> graph;

    public TransportMap() {
        this.graph = new GraphEdgeList<>();
        loadToGraph();
    }


    public Graph<Stop, List<Route>> loadToGraph() {
        List<Stop> stopList = DataImporter.loadStops();

        for (Stop s : stopList) {
            graph.insertVertex(s);
        }

        List<GenericRoute> genericRouteList = DataImporter.loadRoutes(stopList);
        for (GenericRoute gr : genericRouteList) {
            graph.insertEdge(gr.getStopStart(), gr.getStopEnd(), gr.getRoutes());
        }

        return graph;
    }

    public void positionVertex(SmartGraphPanel<Stop, List<Route>> smartGraph) {
        DataImporter.loadCordinates(smartGraph, this.graph);
    }

    public Graph<Stop, List<Route>> getGraph() {
        return this.graph;
    }
}
