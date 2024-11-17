package pt.pa;

import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;

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
     * Carrega as paragens ({@link Stop}) e rotas genéricas ({@link GenericRoute}) para o grafo.
     *
     * @return Grafo carregado contendo as paragens como vértices e as rotas como arestas.
     */
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

    /**
     * Posiciona os vértices do grafo no painel gráfico, utilizando as coordenadas definidas no arquivo CSV correspondente.
     *
     * @param smartGraph Painel gráfico ({@link SmartGraphPanel}) onde os vértices serão posicionados.
     */
    public void positionVertex(SmartGraphPanel<Stop, List<Route>> smartGraph) {
        DataImporter.loadCordinates(smartGraph, this.graph);
    }

    /**
     * Obtém o grafo que representa o mapa de transporte.
     *
     * @return Grafo do mapa de transporte ({@link Graph}).
     */
    public Graph<Stop, List<Route>> getGraph() {
        return this.graph;
    }
}
