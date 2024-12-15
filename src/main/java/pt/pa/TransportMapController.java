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

/**
 * A classe {@code TransportMapController} atua como um Controller para gerir a interação entre o modelo
 * ({@link TransportMap}) e a visualização ({@link MapView}). Responde a eventos da GUI e executa operações
 * no modelo e na visualização.
 *
 * @author Rafael Quintas, Rafael Pato, Guilherme Pereira
 */
public class TransportMapController implements Observer {
    private final TransportMap model;
    private final MapView view;
    private final Logger logger;

    /**
     * Construtor para criar uma instância do Controller, configurando os observadores no modelo e na visualização.
     *
     * @param model modelo do mapa de transporte ({@link TransportMap}).
     * @param view visualização do mapa de transporte ({@link MapView}).
     * @param logger logger usado para registar eventos e ações do Controller.
     */
    public TransportMapController(TransportMap model, MapView view, Logger logger) {
        this.model = model;
        this.view = view;
        this.logger = logger;

        view.setTriggers(this);
        model.addObservers(this);
    }

    /**
     * Exibe os detalhes de uma paragem selecionada na interface.
     *
     * @param vertex o vértice selecionado ({@link SmartGraphVertex}).
     */
    public void doShowVertexDetails(SmartGraphVertex<Stop> vertex) {
        Stop stop = vertex.getUnderlyingVertex().element();
        view.showVertexDetails(stop);
        logger.info("Vertex clicked: " + stop.getStopName());
    }

    /**
     * Exibe os detalhes de uma rota selecionada na interface.
     *
     * @param edge a aresta selecionada ({@link SmartGraphEdge}).
     */
    public void doShowEdgeDetails(SmartGraphEdge<List<Route>, Stop> edge) {
        List<Route> routes = edge.getUnderlyingEdge().element();
        view.showEdgeDetails(routes);
        logger.info("Edge clicked: [" + edge.getUnderlyingEdge().vertices()[0].element().getStopName() + ", " + edge.getUnderlyingEdge().vertices()[1].element().getStopName()+"]");
    }

    /**
     * Exibe os detalhes de centralidade na interface.
     */
    public void doShowCentralityDetails(){
        view.showCentralityDetails();
        logger.info("User has clicked the Centrality button");
    }

    /**
     * Exibe as cinco paragens mais centrais na interface.
     */
    public void doShowTopFive(){
        view.showTopFiveCentralityChart();
        logger.info("User has clicked the Top Five button");
    }

    /**
     * Cria uma interface para calcular e exibir paragens a uma certa distância de uma paragem inicial.
     */
    public void doShowStopsNRoutesAway() {
        logger.info("User has clicked the StopsNRoutesAway button");
        view.createStopsNRoutesAwayPopup();
    }

    /**
     * Calcula e exibe o caminho de menor custo entre duas paragens com base em um critério e nos tipos de transporte selecionados.
     */
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

    /**
     * Gere a seleção de um caminho personalizado pelo utilizador e calcula os custos das conexões.
     *
     * @param vertex vértice selecionado ({@link SmartGraphVertex}).
     */

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

    /**
     * Calcula o custo da aresta entre dois vértices com base numa estratégia de cálculo de peso.
     *
     * @param start vértice de origem.
     * @param end vértice de destino.
     * @param strategy estratégia de cálculo de peso ({@link WeightCalculationStrategy}).
     * @return custo da aresta ou {@code Double.POSITIVE_INFINITY} se não houver uma conexão válida.
     */
    private double calculateEdgeCost(Vertex<Stop> start, Vertex<Stop> end, WeightCalculationStrategy strategy) {
        return model.getGraph().incidentEdges(start).stream()
                .filter(edge -> model.getGraph().opposite(start, edge).equals(end))
                .flatMap(edge -> edge.element().stream())
                .mapToDouble(route -> strategy.calculateWeight(route))
                .min()
                .orElse(Double.POSITIVE_INFINITY);
    }

    /**
     * Atualiza a interface com base em notificações do modelo.
     *
     * @param obj objeto de notificação enviado pelo modelo.
     */
    @Override
    public void update(Object obj) {
        logger.info("Model update received: " + obj.toString());
        view.update(obj);
    }

    /**
     * Regista mensagens personalizadas logger.
     *
     * @param string mensagem a ser registada.
     */
    public void triggerLog(String string) {
        logger.info(string);
    }
}
