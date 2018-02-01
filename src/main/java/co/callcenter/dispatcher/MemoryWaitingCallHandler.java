package co.callcenter.dispatcher;

import co.callcenter.model.Llamada;
import java.util.LinkedList;
import java.util.Queue;
import org.apache.log4j.LogManager;

/**
 *
 * @author edwin
 */
public class MemoryWaitingCallHandler implements WaitingCallHandler, Runnable {

    private static final org.apache.log4j.Logger log = LogManager.getLogger(MemoryWaitingCallHandler.class);
    
    private static final String MENSAJE_DE_ESPERA = " Gracias por esperar en la "
            + "línea, un operador lo atendera pronto";
    private final Queue<Llamada> llamadasEnEspera = new LinkedList<>();
    private boolean running;
    private long delayBetweenMessage = 3000L;

    public MemoryWaitingCallHandler() {

    }

    @Override
    public synchronized void add(Llamada llamada) {
        this.llamadasEnEspera.add(llamada);
        if (!this.running) {
            this.running = true;
            new Thread(this).start();
        }
    }

    @Override
    public synchronized Llamada poll() {

        if (!this.isEmpty()) {
            return llamadasEnEspera.poll();
        }

        return null;
    }

    @Override
    public boolean isEmpty() {
        return llamadasEnEspera.isEmpty();
    }

    @Override
    public void run() {
        try {
            while (!this.llamadasEnEspera.isEmpty()) {
                Thread.sleep(this.delayBetweenMessage);

                this.llamadasEnEspera.stream().sequential().forEach(
                        (llamada) -> llamada.mesaje(llamada + MENSAJE_DE_ESPERA)
                );
            }
        } catch (InterruptedException ex) {
            log.error(ex);
        }

        this.running = false;
    }

}
