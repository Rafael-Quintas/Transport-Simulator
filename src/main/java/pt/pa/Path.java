package pt.pa;

import com.brunomnsilva.smartgraph.graph.Vertex;
import java.util.*;

public class Path {
    private final List<Vertex<Stop>> path;
    private final double totalCost;

    public Path(List<Vertex<Stop>> path, double totalCost) {
        this.path = path;
        this.totalCost = totalCost;
    }

    public List<Vertex<Stop>> getPath() {
        return path;
    }

    public double getTotalCost() {
        return totalCost;
    }

    @Override
    public String toString() {
        return "PathResult{" +
                "path=" + path +
                ", totalCost=" + totalCost +
                '}';
    }
}

