package co.callcenter.model;

import java.util.Observable;
import java.util.Random;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author edwin
 */
public class Llamada extends Observable implements Runnable {

    private static final Logger log = LogManager.getLogger(Llamada.class);

    private final long id;
    private final int tiempo;
    private boolean terminada;
    private CallHandler callHandlerAttends;

    public Llamada(long id) {
        this.id = id;
        this.tiempo = new Random().nextInt(5) + 5;
        this.terminada = false;
    }

    @Override
    public void run() {
        int tiempoTranscurrido = 0;
        while (tiempoTranscurrido < tiempo) {
            tiempoTranscurrido++;
            notificar(TipoMesaje.HABLAR);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {

            }
        }

        terminada = true;
        notificar(TipoMesaje.COLGAR);
    }

    public boolean isTerminada() {
        return this.terminada;
    }

    public void atender(CallHandler callHandler) {
        this.callHandlerAttends = callHandler;
        new Thread(this, "id:" + this.id).start();
    }

    public void mesaje(String mesaje) {
        log.debug(mesaje);
    }

    @Override
    public String toString() {
        return "Llamada [id:" + this.id + "]";
    }

    public int getTiempo() {
        return tiempo;
    }

    public CallHandler getCallHandlerAttends() {
        return callHandlerAttends;
    }

    private void notificar(TipoMesaje tipo) {
        this.setChanged();
        notifyObservers(tipo);
    }

}
