package pt.pa.patterns.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * A classe abstrata {@code Subject} fornece uma implementação base para o padrão Observer,
 * gerindo a lista de observadores e implementando os métodos da interface {@link Observable}.
 *
 * @author Rafael Quintas, Rafael Pato, Guilherme Pereira
 */
public abstract class Subject implements Observable {

    private List<Observer> observerList;

    /**
     * Construtor que inicializa a lista de observadores.
     */
    public Subject() {
        this.observerList = new ArrayList<>();
    }

    /**
     * Adiciona um ou mais observadores à lista de observadores.
     * Observadores duplicados não são adicionados novamente.
     *
     * @param observers observadores a serem adicionados.
     */
    @Override
    public void addObservers(Observer... observers) {
        for (Observer obs : observers) {
            if (!observerList.contains(obs)) {
                this.observerList.add(obs);
            }
        }
    }

    /**
     * Remove um observador da lista de observadores.
     *
     * Se o observador não estiver registado, nada será realizado.
     *
     * @param observer observador a ser removido.
     */
    @Override
    public void removeObservers(Observer observer) {
        this.observerList.remove(observer);
    }

    /**
     * Notifica todos os observadores registados sobre uma mudança de estado ou evento.
     *
     * @param obj o objeto associado à notificação.
     */
    @Override
    public void notifyObservers(Object obj) {
        for (Observer observer : this.observerList) {
            observer.update(obj);
        }
    }
}
