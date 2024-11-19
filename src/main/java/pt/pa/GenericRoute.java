package pt.pa;

import java.util.List;

/**
 * Representa uma Route genérica que conecta duas Stops ({@link Stop})
 * e contém uma lista de Routes ({@link Route}) específicas para diferentes tipos de transportes.
 * @author Rafael Quintas, Rafael Pato, Guilherme Pereira
 */
public class GenericRoute {
    private String stopStart;
    private String stopEnd;
    private List<Route> routes;

    /**
     * Construtor para inicializar uma instância de {@code GenericRoute}.
     *
     * @param start Paragem inicial da Route ({@link Stop}).
     * @param end Paragem final da Route ({@link Stop}).
     * @param routes Lista de Routes específicas ({@link Route}) associadas a esta Route genérica.
     */
    public GenericRoute(String start, String end, List<Route> routes) {
        this.stopStart = start;
        this.stopEnd = end;
        this.routes = routes;
    }

    /**
     * Obtém a Stop inicial da Route.
     *
     * @return a Stop inicial ({@link Stop}).
     */
    public String getStopStart() {
        return this.stopStart;
    }

    /**
     * Obtém a Stop final da Route.
     *
     * @return a Stop de destino ({@link Stop}).
     */
    public String getStopEnd() {
        return this.stopEnd;
    }

    /**
     * Obtém a lista de Routes específicas associadas a esta Route genérica.
     *
     * @return uma lista de objetos {@link Route}.
     */
    public List<Route> getRoutes() {
        return this.routes;
    }
}

