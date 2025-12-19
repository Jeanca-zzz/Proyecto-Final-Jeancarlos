package biblioteca;

import java.io.*;

/**
 * Persistencia en un TXT (biblioteca.txt) en la carpeta del proyecto.
 * Formato:
 *  LIBRO|isbn|titulo|autor|anio|disponible
 *  PRESTAMO|isbn|cliente|fecha
 */
public class TXT {
    private static final String RUTA = "biblioteca.txt";

    public static void guardar(ListaLibros lista, ColaPrestamos cola) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA))) {

            // Libros
            NodoLibro nl = lista.getCabeza();
            while (nl != null) {
                Libro l = nl.dato;
                bw.write("LIBRO|" + limpiar(l.getIsbn()) + "|" + limpiar(l.getTitulo()) + "|" + limpiar(l.getAutor())
                        + "|" + l.getAnio() + "|" + l.isDisponible());
                bw.newLine();
                nl = nl.sig;
            }

            // Préstamos
            NodoPrestamo np = cola.getFrente();
            while (np != null) {
                Prestamo p = np.dato;
                bw.write("PRESTAMO|" + limpiar(p.getIsbnLibro()) + "|" + limpiar(p.getCliente()) + "|" + limpiar(p.getFecha()));
                bw.newLine();
                np = np.sig;
            }
        }
    }

    public static void cargar(ListaLibros lista, ColaPrestamos cola) throws IOException {
        File f = new File(RUTA);
        if (!f.exists()) return;

        // reiniciar estructuras
        lista.setCabeza(null);
        cola.setFrente(null);
        cola.setFin(null);

        try (BufferedReader br = new BufferedReader(new FileReader(RUTA))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] parts = linea.split("\\|");
                if (parts.length == 0) continue;

                if (parts[0].equals("LIBRO") && parts.length == 6) {
                    String isbn = parts[1];
                    String titulo = parts[2];
                    String autor = parts[3];
                    int anio = Integer.parseInt(parts[4]);
                    boolean disp = Boolean.parseBoolean(parts[5]);
                    lista.agregar(new Libro(isbn, titulo, autor, anio, disp));
                } else if (parts[0].equals("PRESTAMO") && parts.length == 4) {
                    String isbn = parts[1];
                    String cliente = parts[2];
                    String fecha = parts[3];
                    cola.encolar(new Prestamo(isbn, cliente, fecha));
                }
            }
        }
    }

    private static String limpiar(String s) {
        if (s == null) return "";
        return s.replace("|", "/").trim();
    }
}
