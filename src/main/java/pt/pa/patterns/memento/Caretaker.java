package pt.pa.patterns.memento;

import java.util.Stack;

/**
 * A classe {@code Caretaker} é responsável por gerir os estados salvos
 * de um objeto do tipo {@link Originator}, utilizando o padrão Memento.
 * Permite salvar e restaurar estados.
 *
 * @author Rafael Quintas, Rafael Pato, Guilherme Pereira
 */

public class Caretaker {
    private Stack<Memento> mementos;
    private Originator originator;

    /**
     * Construtor para inicializar um {@code Caretaker}.
     *
     * @param originator O objeto do tipo {@link Originator} que terá os seus estados salvos e restaurados.
     */
    public Caretaker(Originator originator) {
        this.mementos = new Stack<>();
        this.originator = originator;
    }

    /**
     * Salva o estado atual do objeto {@link Originator}.
     *
     * Adiciona um novo {@link Memento} à pilha de estados guardados.
     */
    public void saveState() {
        mementos.push(originator.createMemento());
    }

    /**
     * Restaura o estado mais recente do objeto {@link Originator}.
     *
     * Remove o {@link Memento} do topo da pilha e utiliza-o para
     * reverter o {@link Originator} ao estado guardado.
     *
     * @throws IllegalArgumentException se não existirem estados salvos para restaurar.
     */
    public void restoreState() {
        if (mementos.isEmpty()) {
            throw new IllegalArgumentException("There is nothing to restore");
        }
        originator.setMemento(mementos.pop());
    }
}
