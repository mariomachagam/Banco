package bancoservidor;

import bancoservidor.persistencia.ServicioPersistencia;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Gestiona las operaciones bancarias solicitadas por el cliente a través del socket.
 */
public class OperacionBancaria {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String usuario;

    /**
     * Constructor actualizado para recibir la comunicación y el usuario actual.
     */
    public OperacionBancaria(Socket socket, BufferedReader in, PrintWriter out, String usuario) {
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.usuario = usuario;
    }

    /**
     * Bucle principal que escucha los comandos enviados desde el BancoFrame.
     */
    public void gestionar() {
        try {
            String linea;
            // Escuchamos comandos como "I 100", "R 50" o "C"
            while ((linea = in.readLine()) != null) {
                if (linea.equalsIgnoreCase("SALIR") || linea.equalsIgnoreCase("EXIT")) {
                    break;
                }

                String[] partes = linea.split(" ");
                String comando = partes[0];

                // Obtenemos la cuenta actualizada de la base de datos
                CuentaBancaria cuenta = ServicioPersistencia.getCuenta(usuario);

                switch (comando) {
                    case "C": // Consultar Saldo
                        out.println("SALDO " + cuenta.getSaldo() + "€");
                        break;

                    case "I": // Ingresar
                        if (partes.length > 1) {
                            double cantidad = Double.parseDouble(partes[1]);
                            cuenta.ingresar(cantidad);
                            // IMPORTANTE: Guardamos el nuevo saldo en SQLite
                            ServicioPersistencia.actualizarSaldo(usuario, cuenta.getSaldo());
                            out.println("SALDO " + cuenta.getSaldo() + "€");
                        }
                        break;

                    case "R": // Retirar
                        if (partes.length > 1) {
                            double cantidad = Double.parseDouble(partes[1]);
                            if (cuenta.getSaldo() >= cantidad) {
                                cuenta.retirar(cantidad);
                                // IMPORTANTE: Guardamos el nuevo saldo en SQLite
                                ServicioPersistencia.actualizarSaldo(usuario, cuenta.getSaldo());
                                out.println("SALDO " + cuenta.getSaldo() + "€");
                            } else {
                                out.println("SALDO_INSUFICIENTE");
                            }
                        }
                        break;
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error en operaciones de " + usuario + ": " + e.getMessage());
        }
    }
}