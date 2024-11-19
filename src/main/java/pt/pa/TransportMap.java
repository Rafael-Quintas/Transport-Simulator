package pt.pa;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um mapa de transporte baseado num grafo que modela as Stops.
 * ({@link Stop}) e as suas conexões ({@link Route}).
 */
public class TransportMap {
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

    ///////////////////////////////////////////////////////////////////////////////////
    // Error Reporting -> First method taken from PA´s laboratory.
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error has occurred. Description below:");
        alert.setContentText(message);

        alert.showAndWait();
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