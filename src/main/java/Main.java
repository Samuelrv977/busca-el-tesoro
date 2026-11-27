import controller.ExploradorController;
import controller.JuegoController;
import dao.CatalogoDAO;
import dao.EstadisticaDAO;
import dao.JugadorDAO;
import dao.PartidaDAO;
import database.ConexionBD;
import service.EstadisticaService;
import service.JuegoService;
import service.JugadorService;
import service.MapaService;
import service.NivelService;
import service.PartidaService;
import service.PistaService;
import service.ReglaService;
import service.TesoroService;
import view.VentanaPrincipal;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        // 1. Base de datos PostgreSQL / Neon (lee db.properties y ejecuta schema.sql / data.sql)
        ConexionBD conexionBD = new ConexionBD();
        conexionBD.conectar();

        // 2. Capa DAO
        JugadorDAO jugadorDAO = new JugadorDAO(conexionBD);
        PartidaDAO partidaDAO = new PartidaDAO(conexionBD, jugadorDAO);
        EstadisticaDAO estadisticaDAO = new EstadisticaDAO(conexionBD);
        CatalogoDAO catalogoDAO = new CatalogoDAO(conexionBD);

        // 3. Capa Service
        JugadorService jugadorService = new JugadorService(jugadorDAO);
        NivelService nivelService = new NivelService(jugadorDAO);
        PistaService pistaService = new PistaService(catalogoDAO);
        MapaService mapaService = new MapaService(catalogoDAO, pistaService);
        TesoroService tesoroService = new TesoroService(jugadorService);
        ReglaService reglaService = new ReglaService();
        EstadisticaService estadisticaService = new EstadisticaService(estadisticaDAO, partidaDAO);
        PartidaService partidaService = new PartidaService(partidaDAO, mapaService, nivelService, estadisticaService);
        JuegoService juegoService = new JuegoService(
                partidaService, pistaService, tesoroService, reglaService, jugadorService, nivelService
        );

        // 4. Capa Controller
        JuegoController juegoController = new JuegoController(
                juegoService, jugadorService, mapaService, estadisticaService, nivelService
        );
        ExploradorController exploradorController = new ExploradorController(
                partidaService, pistaService, tesoroService, juegoController
        );

        // 5. Capa View (Java Swing)
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Usar Look & Feel por defecto si el del sistema no está disponible
            }
            VentanaPrincipal ventana = new VentanaPrincipal(juegoController, exploradorController, conexionBD);
            ventana.setVisible(true);
        });
    }
}
