package co.callcenter.model;

/**
 *
 * @author edwin
 */
public class Supervisor extends Empleado {
    
    public Supervisor(String nombre) {
        super(nombre);
    }
    
    @Override
    public int getPrioridad() {
        return 2;
    }

}
