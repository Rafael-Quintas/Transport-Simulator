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

    public void removeStop(Vertex<Stop> vertex) {
        try {
            graph.removeVertex(vertex);
        }
        catch (Exception e)
        {
            showError(e.getMessage());
        }
    }

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

    private List<Route> getEdgeByConnection(Vertex<Stop> stopStart, Vertex<Stop> stopEnd) {
        for (Edge<List<Route>, Stop> edge : graph.incidentEdges(stopStart)) {
            if (graph.opposite(stopStart, edge).equals(stopEnd)) {
                return edge.element();
            }
        }
        return null;
    }

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
