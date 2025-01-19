package pt.pa.patterns.memento;

public interface Originator {
    Memento createMemento();

    void setMemento(Memento savedState);
}
