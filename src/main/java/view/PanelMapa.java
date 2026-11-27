package view;

import enums.TipoIsla;
import model.Isla;
import model.Mapa;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class PanelMapa extends JPanel {
    private static final int RADIO_ISLA = 38;

    private Mapa mapa;
    private Isla islaSeleccionada;
    private Consumer<Isla> onIslaSeleccionada;
    private final Map<Isla, Point> centrosIslas = new HashMap<>();

    private double faseAnimacion = 0.0;
    private final Timer animadorFlecha;

    public PanelMapa() {
        setPreferredSize(new Dimension(620, 520));
        setBackground(new Color(14, 52, 82));

        this.animadorFlecha = new Timer(60, e -> {
            faseAnimacion += 0.35;
            repaint();
        });

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Isla clicada = buscarIslaEn(e.getPoint());
                if (clicada != null) {
                    islaSeleccionada = clicada;
                    seleccionarIsla();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                setCursor(buscarIslaEn(e.getPoint()) != null
                        ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        : Cursor.getDefaultCursor());
            }
        };
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    public void setMapa(Mapa mapa) {
        this.mapa = mapa;
        this.islaSeleccionada = mapa != null ? mapa.getPosicionActual() : null;
        dibujarMapa();
    }

    public void setOnIslaSeleccionada(Consumer<Isla> onIslaSeleccionada) {
        this.onIslaSeleccionada = onIslaSeleccionada;
    }

    public void dibujarMapa() {
        actualizarAnimacionFlecha();
        revalidate();
        repaint();
    }

    public void seleccionarIsla() {
        if (islaSeleccionada != null && onIslaSeleccionada != null) {
            onIslaSeleccionada.accept(islaSeleccionada);
        }
        dibujarMapa();
    }

    @Override
    public void removeNotify() {
        animadorFlecha.stop();
        super.removeNotify();
    }

    private void actualizarAnimacionFlecha() {
        if (obtenerSiguienteIslaSugerida() != null) {
            if (!animadorFlecha.isRunning()) {
                animadorFlecha.start();
            }
        } else if (animadorFlecha.isRunning()) {
            animadorFlecha.stop();
        }
    }

    private Isla obtenerSiguienteIslaSugerida() {
        if (mapa == null || mapa.getIslas() == null || mapa.getIslas().isEmpty()) {
            return null;
        }
        boolean tesoroYaReclamado = mapa.getIslas().stream()
                .anyMatch(i -> i.getTesoro() != null && i.getTesoro().isReclamado());
        if (tesoroYaReclamado) {
            return null;
        }

        Isla actual = mapa.getPosicionActual();
        if (actual == null || actual.getPista() == null || !actual.getPista().isResuelta()) {
            return null;
        }

        // Si el tesoro está en la isla actual y ya se resolvió su pista, señalar esta isla
        if (actual.getTesoro() != null && !actual.getTesoro().isReclamado()) {
            return actual;
        }

        // Buscar la siguiente isla con pista pendiente (en orden circular)
        List<Isla> islas = mapa.getIslas();
        int idxActual = Math.max(0, islas.indexOf(actual));
        for (int offset = 1; offset < islas.size(); offset++) {
            Isla candidata = islas.get((idxActual + offset) % islas.size());
            if (candidata.getPista() != null && !candidata.getPista().isResuelta()) {
                return candidata;
            }
        }
        return null;
    }

    private Isla buscarIslaEn(Point p) {
        for (Map.Entry<Isla, Point> entry : centrosIslas.entrySet()) {
            if (p.distance(entry.getValue()) <= RADIO_ISLA + 6) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Fondo océano con degradado
        GradientPaint oceano = new GradientPaint(0, 0, new Color(11, 46, 77), w, h, new Color(20, 92, 128));
        g2.setPaint(oceano);
        g2.fillRect(0, 0, w, h);

        // Ondas decorativas
        g2.setColor(new Color(255, 255, 255, 18));
        g2.setStroke(new BasicStroke(1.2f));
        for (int y = 30; y < h; y += 45) {
            for (int x = 15; x < w; x += 90) {
                g2.drawArc(x, y, 40, 14, 0, 180);
            }
        }

        // Brújula decorativa en la esquina inferior izquierda
        dibujarBrujula(g2, 58, h - 58);

        // Leyenda superior
        g2.setColor(new Color(255, 255, 255, 220));
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        g2.drawString("ARCHIPIÉLAGO DEL TESORO  (Haz clic en una isla para viajar)", 18, 28);

        centrosIslas.clear();
        if (mapa == null || mapa.getIslas() == null || mapa.getIslas().isEmpty()) {
            g2.setFont(new Font("SansSerif", Font.PLAIN, 15));
            g2.drawString("Inicia una partida para generar el mapa.", w / 2 - 130, h / 2);
            g2.dispose();
            return;
        }

        List<Isla> islas = mapa.getIslas();
        for (Isla isla : islas) {
            int cx = (int) (w * isla.getRelX());
            int cy = (int) (h * isla.getRelY());
            centrosIslas.put(isla, new Point(cx, cy));
        }

        // Rutas marítimas punteadas entre islas
        float[] dash = {6f, 6f};
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f, dash, 0f));
        g2.setColor(new Color(255, 232, 163, 125));
        for (int i = 0; i < islas.size() - 1; i++) {
            Point a = centrosIslas.get(islas.get(i));
            Point b = centrosIslas.get(islas.get(i + 1));
            g2.drawLine(a.x, a.y, b.x, b.y);
        }
        if (islas.size() >= 4) {
            Point p0 = centrosIslas.get(islas.get(0));
            Point p2 = centrosIslas.get(islas.get(2));
            g2.drawLine(p0.x, p0.y, p2.x, p2.y);
        }

        Isla siguienteSugerida = obtenerSiguienteIslaSugerida();

        // Ruta destacada hacia la siguiente pista sugerida
        if (siguienteSugerida != null && mapa.getPosicionActual() != null
                && !siguienteSugerida.equals(mapa.getPosicionActual())) {
            Point origen = centrosIslas.get(mapa.getPosicionActual());
            Point destino = centrosIslas.get(siguienteSugerida);
            if (origen != null && destino != null) {
                dibujarRutaSugerida(g2, origen, destino);
            }
        }

        // Dibujar cada isla
        for (Isla isla : islas) {
            Point c = centrosIslas.get(isla);
            boolean esActual = isla.equals(mapa.getPosicionActual());
            boolean esSugerida = isla.equals(siguienteSugerida);
            dibujarIsla(g2, isla, c.x, c.y, esActual, esSugerida);
        }

        // Dibujar flecha animada + etiqueta "AQUÍ" por encima de todo
        if (siguienteSugerida != null) {
            Point c = centrosIslas.get(siguienteSugerida);
            if (c != null) {
                boolean esTesoroAqui = siguienteSugerida.equals(mapa.getPosicionActual());
                dibujarFlechaIndicadora(g2, c.x, c.y, esTesoroAqui);
            }
        }

        g2.dispose();
    }

    private void dibujarRutaSugerida(Graphics2D g2, Point origen, Point destino) {
        float[] dash = {8f, 6f};
        g2.setColor(new Color(255, 167, 38, 220));
        g2.setStroke(new BasicStroke(3.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f, dash, 0f));
        g2.drawLine(origen.x, origen.y, destino.x, destino.y);
    }

    private void dibujarIsla(Graphics2D g2, Isla isla, int cx, int cy, boolean esActual, boolean esSugerida) {
        // Sombra y orilla de agua poco profunda
        g2.setColor(new Color(90, 210, 225, 70));
        g2.fillOval(cx - RADIO_ISLA - 8, cy - RADIO_ISLA - 8, (RADIO_ISLA + 8) * 2, (RADIO_ISLA + 8) * 2);

        // Halo pulsante si es la siguiente isla sugerida
        if (esSugerida) {
            int pulso = (int) Math.round(Math.abs(Math.sin(faseAnimacion)) * 6);
            g2.setColor(new Color(255, 112, 67, 170));
            g2.setStroke(new BasicStroke(3.5f));
            int r = RADIO_ISLA + 8 + pulso;
            g2.drawOval(cx - r, cy - r, r * 2, r * 2);
        }

        // Anillo si es la posición actual
        if (esActual) {
            g2.setColor(new Color(255, 215, 64));
            g2.setStroke(new BasicStroke(4f));
            g2.drawOval(cx - RADIO_ISLA - 6, cy - RADIO_ISLA - 6, (RADIO_ISLA + 6) * 2, (RADIO_ISLA + 6) * 2);
        }

        // Color base según TipoIsla
        Color colorBase = colorPorTipo(isla.getTipo());
        Color colorBorde = colorBase.darker();
        g2.setColor(colorBase);
        g2.fillOval(cx - RADIO_ISLA, cy - RADIO_ISLA, RADIO_ISLA * 2, RADIO_ISLA * 2);

        g2.setColor(colorBorde);
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawOval(cx - RADIO_ISLA, cy - RADIO_ISLA, RADIO_ISLA * 2, RADIO_ISLA * 2);

        // Etiqueta de tipo dentro de la isla
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2.setColor(new Color(25, 30, 35));
        String tipoTxt = isla.getTipo().name();
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(tipoTxt, cx - fm.stringWidth(tipoTxt) / 2, cy + 4);

        // Insignia de estado (omitir si encima va la flecha indicadora)
        if (!esSugerida) {
            String badge = "";
            Color badgeBg = null;
            if (isla.getTesoro() != null && isla.getTesoro().isReclamado()) {
                badge = "TESORO";
                badgeBg = new Color(255, 193, 7);
            } else if (isla.getPista() != null && isla.getPista().isResuelta()) {
                badge = "OK";
                badgeBg = new Color(46, 204, 113);
            } else if (esActual) {
                badge = "AQUI";
                badgeBg = new Color(231, 76, 60);
            } else if (isla.isVisitada()) {
                badge = "VISTA";
                badgeBg = new Color(52, 152, 219);
            }

            if (!badge.isEmpty()) {
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                int bw = g2.getFontMetrics().stringWidth(badge) + 12;
                int bx = cx - bw / 2;
                int by = cy - RADIO_ISLA - 18;
                g2.setColor(badgeBg);
                g2.fillRoundRect(bx, by, bw, 17, 10, 10);
                g2.setColor(Color.WHITE);
                g2.drawString(badge, bx + 6, by + 12);
            }
        }

        // Nombre de la isla debajo
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        FontMetrics fmNombre = g2.getFontMetrics();
        int nw = fmNombre.stringWidth(isla.getNombre());
        g2.setColor(new Color(10, 25, 41, 185));
        g2.fillRoundRect(cx - nw / 2 - 6, cy + RADIO_ISLA + 6, nw + 12, 20, 8, 8);
        g2.setColor(Color.WHITE);
        g2.drawString(isla.getNombre(), cx - nw / 2, cy + RADIO_ISLA + 20);
    }

    private void dibujarFlechaIndicadora(Graphics2D g2, int cx, int cy, boolean esTesoroAqui) {
        int rebote = (int) Math.round(Math.sin(faseAnimacion) * 5);
        int puntaY = cy - RADIO_ISLA - 8 + rebote;
        int baseFlechaY = puntaY - 14;
        int pillH = 20;
        int pillY = Math.max(36, baseFlechaY - pillH - 2);
        baseFlechaY = pillY + pillH + 2;
        puntaY = baseFlechaY + 13;

        String texto = esTesoroAqui ? "¡TESORO AQUÍ!" : "SIGUIENTE AQUÍ";
        Color colorFondo = esTesoroAqui ? new Color(245, 158, 11) : new Color(255, 87, 34);
        Color colorTexto = esTesoroAqui ? new Color(20, 25, 30) : Color.WHITE;

        // Etiqueta redondeada encima de la flecha
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        FontMetrics fm = g2.getFontMetrics();
        int pillW = fm.stringWidth(texto) + 16;
        int pillX = cx - pillW / 2;

        g2.setColor(new Color(0, 0, 0, 90));
        g2.fillRoundRect(pillX + 2, pillY + 2, pillW, pillH, 12, 12);

        g2.setColor(colorFondo);
        g2.fillRoundRect(pillX, pillY, pillW, pillH, 12, 12);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(pillX, pillY, pillW, pillH, 12, 12);

        g2.setColor(colorTexto);
        g2.drawString(texto, pillX + 8, pillY + 14);

        // Flecha triangular señalando hacia la isla
        Polygon flecha = new Polygon(
                new int[]{cx - 9, cx + 9, cx},
                new int[]{baseFlechaY, baseFlechaY, puntaY},
                3
        );
        g2.setColor(colorFondo);
        g2.fillPolygon(flecha);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1.4f));
        g2.drawPolygon(flecha);
    }

    private Color colorPorTipo(TipoIsla tipo) {
        if (tipo == null) {
            return new Color(180, 180, 180);
        }
        return switch (tipo) {
            case TROPICAL -> new Color(82, 196, 112);
            case HELADA -> new Color(185, 232, 252);
            case DESIERTO -> new Color(240, 198, 106);
        };
    }

    private void dibujarBrujula(Graphics2D g2, int cx, int cy) {
        int r = 28;
        g2.setColor(new Color(255, 255, 255, 40));
        g2.fillOval(cx - r, cy - r, r * 2, r * 2);
        g2.setColor(new Color(255, 255, 255, 160));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(cx - r, cy - r, r * 2, r * 2);
        g2.drawLine(cx, cy - r + 4, cx, cy + r - 4);
        g2.drawLine(cx - r + 4, cy, cx + r - 4, cy);
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2.drawString("N", cx - 4, cy - r - 4);
        g2.drawString("S", cx - 4, cy + r + 12);
        g2.drawString("E", cx + r + 4, cy + 4);
        g2.drawString("O", cx - r - 12, cy + 4);
    }
}
