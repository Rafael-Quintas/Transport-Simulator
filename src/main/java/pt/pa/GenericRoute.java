package pt.pa;

import java.util.List;

/**
 * Representa uma rota genérica que conecta duas paragens ({@link Stop})
 * e contém uma lista de rotas ({@link Route}) específicas para diferentes tipos de transportes.
 *
 */
public class GenericRoute {
    private Stop stopStart;
    private Stop stopEnd;
    private List<Route> routes;

    /**
     * Construtor para inicializar uma instância de {@code GenericRoute}.
     *
     * @param start Paragem inicial da rota ({@link Stop}).
     * @param end Paragem final da rota ({@link Stop}).
     * @param routes Lista de rotas específicas ({@link Route}) associadas a esta rota genérica.
     */
    public GenericRoute(Stop start, Stop end, List<Route> routes) {
        this.stopStart = start;
        this.stopEnd = end;
        this.routes = routes;
    }

    /**
     * Obtém a paragem inicial da rota.
     *
     * @return a paragem inicial ({@link Stop}).
     */
    public Stop getStopStart() {
        return this.stopStart;
    }

    /**
     * Obtém a paragem final da rota.
     *
     * @return a paragem de destino ({@link Stop}).
     */
    public Stop getStopEnd() {
        return this.stopEnd;
    }

    /**
     * Obtém a lista de rotas específicas associadas a esta rota genérica.
     *
     * @return uma lista de objetos {@link Route}.
     */
    public List<Route> getRoutes() {
        return this.routes;
    }
}

