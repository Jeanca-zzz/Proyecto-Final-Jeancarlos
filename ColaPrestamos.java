package biblioteca;

public class ColaPrestamos {
    private NodoPrestamo frente;
    private NodoPrestamo fin;

    public boolean estaVacia() {
        return frente == null;
    }

    public void encolar(Prestamo p) {
        NodoPrestamo nuevo = new NodoPrestamo(p);
        if (estaVacia()) {
            frente = fin = nuevo;
        } else {
            fin.sig = nuevo;
            fin = nuevo;
        }
    }

    public Prestamo desencolar() {
        if (estaVacia()) return null;
        Prestamo p = frente.dato;
        frente = frente.sig;
        if (frente == null) fin = null;
        return p;
    }

    public Prestamo verFrente() {
        return estaVacia() ? null : frente.dato;
    }

    public NodoPrestamo getFrente() { return frente; }
    public void setFrente(NodoPrestamo frente) { this.frente = frente; }
    public NodoPrestamo getFin() { return fin; }
    public void setFin(NodoPrestamo fin) { this.fin = fin; }
}
