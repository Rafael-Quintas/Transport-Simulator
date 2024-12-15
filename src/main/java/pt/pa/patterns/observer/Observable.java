package pt.pa.patterns.observer;

/**
 * A interface {@code Observable} define o contrato para objetos que podem ser observados
 * por instâncias de {@link Observer} no padrão Observer.
 *
 * @author Rafael Quintas, Rafael Pato, Guilherme Pereira
 */
public interface Observable {

    /**
     * Adiciona um ou mais observadores para receber notificações deste objeto observável.
     *
     * @param observers os observadores a serem adicionados.
     */
    public void addObservers(Observer... observers);

    /**
     * Remove um observador previamente registado, para que não receba mais notificações.
     *
     * @param observer observador a ser removido.
     */
    public void removeObservers(Observer observer);

    /**
     * Notifica todos os observadores registados sobre uma mudança de estado ou evento.
     *
     * @param object objeto associado à notificação
     */
    public void notifyObservers(Object object);
}
