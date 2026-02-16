package bancoservidor.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBBDD {

    // URL para usar el archivo local SQLite
    private static final String DB_URL = "jdbc:sqlite:appbanco.db";

    static {
        try {
            // Cargamos el Driver de SQLite (el que ya tienes en descargas)
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver SQLite no encontrado: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        // SQLite no necesita USER ni PASSWORD
        return DriverManager.getConnection(DB_URL);
    }
}