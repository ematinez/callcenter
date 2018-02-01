package co.callcenter.model;

/**
 *
 * @author edwin
 */
public interface CallHandler {

    boolean atenderLlamada(Llamada llamada);

    String getEstadisticas();

    String getNombre();

    int getPrioridad();

    boolean isDisponible();
    
}
