
import co.callcenter.dispatcher.Dispatcher;
import co.callcenter.dispatcher.MemoryWaitingCallHandler;
import co.callcenter.model.Director;
import co.callcenter.model.Empleado;
import co.callcenter.model.Llamada;
import co.callcenter.model.Operador;
import co.callcenter.model.Supervisor;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author edwin
 */
public class Main {

    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String... args) throws Exception {

        Main.run(30);
    }

    private static List<Empleado> crearEmpleados() {
        List<Empleado> empleados = new LinkedList<>();

        empleados.addAll(IntStream.range(1, 2)
                .mapToObj(i -> new Director("Director " + i))
                .collect(toList()));

        empleados.addAll(IntStream.range(1, 4)
                .mapToObj(i -> new Supervisor("Supervisor " + i))
                .collect(toList()));

        empleados.addAll(IntStream.range(1, 11)
                .mapToObj(i -> new Operador("Operador " + i))
                .collect(toList()));

        return empleados;
    }

    private static void runParallel(int numeroLlamadas) throws Exception {
        List<Llamada> llamadas = IntStream.range(1, numeroLlamadas)
                .mapToObj(i -> new Llamada(i))
                .collect(toList());
        
        List<Empleado> empleados = Main.crearEmpleados();

        Dispatcher dispatcher = new Dispatcher(empleados, new MemoryWaitingCallHandler(), false);
        Future<?> future = Executors.newSingleThreadExecutor().submit(dispatcher);

        llamadas.stream().parallel().forEach(llamada -> dispatcher.dispatchCall(llamada));

        dispatcher.shotDownOnFinish();
        
        future.get();
        
        Main.mostrarEstadisticas(empleados);
    }
    
    private static void run(int numeroLlamadas) throws ExecutionException, InterruptedException {
        List<Empleado> empleados = Main.crearEmpleados();
        
        Dispatcher dispatcher = new Dispatcher(empleados, new MemoryWaitingCallHandler());

        IntStream.range(1, numeroLlamadas).mapToObj(i -> new Llamada(i))
                .forEach(llamada -> dispatcher.dispatchCall(llamada));

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(dispatcher).get();
        executor.shutdown();

        Main.mostrarEstadisticas(empleados);
    }
    
    private static void mostrarEstadisticas(List<Empleado> empleados) {

        log.info("-------------------------------------------------------------");
        log.info("-------------------- Estadisticas ---------------------------");

        empleados.stream().forEach(
                empleado -> log.info(empleado.getEstadisticas()));
    }

}
