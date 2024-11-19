package pt.pa;

import com.brunomnsilva.smartgraph.graph.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class TransportMapTest {

    TransportMap transportMap;

    @BeforeEach
    void setUp() {
        transportMap = new TransportMap();
    }

    @Test
    void addStop() {
        // Como o construtor da classe TransportMap faz o loadToGraph(), o grafo irá inicializar com 31 vertices
        Graph<Stop, List<Route>> graph = transportMap.getGraph();
        assertEquals(31, graph.numVertices());
        // addStop com valores válidos
        transportMap.addStop("S1", "Stop 1", "40.0", "-8.0");
        assertEquals(32, graph.numVertices());

        transportMap.addStop("S2", "Stop 2", "45.0", "-10.0");
        assertEquals(33, graph.numVertices());

        // addStop com valores inválidos
        assertThrows(IllegalArgumentException.class, () -> transportMap.addStop("", "Stop 2", "40.0", "-8.0"));
        assertEquals(33, graph.numVertices());
        assertThrows(IllegalArgumentException.class, () -> transportMap.addStop("S3", "", "40.0", "-8.0"));
        assertEquals(33, graph.numVertices());
        assertThrows(IllegalArgumentException.class, () -> transportMap.addStop("S4", "Stop 4", "invalid", "-8.0"));
        assertEquals(33, graph.numVertices());

    }

    @Test
    void addRoute() {
        // Como o construtor da classe TransportMap faz o loadToGraph(), o grafo irá inicializar com 39 arestas
        Graph<Stop, List<Route>> graph = transportMap.getGraph();
        assertEquals(39, graph.numEdges());

        Vertex<Stop> v1 = transportMap.addStop("S1", "Stop 1", "40.0", "-8.0");
        Vertex<Stop> v2 = transportMap.addStop("S2", "Stop 2", "41.0", "-9.0");
        Vertex<Stop> v3 = transportMap.addStop("S3", "Stop 3", "45.0", "-14.0");
        assertNotNull(v1);
        assertNotNull(v2);
        assertNotNull(v3);

        // addRoute com valores válidas

        transportMap.addRoute(v1, v2, "BUS", "5.0", "10", "2.5");
        assertEquals(40, graph.numEdges());

        transportMap.addRoute(v2, v3, "TRAIN", "10.0", "25", "2.5");
        assertEquals(41, graph.numEdges());

        // addRoute com valores inválidas
        assertThrows(IllegalArgumentException.class, () -> transportMap.addRoute(v1, v2, "CAR", "invalid", "10", "2.5"));
        assertEquals(41, graph.numEdges());
        assertThrows(IllegalArgumentException.class, () -> transportMap.addRoute(v1, v2, "CAR", "5.0", "invalid", "2.5"));
        assertEquals(41, graph.numEdges());
    }

    @Test
    void removeStop() {
        // Como o construtor da classe TransportMap faz o loadToGraph(), o grafo irá inicializar com 31 vertices
        Graph<Stop, List<Route>> graph = transportMap.getGraph();
        assertEquals(31, graph.numVertices());

        // removeStop com vertices validos

        Vertex<Stop> v1 = transportMap.addStop("S1", "Stop 1", "40.0", "-8.0");
        assertNotNull(v1);
        assertEquals(32, graph.numVertices());

        Vertex<Stop> v2 = transportMap.addStop("S2", "Stop 2", "45.0", "-10.0");
        assertNotNull(v2);
        assertEquals(33, graph.numVertices());

        transportMap.removeStop(v1);
        assertEquals(32, graph.numVertices());

        transportMap.removeStop(v2);
        assertEquals(31, graph.numVertices());

        // removeStop com vertice invalido (nulo)
        assertThrows(InvalidVertexException.class, () -> transportMap.removeStop(null));
        assertEquals(31, graph.numVertices());
    }

    @Test
    void removeRoute() {
        // Como o construtor da classe TransportMap faz o loadToGraph(), o grafo irá inicializar com 39 arestas
        Graph<Stop, List<Route>> graph = transportMap.getGraph();
        assertEquals(39, graph.numEdges());

        // removeRoute com arestas validas

        Vertex<Stop> v1 = transportMap.addStop("S1", "Stop 1", "40.0", "-8.0");
        Vertex<Stop> v2 = transportMap.addStop("S2", "Stop 2", "41.0", "-9.0");
        Vertex<Stop> v3 = transportMap.addStop("S3", "Stop 3", "45.0", "-14.0");
        assertNotNull(v1);
        assertNotNull(v2);
        assertNotNull(v3);

        Edge<List<Route>, Stop> e1 = transportMap.addRoute(v1, v2, "BUS", "5.0", "10", "2.5");
        assertEquals(40, graph.numEdges());

        Edge<List<Route>, Stop> e2 = transportMap.addRoute(v2, v3, "TRAIN", "10.0", "25", "2.5");
        assertEquals(41, graph.numEdges());

        transportMap.removeRoute(e1);
        assertEquals(40, graph.numEdges());

        transportMap.removeRoute(e2);
        assertEquals(39, graph.numEdges());

        // removeRoute com aresta invalida (nulo)
        assertThrows(InvalidEdgeException.class, () -> transportMap.removeRoute(null));
        assertEquals(39, graph.numEdges());
    }
}