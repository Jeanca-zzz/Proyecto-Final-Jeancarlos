package biblioteca;

/**
 * Representa un préstamo de un libro a un cliente con fecha (String).
 */
public class Prestamo {
    private String isbnLibro;
    private String cliente;
    private String fecha; // yyyy-MM-dd

    public Prestamo(String isbnLibro, String cliente, String fecha) {
        this.isbnLibro = isbnLibro;
        this.cliente = cliente;
        this.fecha = fecha;
    }

    public String getIsbnLibro() { return isbnLibro; }
    public void setIsbnLibro(String isbnLibro) { this.isbnLibro = isbnLibro; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    @Override
    public String toString() {
        return isbnLibro + " | " + cliente + " | " + fecha;
    }
}
