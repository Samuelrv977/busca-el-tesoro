package view;

import controller.ExploradorController;
import controller.JuegoController;
import database.ConexionBD;
import dto.EstadisticaDTO;
import dto.JugadorDTO;
import dto.PartidaDTO;
import model.Jugador;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VentanaPrincipal extends JFrame {
    private static final String CARD_MENU = "MENU";
    private static final String CARD_MAPA = "MAPA";
    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final JuegoController juegoController;
    private final ExploradorController exploradorController;
    private final ConexionBD conexionBD;

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contenedorPrincipal = new JPanel(cardLayout);

    private final PanelMapa panelMapa = new PanelMapa();
    private final PanelJuego panelJuego = new PanelJuego();

    private final JComboBox<Jugador> cmbJugadores = new JComboBox<>();
    private final JTextField txtNuevoJugador = new JTextField(16);
    private final JLabel lblResumenStats = new JLabel("Selecciona o crea un jugador para ver sus estadísticas.");
    private final DefaultTableModel modeloHistorial = new DefaultTableModel(new String[]{"ID", "Fecha", "Estado"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final DefaultTableModel modeloRanking = new DefaultTableModel(new String[]{"ID", "Jugador", "Puntos"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public VentanaPrincipal(JuegoController juegoController, ExploradorController exploradorController,
                            ConexionBD conexionBD) {
        this.juegoController = juegoController;
        this.exploradorController = exploradorController;
        this.conexionBD = conexionBD;

        setTitle("Búsqueda del Tesoro - MVC + PostgreSQL (Neon) + Swing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1020, 620));
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                conexionBD.cerrar();
            }
        });

        contenedorPrincipal.add(construirPanelMenu(), CARD_MENU);
        contenedorPrincipal.add(construirPanelJuegoCompleto(), CARD_MAPA);
        add(contenedorPrincipal);

        conectarEventosJuego();
        mostrarMenu();
    }

    private JPanel construirPanelMenu() {
        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBackground(new Color(16, 38, 60));
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel titulo = new JLabel("BÚSQUEDA DEL TESORO", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 30));
        titulo.setForeground(new Color(255, 215, 64));

        JLabel subtitulo = new JLabel("Explora islas, descifra pistas y encuentra el cofre legendario (PostgreSQL / Neon)", SwingConstants.CENTER);
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitulo.setForeground(new Color(210, 228, 242));

        JPanel header = new JPanel(new BorderLayout(4, 6));
        header.setOpaque(false);
        header.add(titulo, BorderLayout.NORTH);
        header.add(subtitulo, BorderLayout.SOUTH);

        // Panel central: gestión de jugador + botones
        JPanel panelJugador = new JPanel(new GridBagLayout());
        panelJugador.setBackground(Color.WHITE);
        panelJugador.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Perfil del Explorador"),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panelJugador.add(new JLabel("Nuevo jugador:"), gbc);
        gbc.gridx = 1;
        panelJugador.add(txtNuevoJugador, gbc);
        JButton btnCrear = crearBotonSolido("Crear Jugador", new Color(39, 174, 96), 13);
        gbc.gridx = 2;
        panelJugador.add(btnCrear, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelJugador.add(new JLabel("Jugador activo:"), gbc);
        gbc.gridx = 1;
        panelJugador.add(cmbJugadores, gbc);
        JButton btnIniciar = crearBotonSolido("Iniciar Expedición", new Color(41, 128, 185), 13);
        gbc.gridx = 2;
        panelJugador.add(btnIniciar, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        lblResumenStats.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblResumenStats.setForeground(new Color(44, 62, 80));
        panelJugador.add(lblResumenStats, gbc);

        // Panel inferior: Tablas de Historial (PartidaDTO) y Ranking (JugadorDTO)
        JPanel tablas = new JPanel(new java.awt.GridLayout(1, 2, 14, 0));
        tablas.setOpaque(false);

        JPanel pHistorial = new JPanel(new BorderLayout());
        pHistorial.setBackground(Color.WHITE);
        pHistorial.setBorder(BorderFactory.createTitledBorder("Historial de Partidas del Jugador (PartidaDTO)"));
        pHistorial.add(new JScrollPane(new JTable(modeloHistorial)), BorderLayout.CENTER);

        JPanel pRanking = new JPanel(new BorderLayout());
        pRanking.setBackground(Color.WHITE);
        pRanking.setBorder(BorderFactory.createTitledBorder("Ranking de Exploradores (JugadorDTO)"));
        pRanking.add(new JScrollPane(new JTable(modeloRanking)), BorderLayout.CENTER);

        tablas.add(pHistorial);
        tablas.add(pRanking);

        JPanel cuerpo = new JPanel(new BorderLayout(12, 14));
        cuerpo.setOpaque(false);
        cuerpo.add(panelJugador, BorderLayout.NORTH);
        cuerpo.add(tablas, BorderLayout.CENTER);

        root.add(header, BorderLayout.NORTH);
        root.add(cuerpo, BorderLayout.CENTER);

        // Eventos del menú
        btnCrear.addActionListener(e -> {
            String nombre = txtNuevoJugador.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingresa un nombre para el explorador.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Jugador nuevo = juegoController.crearJugador(nombre);
            txtNuevoJugador.setText("");
            recargarDatosMenu(nuevo.getId());
        });

        cmbJugadores.addActionListener(e -> {
            Jugador seleccionado = (Jugador) cmbJugadores.getSelectedItem();
            if (seleccionado != null) {
                juegoController.seleccionarJugador(seleccionado);
                actualizarPanelEstadisticas(seleccionado);
            }
        });

        btnIniciar.addActionListener(e -> iniciarJuego());
        return root;
    }

    private JPanel construirPanelJuegoCompleto() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel barraSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        barraSuperior.setBackground(new Color(12, 34, 54));

        JButton btnMenu = crearBotonSolido("← Menú Principal", new Color(44, 82, 130), 12);
        JButton btnNueva = crearBotonSolido("Nueva Partida", new Color(39, 174, 96), 12);
        JButton btnVerPista = crearBotonSolido("Ver Pista Actual", new Color(142, 68, 173), 12);

        for (JButton b : List.of(btnMenu, btnNueva, btnVerPista)) {
            barraSuperior.add(b);
        }

        btnMenu.addActionListener(e -> {
            juegoController.finalizarJuego();
            mostrarMenu();
        });
        btnNueva.addActionListener(e -> iniciarJuego());
        btnVerPista.addActionListener(e -> exploradorController.mostrarPista());

        panel.add(barraSuperior, BorderLayout.NORTH);
        panel.add(panelMapa, BorderLayout.CENTER);
        panel.add(panelJuego, BorderLayout.EAST);
        return panel;
    }

    private JButton crearBotonSolido(String texto, Color fondo, int tamanoFuente) {
        JButton btn = new JButton(texto);
        btn.setUI(new BasicButtonUI());
        btn.setBackground(fondo);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, tamanoFuente));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void conectarEventosJuego() {
        exploradorController.setOnActualizarVista(() -> {
            panelMapa.dibujarMapa();
            panelJuego.actualizarDatos();
            String msg = exploradorController.getUltimoMensaje();
            if (msg != null && !msg.isBlank()) {
                panelJuego.mostrarMensaje(msg);
            }
        });

        panelMapa.setOnIslaSeleccionada(isla -> {
            if (isla != null) {
                exploradorController.seleccionarIsla(isla.getId());
            }
        });

        panelJuego.setOnResolverPista(respuesta -> {
            juegoController.resolverPista(respuesta);
            panelMapa.dibujarMapa();
            panelJuego.actualizarDatos();
            panelJuego.mostrarMensaje(juegoController.getUltimoMensaje());
        });

        panelJuego.setOnVerificarTesoro(exploradorController::verificarTesoro);

        panelJuego.setOnFinalizarPartida(() -> {
            juegoController.finalizarJuego();
            panelJuego.actualizarDatos();
            panelJuego.mostrarMensaje(juegoController.getUltimoMensaje());
        });
    }

    public void iniciarJuego() {
        Jugador seleccionado = (Jugador) cmbJugadores.getSelectedItem();
        if (seleccionado != null) {
            juegoController.seleccionarJugador(seleccionado);
        }
        juegoController.iniciarJuego();
        panelMapa.setMapa(juegoController.getPartidaActual().getMapa());
        panelJuego.setPartida(juegoController.getPartidaActual());
        panelJuego.mostrarMensaje(juegoController.getUltimoMensaje());
        mostrarMapa();
    }

    public void mostrarMenu() {
        Jugador actual = juegoController.getJugadorActual();
        recargarDatosMenu(actual != null ? actual.getId() : null);
        cardLayout.show(contenedorPrincipal, CARD_MENU);
    }

    public void mostrarMapa() {
        exploradorController.mostrarMapa();
        cardLayout.show(contenedorPrincipal, CARD_MAPA);
    }

    private void recargarDatosMenu(Long seleccionarId) {
        List<Jugador> jugadores = juegoController.listarJugadores();
        DefaultComboBoxModel<Jugador> comboModel = new DefaultComboBoxModel<>();
        Jugador porSeleccionar = null;
        for (Jugador j : jugadores) {
            comboModel.addElement(j);
            if (seleccionarId != null && seleccionarId.equals(j.getId())) {
                porSeleccionar = j;
            }
        }
        cmbJugadores.setModel(comboModel);
        if (porSeleccionar != null) {
            cmbJugadores.setSelectedItem(porSeleccionar);
            juegoController.seleccionarJugador(porSeleccionar);
            actualizarPanelEstadisticas(porSeleccionar);
        } else if (!jugadores.isEmpty()) {
            cmbJugadores.setSelectedIndex(0);
            juegoController.seleccionarJugador(jugadores.get(0));
            actualizarPanelEstadisticas(jugadores.get(0));
        } else {
            lblResumenStats.setText("Aún no hay jugadores registrados. Crea uno para comenzar.");
            modeloHistorial.setRowCount(0);
        }

        // Ranking desde JugadorDTO
        modeloRanking.setRowCount(0);
        for (JugadorDTO dto : juegoController.listarJugadoresDTO()) {
            modeloRanking.addRow(new Object[]{dto.getId(), dto.getNombre(), dto.getPuntos()});
        }
    }

    private void actualizarPanelEstadisticas(Jugador jugador) {
        EstadisticaDTO stats = juegoController.consultarEstadisticas(jugador.getId());
        lblResumenStats.setText(String.format(
                "Jugador: %s  |  Nivel: %d  |  Puntos: %d  |  Partidas jugadas: %d  |  Tesoros encontrados: %d",
                jugador.getNombre(), jugador.getNivel(), jugador.getPuntos(),
                stats.getPartidasJugadas(), stats.getTesorosEncontrados()
        ));

        modeloHistorial.setRowCount(0);
        for (PartidaDTO p : juegoController.consultarHistorial(jugador.getId())) {
            modeloHistorial.addRow(new Object[]{
                    p.getId(),
                    p.getFecha() != null ? p.getFecha().format(FMT_FECHA) : "-",
                    p.getEstado()
            });
        }
    }
}
