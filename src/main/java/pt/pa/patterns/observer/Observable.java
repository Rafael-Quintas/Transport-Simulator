package pt.pa.patterns.observer;

public interface Observable {
    public void addObservers(Observer... observers);
    public void removeObservers(Observer observer);
    public void notifyObservers(Object object);
}
