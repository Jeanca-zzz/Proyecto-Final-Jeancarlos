package biblioteca;

/**
 * Nodo para la cola de préstamos.
 */
public class NodoPrestamo {
    Prestamo dato;
    NodoPrestamo sig;

    public NodoPrestamo(Prestamo dato) {
        this.dato = dato;
        this.sig = null;
    }
}
