package bancoservidor.persistencia;

import bancoservidor.CuentaBancaria;
import java.util.List;

public class ServicioPersistencia {

    private static ClienteDAO clienteDAO = new ClienteDAO();

    public static boolean registrarUsuario(String nombre, String usuario, String password) {
        return clienteDAO.añadirUsuario(nombre, usuario, password);
    }

    public static boolean validarLogin(String usuario, String password) {
        return clienteDAO.validarCredenciales(usuario, password);
    }

    public static boolean registrarCuenta(String usuario, CuentaBancaria cuenta) {
        return clienteDAO.añadirCuenta(usuario, cuenta);
    }

    public static CuentaBancaria getCuenta(String usuario) {
        return clienteDAO.getCuentaPorUsuario(usuario);
    }

    public static boolean actualizarSaldo(String usuario, double nuevoSaldo) {
        return clienteDAO.actualizarSaldo(usuario, nuevoSaldo);
    }

    public static List<String> obtenerUsuarios() {
        return clienteDAO.listarUsuarios();
    }
}
