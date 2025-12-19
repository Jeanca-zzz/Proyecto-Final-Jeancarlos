package biblioteca;


public class Libro {
    private String isbn;
    private String titulo;
    private String autor;
    private int anio;
    private boolean disponible;

    public Libro(String isbn, String titulo, String autor, int anio, boolean disponible) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.anio = anio;
        this.disponible = disponible;
    }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    @Override
    public String toString() {
        return isbn + " | " + titulo + " | " + autor + " | " + anio + " | " + (disponible ? "SI" : "NO");
    }
}
