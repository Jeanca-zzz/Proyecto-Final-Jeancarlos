package biblioteca;

/**
 * Lista enlazada simple para el catálogo de libros.
 * (No usa ArrayList/LinkedList de Java.)
 */
public class ListaLibros {
    private NodoLibro cabeza;

    public boolean estaVacia() {
        return cabeza == null;
    }

    public void agregar(Libro libro) {
        NodoLibro nuevo = new NodoLibro(libro);
        if (cabeza == null) {
            cabeza = nuevo;
            return;
        }
        NodoLibro aux = cabeza;
        while (aux.sig != null) aux = aux.sig;
        aux.sig = nuevo;
    }

    public Libro buscarPorIsbn(String isbn) {
        NodoLibro aux = cabeza;
        while (aux != null) {
            if (aux.dato.getIsbn().equalsIgnoreCase(isbn)) return aux.dato;
            aux = aux.sig;
        }
        return null;
    }

    public boolean eliminarPorIsbn(String isbn) {
        if (cabeza == null) return false;

        if (cabeza.dato.getIsbn().equalsIgnoreCase(isbn)) {
            cabeza = cabeza.sig;
            return true;
        }

        NodoLibro ant = cabeza;
        NodoLibro act = cabeza.sig;
        while (act != null) {
            if (act.dato.getIsbn().equalsIgnoreCase(isbn)) {
                ant.sig = act.sig;
                return true;
            }
            ant = act;
            act = act.sig;
        }
        return false;
    }

    public void ordenarPorTituloBurbuja() {
        if (cabeza == null || cabeza.sig == null) return;

        boolean cambio;
        do {
            cambio = false;
            NodoLibro a = cabeza;
            while (a.sig != null) {
                String t1 = a.dato.getTitulo().toLowerCase();
                String t2 = a.sig.dato.getTitulo().toLowerCase();
                if (t1.compareTo(t2) > 0) {
                    Libro tmp = a.dato;
                    a.dato = a.sig.dato;
                    a.sig.dato = tmp;
                    cambio = true;
                }
                a = a.sig;
            }
        } while (cambio);
    }

    public NodoLibro getCabeza() { return cabeza; }
    public void setCabeza(NodoLibro cabeza) { this.cabeza = cabeza; }
}
