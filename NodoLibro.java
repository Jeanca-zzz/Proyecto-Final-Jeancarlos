package biblioteca;

/**
 * Nodo para la lista enlazada de libros.
 */
public class NodoLibro {
    Libro dato;
    NodoLibro sig;

    public NodoLibro(Libro dato) {
        this.dato = dato;
        this.sig = null;
    }
}
