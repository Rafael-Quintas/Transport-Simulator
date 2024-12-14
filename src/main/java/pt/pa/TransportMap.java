package pt.pa;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import pt.pa.patterns.observer.Subject;
import pt.pa.patterns.strategy.*;

import java.util.*;

/**
 * Representa um mapa de transporte baseado num grafo que modela as Stops.
 * ({@link Stop}) e as suas conexões ({@link Route}).
 *  @author Rafael Quintas, Rafael Pato, Guilherme Pereira
 */
public class TransportMap extends Subject {
    private Graph<Stop, List<Route>> graph;

    /**
     * Construtor que inicializa o mapa de transporte carregando os Stops e Routes no grafo.
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
     *
     * Este método permite criar e adicionar um novo Stop (vértice) ao grafo que representa a rede de transportes.
     * Antes de adicionar, valida os valores fornecidos para garantir que o código, nome, latitude e longitude são válidos.
     * Se os valores forem inválidos, é lançada uma exceção com uma mensagem de erro.
     *
     * @param stopCode o código único da Stop (não pode ser vazio).
     * @param stopName o nome da Stop (não pode ser vazio).
     * @param latitude a latitude da localização da Stop (deve ser um número válido).
     * @param longitude a longitude da localização da Stop (deve ser um número válido).
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
     *
     * Este método permite criar ou atualizar uma Route entre os Stops especificadas (vértices `v1` e `v2`),
     * adicionando um novo meio de transporte com os parâmetros fornecidos.
     * Se os valores de distância, duração ou custo não forem numéricos, é lançada uma exceção com mensagem de erro.
     * Caso a Route seja adicionada com sucesso, ela será inserida no grafo.
     *
     * @param v1 o vértice de origem representando a Stop inicial.
     * @param v2 o vértice de destino representando a Stop final.
     * @param type o tipo de transporte (deve corresponder a um valor válido de {@link TransportType}).
     * @param distance a distância do percurso (em formato de string, será convertida para {@code double}).
     * @param duration a duração do percurso (em formato de string, será convertida para {@code int}).
     * @param cost o custo associado ao percurso (em formato de string, será convertido para {@code double}).
     * @return a aresta correspondente à nova Route adicionada.
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
     *
     * Este método tenta remover o vértice (Stop) do grafo que representa a rede de transportes.
     *
     * @param vertex o vértice ({@link Vertex}) que representa a Stop a ser removida.
     */
    public void removeStop(Vertex<Stop> vertex) {
        graph.removeVertex(vertex);
    }

    /**
     * Remove uma Route.
     *
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
     * @param code Código da Stop.
     * @param stopList Lista de Stops disponíveis.
     * @return uma instância de ({@link Stop}) correspondente ao código, ou null se não for encontrada.
     */
    private static Stop getStopByDesignation(String code, List<Stop> stopList) {
        for (Stop s : stopList) {
            if (s.getStopCode().equals(code)) { return s; }
        }
        return null;
    }

    /**
     * Obtém a lista de Routes associadas à conexão entre duas Stops.
     *
     * Este método verifica todas as arestas incidentes no vértice de origem (`stopStart`)
     * e retorna a lista de Routes associadas à aresta que conecta o vértice de origem
     * ao vértice de destino (`stopEnd`). Se nenhuma conexão for encontrada, retorna {@code null}.
     *
     * @param stopStart o vértice representando a Stop de origem.
     * @param stopEnd o vértice representando a Stop de destino.
     * @return a lista de Routes associadas à conexão entre as Stops, ou {@code null} se não houver conexão.
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
     *
     * Este método verifica se existe uma lista de Routes entre as Stops especificadas
     * (representadas pelos vértices fornecidos). Se nenhuma lista de Routes for encontrada,
     * retorna uma nova lista vazia.
     *
     * @param stopStart o vértice representando a Stop de origem.
     * @param stopEnd o vértice representando a Stop de destino.
     * @return a lista de Routes entre as Stops, ou uma nova lista vazia se não existir nenhuma.
     */
    private List<Route> getOrCreateRouteList(Vertex<Stop> stopStart, Vertex<Stop> stopEnd) {
        List<Route> routeList = getEdgeByConnection(stopStart, stopEnd);
        if (routeList == null) {
            return new ArrayList<>();
        }
        return routeList;
    }

    public int numberOfNonIsolatedStops() {
        return graph.numVertices() - numberOfIsolatedStops();
    }

    public int numberOfIsolatedStops() {
        List<Vertex<Stop>> vertexList = (List<Vertex<Stop>>)graph.vertices();
        int isolatedCounter = 0;

        for (Vertex<Stop> v : vertexList) {
            if (graph.incidentEdges(v).isEmpty()) {
                isolatedCounter++;
            }
        }

        return isolatedCounter;
    }

    public int numberOfPossibleRoutes() {
        List<Edge<List<Route>, Stop>> edgeList = (List<Edge<List<Route>, Stop>>)graph.edges();
        int total = 0;

        for (Edge<List<Route>, Stop> e : edgeList) {
            total += e.element().size();
        }

        return total;
    }

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

    public List<Map.Entry<Vertex<Stop>, Integer>> topFiveCentrality() {
        LinkedHashMap<Vertex<Stop>, Integer> centralityMap = centrality();
        List<Map.Entry<Vertex<Stop>, Integer>> entryList = new ArrayList<>(centralityMap.entrySet());

        return entryList.stream().limit(5).toList();
    }

    public List<Stop> getStopsNRoutesAway(Vertex<Stop> start, int N) {
        if (start == null || N < 0) {
            throw new IllegalArgumentException("Stop cannot be null, N cannot be negative.");
        }

        Set<Stop> result = new LinkedHashSet<>();
        Queue<Vertex<Stop>> queue = new LinkedList<>();
        Map<Vertex<Stop>, Integer> distances = new HashMap<>();

        queue.add(start);
        distances.put(start, 0);
        result.add(start.element());

        while (!queue.isEmpty()) {
            Vertex<Stop> current = queue.poll();
            int currentDistance = distances.get(current);

            if (currentDistance < N) {
                for (Edge<List<Route>, Stop> edge : graph.incidentEdges(current)) {
                    Vertex<Stop> neighbor = graph.opposite(current, edge);

                    if (!distances.containsKey(neighbor)) {
                        distances.put(neighbor, currentDistance + 1);
                        queue.add(neighbor);
                        result.add(neighbor.element());
                    }
                }
            }
        }

        return new ArrayList<>(result);
    }

    public Path leastCostBetweenStops(String origin, String destination, String criteria, List<TransportType> transports) {
        Map<Vertex<Stop>, Double> costs = new HashMap<>();
        Map<Vertex<Stop>, Vertex<Stop>> predecessors = new HashMap<>();
        List<Vertex<Stop>> vertices = new ArrayList<>(graph.vertices());

        // Estratégia de cálculo de peso
        WeightCalculationStrategy strategy = createStrategy(criteria);

        Vertex<Stop> originVertex = getVertexByDesignation(origin, vertices);
        Vertex<Stop> destinationVertex = getVertexByDesignation(destination, vertices);

        if (originVertex == null || destinationVertex == null) {
            throw new IllegalArgumentException("Invalid origin or destination stop.");
        }

        // Inicializar custos e predecessores
        for (Vertex<Stop> vertex : vertices) {
            costs.put(vertex, Double.POSITIVE_INFINITY);
            predecessors.put(vertex, null);
        }
        costs.put(originVertex, 0.0);

        // Relaxar arestas (Bellman-Ford)
        for (int i = 0; i < vertices.size() - 1; i++) {
            for (Vertex<Stop> vertex : vertices) {
                for (Edge<List<Route>, Stop> edge : graph.incidentEdges(vertex)) {
                    relaxEdge(vertex, edge, costs, predecessors, transports, strategy);
                }
            }
        }

        // Verificar ciclos negativos
        for (Vertex<Stop> vertex : vertices) {
            for (Edge<List<Route>, Stop> edge : graph.incidentEdges(vertex)) {
                if (hasNegativeCycle(vertex, edge, costs, transports, strategy)) {
                    throw new IllegalStateException("The graph contains a negative weight cycle.");
                }
            }
        }

        // Construir caminho
        List<Vertex<Stop>> path = makeStopPath(destinationVertex, originVertex, predecessors);

        // Calcular custo total do caminho
        double totalCost = calculateTotalCost(path, transports, strategy);

        return new Path(path, totalCost);
    }

    private void relaxEdge(
            Vertex<Stop> u,
            Edge<List<Route>, Stop> edge,
            Map<Vertex<Stop>, Double> costs,
            Map<Vertex<Stop>, Vertex<Stop>> predecessors,
            List<TransportType> transports,
            WeightCalculationStrategy strategy
    ) {
        Vertex<Stop> v = graph.opposite(u, edge);

        // Inicializar o menor peso como infinito
        double minWeight = Double.POSITIVE_INFINITY;

        // Percorrer todas as rotas na aresta
        for (Route route : edge.element()) {
            // Ignorar tipos de transporte não selecionados
            if (!transports.contains(route.getTransportType())) {
                continue;
            }

            // Calcular o peso da rota
            double weight = strategy.calculateWeight(route);

            // Atualizar o menor peso se necessário
            if (weight < minWeight) {
                minWeight = weight;
            }
        }

        // Verificar se o menor peso foi encontrado
        if (minWeight == Double.POSITIVE_INFINITY) {
            return; // Nenhuma rota válida foi encontrada
        }

        // Verificar se o caminho via 'u' oferece menor custo acumulado para 'v'
        double newCost = costs.get(u) + minWeight;
        if (newCost < costs.get(v)) {
            costs.put(v, newCost); // Atualizar custo acumulado
            predecessors.put(v, u); // Atualizar predecessor
        }
    }

    private boolean hasNegativeCycle(
            Vertex<Stop> u,
            Edge<List<Route>, Stop> edge,
            Map<Vertex<Stop>, Double> costs,
            List<TransportType> transports,
            WeightCalculationStrategy strategy
    ) {
        Vertex<Stop> v = graph.opposite(u, edge);

        for (Route route : edge.element()) {
            if (!transports.contains(route.getTransportType())) {
                continue;
            }

            double weight = strategy.calculateWeight(route);

            if (costs.get(u) + weight < costs.get(v)) {
                return true;
            }
        }
        return false;
    }

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
                        if (transports.contains(route.getTransportType())) {
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

        // If the strategy is SustainabilityStrategy, subtract the offset for each edge
        if (strategy instanceof SustainabilityStrategy) {
            totalCost -= SustainabilityStrategy.OFFSET * (path.size() - 1);
        }

        return Math.round(totalCost * 100.0) / 100.0;
    }

    public WeightCalculationStrategy createStrategy(String criteria){
        return switch (criteria) {
            case "distance" -> new DistanceStrategy();
            case "duration" -> new DurationStrategy();
            case "sustainability" -> new SustainabilityStrategy();
            default -> null;
        };
    }

    // Obter um vértice pelo nome da paragem
    private Vertex<Stop> getVertexByDesignation(String stopName, Collection<Vertex<Stop>> vertexList) {
        for (Vertex<Stop> v : vertexList) {
            if (v.element().getStopName().equals(stopName)) {
                return v;
            }
        }
        return null;
    }

    public boolean isAdjacent(Vertex<Stop> current, Vertex<Stop> next) {
        return graph.incidentEdges(current).stream()
                .anyMatch(edge -> graph.opposite(current, edge).equals(next));
    }

    /**
     * Verifica se uma string é numérica.
     *
     * Este método verifica se a string fornecida é um número válido,
     *Retorna {@code false} se a string for nula, vazia ou não puder ser convertida para um número.
     *
     * @param string a string a ser verificada.
     * @return {@code true} se a string for numérica; {@code false} caso contrário.
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
}






