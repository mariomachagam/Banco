package bancoservidor.persistencia;

import bancoservidor.CuentaBancaria;
import java.util.List;

public class ServicioPersistencia {

    private static ClienteDAO clienteDAO = new ClienteDAO();

    public static boolean registrarUsuario(String user, String password, double saldo) {
        return clienteDAO.añadirUsuario(user, password, saldo);
    }

    public static boolean validarLogin(String usuario, String password) {
        return clienteDAO.validarCredenciales(usuario, password);
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

    // Si no usas registrarCuenta, puedes borrarlo o dejarlo así para que no falle el resto del código
    public static boolean registrarCuenta(String usuario, CuentaBancaria cuenta) {
        return true;
    }
}