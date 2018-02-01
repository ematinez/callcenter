package co.callcenter.dispatcher;

import co.callcenter.model.Empleado;
import co.callcenter.model.Llamada;
import co.callcenter.model.TipoMesaje;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Implementación del dispatcher para el manejo de las llamadas de un callcenter.
 * Tiene como responsabilidad recibir las llamadas y asignarlas a los empleados
 * del callcenter, garantizando que las llamadas se asignen a los empleados
 * disponibles teniendo en cuenta la prioridad asignada a cada uno.
 * 
 * No existe limite en número de llamadas que puede manejar, si la cola de llamadas
 * tiene mas llamadas que el número de empleados el dispatcher delega al
 * waitingCallHandler la tarea de manejar la espera de las llamadas.
 * 
 * @author Edwin Martinez
 */
public class Dispatcher implements Observer, Runnable {

    private static final Logger log = LogManager.getLogger(Dispatcher.class);

    private final List<Llamada> llamadasEnAtencion = new LinkedList<>();
    private boolean shotDownOnFinish;
    private WaitingCallHandler waitingCallHandler;

    /**
     * Se utiliza un TreeSet para garantizar el orden y asegurar que primero se
     * guardaran los empleados con mas prioridad, en este caso: operadores luego
     * supervisores y finalmente directores.
     */
    private final Set<Empleado> empleados = new TreeSet<>(new Comparator<Empleado>() {
        @Override
        public int compare(Empleado e1, Empleado e2) {
            return e1.getPrioridad() < e2.getPrioridad() ? -1 : 1;
        }
    });

    /**
     * Crea un Dispatcher que procesa las llamadas en la lista llamadasEnEspera
     * y luego termina el procesa.
     *
     * @param empleados
     * @param waitingCallHandler
     */
    public Dispatcher(Collection<Empleado> empleados, WaitingCallHandler waitingCallHandler) {
        this(empleados, waitingCallHandler, true);
    }

    /**
     * Crea un Dispatcher que procesa las llamadas en la lista llamadasEnEspera
     * y luego continua activo esperando nuevas hasta que se llame el metodo
     * shotDownOnFinish.
     *
     * @param empleados
     * @param shotDownOnFinish
     */
    public Dispatcher(Collection<Empleado> empleados,
            WaitingCallHandler waitingCallHandler, boolean shotDownOnFinish) {

        this.waitingCallHandler = waitingCallHandler;
        this.shotDownOnFinish = shotDownOnFinish;
        this.empleados.addAll(empleados);

        this.empleados.stream().forEach(empleado -> log.debug(empleado));
    }

    /**
     * La llamada ingresada se agrega a la cola de llamadas en espera para luego
     * ser atendida por un empleado disponible.
     *
     * @param llamada
     */
    public synchronized void dispatchCall(Llamada llamada) {
        log.debug("Nueva llamada: " + llamada);

        llamada.addObserver(this);
        this.waitingCallHandler.add(llamada);
    }

    /**
     * Metodo que recibe las notificacon de las llamadas. si la llamada notifica
     * que ha terminado, se remueve de las llamadas de la cola de llamadas en
     * atención.
     *
     * @param llamada que notifica que ha terminado.
     * @param tipoMesaje tipo de mensaje que envia la llamada
     */
    @Override
    public synchronized void update(Observable llamada, Object tipoMesaje) {
        if (TipoMesaje.COLGAR.equals(tipoMesaje)) {
            Llamada llamadaAtendida = (Llamada) llamada;
            this.llamadasEnAtencion.remove(llamadaAtendida);

            log.debug("Llamada atendida: " + llamadaAtendida + " Tiempo: " + llamadaAtendida.getTiempo());
        }
    }

    /**
     *
     */
    @Override
    public void run() {

        if (log.isInfoEnabled() && shotDownOnFinish && this.waitingCallHandler.isEmpty()) {
            log.debug("para la opción shotDownOnFinish::true se debe llamar antes"
                    + " el metodo dispatchCall para llenar la cola de llamadas en espera"
                    + " y luego procesarlas");
        }

        while (this.seguir()) {
            synchronized (this.empleados) {

                /**
                 * Se busca un empleado disponible recorriendo la lista en el
                 * orden de prioridad que se estableció en el comparador del
                 * TreeSet
                 */
                Optional<Empleado> empleado = this.empleados
                        .stream().filter(l -> l.isDisponible()).findFirst();

                if (empleado.isPresent()) {
                    Llamada llamadaPendiente = this.getLlamadaPendiente();
                    if (llamadaPendiente != null) {
                        empleado.get().atenderLlamada(llamadaPendiente);
                        this.llamadasEnAtencion.add(llamadaPendiente);
                    }
                }
            }
        }
    }

    private synchronized Llamada getLlamadaPendiente() {
        if (!this.waitingCallHandler.isEmpty()) {
            return this.waitingCallHandler.poll();
        }

        return null;
    }

    private boolean seguir() {
        return (!this.waitingCallHandler.isEmpty()
                || !this.llamadasEnAtencion.isEmpty()
                || !shotDownOnFinish);
    }

    public void shotDownOnFinish() {
        this.shotDownOnFinish = true;
    }
}
