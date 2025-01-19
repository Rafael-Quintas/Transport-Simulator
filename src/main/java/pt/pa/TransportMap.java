package pt.pa;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import pt.pa.patterns.memento.Memento;
import pt.pa.patterns.memento.Originator;
import pt.pa.patterns.observer.Subject;
import pt.pa.patterns.strategy.*;

import java.util.*;

/**
 * A classe {@code TransportMap} representa um mapa de transporte baseado num grafo que modela Stops
 * ({@link Stop}) e as suas Routes ({@link Route}). Esta classe fornece métodos para manipulação, cálculo e análise
 * de Stops e Routes.
 *
 * A estrutura principal é um grafo do tipo {@code GraphEdgeList} que armazena os Stops como vértices
 * e as conexões entre eles como arestas, onde cada aresta pode conter uma lista de Routes disponíveis.
 *
 * @author Rafael Quintas, Rafael Pato, Guilherme Pereira
 */
public class TransportMap extends Subject implements Originator {
    private Graph<Stop, List<Route>> graph;

    /**
     * Construtor de um novo mapa de transportes inicializando o grafo e carregando as Stops e Routes disponíveis.
     */
    public TransportMap() {
        this.graph = new GraphEdgeList<>();
        loadToGraph();
    }

    /**
     * Obtém o grafo que representa o mapa de transporte.
     *
     * @return Grafo do mapa de transporte ({@link Graph}).
     */
    public Graph<Stop, List<Route>> getGraph() {
        return this.graph;
    }

    /**
     * Carrega as Stops ({@link Stop}) e Routes genéricas ({@link GenericRoute}) para o grafo.
     */
    public void loadToGraph() {
        List<Stop> stopList = DataImporter.loadStops();

        for (Stop s : stopList) {
            graph.insertVertex(s);
        }

        List<GenericRoute> genericRouteList = DataImporter.loadRoutes();
        for (GenericRoute gr : genericRouteList) {
            graph.insertEdge(getStopByDesignation(gr.getStopStart(), stopList), getStopByDesignation(gr.getStopEnd(), stopList), gr.getRoutes());
        }
    }

    /**
     * Posiciona os vértices do grafo no painel gráfico, utilizando as coordenadas definidas no arquivo CSV correspondente.
     *
     * @param smartGraph Painel gráfico ({@link SmartGraphPanel}) onde os vértices serão posicionados.
     */
    public void positionVertex(SmartGraphPanel<Stop, List<Route>> smartGraph) {
        DataImporter.loadCordinates(smartGraph, this.graph);
    }

    /**
     * Adiciona uma nova Stop.
     * <p>
     * Este método permite criar e adicionar uma nova Stop (vértice) ao grafo que representa a rede de transportes.
     * Antes de adicionar, valida os valores fornecidos para garantir que o código, nome, latitude e longitude são válidos.
     * Se os valores forem inválidos, é lançada uma exceção com uma mensagem de erro.
     *
     * @param stopCode  o código único da Stop (não pode ser vazio).
     * @param stopName  o nome da Stop (não pode ser vazio).
     * @param latitude  a latitude da Stop (deve ser um número válido).
     * @param longitude a longitude da Stop (deve ser um número válido).
     * @return o vértice correspondente à nova Stop adicionada.
     * @throws IllegalArgumentException se os valores fornecidos forem inválidos.
     */
    public Vertex<Stop> addStop(String stopCode, String stopName, String latitude, String longitude) {
        if (stopCode.isBlank() || stopName.isBlank()) {
            throw new IllegalArgumentException("Stop name/code must not be empty.");
        }

        if (!isNumeric(latitude) || !isNumeric(longitude)) {
            throw new IllegalArgumentException("Latitude and Longitude must be valid numbers.");
        }

        return graph.insertVertex(new Stop(stopCode, stopName, Double.parseDouble(latitude), Double.parseDouble(longitude)));
    }

    /**
     * Adiciona uma Route entre dois Stops.
     * <p>
     * Este método permite criar ou atualizar uma Route entre os Stops especificadas (vértices `v1` e `v2`),
     * adicionando um novo meio de transporte com os parâmetros fornecidos.
     * Se os valores de distância, duração ou custo não forem numéricos, é lançada uma exceção com mensagem de erro.
     * Caso a Route seja adicionada com sucesso, será inserida no grafo.
     *
     * @param v1       vértice de origem representando a Stop inicial.
     * @param v2       vértice de destino representando a Stop final.
     * @param type     tipo de transporte (deve corresponder a um valor válido de {@link TransportType}).
     * @param distance distância do percurso (em formato de string, será convertida para {@code double}).
     * @param duration duração do percurso (em formato de string, será convertida para {@code int}).
     * @param cost     custo associado ao percurso (em formato de string, será convertido para {@code double}).
     * @return aresta correspondente à nova Route adicionada.
     * @throws IllegalArgumentException se os valores de distância, duração ou custo forem inválidos.
     */
    public Edge<List<Route>, Stop> addRoute(Vertex<Stop> v1, Vertex<Stop> v2, String type, String distance, String duration, String cost) {
        if (!isNumeric(distance) || !isNumeric(cost) || !isNumeric(duration)) {
            throw new IllegalArgumentException("Distance, Duration and Cost must be valid numbers.");
        }

        List<Route> list = getOrCreateRouteList(v1, v2);
        TransportType transportType = TransportType.valueOf(type.toUpperCase());
        list.add(new Route(transportType, Double.parseDouble(distance), Integer.parseInt(duration), Double.parseDouble(cost)));
        return graph.insertEdge(v1, v2, list);
    }

    /**
     * Remove uma Stop.
     * <p>
     * Este método tenta remover o vértice (Stop) do grafo que representa a rede de transportes.
     *
     * @param vertex o vértice ({@link Vertex}) que representa a Stop a ser removida.
     */
    public void removeStop(Vertex<Stop> vertex) {
        graph.removeVertex(vertex);
    }

    /**
     * Remove uma Route.
     * <p>
     * Este método tenta remover a aresta (Route) do grafo que representa a rede de transportes.
     *
     * @param edge a aresta ({@link Edge}) que representa a Route a ser removida.
     */
    public void removeRoute(Edge<List<Route>, Stop> edge) {
        graph.removeEdge(edge);
    }

    /**
     * Encontra uma Stop ({@link Stop}) pelo código de designação
     *
     * @param code     Código da Stop.
     * @param stopList Lista de Stops disponíveis.
     * @return uma instância de ({@link Stop}) correspondente ao código, ou null se não for encontrada.
     */
    private static Stop getStopByDesignation(String code, List<Stop> stopList) {
        for (Stop s : stopList) {
            if (s.getStopCode().equals(code)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Obtém a lista de Routes associadas à conexão entre duas Stops.
     * <p>
     * Este método verifica todas as arestas incidentes no vértice de origem (`stopStart`)
     * e retorna a lista de Routes associadas à aresta que conecta o vértice de origem
     * ao vértice de destino (`stopEnd`). Se nenhuma conexão for encontrada, retorna {@code null}.
     *
     * @param stopStart vértice representando a Stop de origem.
     * @param stopEnd   vértice representando a Stop de destino.
     * @return lista de Routes associadas à conexão entre as Stops, ou {@code null} se não houver conexão.
     */
    private List<Route> getEdgeByConnection(Vertex<Stop> stopStart, Vertex<Stop> stopEnd) {
        for (Edge<List<Route>, Stop> edge : graph.incidentEdges(stopStart)) {
            if (graph.opposite(stopStart, edge).equals(stopEnd)) {
                return edge.element();
            }
        }
        return null;
    }

    /**
     * Obtém a lista de Routes entre dois Stops ou cria uma nova lista vazia.
     * <p>
     * Este método verifica se existe uma lista de Routes entre as Stops especificadas
     * (representadas pelos vértices fornecidos). Se nenhuma lista de Routes for encontrada,
     * retorna uma nova lista vazia.
     *
     * @param stopStart vértice que representa a Stop de origem.
     * @param stopEnd   vértice que representa a Stop de destino.
     * @return a lista de Routes entre as Stops, ou uma nova lista vazia se não existir nenhuma.
     */
    private List<Route> getOrCreateRouteList(Vertex<Stop> stopStart, Vertex<Stop> stopEnd) {
        List<Route> routeList = getEdgeByConnection(stopStart, stopEnd);
        if (routeList == null) {
            return new ArrayList<>();
        }
        return routeList;
    }

    /**
     * Calcula o número de Stops não isoladas no grafo.
     *
     * @return número de Stops conectadas a pelo menos uma Route.
     */
    public int numberOfNonIsolatedStops() {
        return graph.numVertices() - numberOfIsolatedStops();
    }

    /**
     * Calcula o número de Stops isoladas no grafo.
     *
     * @return número de Stops sem conexões.
     */
    public int numberOfIsolatedStops() {
        List<Vertex<Stop>> vertexList = (List<Vertex<Stop>>) graph.vertices();
        int isolatedCounter = 0;

        for (Vertex<Stop> v : vertexList) {
            if (graph.incidentEdges(v).isEmpty()) {
                isolatedCounter++;
            }
        }

        return isolatedCounter;
    }

    /**
     * Calcula o número total de Routes possíveis no grafo.
     *
     * @return número total de Routes.
     */
    public int numberOfPossibleRoutes() {
        List<Edge<List<Route>, Stop>> edgeList = (List<Edge<List<Route>, Stop>>) graph.edges();
        int total = 0;

        for (Edge<List<Route>, Stop> edge : edgeList) {
            total += (int) edge.element().stream().filter(Route::getState).count();
        }

        return total;
    }

    /**
     * Calcula o número de rotas no grafo que utilizam o tipo de transporte recebido por parâmetro.
     * <p>
     * Este método percorre todas as arestas do grafo e soma a quantidade de rotas
     * em cada lista de rotas associada às arestas que correspondem ao {@code TransportType}.
     *
     * @param type o tipo de transporte a verificar.
     * @return o número total de rotas no grafo que utilizam o tipo de transporte especificado.
     */
    public int numberOfRoutesByTransport(TransportType type) {
        List<Edge<List<Route>, Stop>> edgeList = (List<Edge<List<Route>, Stop>>) graph.edges();
        int total = 0;

        for (Edge<List<Route>, Stop> edge : edgeList) {
            List<Route> routes = edge.element();
            total += countRoutesByTransport(routes, type);
        }

        return total;
    }

    /**
     * .
     * Método auxiliar para o método numberOfRoutesByTransport que conta e retorna o número de rotas que correspondem ao transporte fornecido.
     *
     * @param routes a lista de rotas a ser verificada.
     * @param type   o tipo de transporte a verificar.
     * @return o número de rotas na lista que utilizam o tipo de transporte especificado.
     */
    private int countRoutesByTransport(List<Route> routes, TransportType type) {
        int count = 0;
        for (Route route : routes) {
            if (route.getTransportType().equals(type) && route.getState()) {
                count++;
            }
        }

        return count;
    }

    /**
     * Calcula a centralidade de cada Stop com base no número de conexões.
     *
     * @return mapa ordenado de Stops e os seus valores de centralidade.
     */
    public LinkedHashMap<Vertex<Stop>, Integer> centrality() {
        HashMap<Vertex<Stop>, Integer> map = new HashMap<>();
        List<Vertex<Stop>> vertexList = (List<Vertex<Stop>>) graph.vertices();

        for (Vertex<Stop> v : vertexList) {
            map.put(v, graph.incidentEdges(v).size());
        }

        // Ordena o mapa por valores decrescentes
        List<Map.Entry<Vertex<Stop>, Integer>> entryList = new ArrayList<>(map.entrySet());
        entryList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        // LinkedHashMap para termos ordem
        LinkedHashMap<Vertex<Stop>, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Vertex<Stop>, Integer> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    /**
     * Obtém as cinco Stops mais centrais com base na centralidade calculada.
     *
     * @return lista das 5 Stops mais centrais e os seus valores de centralidade.
     */
    public List<Map.Entry<Vertex<Stop>, Integer>> topFiveCentrality() {
        LinkedHashMap<Vertex<Stop>, Integer> centralityMap = centrality();
        List<Map.Entry<Vertex<Stop>, Integer>> entryList = new ArrayList<>(centralityMap.entrySet());

        return entryList.stream().limit(5).toList();
    }

    /**
     * Obtém uma lista de Stops que estão a exatamente N arestas (Routes) de um Stop inicial.
     *
     * @param start Stop inicial.
     * @param N     número de conexões.
     * @return lista de Stops a N conexões.
     * @throws IllegalArgumentException se a Stop inicial for nula ou N for negativo.
     */
    public List<Stop> getStopsNRoutesAway(Vertex<Stop> start, int N) {
        if (start == null || N < 0) {
            throw new IllegalArgumentException("Stop cannot be null, N cannot be negative.");
        }

        Set<Stop> result = new LinkedHashSet<>();
        Queue<Vertex<Stop>> queue = new LinkedList<>();
        Map<Vertex<Stop>, Integer> distances = new HashMap<>();

        queue.add(start);
        distances.put(start, 0);

        while (!queue.isEmpty()) {
            Vertex<Stop> current = queue.poll();
            int currentDistance = distances.get(current);

            // Procuramos neighbors até chegarmos à distância N
            if (currentDistance < N) {
                for (Edge<List<Route>, Stop> edge : graph.incidentEdges(current)) {
                    Vertex<Stop> neighbor = graph.opposite(current, edge);

                    if (!distances.containsKey(neighbor)) {
                        int neighborDistance = currentDistance + 1;
                        distances.put(neighbor, neighborDistance);
                        queue.add(neighbor);

                        if (neighborDistance == N) {
                            result.add(neighbor.element());
                        }
                    }
                }
            }
        }

        return new ArrayList<>(result);
    }


    /**
     * Encontra o Path de menor custo entre duas Stops com base num critério.
     *
     * @param origin      Stop de origem.
     * @param destination Stop de destino.
     * @param strategy    estratégia de otimização ("distance", "duration", "sustainability").
     * @param transports  tipos de transporte disponíveis.
     * @return Path de menor custo como um objeto {@link Path}.
     */
    public Path leastCostBetweenStops(String origin, String destination, WeightCalculationStrategy strategy, List<TransportType> transports) {
        Map<Vertex<Stop>, Double> costs = new HashMap<>();
        Map<Vertex<Stop>, Vertex<Stop>> predecessors = new HashMap<>();
        List<Vertex<Stop>> vertices = new ArrayList<>(graph.vertices());

        Vertex<Stop> originVertex = getVertexByDesignation(origin, vertices);
        Vertex<Stop> destinationVertex = getVertexByDesignation(destination, vertices);

        if (originVertex == null || destinationVertex == null) {
            throw new IllegalArgumentException("Invalid origin or destination stop.");
        }

        for (Vertex<Stop> vertex : vertices) {
            costs.put(vertex, Double.POSITIVE_INFINITY);
            predecessors.put(vertex, null);
        }
        costs.put(originVertex, 0.0);

        for (int i = 0; i < vertices.size() - 1; i++) {
            for (Vertex<Stop> vertex : vertices) {
                for (Edge<List<Route>, Stop> edge : graph.incidentEdges(vertex)) {
                    relaxEdge(vertex, edge, costs, predecessors, transports, strategy);
                }
            }
        }

        for (Vertex<Stop> vertex : vertices) {
            for (Edge<List<Route>, Stop> edge : graph.incidentEdges(vertex)) {
                if (hasNegativeCycle(vertex, edge, costs, transports, strategy)) {
                    throw new IllegalStateException("The graph contains a negative weight cycle.");
                }
            }
        }

        List<Vertex<Stop>> path = makeStopPath(destinationVertex, originVertex, predecessors);
        double totalCost = calculateTotalCost(path, transports, strategy);

        return new Path(path, totalCost);
    }


    /**
     * Relaxa uma aresta no algoritmo de Bellman-Ford, atualizando custos e predecessores.
     *
     * @param u            o vértice de origem.
     * @param edge         a aresta a ser relaxada.
     * @param costs        o mapa de custos acumulados.
     * @param predecessors o mapa de predecessores no Path.
     * @param transports   a lista de tipos de transporte permitidos.
     * @param strategy     a estratégia de cálculo de peso.
     */
    private void relaxEdge(
            Vertex<Stop> u,
            Edge<List<Route>, Stop> edge,
            Map<Vertex<Stop>, Double> costs,
            Map<Vertex<Stop>, Vertex<Stop>> predecessors,
            List<TransportType> transports,
            WeightCalculationStrategy strategy
    ) {
        Vertex<Stop> v = graph.opposite(u, edge);
        double minWeight = Double.POSITIVE_INFINITY;

        for (Route route : edge.element()) {
            // Filtro de rotas ativas e tipos de transporte permitidos
            if (!route.getState() || !transports.contains(route.getTransportType())) {
                continue;
            }

            double weight = strategy.calculateWeight(route);
            if (weight < minWeight) {
                minWeight = weight;
            }
        }

        if (minWeight == Double.POSITIVE_INFINITY) {
            return; // Nenhuma rota válida
        }

        double newCost = costs.get(u) + minWeight;
        if (newCost < costs.get(v)) {
            costs.put(v, newCost);
            predecessors.put(v, u);
        }
    }


    /**
     * Verifica a existência de ciclos de peso negativo no grafo.
     *
     * @param u          vértice de origem.
     * @param edge       aresta a ser verificada.
     * @param costs      mapa de custos acumulados.
     * @param transports lista de tipos de transporte permitidos.
     * @param strategy   estratégia de cálculo de peso.
     * @return {@code true} se houver um ciclo de peso negativo; caso contrário, {@code false}.
     */
    private boolean hasNegativeCycle(
            Vertex<Stop> u,
            Edge<List<Route>, Stop> edge,
            Map<Vertex<Stop>, Double> costs,
            List<TransportType> transports,
            WeightCalculationStrategy strategy
    ) {
        Vertex<Stop> v = graph.opposite(u, edge);

        for (Route route : edge.element()) {
            if (!transports.contains(route.getTransportType()) || !route.getState()) {
                continue;
            }

            double weight = strategy.calculateWeight(route);

            if (costs.get(u) + weight < costs.get(v)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Constrói o Path de Stops do destino para a origem com base no mapa de predecessores.
     *
     * @param destinationVertex vértice de destino.
     * @param originVertex      vértice de origem.
     * @param predecessors      mapa de predecessores no Path.
     * @return lista ordenada de vértices representando o Path.
     * @throws IllegalStateException se não houver Path entre a origem e o destino.
     */
    private List<Vertex<Stop>> makeStopPath(Vertex<Stop> destinationVertex, Vertex<Stop> originVertex, Map<Vertex<Stop>, Vertex<Stop>> predecessors) {
        List<Vertex<Stop>> path = new ArrayList<>();
        Vertex<Stop> step = destinationVertex;

        while (step != null) {
            path.add(0, step);
            step = predecessors.get(step);
        }

        if (path.isEmpty() || !path.get(0).equals(originVertex)) {
            throw new IllegalStateException("There is no possible path between the given stops.");
        }

        return path;
    }

    /**
     * Calcula o custo total de um Path com base nos tipos de transporte e na estratégia de cálculo de peso.
     *
     * @param path       a lista de vértices que compõem o Path.
     * @param transports a lista de tipos de transporte permitidos.
     * @param strategy   a estratégia de cálculo de peso.
     * @return o custo total do Path.
     * @throws IllegalStateException se não houver Routes válidas para o Path escolhido.
     */
    private double calculateTotalCost(
            List<Vertex<Stop>> path,
            List<TransportType> transports,
            WeightCalculationStrategy strategy
    ) {
        double totalCost = 0.0;

        for (int i = 0; i < path.size() - 1; i++) {
            Vertex<Stop> u = path.get(i);
            Vertex<Stop> v = path.get(i + 1);
            double minCost = Double.POSITIVE_INFINITY;

            for (Edge<List<Route>, Stop> edge : graph.incidentEdges(u)) {
                if (graph.opposite(u, edge).equals(v)) {
                    for (Route route : edge.element()) {
                        if (route.getState() && transports.contains(route.getTransportType())) {
                            double weight = strategy.calculateWeight(route);
                            minCost = Math.min(minCost, weight);
                        }
                    }
                }
            }

            if (minCost == Double.POSITIVE_INFINITY) {
                throw new IllegalStateException("No valid routes found for the chosen path.");
            }

            totalCost += minCost;
        }

        return Math.round(totalCost * 100.0) / 100.0;
    }


    /**
     * Cria uma estratégia de cálculo de peso com base no critério fornecido.
     *
     * @param criteria critério de otimização ("distance", "duration", "sustainability").
     * @return uma instância de {@link WeightCalculationStrategy}.
     * @throws IllegalArgumentException se o critério for inválido.
     */
    public WeightCalculationStrategy createStrategy(String criteria) {
        if (criteria == null) {
            throw new IllegalArgumentException("You must choose a valid criteria before calculating the least cost route");
        }
        return switch (criteria.toLowerCase()) {
            case "distance" -> new DistanceStrategy();
            case "duration" -> new DurationStrategy();
            case "sustainability" -> new SustainabilityStrategy();
            default -> null;
        };
    }

    /**
     * Obtém um vértice no grafo com base no nome da Stop.
     *
     * @param stopName   nome da Stop.
     * @param vertexList lista de vértices do grafo.
     * @return vértice correspondente, ou {@code null} se não for encontrado.
     */
    private Vertex<Stop> getVertexByDesignation(String stopName, Collection<Vertex<Stop>> vertexList) {
        for (Vertex<Stop> v : vertexList) {
            if (v.element().getStopName().equals(stopName)) {
                return v;
            }
        }
        return null;
    }

    /**
     * Verifica se dois vértices estão diretamente conectados no grafo.
     *
     * @param current vértice atual.
     * @param next    próximo vértice.
     * @return {@code true} se os vértices forem adjacentes; caso contrário, {@code false}.
     */
    public boolean isAdjacent(Vertex<Stop> current, Vertex<Stop> next) {
        return graph.incidentEdges(current).stream()
                .filter(edge -> !edge.element().isEmpty())
                .anyMatch(edge -> graph.opposite(current, edge).equals(next));
    }

    /**
     * Verifica se uma String é numérica.
     *
     * @param string String a ser verificada.
     * @return {@code true} se a String for numérica; {@code false} caso contrário.
     */
    private boolean isNumeric(String string) {
        if (string == null || string.isBlank()) return false;
        try {
            Double.parseDouble(string);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public Graph<Stop, List<Route>> copyGraph(Graph<Stop, List<Route>> original) {
        Graph<Stop, List<Route>> copy = new GraphEdgeList<>();

        Map<Stop, Stop> stopMap = new HashMap<>();
        for (Vertex<Stop> stop : original.vertices()) {
            Stop copiedStop = stop.element().copyStop();
            stopMap.put(stop.element(), copiedStop);
            copy.insertVertex(copiedStop);
        }

        for (Edge<List<Route>, Stop> edge : original.edges()) {
            Vertex<Stop>[] vertexList = edge.vertices();
            Stop origin = stopMap.get(vertexList[0].element());
            Stop destination = stopMap.get(vertexList[1].element());

            List<Route> copiedRoutes = new ArrayList<>();
            for (Route route : edge.element()) {
                copiedRoutes.add(route.copyRoute());
            }

            copy.insertEdge(origin, destination, copiedRoutes);
        }
        return copy;
    }

    public void disableRoute(Edge<List<Route>, Stop> edge, List<Route> routesToDisable) {
        for (Route route : routesToDisable) {
            route.setState(false);
        }
    }

    public void changeBicycleRouteDuration(Route route, int duration) {
        route.setDuration(duration);
    }

    @Override
    public Memento createMemento() {
        return new TransportMapMemento(this.graph);
    }

    @Override
    public void setMemento(Memento savedState) {
        if (savedState instanceof TransportMapMemento) {
            this.graph = ((TransportMapMemento) savedState).getGraph();
        }
    }

    private class TransportMapMemento implements Memento {
        private Graph<Stop, List<Route>> graph;

        public TransportMapMemento(Graph<Stop, List<Route>> graph) {
            this.graph = copyGraph(graph);
        }

        public Graph<Stop, List<Route>> getGraph() {
            return this.graph;
        }
    }
}


