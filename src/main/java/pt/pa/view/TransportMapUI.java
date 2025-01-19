package pt.pa.view;

import pt.pa.TransportMapController;

/**
 * A interface {@code TransportMapUI} define o contrato para a UI no sistema de mapa de transporte.
 *
 * Fornece um método para configurar os triggers de interação com o Controller.
 *
 * @author Rafael Quintas, Rafael Pato, Guilherme Pereira
 */
public interface TransportMapUI {

    /**
     * Configura os triggers de interação para a UI, de forma a conectar os elementos da UI
     * às ações definidas no {@link TransportMapController}.
     *
     */
    void setTriggers();
}
