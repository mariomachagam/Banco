package bancoservidor;

import java.net.Socket;
import java.io.*;
import bancoservidor.persistencia.ServicioPersistencia;

public class ClienteHandler implements Runnable {

    private Socket socket;
    private GestorClientes gestorClientes;

    public ClienteHandler(Socket socket, GestorClientes gestorClientes) {
        this.socket = socket;
        this.gestorClientes = gestorClientes;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // --- LOGIN ---
            out.println("USUARIO:");
            String usuario = in.readLine();

            out.println("PASSWORD:");
            String password = in.readLine();

            if (!ServicioPersistencia.validarLogin(usuario, password)) {
                out.println("LOGIN_ERROR");
                // No cerramos el socket aquí para que el cliente pueda mostrar el mensaje
                return;
            }

            CuentaBancaria cuenta = ServicioPersistencia.getCuenta(usuario);
            if (cuenta == null) {
                out.println("ERROR_CUENTA");
                return;
            }

            out.println("LOGIN_OK");
            out.println("IBAN " + cuenta.getIban());
            out.println("SALDO " + cuenta.getSaldo());

            // --- BUCLE DE COMANDOS ---
            String linea;
            while ((linea = in.readLine()) != null) {
                String[] partes = linea.split(" ");
                String comando = partes[0].toUpperCase();

                switch (comando) {
                    case "I":
                        double ingreso = Double.parseDouble(partes[1]);
                        cuenta.ingresar(ingreso);
                        ServicioPersistencia.actualizarSaldo(usuario, cuenta.getSaldo());
                        out.println("SALDO " + cuenta.getSaldo());
                        break;

                    case "R":
                        double retirada = Double.parseDouble(partes[1]);
                        boolean ok = cuenta.retirar(retirada);
                        if (ok) {
                            ServicioPersistencia.actualizarSaldo(usuario, cuenta.getSaldo());
                            out.println("SALDO " + cuenta.getSaldo());
                        } else {
                            out.println("SALDO_INSUFICIENTE");
                        }
                        break;

                    case "C":
                        out.println("SALDO " + cuenta.getSaldo());
                        break;

                    case "EXIT":
                        out.println("FIN");
                        socket.close();
                        return;

                    default:
                        out.println("COMANDO_DESCONOCIDO");
                }
            }

        } catch (Exception e) {
            System.err.println("Error con cliente: " + e.getMessage());
        }
    }
}

