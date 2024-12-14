package pt.pa;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartGraphEdge;
import com.brunomnsilva.smartgraph.graphview.SmartGraphVertex;
import pt.pa.patterns.observer.Observer;
import pt.pa.patterns.strategy.WeightCalculationStrategy;
import pt.pa.view.MapView;
import pt.pa.patterns.strategy.SustainabilityStrategy;
import java.util.List;
import java.util.logging.Logger;

public class TransportMapController implements Observer {
    private final TransportMap model;
    private final MapView view;
    private final Logger logger;

    public TransportMapController(TransportMap model, MapView view, Logger logger) {
        this.model = model;
        this.view = view;
        this.logger = logger;

        view.setTriggers(this);
        model.addObservers(this);
    }

    public void doShowVertexDetails(SmartGraphVertex<Stop> vertex) {
        Stop stop = vertex.getUnderlyingVertex().element();
        view.showVertexDetails(stop);
        logger.info("Vertex clicked: " + stop.getStopName());
    }

    public void doShowEdgeDetails(SmartGraphEdge<List<Route>, Stop> edge) {
        List<Route> routes = edge.getUnderlyingEdge().element();
        view.showEdgeDetails(routes);
        logger.info("Edge clicked: [" + edge.getUnderlyingEdge().vertices()[0].element().getStopName() + ", " + edge.getUnderlyingEdge().vertices()[1].element().getStopName()+"]");
    }

    public void doShowCentralityDetails(){
        view.showCentralityDetails();
        logger.info("User has clicked the Centrality button");
    }

    public void doShowTopFive(){
        view.showTopFiveCentralityChart();
        logger.info("User has clicked the Top Five button");
    }

    public void doShowStopsNRoutesAway() {
        logger.info("User has clicked the StopsNRoutesAway button");
        view.createStopsNRoutesAwayPopup();
    }

    public void doShowLeastCostRoute() {
        String origin = view.getOriginDropdown().getValue();
        String destination = view.getDestinationDropdown().getValue();
        String criteria = view.getCriteriaDropdown().getValue().toLowerCase();
        List<TransportType> transportTypes = view.getSelectedTransportTypes();

        try {
            logger.info("User has selected the following origin Stop: " + origin);
            logger.info("User has selected the following destination Stop: " + destination);
            logger.info("User has selected: " + criteria + " criteria");
            logger.info("User has clicked the Calculate Cost button");
            Path path = model.leastCostBetweenStops(origin, destination, criteria, transportTypes);

            // Atualizar a label de custo no visualizador
            view.updateCostLabel("Cost: " + path.getTotalCost());

            // Colorir as arestas no grafo chamando o método da view
            WeightCalculationStrategy strategy = model.createStrategy(criteria);

            view.highlightPath(path.getPath(), transportTypes, strategy);
        } catch (IllegalStateException e) {
            view.showWarning(e.getMessage());
        }
    }

    public void doShowCustomPath(SmartGraphVertex<Stop> vertex) {
        if (view.getIsSelectingCustomPath()) {
            Vertex<Stop> selectedVertex = vertex.getUnderlyingVertex();

            if (view.getCustomPath().isEmpty() || model.isAdjacent(view.getCustomPath().get(view.getCustomPath().size() - 1), selectedVertex)) {
                List<Vertex<Stop>> customPath = view.getCustomPath();
                view.addToCustomPath(selectedVertex);

                if (customPath.size() > 1) {
                    Vertex<Stop> lastVertex = customPath.get(customPath.size() - 2);
                    WeightCalculationStrategy strategy = model.createStrategy(view.getCriteriaDropdown().getValue().toLowerCase());

                    // Calcular o custo da nova aresta e atualizar o custo total
                    double edgeCost = calculateEdgeCost(lastVertex, selectedVertex, strategy);

                    // Aplicar o OFFSET se for SustainabilityStrategy
                    if (strategy instanceof SustainabilityStrategy) {
                        edgeCost -= SustainabilityStrategy.OFFSET;
                    }

                    edgeCost = Math.round(edgeCost * 100.0) / 100.0;
                    view.updateCurrentCustomPathCost(edgeCost);

                    // Destacar a aresta entre o último e o atual
                    view.highlightEdge(lastVertex, selectedVertex, strategy);
                }

                // Notificar a seleção do vértice
                view.showNotification("Vertex added to custom path: " + selectedVertex.element().getStopName());
            } else {
                view.showWarning("Selected Stop is not adjacent to the last Stop in the path.");
            }
        }
    }

    private double calculateEdgeCost(Vertex<Stop> start, Vertex<Stop> end, WeightCalculationStrategy strategy) {
        return model.getGraph().incidentEdges(start).stream()
                .filter(edge -> model.getGraph().opposite(start, edge).equals(end))
                .flatMap(edge -> edge.element().stream())
                .mapToDouble(route -> strategy.calculateWeight(route))
                .min()
                .orElse(Double.POSITIVE_INFINITY);
    }

    @Override
    public void update(Object obj) {
        logger.info("Model update received: " + obj.toString());
        view.update(obj);
    }

    public void triggerLog(String string) {
        logger.info(string);
    }
}
