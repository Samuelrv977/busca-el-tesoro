package view;

import enums.EstadoJuego;
import model.Isla;
import model.Jugador;
import model.Partida;
import model.Pista;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PanelJuego extends JPanel {
    private Partida partida;
    private String ultimoMensaje = "";
    private boolean resultadoNotificado = false;

    private final JLabel lblJugador = new JLabel("Jugador: -");
    private final JLabel lblNivelPuntos = new JLabel("Nivel: 1 | Puntos: 0");
    private final JLabel lblEstadoIntentos = new JLabel("Estado: - | Intentos: -");

    private final JLabel lblIslaNombre = new JLabel("Isla actual: -");
    private final JTextArea txtIslaDesc = new JTextArea(2, 24);

    private final JLabel lblTipoPista = new JLabel("Pista (-)");
    private final JTextArea txtPista = new JTextArea(3, 24);

    private final ButtonGroup grupoOpciones = new ButtonGroup();
    private final List<JRadioButton> radioOpciones = new ArrayList<>();
    private final JPanel panelOpciones = new JPanel(new GridLayout(2, 2, 6, 4));

    private final JButton btnResolver = new JButton("Resolver Pista");
    private final JButton btnBuscarTesoro = new JButton("Verificar / Reclamar Tesoro");
    private final JButton btnFinalizar = new JButton("Finalizar Partida");

    private final JTextArea txtBitacora = new JTextArea(5, 24);

    private Consumer<String> onResolverPista;
    private Runnable onVerificarTesoro;
    private Runnable onFinalizarPartida;

    public PanelJuego() {
        setPreferredSize(new Dimension(390, 560));
        setLayout(new BorderLayout(8, 8));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setOpaque(false);

        // Tarjeta de HUD del jugador
        JPanel cardJugador = crearTarjeta("Estado de la Expedición");
        cardJugador.setLayout(new GridLayout(3, 1, 2, 4));
        lblJugador.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblNivelPuntos.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblEstadoIntentos.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblEstadoIntentos.setForeground(new Color(21, 101, 192));
        cardJugador.add(lblJugador);
        cardJugador.add(lblNivelPuntos);
        cardJugador.add(lblEstadoIntentos);

        // Tarjeta de Isla actual
        JPanel cardIsla = crearTarjeta("Isla Actual");
        cardIsla.setLayout(new BorderLayout(4, 4));
        lblIslaNombre.setFont(new Font("SansSerif", Font.BOLD, 13));
        configurarAreaLectura(txtIslaDesc);
        cardIsla.add(lblIslaNombre, BorderLayout.NORTH);
        cardIsla.add(txtIslaDesc, BorderLayout.CENTER);

        // Tarjeta de Pista y opciones seleccionables (RF05)
        JPanel cardPista = crearTarjeta("Desafío / Pista (Selecciona una opción)");
        cardPista.setLayout(new BorderLayout(6, 6));
        lblTipoPista.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTipoPista.setForeground(new Color(142, 68, 173));
        configurarAreaLectura(txtPista);
        txtPista.setFont(new Font("SansSerif", Font.PLAIN, 13));

        panelOpciones.setOpaque(false);
        panelOpciones.setBorder(BorderFactory.createEmptyBorder(4, 2, 6, 2));
        for (int i = 0; i < 4; i++) {
            JRadioButton rb = new JRadioButton("Opción " + (i + 1));
            rb.setOpaque(false);
            rb.setFocusPainted(false);
            rb.setFont(new Font("SansSerif", Font.BOLD, 12));
            rb.setForeground(new Color(30, 41, 59));
            rb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            grupoOpciones.add(rb);
            radioOpciones.add(rb);
            panelOpciones.add(rb);
        }

        JPanel panelRespuesta = new JPanel(new BorderLayout(6, 6));
        panelRespuesta.setOpaque(false);
        estilizarBoton(btnResolver, new Color(41, 128, 185));
        estilizarBoton(btnBuscarTesoro, new Color(212, 140, 15));
        estilizarBoton(btnFinalizar, new Color(192, 57, 43));

        JPanel botonesAccion = new JPanel(new GridLayout(3, 1, 6, 6));
        botonesAccion.setOpaque(false);
        botonesAccion.add(btnResolver);
        botonesAccion.add(btnBuscarTesoro);
        botonesAccion.add(btnFinalizar);

        panelRespuesta.add(panelOpciones, BorderLayout.NORTH);
        panelRespuesta.add(botonesAccion, BorderLayout.CENTER);

        cardPista.add(lblTipoPista, BorderLayout.NORTH);
        cardPista.add(new JScrollPane(txtPista), BorderLayout.CENTER);
        cardPista.add(panelRespuesta, BorderLayout.SOUTH);

        // Bitácora de mensajes
        JPanel cardBitacora = crearTarjeta("Bitácora del Explorador");
        cardBitacora.setLayout(new BorderLayout());
        configurarAreaLectura(txtBitacora);
        txtBitacora.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cardBitacora.add(new JScrollPane(txtBitacora), BorderLayout.CENTER);

        contenido.add(cardJugador);
        contenido.add(Box.createVerticalStrut(6));
        contenido.add(cardIsla);
        contenido.add(Box.createVerticalStrut(6));
        contenido.add(cardPista);

        add(contenido, BorderLayout.NORTH);
        add(cardBitacora, BorderLayout.CENTER);

        // Eventos
        btnResolver.addActionListener(e -> enviarRespuesta());
        btnBuscarTesoro.addActionListener(e -> {
            if (onVerificarTesoro != null) {
                onVerificarTesoro.run();
            }
        });
        btnFinalizar.addActionListener(e -> {
            if (onFinalizarPartida != null) {
                onFinalizarPartida.run();
            }
        });
    }

    private void enviarRespuesta() {
        String seleccionada = obtenerOpcionSeleccionada();
        if (seleccionada == null) {
            mostrarMensaje("Selecciona una de las opciones antes de resolver la pista.");
            return;
        }
        if (onResolverPista != null) {
            onResolverPista.accept(seleccionada);
        }
    }

    private String obtenerOpcionSeleccionada() {
        for (JRadioButton rb : radioOpciones) {
            if (rb.isVisible() && rb.isSelected()) {
                return rb.getText();
            }
        }
        return null;
    }

    public void setPartida(Partida partida) {
        this.partida = partida;
        this.resultadoNotificado = false;
        txtBitacora.setText("");
        actualizarDatos();
    }

    public void setOnResolverPista(Consumer<String> onResolverPista) {
        this.onResolverPista = onResolverPista;
    }

    public void setOnVerificarTesoro(Runnable onVerificarTesoro) {
        this.onVerificarTesoro = onVerificarTesoro;
    }

    public void setOnFinalizarPartida(Runnable onFinalizarPartida) {
        this.onFinalizarPartida = onFinalizarPartida;
    }

    public void actualizarDatos() {
        if (partida == null) {
            return;
        }
        Jugador j = partida.getJugador();
        lblJugador.setText("Jugador: " + (j != null ? j.getNombre() : "-"));
        lblNivelPuntos.setText("Nivel: " + (j != null ? j.getNivel() : 1)
                + "  |  Puntos: " + (j != null ? j.getPuntos() : 0));
        lblEstadoIntentos.setText("Estado: " + partida.getEstado()
                + "  |  Intentos: " + partida.getIntentosRestantes());

        boolean enCurso = partida.getEstado() == EstadoJuego.EN_CURSO;
        Isla actual = partida.getMapa() != null ? partida.getMapa().getPosicionActual() : null;
        if (actual != null) {
            lblIslaNombre.setText(actual.getNombre() + " [" + actual.getTipo() + "]");
            txtIslaDesc.setText(actual.getDescripcion());
        }

        mostrarPista();

        boolean pistaPendiente = actual != null && actual.getPista() != null && !actual.getPista().isResuelta();
        for (JRadioButton rb : radioOpciones) {
            rb.setEnabled(enCurso && pistaPendiente);
        }
        btnResolver.setEnabled(enCurso && pistaPendiente);
        btnBuscarTesoro.setEnabled(enCurso);
        btnFinalizar.setEnabled(enCurso);

        if (!enCurso && !resultadoNotificado && isShowing()) {
            resultadoNotificado = true;
            notificarResultadoFinal();
        }
    }

    private void notificarResultadoFinal() {
        Jugador j = partida.getJugador();
        if (partida.getEstado() == EstadoJuego.COMPLETADO) {
            JOptionPane.showMessageDialog(
                    this,
                    "¡Felicidades, " + j.getNombre() + "!\nHas encontrado el tesoro y completado la expedición.\n\n"
                            + "Puntos totales: " + j.getPuntos() + "\nNuevo nivel: " + j.getNivel(),
                    "¡Objetivo Alcanzado!",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else if (partida.getEstado() == EstadoJuego.PERDIDO) {
            JOptionPane.showMessageDialog(
                    this,
                    "La expedición terminó sin hallar el tesoro.\n¡Puedes iniciar una nueva partida para intentarlo de nuevo!",
                    "Partida Finalizada",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    public void mostrarPista() {
        grupoOpciones.clearSelection();
        if (partida == null || partida.getMapa() == null || partida.getMapa().getPosicionActual() == null) {
            lblTipoPista.setText("Pista (-)");
            txtPista.setText("Selecciona una isla para ver su pista.");
            ocultarOpciones();
            return;
        }
        Pista pista = partida.getMapa().getPosicionActual().getPista();
        if (pista == null) {
            lblTipoPista.setText("Sin pista");
            txtPista.setText("No hay pista en esta isla.");
            ocultarOpciones();
            return;
        }
        String estadoPista = pista.isResuelta() ? "RESUELTA ✓" : "PENDIENTE";
        lblTipoPista.setText("Tipo: " + pista.getTipo() + " (" + pista.getCategoria() + ") | " + estadoPista);
        txtPista.setText(pista.getDescripcion());
        txtPista.setCaretPosition(0);

        List<String> opciones = pista.getOpciones();
        for (int i = 0; i < radioOpciones.size(); i++) {
            JRadioButton rb = radioOpciones.get(i);
            if (opciones != null && i < opciones.size()) {
                rb.setText(opciones.get(i));
                rb.setVisible(true);
            } else {
                rb.setVisible(false);
            }
        }
    }

    private void ocultarOpciones() {
        for (JRadioButton rb : radioOpciones) {
            rb.setVisible(false);
        }
    }

    public void mostrarMensaje() {
        if (ultimoMensaje != null && !ultimoMensaje.isBlank()) {
            txtBitacora.append("• " + ultimoMensaje + "\n");
            txtBitacora.setCaretPosition(txtBitacora.getDocument().getLength());
        }
    }

    public void mostrarMensaje(String mensaje) {
        this.ultimoMensaje = mensaje;
        mostrarMensaje();
    }

    private JPanel crearTarjeta(String titulo) {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(210, 218, 226)),
                        titulo),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        return p;
    }

    private void configurarAreaLectura(JTextArea area) {
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setOpaque(false);
    }

    private void estilizarBoton(JButton btn, Color bg) {
        btn.setUI(new BasicButtonUI());
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
