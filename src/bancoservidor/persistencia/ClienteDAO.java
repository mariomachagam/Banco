package bancoservidor.persistencia;

import bancoservidor.CuentaBancaria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public ClienteDAO() {
        crearTablaUnica();
    }

    private void crearTablaUnica() {
        String sql = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL," +
                "saldo REAL NOT NULL" +
                ");";
        try (Connection conn = ConexionBBDD.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean añadirUsuario(String user, String password, double saldo) {
        String sql = "INSERT INTO usuarios(user, password, saldo) VALUES(?,?,?)";
        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user);
            ps.setString(2, password);
            ps.setDouble(3, saldo);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean validarCredenciales(String user, String passwordPlain) {
        String sql = "SELECT password FROM usuarios WHERE user = ?";
        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return passwordPlain.equals(rs.getString("password"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public CuentaBancaria getCuentaPorUsuario(String user) {
        String sql = "SELECT saldo FROM usuarios WHERE user = ?";
        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new CuentaBancaria(rs.getDouble("saldo"), "ES-" + user.toUpperCase());
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean actualizarSaldo(String user, double nuevoSaldo) {
        String sql = "UPDATE usuarios SET saldo = ? WHERE user = ?";
        try (Connection conn = ConexionBBDD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, nuevoSaldo);
            ps.setString(2, user);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { return false; }
    }

    // ESTE MÉTODO ES EL QUE NECESITA TU SERVICIO PERSISTENCIA
    public List<String> listarUsuarios() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT user FROM usuarios";
        try (Connection conn = ConexionBBDD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(rs.getString("user"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }
}