package pt.pa;

import com.brunomnsilva.smartgraph.graph.Vertex;
import java.util.*;

/**
 * A classe {@code Path} representa um Path entre Stops ({@link Stop}) no grafo,
 * incluindo a lista de vértices que compõem o Path e o custo total associado.
 *
 * Esta classe é usada para encapsular os resultados de cálculos de Paths no modelo de transporte.
 * Armazena o Path calculado e o custo total para facilitar a exibição e o processamento posterior.
 *
 * @author Rafael Quintas, Rafael Pato, Guilherme Pereira
 */
public class Path {
    private final List<Vertex<Stop>> path;
    private final double totalCost;

    /**
     * Construtor que cria uma instância de {@code Path}.
     *
     * @param path lista de vértices ({@link Vertex}) que compõem o Path.
     * @param totalCost custo total do Path.
     */
    public Path(List<Vertex<Stop>> path, double totalCost) {
        this.path = path;
        this.totalCost = totalCost;
    }

    /**
     * Retorna a lista de vértices que compõem o Path.
     *
     * @return lista de vértices ({@link Vertex}) representando o Path.
     */
    public List<Vertex<Stop>> getPath() {
        return path;
    }

    /**
     * Retorna o custo total do Path.
     *
     * @return custo total como um valor {@code double}.
     */
    public double getTotalCost() {
        return totalCost;
    }

    /**
     * Retorna uma representação em string do objeto {@code Path}.
     *
     * Inclui os vértices que compõem o Path e o custo total.
     *
     * @return String que representa o Path e o custo total.
     */
    @Override
    public String toString() {
        return "PathResult{" +
                "path=" + path +
                ", totalCost=" + totalCost +
                '}';
    }
}

