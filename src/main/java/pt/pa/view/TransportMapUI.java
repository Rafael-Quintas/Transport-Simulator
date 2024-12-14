package pt.pa.view;

import pt.pa.TransportMapController;
import pt.pa.patterns.observer.Observer;

public interface TransportMapUI extends Observer {
    void setTriggers(TransportMapController controller);
}
