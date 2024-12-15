package pt.pa.patterns.observer;

/**
 * Implementação do padrão Observer
 *
 * @author Rafael Quintas, Rafael Pato, Guilherme Pereira
 */
public interface Observer {

    /**
     * Método chamado pelo {@code Subject} observado para notificar este observador
     * sobre uma mudança de estado ou evento.
     *
     * @param obj objeto associado à notificação.
     */
    void update(Object obj);
}
