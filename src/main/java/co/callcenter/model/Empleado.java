package co.callcenter.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author edwin
 */
public abstract class Empleado implements Observer {

    private static final Logger log = LogManager.getLogger(Empleado.class);

    private final String nombre;
    private boolean disponible;
    private final List<Llamada> llamadasAtendidas;

    public Empleado(String nombre) {
        this.nombre = nombre;
        this.disponible = true;
        this.llamadasAtendidas = new ArrayList<>();
    }

    public boolean atenderLlamada(Llamada llamada) {
        log.info(this.toString() + " atendiendo " + llamada);

        if (!this.disponible) {
            return false;
        }
        this.disponible = false;

        llamada.addObserver(this);
        llamada.atender(this);

        return true;
    }

    public boolean isDisponible() {
        return this.disponible;
    }

    @Override
    public void update(Observable o, Object arg) {

        TipoMesaje tipoMesaje = (TipoMesaje) arg;
        Llamada llamada = (Llamada) o;

        switch (tipoMesaje) {
            case COLGAR:
                this.llamadasAtendidas.add(llamada);
                this.disponible = true;
                log.info(this.toString() + " disponible");
                break;
            case HABLAR:
                log.debug(arg);
                break;
            default:
        }
    }

    @Override
    public String toString() {
        return "[" + this.getClass().getSimpleName() + ": " + this.nombre + "]";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.nombre);
        hash = 29 * hash + (this.disponible ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Empleado other = (Empleado) obj;
        if (this.disponible != other.disponible) {
            return false;
        }
        return Objects.equals(this.nombre, other.nombre);
    }

    public String getEstadisticas() {
        String estadisticas = this.toString() +  " Llamadas atendidas: " + llamadasAtendidas.size();
        estadisticas += " Tiempos: [ ";
        
        estadisticas = llamadasAtendidas.stream()
                .map((llamadasAtendida) -> llamadasAtendida.getTiempo() + " ")
                .reduce(estadisticas, String::concat);
        
        estadisticas += "]";
        
        return estadisticas;
    }

    public String getNombre() {
        return nombre;
    }

    public abstract int getPrioridad();
}
