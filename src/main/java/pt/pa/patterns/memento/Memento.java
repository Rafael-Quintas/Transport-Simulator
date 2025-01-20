package pt.pa.patterns.memento;

/**
 * A interface {@code Memento} representa o estado de um objeto a ser salvo ou restaurado,
 * segundo o padrão Memento, o objetivo principal desta interface é mascarar o ConcreteMemento.
 *
 * Esta interface é utilizada em conjunto com as classes {@code Originator} e {@code Caretaker}.
 * O {@code Originator} cria o Memento para salvar o estado atual, e o {@code Caretaker} armazena e
 * gere os Mementos para restaurar estados quando necessário.
 *
 * @author Rafael Quintas
 */

public interface Memento {
}
