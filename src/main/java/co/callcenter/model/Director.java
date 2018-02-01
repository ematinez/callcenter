package co.callcenter.model;

/**
 *
 * @author edwin
 */
public class Director extends Empleado {
    
    public Director(String nombre) {
        super(nombre);
    }
    
    @Override
    public int getPrioridad() {
        return 1;
    }
    
}
