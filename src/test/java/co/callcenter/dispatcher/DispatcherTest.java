package co.callcenter.dispatcher;

import co.callcenter.model.Director;
import co.callcenter.model.CallHandler;
import co.callcenter.model.Llamada;
import co.callcenter.model.Operador;
import co.callcenter.model.Supervisor;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author edwin
 */
public class DispatcherTest {

    /**
     * executor para ejecutar el dispatcher en un hilo diferente al principal.
     */
    private ExecutorService executor;
    private final List<CallHandler> empleados = this.getCallHandlers();

    @Before
    public void setUp() {
        executor = Executors.newSingleThreadExecutor();
    }

    @After
    public void tearDown() {
        executor.shutdown();
    }

    /**
     * Evalua que el diapatcher atienda 10 llamadas en de forma concurrente.
     * En este caso las 10 llamadas se asignan antes de inciar el despatcher
     * de tal forma que al iniciar el despatcher debe atenderlas en simultaneo,
     * y finalizar el proceso.
     * @throws Exception 
     */
    @Test
    public void test10Llamadas() throws Exception {
        List<Llamada> llamadas = this.getLlamadas(10);

        Dispatcher dispatcher = new Dispatcher(this.empleados, new MemoryCallWaitingHandler());
        llamadas.stream().forEach(llamada -> dispatcher.dispatchCall(llamada));

        executor.submit(dispatcher).get();

        llamadas.stream().forEach(
                llamada ->  assertNotNull(
                        "Llamada no atendida", llamada.getCallHandlerAttends()));
        
    }
    
    /**
     * Evalua que el diapatcher atienda 10 llamadas en forma concurrente.
     * En este caso se inicia en dispatcher sin llamadas en espera, una vez 
     * está corriendo se ingresan las llamadas en forma paralela,
     * finalmente se le pide al dispatcher que finalice el 
     * proceso despues de procesar todas las llamadas.
     * 
     * @throws Exception 
     */
    @Test
    public void test10LlamadasParallel() throws Exception {
        List<Llamada> llamadas = this.getLlamadas(10);

        Dispatcher dispatcher = new Dispatcher(this.empleados, new MemoryCallWaitingHandler(), false);
        Future<?> future = executor.submit(dispatcher);
        
        llamadas.stream().parallel().forEach(llamada -> dispatcher.dispatchCall(llamada));

        dispatcher.shotDownOnFinish();
        
        future.get();
        
        llamadas.stream().forEach(
                llamada ->  assertNotNull(
                        "Llamada no atendida", llamada.getCallHandlerAttends()));
        
    }
    
    /**
     * Evalua que el diapatcher atienda mas de 10 llamadas de forma concurrente.
     * En este caso se inicia en dispatcher sin llamadas es espera, una vez 
     * está corriendo se ingresan 100 llamadas en forma paralela, dispatcher
     * guarda las llamadas entrantes en la cola de llamadas en espera y lass va 
     * asignando a los empleados que se encuenten disponibles.
     * 
     * @throws Exception 
     */
    @Test
    public void testMasDe10LlamadasParallel() throws Exception {
        List<Llamada> llamadas = this.getLlamadas(50);

        Dispatcher dispatcher = new Dispatcher(this.empleados, new MemoryCallWaitingHandler(), false);
        Future<?> future = executor.submit(dispatcher);
        
        llamadas.stream().parallel().forEach(llamada -> dispatcher.dispatchCall(llamada));

        dispatcher.shotDownOnFinish();
        
        future.get();
        
        llamadas.stream().forEach(
                llamada ->  assertNotNull(
                        "Llamada no atendida", llamada.getCallHandlerAttends()));
        
    }

    /**
     * Test de atención de una llamada asegurando que la llamada es atendida
     * por el operador y no por otro rol sin importar el orden de la 
     * lista de empleados.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testPrioridadOperadores() throws Exception {

        Llamada llamada = new Llamada(1L);
        CallHandler operador = new Operador("Juan");
        
        List<CallHandler> emplados = Arrays.asList(
                new Supervisor("Alejandro"),
                new Supervisor("Pedro"),
                new Director("Luisa"),
                operador, 
                new Director("Fabiola"));
        
        Dispatcher dispatcher = new Dispatcher(emplados, new MemoryCallWaitingHandler());
        dispatcher.dispatchCall(llamada);
        
        executor.submit(dispatcher).get();

        assertEquals("Todas las llamadas se deben atender", 
                    operador, llamada.getCallHandlerAttends());
    }

    private List<CallHandler> getCallHandlers() {
        return Arrays.asList(
                new Supervisor("Alejandro"),
                new Supervisor("Pedro"),
                new Director("Fabiola"),
                new Operador("Luisa"),
                new Operador("Tomas"),
                new Operador("Clara"),
                new Operador("Sofia"),
                new Operador("Nataly"));
    }

    private List<Llamada> getLlamadas(final int numeroLlamadas) {

        return IntStream.range(1, numeroLlamadas + 1)
                .mapToObj(i -> new Llamada(i))
                .collect(toList());
    }

}
