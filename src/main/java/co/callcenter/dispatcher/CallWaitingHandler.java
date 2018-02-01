package co.callcenter.dispatcher;

import co.callcenter.model.Llamada;

/**
 * 
 * @author edwin
 */
public interface CallWaitingHandler {
    
    void add(Llamada llamada);
    
    Llamada poll();
    
    boolean isEmpty();
    
}
