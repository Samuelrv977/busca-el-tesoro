package database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ConexionBD {
    private static final String ARCHIVO_PROPIEDADES = "/db.properties";

    private final String URL;
    private final String USER;
    private final String PASSWORD;
    private Connection connection;
    private boolean esquemaInicializado;

    public ConexionBD() {
        Properties props = cargarPropiedades();
        this.URL = obtenerValor("DB_URL", props, "db.url");
        this.USER = obtenerValor("DB_USER", props, "db.user");
        this.PASSWORD = obtenerValor("DB_PASSWORD", props, "db.password");
        this.esquemaInicializado = false;
    }

    public ConexionBD(String url, String user, String password) {
        this.URL = url;
        this.USER = user;
        this.PASSWORD = password;
        this.esquemaInicializado = false;
    }

    public synchronized Connection conectar() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException ignored) {
                    }
                }
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                if (!esquemaInicializado) {
                    inicializarBaseDeDatos(connection);
                    esquemaInicializado = true;
                }
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar con la base de datos: " + e.getMessage(), e);
        }
    }

    public synchronized void cerrar() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error al cerrar la conexión de base de datos: " + e.getMessage(), e);
            } finally {
                connection = null;
            }
        }
    }

    private Properties cargarPropiedades() {
        Properties props = new Properties();
        try (InputStream is = ConexionBD.class.getResourceAsStream(ARCHIVO_PROPIEDADES)) {
            if (is != null) {
                props.load(new InputStreamReader(is, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al leer " + ARCHIVO_PROPIEDADES + ": " + e.getMessage(), e);
        }
        return props;
    }

    private String obtenerValor(String envVar, Properties props, String propKey) {
        String desdeEnv = System.getenv(envVar);
        if (desdeEnv != null && !desdeEnv.isBlank()) {
            return desdeEnv.trim();
        }
        String desdeProp = props.getProperty(propKey);
        if (desdeProp != null && !desdeProp.isBlank()) {
            return desdeProp.trim();
        }
        throw new IllegalStateException(
                "Falta la propiedad '" + propKey + "' en " + ARCHIVO_PROPIEDADES + " (o variable de entorno " + envVar + ")."
        );
    }

    private void inicializarBaseDeDatos(Connection conn) throws SQLException {
        ejecutarScriptSQL(conn, "/schema.sql");
        if (estaCatalogoVacio(conn)) {
            ejecutarScriptSQL(conn, "/data.sql");
        }
    }

    private boolean estaCatalogoVacio(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM pistas_catalogo")) {
            return rs.next() && rs.getInt(1) == 0;
        }
    }

    private void ejecutarScriptSQL(Connection conn, String rutaRecurso) throws SQLException {
        InputStream is = ConexionBD.class.getResourceAsStream(rutaRecurso);
        if (is == null) {
            throw new RuntimeException("No se encontró el script SQL en resources: " + rutaRecurso);
        }

        StringBuilder contenido = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String limpia = linea.trim();
                if (!limpia.startsWith("--")) {
                    contenido.append(linea).append('\n');
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el script SQL " + rutaRecurso + ": " + e.getMessage(), e);
        }

        List<String> sentencias = dividirSentenciasSQL(contenido.toString());
        try (Statement stmt = conn.createStatement()) {
            for (String sql : sentencias) {
                stmt.execute(sql);
            }
        }
    }

    private List<String> dividirSentenciasSQL(String script) {
        List<String> sentencias = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        boolean enComillaSimple = false;

        for (int i = 0; i < script.length(); i++) {
            char c = script.charAt(i);
            if (c == '\'') {
                actual.append(c);
                if (enComillaSimple && i + 1 < script.length() && script.charAt(i + 1) == '\'') {
                    actual.append(script.charAt(++i));
                } else {
                    enComillaSimple = !enComillaSimple;
                }
            } else if (c == ';' && !enComillaSimple) {
                String sql = actual.toString().trim();
                if (!sql.isEmpty()) {
                    sentencias.add(sql);
                }
                actual.setLength(0);
            } else {
                actual.append(c);
            }
        }

        String restante = actual.toString().trim();
        if (!restante.isEmpty()) {
            sentencias.add(restante);
        }
        return sentencias;
    }
}
