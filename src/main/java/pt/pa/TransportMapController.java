package pt.pa;

import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartGraphEdge;
import com.brunomnsilva.smartgraph.graphview.SmartGraphVertex;
import pt.pa.patterns.memento.Caretaker;
import pt.pa.patterns.strategy.SustainabilityStrategy;
import pt.pa.patterns.strategy.WeightCalculationStrategy;
import pt.pa.view.MapView;
import java.util.List;
import java.util.logging.Logger;

/**
 * A classe {@code TransportMapController} atua como um Controller para gerir a interação entre o modelo
 * ({@link TransportMap}) e a visualização ({@link MapView}). Responde a eventos da UI e executa operações
 * no modelo e na visualização.
 *
 * @author Rafael Quintas, Rafael Pato, Guilherme Pereira
 */

public class TransportMapController {
    private TransportMap model;
    private MapView view;
    private Logger logger;
    private Caretaker caretaker;

    /**
     * Construtor para criar uma instância do Controller, configura os observadores no modelo e na visualização.
     *
     * @param model modelo do mapa de transporte ({@link TransportMap}).
     * @param view visualização do mapa de transporte ({@link MapView}).
     * @param logger logger usado para registar eventos e ações do Controller.
     */
    public TransportMapController(TransportMap model, MapView view, Logger logger) {
        this.model = model;
        this.view = view;
        this.logger = logger;
        this.caretaker = new Caretaker(model);
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
        view.showEdgeDetails(edge);
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
        String criteria = view.getCriteriaDropdown().getValue();
        List<TransportType> transportTypes = view.getSelectedTransportTypes();

        try {
            logger.info("User has selected the following origin Stop: " + origin);
            logger.info("User has selected the following destination Stop: " + destination);
            logger.info("User has selected: " + criteria + " criteria");
            logger.info("User has clicked the Calculate Cost button");

            WeightCalculationStrategy strategy = model.createStrategy(criteria);

            Path path = model.leastCostBetweenStops(origin, destination, strategy, transportTypes);

            view.updateCostLabel("Total Path Cost: " + Math.round(path.getTotalCost() * 100.0) / 100.0);

            view.highlightPath(path.getPath(), transportTypes, strategy);
        } catch (IllegalStateException | IllegalArgumentException e) {
            view.showWarning(e.getMessage());
        }
    }

    /**
     * Gere a seleção de um caminho personalizado pelo utilizador e calcula os custos das conexões.
     *
     * @param vertex vértice selecionado ({@link SmartGraphVertex}).
     */
    public void doShowCustomPath(SmartGraphVertex<Stop> vertex) {
        if (!view.getIsSelectingCustomPath()) {
            return;
        }

        Vertex<Stop> selectedVertex = vertex.getUnderlyingVertex();
        try {
            List<Vertex<Stop>> customPath = view.getCustomPath();
            if (customPath.contains(selectedVertex)) {
                throw new IllegalArgumentException("This Stop is already in the custom path: " + selectedVertex.element().getStopName());
            }

            if (!customPath.isEmpty() && !model.isAdjacent(customPath.get(customPath.size() - 1), selectedVertex)) {
                throw new IllegalArgumentException("Selected Stop is not adjacent to the last Stop in the path.");
            }

            handleCustomPath(selectedVertex);
        } catch (Exception e) {
            view.showWarning(e.getMessage());
        }
    }

    private void handleCustomPath(Vertex<Stop> selectedVertex) {
        List<Vertex<Stop>> customPath = view.getCustomPath();
        Vertex<Stop> lastVertex = customPath.isEmpty() ? null : customPath.get(customPath.size() - 1);

        if (lastVertex != null) {
            WeightCalculationStrategy strategy = model.createStrategy(view.getCriteriaDropdown().getValue().toLowerCase());

            // Considerar todos os transportes
            List<TransportType> allTransportTypes = List.of(TransportType.values());

            double edgeCost = model.calculateCostBetweenStops(lastVertex, selectedVertex, allTransportTypes, strategy);

            if (strategy instanceof SustainabilityStrategy) {
                edgeCost -= SustainabilityStrategy.OFFSET;
            }

            if (edgeCost == Double.POSITIVE_INFINITY) {
                throw new IllegalArgumentException("No valid route between " + lastVertex.element().getStopName() + " and " + selectedVertex.element().getStopName());
            }

            view.updateCurrentCustomPathCost(edgeCost);
            view.highlightEdge(lastVertex, selectedVertex, strategy);
        }

        view.addToCustomPath(selectedVertex);
        view.showNotification("Vertex added to custom path: " + selectedVertex.element().getStopName());
    }

    /**
     * Desativa as rotas selecionadas numa aresta, alterando o seu estado para inativo.
     *
     * @param edge            a aresta que contém as rotas a serem desativadas.
     * @param routesToDisable a lista de rotas a desativar.
     */
    public void doDisableRoute(SmartGraphEdge<List<Route>, Stop> edge, List<Route> routesToDisable) {
        logger.info("INFO: User disabled an edge: " + edge.getUnderlyingEdge());
        caretaker.saveState();
        model.disableRoute(edge.getUnderlyingEdge(), routesToDisable);
    }

    /**
     * Altera a duração de uma rota de bicicleta para o valor fornecido.
     *
     * @param route    a rota de bicicleta cuja duração será alterada.
     * @param duration o novo valor de duração (em minutos).
     * @throws IllegalArgumentException se a rota não for do tipo bicicleta ou se a duração for menor que 0.
     */
    public void doChangeBicycleRouteDuration(Route route, int duration) {
        if (route.getTransportType() != TransportType.BICYCLE) {
            logger.info("ERROR: User attemped to change the duration of a Route but it failed");
            throw new IllegalArgumentException("You can only change the duration of a Bicycle Route");
        } else if (duration < 0) {
            logger.info("ERROR: User attemped to change the duration of a Route but it failed");
            throw new IllegalArgumentException("The duration has to be greater than zero");
        }
        logger.info("INFO: User has changed the duration of a Route");
        caretaker.saveState();

        model.changeBicycleRouteDuration(route, duration);
    }

    /**
     * Reverte o estado do modelo para a última versão guardada.
     *
     * @throws IllegalStateException se não houver estados para restaurar.
     */
    public void undo() {
        caretaker.restoreState();
    }

    /**
     * Regista mensagens personalizadas no logger.
     *
     * @param string mensagem a ser registada.
     */
    public void triggerLog(String string) {
        logger.info(string);
    }
}