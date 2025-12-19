package biblioteca;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.time.LocalDate;

/**
 * Interfaz JFrame (sin .form) para NetBeans.
 * Cumple: agregar/mostrar/buscar/eliminar, préstamo/devolución (cola),
 * ordenar (burbuja), guardar/cargar TXT.
 */
public class FrmBiblioteca extends JFrame {

    private final ListaLibros lista = new ListaLibros();
    private final ColaPrestamos cola = new ColaPrestamos();

    // Campos libro
    private JTextField txtIsbn, txtTitulo, txtAutor, txtAnio;

    // Préstamo
    private JTextField txtCliente;

    // Tabla
    private JTable tabla;
    private DefaultTableModel modelo;

    // Área préstamos
    private JTextArea txtPrestamos;

    private JLabel lblEstado;

    public FrmBiblioteca() {
        setTitle("Biblioteca Personal (EDD) - Lista Enlazada + Cola");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(980, 560);
        setLocationRelativeTo(null);

        construirUI();
        cargarAlIniciar();
        refrescarTabla();
        refrescarPrestamos();

        // Guardar al cerrar
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int op = JOptionPane.showConfirmDialog(
                        FrmBiblioteca.this,
                        "¿Deseás guardar antes de salir?",
                        "Salir",
                        JOptionPane.YES_NO_CANCEL_OPTION
                );
                if (op == JOptionPane.CANCEL_OPTION) return;

                if (op == JOptionPane.YES_OPTION) {
                    if (!guardar()) return; // si falla, no cerrar
                }
                dispose();
                System.exit(0);
            }
        });
    }

    private void construirUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(root);

        // Panel superior: formulario
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(BorderFactory.createTitledBorder("Datos del Libro"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        txtIsbn = new JTextField(14);
        txtTitulo = new JTextField(20);
        txtAutor = new JTextField(18);
        txtAnio = new JTextField(6);
        txtCliente = new JTextField(18);

        int y = 0;

        c.gridx = 0; c.gridy = y; pnlForm.add(new JLabel("ISBN:"), c);
        c.gridx = 1; c.gridy = y; pnlForm.add(txtIsbn, c);

        c.gridx = 2; c.gridy = y; pnlForm.add(new JLabel("Título:"), c);
        c.gridx = 3; c.gridy = y; pnlForm.add(txtTitulo, c);

        y++;
        c.gridx = 0; c.gridy = y; pnlForm.add(new JLabel("Autor:"), c);
        c.gridx = 1; c.gridy = y; pnlForm.add(txtAutor, c);

        c.gridx = 2; c.gridy = y; pnlForm.add(new JLabel("Año:"), c);
        c.gridx = 3; c.gridy = y; pnlForm.add(txtAnio, c);

        y++;
        c.gridx = 0; c.gridy = y; pnlForm.add(new JLabel("Cliente (para préstamo):"), c);
        c.gridx = 1; c.gridy = y; c.gridwidth = 3; pnlForm.add(txtCliente, c);
        c.gridwidth = 1;

        root.add(pnlForm, BorderLayout.NORTH);

        // Centro: tabla + préstamos
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.72);

        // Tabla
        modelo = new DefaultTableModel(new Object[]{"ISBN", "Título", "Autor", "Año", "Disponible"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabla = new JTable(modelo);
        JScrollPane spTabla = new JScrollPane(tabla);
        spTabla.setBorder(BorderFactory.createTitledBorder("Catálogo"));

        split.setLeftComponent(spTabla);

        // Préstamos (cola)
        txtPrestamos = new JTextArea();
        txtPrestamos.setEditable(false);
        txtPrestamos.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane spPrestamos = new JScrollPane(txtPrestamos);
        spPrestamos.setBorder(BorderFactory.createTitledBorder("Préstamos en cola (FIFO)"));

        split.setRightComponent(spPrestamos);

        root.add(split, BorderLayout.CENTER);

        // Panel inferior: botones + estado
        JPanel pnlBottom = new JPanel(new BorderLayout(10, 10));

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));

        JButton btnAgregar = new JButton("Agregar");
        JButton btnMostrar = new JButton("Mostrar");
        JButton btnBuscar = new JButton("Buscar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnOrdenar = new JButton("Ordenar Título (Burbuja)");
        JButton btnPrestar = new JButton("Prestar (Encolar)");
        JButton btnDevolver = new JButton("Devolver (Desencolar)");
        JButton btnGuardar = new JButton("Guardar TXT");
        JButton btnCargar = new JButton("Cargar TXT");

        pnlBtns.add(btnAgregar);
        pnlBtns.add(btnMostrar);
        pnlBtns.add(btnBuscar);
        pnlBtns.add(btnEliminar);
        pnlBtns.add(btnOrdenar);
        pnlBtns.add(btnPrestar);
        pnlBtns.add(btnDevolver);
        pnlBtns.add(btnGuardar);
        pnlBtns.add(btnCargar);

        pnlBottom.add(pnlBtns, BorderLayout.CENTER);

        lblEstado = new JLabel("Listo.");
        pnlBottom.add(lblEstado, BorderLayout.SOUTH);

        root.add(pnlBottom, BorderLayout.SOUTH);

        // Acciones
        btnAgregar.addActionListener(e -> agregarLibro());
        btnMostrar.addActionListener(e -> { refrescarTabla(); setEstado("Mostrando catálogo."); });
        btnBuscar.addActionListener(e -> buscarLibro());
        btnEliminar.addActionListener(e -> eliminarLibro());
        btnOrdenar.addActionListener(e -> ordenarLibros());
        btnPrestar.addActionListener(e -> prestarLibro());
        btnDevolver.addActionListener(e -> devolverLibro());
        btnGuardar.addActionListener(e -> guardar());
        btnCargar.addActionListener(e -> { cargarAlIniciar(); refrescarTabla(); refrescarPrestamos(); });
    }

    private void setEstado(String msg) {
        lblEstado.setText(msg);
    }

    private void agregarLibro() {
        String isbn = txtIsbn.getText().trim();
        String titulo = txtTitulo.getText().trim();
        String autor = txtAutor.getText().trim();
        String anioStr = txtAnio.getText().trim();

        if (isbn.isEmpty() || titulo.isEmpty() || autor.isEmpty() || anioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa ISBN, Título, Autor y Año.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int anio;
        try {
            anio = Integer.parseInt(anioStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Año inválido (solo números).", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (lista.buscarPorIsbn(isbn) != null) {
            JOptionPane.showMessageDialog(this, "Ya existe un libro con ese ISBN.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        lista.agregar(new Libro(isbn, titulo, autor, anio, true));
        limpiarCamposLibro();
        refrescarTabla();
        setEstado("Libro agregado: " + isbn);
    }

    private void buscarLibro() {
        String isbn = JOptionPane.showInputDialog(this, "Ingrese ISBN a buscar:");
        if (isbn == null) return; // cancel
        isbn = isbn.trim();
        if (isbn.isEmpty()) return;

        Libro l = lista.buscarPorIsbn(isbn);
        if (l == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el libro con ISBN: " + isbn, "Buscar", JOptionPane.INFORMATION_MESSAGE);
            setEstado("No encontrado: " + isbn);
        } else {
            JOptionPane.showMessageDialog(this, "Encontrado:\n" + l.toString(), "Buscar", JOptionPane.INFORMATION_MESSAGE);
            setEstado("Encontrado: " + isbn);
        }
    }

    private void eliminarLibro() {
        String isbn = JOptionPane.showInputDialog(this, "Ingrese ISBN a eliminar:");
        if (isbn == null) return;
        isbn = isbn.trim();
        if (isbn.isEmpty()) return;

        Libro l = lista.buscarPorIsbn(isbn);
        if (l == null) {
            JOptionPane.showMessageDialog(this, "No existe ese ISBN.", "Eliminar", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int op = JOptionPane.showConfirmDialog(this, "¿Seguro que deseas eliminar el libro?\n" + l.toString(),
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (op != JOptionPane.YES_OPTION) return;

        boolean ok = lista.eliminarPorIsbn(isbn);
        refrescarTabla();
        setEstado(ok ? "Eliminado: " + isbn : "No se pudo eliminar: " + isbn);
    }

    private void ordenarLibros() {
        lista.ordenarPorTituloBurbuja();
        refrescarTabla();
        setEstado("Catálogo ordenado por título (Burbuja).");
    }

    private void prestarLibro() {
        String isbn = txtIsbn.getText().trim();
        String cliente = txtCliente.getText().trim();

        if (isbn.isEmpty()) {
            isbn = JOptionPane.showInputDialog(this, "Ingrese ISBN del libro a prestar:");
            if (isbn == null) return;
            isbn = isbn.trim();
        }
        if (isbn.isEmpty()) return;

        if (cliente.isEmpty()) {
            cliente = JOptionPane.showInputDialog(this, "Ingrese nombre del cliente:");
            if (cliente == null) return;
            cliente = cliente.trim();
        }
        if (cliente.isEmpty()) return;

        Libro l = lista.buscarPorIsbn(isbn);
        if (l == null) {
            JOptionPane.showMessageDialog(this, "No existe un libro con ese ISBN.", "Préstamo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!l.isDisponible()) {
            JOptionPane.showMessageDialog(this, "Ese libro ya está prestado (no disponible).", "Préstamo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String fecha = LocalDate.now().toString();
        cola.encolar(new Prestamo(isbn, cliente, fecha));
        l.setDisponible(false);

        refrescarTabla();
        refrescarPrestamos();
        setEstado("Préstamo encolado: " + isbn + " a " + cliente);
    }

    private void devolverLibro() {
        Prestamo p = cola.desencolar();
        if (p == null) {
            JOptionPane.showMessageDialog(this, "No hay préstamos en cola.", "Devolver", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Libro l = lista.buscarPorIsbn(p.getIsbnLibro());
        if (l != null) {
            l.setDisponible(true);
        }

        refrescarTabla();
        refrescarPrestamos();
        setEstado("Devuelto: " + p.getIsbnLibro() + " (cliente: " + p.getCliente() + ")");
        JOptionPane.showMessageDialog(this,
                "Devolución procesada:\nISBN: " + p.getIsbnLibro() + "\nCliente: " + p.getCliente() + "\nFecha préstamo: " + p.getFecha(),
                "Devolver", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean guardar() {
        try {
            ArchivoTXT.guardar(lista, cola);
            setEstado("Guardado en biblioteca.txt");
            JOptionPane.showMessageDialog(this, "Guardado correcto en biblioteca.txt", "Guardar", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error guardando: " + ex.getMessage(), "Guardar", JOptionPane.ERROR_MESSAGE);
            setEstado("Error guardando.");
            return false;
        }
    }

    private void cargarAlIniciar() {
        try {
            ArchivoTXT.cargar(lista, cola);
            setEstado("Datos cargados desde biblioteca.txt");
        } catch (IOException ex) {
            setEstado("No se pudo cargar TXT (se iniciará vacío).");
        }
    }

    private void limpiarCamposLibro() {
        txtIsbn.setText("");
        txtTitulo.setText("");
        txtAutor.setText("");
        txtAnio.setText("");
    }

    private void refrescarTabla() {
        modelo.setRowCount(0);
        NodoLibro aux = lista.getCabeza();
        while (aux != null) {
            Libro l = aux.dato;
            modelo.addRow(new Object[]{
                    l.getIsbn(),
                    l.getTitulo(),
                    l.getAutor(),
                    l.getAnio(),
                    l.isDisponible() ? "SI" : "NO"
            });
            aux = aux.sig;
        }
    }

    private void refrescarPrestamos() {
        StringBuilder sb = new StringBuilder();
        sb.append("ISBN | Cliente | Fecha\n");
        sb.append("---------------------------------------------\n");
        NodoPrestamo aux = cola.getFrente();
        while (aux != null) {
            sb.append(aux.dato.toString()).append("\n");
            aux = aux.sig;
        }
        txtPrestamos.setText(sb.toString());
    }
}
