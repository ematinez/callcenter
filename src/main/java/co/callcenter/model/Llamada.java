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

    private long id;
    private int tiempo;
    private boolean terminada;
    private Empleado empleadoAtendio;

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

    public void atender(Empleado empleado) {
        this.empleadoAtendio = empleado;
        new Thread(this, "id:" + this.id).start();
    }

    private void notificar(TipoMesaje tipo) {
        this.setChanged();
        notifyObservers(tipo);
    }

    @Override
    public String toString() {
        return "Llamada [id:" + this.id + "]";
    }

    public int getTiempo() {
        return tiempo;
    }

    public Empleado getEmpleadoAtendio() {
        return empleadoAtendio;
    }

}
