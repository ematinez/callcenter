package co.callcenter.dispatcher;

import co.callcenter.model.Llamada;

/**
 * 
 * @author edwin
 */
public interface WaitingCallHandler {
    
    void add(Llamada llamada);
    
    Llamada poll();
    
    boolean isEmpty();
    
}
