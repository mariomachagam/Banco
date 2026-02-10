package bancoservidor.persistencia;

import bancoservidor.Cliente;
import bancoservidor.CuentaBancaria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public ClienteDAO() {
        crearTablaUsuarios();
        crearTablaCuentas();
    }

    private void crearTablaUsuarios() {
        String sql = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT NOT NULL," +
                "username TEXT NOT NULL UNIQUE," +
                "password_hash TEXT NOT NULL," +
                "creado_en DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ");";
        try (Connection conn = ConexionBBDD.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean añadirUsuario(String nombre, String username, String passwordPlain) {
        String sql = "INSERT INTO usuarios(nombre, username, password_hash) VALUES(?,?,?)";
        String hash = PasswordUtils.hashPassword(passwordPlain);

        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, username);
            ps.setString(3, hash);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al añadir usuario: " + e.getMessage());
            return false;
        }
    }

    public Cliente getUsuarioPorUsername(String username) {
        String sql = "SELECT username, password_hash FROM usuarios WHERE username = ?";
        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Cliente c = new Cliente();
                    c.setUsuario(rs.getString("username"));
                    c.setPasswordHash(rs.getString("password_hash"));
                    return c;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean validarCredenciales(String username, String passwordPlain) {
        Cliente c = getUsuarioPorUsername(username);
        if (c == null) return false;
        String hash = PasswordUtils.hashPassword(passwordPlain);
        return hash.equals(c.getPasswordHash());
    }

    private void crearTablaCuentas() {
        String sql = "CREATE TABLE IF NOT EXISTS cuentas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "usuario TEXT NOT NULL," +
                "iban TEXT NOT NULL UNIQUE," +
                "saldo REAL NOT NULL," +
                "FOREIGN KEY(usuario) REFERENCES usuarios(username)" +
                ");";
        try (Connection conn = ConexionBBDD.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean añadirCuenta(String usuario, CuentaBancaria cuenta) {
        String sql = "INSERT INTO cuentas(usuario, iban, saldo) VALUES(?,?,?)";
        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, cuenta.getIban());
            ps.setDouble(3, cuenta.getSaldo());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public CuentaBancaria getCuentaPorUsuario(String usuario) {
        String sql = "SELECT iban, saldo FROM cuentas WHERE usuario = ?";
        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String iban = rs.getString("iban");
                    double saldo = rs.getDouble("saldo");
                    return new CuentaBancaria(saldo, iban);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean actualizarSaldo(String usuario, double nuevoSaldo) {
        String sql = "UPDATE cuentas SET saldo = ? WHERE usuario = ?";
        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, nuevoSaldo);
            ps.setString(2, usuario);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> listarUsuarios() {
        List<String> usuarios = new ArrayList<>();
        String sql = "SELECT username FROM usuarios";
        try (Connection conn = ConexionBBDD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) usuarios.add(rs.getString("username"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }
}
