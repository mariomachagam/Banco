package bancoservidor;

import bancoservidor.persistencia.ServicioPersistencia;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteHandler implements Runnable {
    private Socket socket;
    private GestorClientes gestorClientes;

    public ClienteHandler(Socket socket, GestorClientes gestorClientes) {
        this.socket = socket;
        this.gestorClientes = gestorClientes;
    }

    @Override
    public void run() {
        try (Socket s = this.socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
             PrintWriter out = new PrintWriter(s.getOutputStream(), true)) {

            // --- 1. PROTOCOLO INICIAL ---
            out.println("OPCION (LOGIN/REGISTRO):");
            String opcion = in.readLine();
            if (opcion == null) return;

            // CASO REGISTRO: Solo pedimos user y password
            if (opcion.equalsIgnoreCase("REGISTRO")) {
                out.println("USUARIO:");
                String user = in.readLine();
                out.println("PASSWORD:");
                String pass = in.readLine();

                // CORRECCIÓN: Pasamos user, pass y el saldo inicial como double
                out.println("SALDO:");
                String saldoString = in.readLine(); // Recibe el String del cliente
                double saldo = Double.parseDouble(saldoString); // Lo convierte a double
                boolean registrado = ServicioPersistencia.registrarUsuario(user, pass, saldo);

                if (registrado) {
                    out.println("REGISTRO_OK");
                } else {
                    out.println("REGISTRO_ERROR");
                }
                return;
            }

            // CASO LOGIN
            out.println("USUARIO:");
            String usuario = in.readLine();
            out.println("PASSWORD:");
            String password = in.readLine();

            if (usuario == null || password == null || !ServicioPersistencia.validarLogin(usuario, password)) {
                out.println("LOGIN_ERROR");
                return;
            }

            // Obtenemos la cuenta (que ahora solo tiene el saldo según tu tabla)
            CuentaBancaria cuenta = ServicioPersistencia.getCuenta(usuario);
            if (cuenta == null) {
                out.println("ERROR_CUENTA");
                return;
            }

            out.println("LOGIN_OK");
            // Enviamos un IBAN ficticio o vacío ya que tu tabla no tiene ese campo
            out.println("IBAN " + usuario.toUpperCase() + "001");
            out.println("SALDO " + cuenta.getSaldo());

            // --- 2. BUCLE DE OPERACIONES ---
            String linea;
            while ((linea = in.readLine()) != null) {
                String[] partes = linea.split(" ");
                String comando = partes[0].toUpperCase();

                switch (comando) {
                    case "I": // INGRESAR
                        if (partes.length > 1) {
                            double monto = Double.parseDouble(partes[1]);
                            cuenta.ingresar(monto);
                            ServicioPersistencia.actualizarSaldo(usuario, cuenta.getSaldo());
                            out.println("SALDO " + cuenta.getSaldo());
                        }
                        break;
                    case "R": // RETIRAR
                        if (partes.length > 1) {
                            double monto = Double.parseDouble(partes[1]);
                            if (cuenta.retirar(monto)) {
                                ServicioPersistencia.actualizarSaldo(usuario, cuenta.getSaldo());
                                out.println("SALDO " + cuenta.getSaldo());
                            } else {
                                out.println("SALDO_INSUFICIENTE");
                            }
                        }
                        break;
                    case "C": // CONSULTAR
                        out.println("SALDO " + cuenta.getSaldo());
                        break;
                    case "SALIR":
                        out.println("FIN");
                        return;
                    default:
                        out.println("COMANDO_DESCONOCIDO");
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error en ClienteHandler: " + e.getMessage());
        }
    }
}