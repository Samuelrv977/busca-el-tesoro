package service;

import dao.CatalogoDAO;
import model.Isla;
import model.Pista;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PistaService {
    private final CatalogoDAO catalogoDAO;
    private final Random random;

    public PistaService(CatalogoDAO catalogoDAO) {
        this.catalogoDAO = catalogoDAO;
        this.random = new Random();
    }

    public Pista obtenerPista(Isla isla) {
        if (isla == null) {
            return null;
        }
        return isla.getPista();
    }

    public List<String> generarOpcionesAleatorias(Pista pista, int cantidadOpciones) {
        if (pista == null || pista.getRespuestaCorrecta() == null) {
            return List.of();
        }
        int total = Math.max(2, cantidadOpciones);
        String correcta = pista.getRespuestaCorrecta();
        String correctaNorm = normalizar(correcta);

        List<String> relacionadas = new ArrayList<>();
        if (catalogoDAO != null && pista.getCategoria() != null) {
            for (String candidata : catalogoDAO.listarRespuestasPorCategoria(pista.getCategoria())) {
                if (candidata != null && !normalizar(candidata).equals(correctaNorm)) {
                    relacionadas.add(candidata);
                }
            }
        }

        Collections.shuffle(relacionadas, random);

        List<String> opciones = new ArrayList<>();
        opciones.add(correcta);
        for (int i = 0; i < relacionadas.size() && opciones.size() < total; i++) {
            String distractor = relacionadas.get(i);
            boolean yaIncluida = opciones.stream().anyMatch(o -> normalizar(o).equals(normalizar(distractor)));
            if (!yaIncluida) {
                opciones.add(distractor);
            }
        }

        Collections.shuffle(opciones, random);
        pista.setOpciones(opciones);
        return opciones;
    }

    public boolean validarRespuesta(Pista pista, String respuesta) {
        if (pista == null || respuesta == null || pista.getRespuestaCorrecta() == null) {
            return false;
        }
        String normalizadaUsuario = normalizar(respuesta);
        String normalizadaCorrecta = normalizar(pista.getRespuestaCorrecta());
        boolean correcta = !normalizadaUsuario.isEmpty() && normalizadaUsuario.equals(normalizadaCorrecta);
        if (correcta) {
            pista.setResuelta(true);
        }
        return correcta;
    }

    private String normalizar(String texto) {
        String sinAcentos = Normalizer.normalize(texto.trim().toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return sinAcentos.replaceAll("[^a-z0-9]", "");
    }
}
