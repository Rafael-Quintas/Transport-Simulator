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
 * Representa um mapa de transporte baseado num grafo que modela as paragens.
 * ({@link Stop}) e as suas conexões ({@link Route}).
 */
public class TransportMap {
    private Graph<Stop, List<Route>> graph;

    /**
     * Construtor que inicializa o mapa de transporte carregando as paragens e rotas no grafo.
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
     * Carrega as paragens ({@link Stop}) e rotas genéricas ({@link GenericRoute}) para o grafo.
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
     * Adiciona uma nova paragem.
     *
     * Este método permite criar e adicionar uma nova paragem (vértice) ao grafo que representa a rede de transportes.
     * Antes de adicionar, valida os valores fornecidos para garantir que o código, nome, latitude e longitude são válidos.
     * Se os valores forem inválidos, é exibida uma mensagem de erro.
     *
     * @param stopCode o código único da paragem (não pode ser vazio).
     * @param stopName o nome da paragem (não pode ser vazio).
     * @param latitude a latitude da localização da paragem (deve ser um número válido).
     * @param longitude a longitude da localização da paragem (deve ser um número válido).
     */
    public void addStop(String stopCode, String stopName, String latitude, String longitude) {
        if (stopCode.isBlank() || stopName.isBlank()) {
            showError("Stop name/code must not be empty.");
            return;
        }

        if (!isNumeric(latitude) || !isNumeric(longitude)) {
            showError("Latitude and Longitude must be valid numbers.");
            return;
        }

        try {
            graph.insertVertex(new Stop(stopCode, stopName, Double.parseDouble(latitude), Double.parseDouble(longitude)));
        }
        catch (Exception e) {
            showError(e.getMessage());
        }
    }

    /**
     * Adiciona uma rota entre duas paragens.
     *
     * Este método permite criar ou atualizar uma rota entre as paragens especificadas (vértices `v1` e `v2`),
     * adicionando um novo meio de transporte com os parâmetros fornecidos.
     * Se os valores de distância, duração ou custo não forem numéricos, é exibida uma mensagem de erro.
     * Caso a rota seja adicionada com sucesso, ela será inserida no grafo.
     *
     * @param v1 o vértice de origem representando a paragem inicial.
     * @param v2 o vértice de destino representando a paragem final.
     * @param type o tipo de transporte (deve corresponder a um valor válido de {@link TransportType}).
     * @param distance a distância do percurso (em formato de string, será convertida para {@code double}).
     * @param duration a duração do percurso (em formato de string, será convertida para {@code int}).
     * @param cost o custo associado ao percurso (em formato de string, será convertido para {@code double}).
     */
    public void addRoute(Vertex<Stop> v1, Vertex<Stop> v2, String type, String distance, String duration, String cost) {
        if (!isNumeric(distance) || !isNumeric(cost) || !isNumeric(duration)) {
            showError("Distance, Duration and Cost must be valid numbers.");
            return;
        }
        try {
            List<Route> list = getOrCreateRouteList(v1, v2);
            TransportType transportType = TransportType.valueOf(type.toUpperCase());
            list.add(new Route(transportType, Double.parseDouble(distance), Integer.parseInt(duration), Double.parseDouble(cost)));
            graph.insertEdge(v1, v2, list);
        }
        catch (Exception e)
        {
            showError(e.getMessage());
        }
    }

    /**
     * Remove uma paragem.
     *
     * Este método tenta remover o vértice especificado (`vertex`) do grafo que representa a rede de transportes.
     * Caso ocorra um erro durante a remoção, uma mensagem de erro será exibida utilizando o método `showError`.
     *
     * @param vertex o vértice ({@link Vertex}) que representa a paragem a ser removida.
     */
    public void removeStop(Vertex<Stop> vertex) {
        try {
            graph.removeVertex(vertex);
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    /**
     * Remove uma rota.
     *
     * Este método tenta remover a aresta especificada (`edge`) do grafo que representa a rede de transportes.
     * Caso ocorra um erro durante a remoção, uma mensagem de erro será exibida utilizando o método `showError`.
     *
     * @param edge a aresta ({@link Edge}) que representa a rota a ser removida.
     */
    public void removeRoute(Edge<List<Route>, Stop> edge) {
        try {
            graph.removeEdge(edge);
        }
        catch (Exception e)
        {
            showError(e.getMessage());
        }
    }

    /**
     * Encontra uma paragem ({@link Stop}) pelo código de designação
     *
     * @param code Código da paragem.
     * @param stopList Lista de paragens disponíveis.
     * @return uma instância de ({@link Stop}) correspondente ao código, ou null se não for encontrada.
     */
    private static Stop getStopByDesignation(String code, List<Stop> stopList) {
        for (Stop s : stopList) {
            if (s.getStopCode().equals(code)) { return s; }
        }
        return null;
    }

    /**
     * Obtém a lista de rotas associadas à conexão entre duas paragens.
     *
     * Este método verifica todas as arestas incidentes no vértice de origem (`stopStart`)
     * e retorna a lista de rotas associadas à aresta que conecta o vértice de origem
     * ao vértice de destino (`stopEnd`). Se nenhuma conexão for encontrada, retorna {@code null}.
     *
     * @param stopStart o vértice representando a paragem de origem.
     * @param stopEnd o vértice representando a paragem de destino.
     * @return a lista de rotas associadas à conexão entre as paragens, ou {@code null} se não houver conexão.
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
     * Obtém a lista de rotas entre duas paragens ou cria uma nova lista vazia.
     *
     * Este método verifica se existe uma lista de rotas entre as paragens especificadas
     * (representadas pelos vértices fornecidos). Se nenhuma lista de rotas for encontrada,
     * retorna uma nova lista vazia.
     *
     * @param stopStart o vértice representando a paragem de origem.
     * @param stopEnd o vértice representando a paragem de destino.
     * @return a lista de rotas entre as paragens, ou uma nova lista vazia se não existir nenhuma.
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
     * incluindo valores de ponto flutuante. Retorna {@code false} se a string
     * for nula, vazia ou não puder ser convertida para um número.
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
