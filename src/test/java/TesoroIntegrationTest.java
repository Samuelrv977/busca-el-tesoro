import controller.ExploradorController;
import controller.JuegoController;
import dao.CatalogoDAO;
import dao.EstadisticaDAO;
import dao.JugadorDAO;
import dao.PartidaDAO;
import database.ConexionBD;
import dto.EstadisticaDTO;
import dto.PartidaDTO;
import enums.EstadoJuego;
import model.Isla;
import model.Jugador;
import model.Partida;
import model.Pista;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.EstadisticaService;
import service.JuegoService;
import service.JugadorService;
import service.MapaService;
import service.NivelService;
import service.PartidaService;
import service.PistaService;
import service.ReglaService;
import service.TesoroService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TesoroIntegrationTest {

    private ConexionBD bd;
    private CatalogoDAO catalogoDAO;
    private JugadorService jugadorService;
    private JuegoController juegoController;
    private ExploradorController exploradorController;

    @BeforeEach
    void setUp() {
        String dbName = "tesoro_test_" + UUID.randomUUID().toString().replace("-", "");
        bd = new ConexionBD("jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1", "sa", "");
        bd.conectar();

        JugadorDAO jugadorDAO = new JugadorDAO(bd);
        PartidaDAO partidaDAO = new PartidaDAO(bd, jugadorDAO);
        EstadisticaDAO estadisticaDAO = new EstadisticaDAO(bd);
        catalogoDAO = new CatalogoDAO(bd);

        jugadorService = new JugadorService(jugadorDAO);
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

        juegoController = new JuegoController(
                juegoService, jugadorService, mapaService, estadisticaService, nivelService
        );
        exploradorController = new ExploradorController(
                partidaService, pistaService, tesoroService, juegoController
        );
    }

    @AfterEach
    void tearDown() {
        if (bd != null) {
            bd.cerrar();
        }
    }

    @Test
    void cargaInicialSqlYGeneracionDeOpcionesRelacionadas() {
        // Verifica carga inicial de data.sql
        assertTrue(jugadorService.listarJugadores().size() >= 2, "Debe cargar jugadores iniciales desde data.sql");
        assertTrue(catalogoDAO.listarPistasCatalogo().size() >= 10, "Debe cargar catálogo de pistas desde data.sql");

        juegoController.iniciarJuego();
        Partida partida = juegoController.getPartidaActual();

        for (Isla isla : partida.getMapa().getIslas()) {
            Pista pista = isla.getPista();
            assertNotNull(pista);
            List<String> opciones = pista.getOpciones();
            assertEquals(4, opciones.size(), "Cada pista debe generar 4 opciones aleatorias");
            assertEquals(4, new HashSet<>(opciones).size(), "Las 4 opciones deben ser distintas");
            assertTrue(opciones.contains(pista.getRespuestaCorrecta()), "Debe incluir la respuesta correcta");

            Set<String> poolCategoria = new HashSet<>(catalogoDAO.listarRespuestasPorCategoria(pista.getCategoria()));
            assertTrue(poolCategoria.containsAll(opciones), "Todas las opciones deben pertenecer a la misma categoría temática");
        }
    }

    @Test
    void flujoCompletoDePartidaYPersistenciaH2() {
        Jugador j = juegoController.crearJugador("Ana Exploradora");
        assertNotNull(j.getId());
        assertEquals("Ana Exploradora", jugadorService.obtenerPerfil(j.getId()).getNombre());

        juegoController.iniciarJuego();
        Partida partida = juegoController.getPartidaActual();
        assertNotNull(partida);
        assertNotNull(partida.getId());
        assertEquals(EstadoJuego.EN_CURSO, partida.getEstado());
        assertEquals(4, partida.getMapa().getIslas().size());

        Isla islaTesoro = partida.getMapa().getIslas().stream()
                .filter(i -> i.getTesoro() != null)
                .findFirst()
                .orElseThrow();

        exploradorController.seleccionarIsla(islaTesoro.getId());
        assertEquals(islaTesoro, partida.getMapa().getPosicionActual());

        // No debe permitir reclamar antes de resolver la pista
        exploradorController.verificarTesoro();
        assertFalse(partida.isTesoroEncontrado());

        // Resolver pista eligiendo la opción correcta de la lista generada
        String opcionCorrecta = islaTesoro.getPista().getOpciones().stream()
                .filter(o -> o.equals(islaTesoro.getPista().getRespuestaCorrecta()))
                .findFirst()
                .orElseThrow();
        juegoController.resolverPista(opcionCorrecta);
        assertTrue(islaTesoro.getPista().isResuelta());
        assertTrue(j.getPuntos() > 0);

        // Reclamar tesoro y ganar la partida
        exploradorController.verificarTesoro();
        assertTrue(partida.isTesoroEncontrado());
        assertEquals(EstadoJuego.COMPLETADO, partida.getEstado());
        assertEquals(2, j.getNivel());

        Jugador desdeBD = jugadorService.obtenerPerfil(j.getId());
        assertEquals(2, desdeBD.getNivel());
        assertEquals(j.getPuntos(), desdeBD.getPuntos());

        EstadisticaDTO stats = juegoController.consultarEstadisticas(j.getId());
        assertEquals(1, stats.getPartidasJugadas());
        assertEquals(1, stats.getTesorosEncontrados());

        List<PartidaDTO> historial = juegoController.consultarHistorial(j.getId());
        assertEquals(1, historial.size());
        assertEquals("COMPLETADO", historial.get(0).getEstado());
    }
}
