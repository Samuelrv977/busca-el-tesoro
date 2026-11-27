package service;

import dao.CatalogoDAO;
import enums.NivelDificultad;
import model.Isla;
import model.Mapa;
import model.Pista;
import model.Tesoro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class MapaService {
    private static final int OPCIONES_POR_PISTA = 4;

    private final CatalogoDAO catalogoDAO;
    private final PistaService pistaService;
    private final AtomicLong mapaIdGen = new AtomicLong(1);
    private final Random random = new Random();

    public MapaService(CatalogoDAO catalogoDAO, PistaService pistaService) {
        this.catalogoDAO = catalogoDAO;
        this.pistaService = pistaService;
    }

    public Mapa generarMapa(NivelDificultad dificultad) {
        NivelDificultad nivel = dificultad != null ? dificultad : NivelDificultad.FACIL;
        int cantidadDeseada = switch (nivel) {
            case FACIL -> 4;
            case MEDIO -> 5;
            case DIFICIL -> 6;
        };

        List<Isla> catalogoIslas = catalogoDAO.listarIslasCatalogo(nivel);
        List<Pista> catalogoPistas = new ArrayList<>(catalogoDAO.listarPistasCatalogo());
        List<Tesoro> catalogoTesoros = catalogoDAO.listarTesorosCatalogo();

        if (catalogoIslas.size() < 2 || catalogoPistas.isEmpty() || catalogoTesoros.isEmpty()) {
            throw new IllegalStateException("Los catálogos iniciales de la base de datos no están cargados.");
        }

        int cantidadIslas = Math.min(cantidadDeseada, catalogoIslas.size());
        Collections.shuffle(catalogoPistas, random);

        int indiceTesoro = 1 + random.nextInt(cantidadIslas - 1);
        Tesoro tesoroBase = catalogoTesoros.get(random.nextInt(catalogoTesoros.size()));
        int bonoDificultad = switch (nivel) {
            case FACIL -> 0;
            case MEDIO -> 100;
            case DIFICIL -> 200;
        };

        List<Isla> islas = new ArrayList<>();
        for (int i = 0; i < cantidadIslas; i++) {
            Isla isla = catalogoIslas.get(i);
            Pista pista = catalogoPistas.get(i % catalogoPistas.size());
            pistaService.generarOpcionesAleatorias(pista, OPCIONES_POR_PISTA);
            isla.setPista(pista);

            if (i == indiceTesoro) {
                Tesoro tesoro = new Tesoro(
                        tesoroBase.getId(),
                        tesoroBase.getNombre(),
                        tesoroBase.getDescripcion(),
                        tesoroBase.getPuntos() + bonoDificultad
                );
                isla.setTesoro(tesoro);
            }
            if (i == 0) {
                isla.setVisitada(true);
            }
            islas.add(isla);
        }

        return new Mapa(mapaIdGen.getAndIncrement(), islas, islas.get(0));
    }

    public List<Isla> obtenerIslas(Mapa mapa) {
        if (mapa == null || mapa.getIslas() == null) {
            return List.of();
        }
        return mapa.getIslas();
    }

    public boolean validarDestino(Isla isla) {
        return isla != null && isla.getId() != null;
    }

    public boolean validarDestino(Mapa mapa, Isla isla) {
        return mapa != null && validarDestino(isla) && mapa.getIslas().contains(isla);
    }
}
