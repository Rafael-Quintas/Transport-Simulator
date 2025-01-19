package pt.pa.patterns.memento;

import java.util.Stack;

public class Caretaker {
    private Stack<Memento> mementos;
    private Originator originator;

    public Caretaker(Originator originator) {
        this.mementos = new Stack<>();
        this.originator = originator;
    }

    public void saveState() {
        mementos.push(originator.createMemento());
    }

    public void restoreState() {
        if (mementos.isEmpty()) {
            throw new IllegalArgumentException("There is nothing to restore");
        }
        originator.setMemento(mementos.pop());
    }
}
