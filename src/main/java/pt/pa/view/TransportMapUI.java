package pt.pa.view;

import pt.pa.TransportMapController;
import pt.pa.patterns.observer.Observer;

/**
 * A interface {@code TransportMapUI} define o contrato para a UI no sistema de mapa de transporte.
 *
 * A interface estende {@link Observer}, permitindo que a UI seja notificada de atualizações do modelo
 * no padrão Observer.
 * Fornece um método para configurar os triggers de interação com o Controller.
 *
 * @author Rafael Quintas, Rafael Pato, Guilherme Pereira
 */
public interface TransportMapUI extends Observer {

    /**
     * Configura os triggers de interação para a UI, conectando os elementos da UI
     * às ações definidas no {@link TransportMapController}.
     *
     * @param controller Controller responsável por geir as interações do utilizador.
     */
    void setTriggers(TransportMapController controller);
}
