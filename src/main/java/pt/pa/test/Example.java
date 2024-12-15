package pt.pa.test;

import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.GraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Vertex;

/**
 * A classe {@code Example} demonstra o uso básico de um grafo genérico ({@link Graph})
 * utilizando a implementação {@link GraphEdgeList}.
 *
 * Este exemplo cria um grafo de strings e insere dois vértices, representados por "A" e "B".
 *
 * Esta classe é usada apenas para fins de demonstração e teste.
 *
 * @author Rafael Quintas, Rafael Pato, Guilherme Pereira
 */
public class Example {

    /**
     * Método principal que executa o exemplo de manipulação básica de grafos.
     *
     * @param args argumentos da linha de comando.
     */
    public static void main(String[] args) {
        // Criação de um grafo genérico com vértices de tipo String e arestas de tipo Integer.
        Graph<String, Integer> g = new GraphEdgeList<>();

        // Inserção de dois vértices no grafo.
        Vertex<String> a = g.insertVertex("A");
        Vertex<String> b = g.insertVertex("B");

        // Demonstrativo - Nenhuma operação adicional é realizada.
    }
}
