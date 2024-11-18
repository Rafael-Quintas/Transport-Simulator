package pt.pa;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;

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

    public void addStop(Stop stop) {
        if (stop == null) { throw new IllegalArgumentException("O vértice a adicionar não pode ser nulo."); }
        graph.insertVertex(stop);
    }

    public void addRoute(String start, String end, Route route) {
        List<Vertex<Stop>> list = (List<Vertex<Stop>>) graph.vertices();
        Vertex<Stop> stopStart = getVertexByDesignation(start, list);
        Vertex<Stop> stopEnd = getVertexByDesignation(end, list);

        if (stopStart == null || stopEnd == null) {
            throw new IllegalArgumentException("Um ou ambos os vértices não existem no grafo.");
        }

        List<Route> routeList = getOrCreateRouteList(stopStart, stopEnd);
        graph.insertEdge(stopStart, stopEnd, routeList);
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

    private Vertex<Stop> getVertexByDesignation(String code,  List<Vertex<Stop>> list) {
        for (Vertex<Stop> v : list) {
            if (v.element().getStopCode().equals(code)) { return v; }
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
}
