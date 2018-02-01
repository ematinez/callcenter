package co.callcenter.model;

/**
 *
 * @author edwin
 */
public class Operador extends Empleado {
    
    public Operador(String nombre) {
        super(nombre);
    }

    @Override
    public int getPrioridad() {
        return 3;
    }
    
}
