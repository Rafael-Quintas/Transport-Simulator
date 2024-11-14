package pt.pa.test;

import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Vertex;

public class Example {
    public static void main(String[] args) {
        Graph<String, Integer> g = new GraphEdgeList<>();

        Vertex<String> a = g.insertVertex("A");
        Vertex<String> b = g.insertVertex("B");
    }
}
