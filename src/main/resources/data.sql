-- Carga inicial de datos del juego Búsqueda del Tesoro

-- Jugadores iniciales de demostración
INSERT INTO jugadores (nombre, nivel, puntos) VALUES ('Capitán Morgan', 1, 0);
INSERT INTO jugadores (nombre, nivel, puntos) VALUES ('Elena Brújula', 2, 230);

-- Historial y estadística inicial para Elena Brújula
INSERT INTO partidas (jugador_id, estado, fecha_inicio, tesoro_encontrado)
VALUES ((SELECT id FROM jugadores WHERE nombre = 'Elena Brújula' LIMIT 1), 'COMPLETADO', CURRENT_TIMESTAMP, TRUE);

INSERT INTO estadisticas (jugador_id, partidas_jugadas, tesoros_encontrados)
VALUES ((SELECT id FROM jugadores WHERE nombre = 'Elena Brújula' LIMIT 1), 1, 1);

-- Catálogo de islas del archipiélago
INSERT INTO islas_catalogo (nombre, descripcion, tipo, rel_x, rel_y) VALUES
('Puerto Coral', 'Muelle inicial rodeado de palmeras y viejos mapas náuticos.', 'TROPICAL', 0.16, 0.52),
('Bahía Esmeralda', 'Selva densa con ruinas cubiertas de musgo brillante.', 'TROPICAL', 0.38, 0.24),
('Dunas de Ámbar', 'Desierto dorado donde el viento descubre inscripciones antiguas.', 'DESIERTO', 0.42, 0.76),
('Cabo Glaciar', 'Acantilados de hielo azul que custodian cavernas secretas.', 'HELADA', 0.66, 0.28),
('Oasis del Espejismo', 'Palmeras solitarias junto a un pozo de agua cristalina.', 'DESIERTO', 0.68, 0.72),
('Cumbre Boreal', 'Isla nevada coronada por un faro abandonado bajo auroras.', 'HELADA', 0.85, 0.48);

-- Catálogo de pistas y acertijos con su categoría temática
INSERT INTO pistas_catalogo (descripcion, tipo, categoria, respuesta_correcta, puntos_recompensa) VALUES
('Acertijo: Siempre señala el norte para guiar a los navegantes. ¿Qué objeto es?', 'ACERTIJO', 'NAVEGACION', 'Brújula', 40),
('Imagen tallada [⚓]: Símbolo de hierro que detiene al barco en la bahía. ¿Cómo se llama?', 'IMAGEN', 'NAVEGACION', 'Ancla', 40),
('Acertijo: Tiene ciudades sin casas, ríos sin agua y bosques sin árboles. ¿Qué es?', 'ACERTIJO', 'NAVEGACION', 'Mapa', 50),
('Pregunta: ¿Qué torre iluminada en la costa avisa a los barcos de las rocas?', 'PREGUNTA', 'NAVEGACION', 'Faro', 40),
('Pregunta: ¿Qué astro guía a los marineros durante el día?', 'PREGUNTA', 'NATURALEZA', 'Sol', 35),
('Pregunta: ¿Cuál es el planeta azul donde navegamos estos mares?', 'PREGUNTA', 'NATURALEZA', 'Tierra', 35),
('Texto antiguo: "No tengo alas pero vuelo y empujo las velas del barco". ¿Qué soy?', 'TEXTO', 'NATURALEZA', 'Viento', 45),
('Pregunta: ¿Qué fenómeno hace subir y bajar el nivel del mar por la luna?', 'PREGUNTA', 'NATURALEZA', 'Marea', 45),
('Texto descifrado: "Cuanto más quitas de mí, más grande me hago en la arena". ¿Qué soy?', 'TEXTO', 'ENIGMA', 'Agujero', 45),
('Imagen tallada [⏳]: Mide el paso del tiempo con granos dorados. ¿Qué objeto es?', 'IMAGEN', 'ENIGMA', 'Reloj', 45),
('Acertijo: Te sigue a todas partes bajo el sol, pero desaparece en la oscuridad. ¿Qué es?', 'ACERTIJO', 'ENIGMA', 'Sombra', 45),
('Texto descifrado: "Hablo sin boca y oigo sin oídos; respondo en las cavernas". ¿Qué soy?', 'TEXTO', 'ENIGMA', 'Eco', 50);

-- Banco de respuestas relacionadas por categoría (para generar distractores aleatorios)
INSERT INTO respuestas_catalogo (texto, categoria) VALUES
('Brújula', 'NAVEGACION'),
('Ancla', 'NAVEGACION'),
('Mapa', 'NAVEGACION'),
('Faro', 'NAVEGACION'),
('Timón', 'NAVEGACION'),
('Catalejo', 'NAVEGACION'),
('Sextante', 'NAVEGACION'),
('Velamen', 'NAVEGACION'),
('Sol', 'NATURALEZA'),
('Tierra', 'NATURALEZA'),
('Viento', 'NATURALEZA'),
('Marea', 'NATURALEZA'),
('Luna', 'NATURALEZA'),
('Estrella Polar', 'NATURALEZA'),
('Océano', 'NATURALEZA'),
('Volcán', 'NATURALEZA'),
('Agujero', 'ENIGMA'),
('Reloj', 'ENIGMA'),
('Sombra', 'ENIGMA'),
('Eco', 'ENIGMA'),
('Espejo', 'ENIGMA'),
('Silencio', 'ENIGMA'),
('Huella', 'ENIGMA'),
('Llave', 'ENIGMA');

-- Catálogo de tesoros
INSERT INTO tesoros_catalogo (nombre, descripcion, puntos_base) VALUES
('Cofre del Galeón Dorado', 'Antiguo cofre repleto de doblones, rubíes y una brújula legendaria.', 150),
('Corona de los Siete Mares', 'Corona forjada en oro y perlas abisales de un antiguo rey navegante.', 180),
('Reliquia del Kraken', 'Astrolabio de esmeralda custodiado durante siglos bajo la arena.', 200);
