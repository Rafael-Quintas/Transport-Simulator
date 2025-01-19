package pt.pa.patterns.memento;

/**
 * A interface {@code Originator} define o comportamento necessário para um objeto criar e restaurar
 * o seu estado utilizando o padrão de design Memento.
 *
 * O {@code Originator} é responsável por gerar um {@code Memento} que representa o seu estado atual
 * e por restaurar o estado a partir de um {@code Memento} previamente salvo.
 *
 * @author Rafael Quintas
 */

public interface Originator {
    /**
     * Cria um {@code Memento} que encapsula o estado atual do objeto.
     *
     * @return um {@link Memento} representando o estado atual.
     */
    Memento createMemento();

    /**
     * Restaura o estado do objeto com base num {@code Memento} previamente salvo.
     *
     * @param savedState o {@link Memento} que contém o estado a ser restaurado.
     */
    void setMemento(Memento savedState);
}
