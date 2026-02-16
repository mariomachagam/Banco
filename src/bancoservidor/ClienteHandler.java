package bancoservidor;

import bancoservidor.persistencia.ServicioPersistencia;
import java.io.*;
import java.net.Socket;

public class ClienteHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private GestorClientes gestor;

    public ClienteHandler(Socket socket, GestorClientes gestor) {
        this.socket = socket;
        this.gestor = gestor;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            boolean continuar = true;
            while (continuar) {
                // 1. Enviamos invitación de opción
                out.println("OPCION (LOGIN/REGISTRO/EXIT):");
                String opcion = in.readLine();

                if (opcion == null || opcion.equalsIgnoreCase("EXIT") || opcion.equalsIgnoreCase("SALIR")) {
                    continuar = false;
                    break;
                }

                if (opcion.equalsIgnoreCase("REGISTRO")) {
                    procesarRegistro();
                    // Al terminar procesarRegistro, el bucle while vuelve arriba
                    // y el cliente puede enviar "LOGIN" inmediatamente.
                } else if (opcion.equalsIgnoreCase("LOGIN")) {
                    procesarLogin();
                    // Si el login tiene éxito, el hilo entrará en la gestión de operaciones
                    // y luego morirá al salir el cliente.
                    continuar = false;
                }
            }
        } catch (IOException e) {
            System.err.println("Error en comunicación con cliente: " + e.getMessage());
        } finally {
            cerrarConexion();
        }
    }

    private void procesarRegistro() throws IOException {
        out.println("USUARIO:");
        String user = in.readLine();
        out.println("PASSWORD:");
        String pass = in.readLine();
        out.println("SALDO:");
        String saldoStr = in.readLine();

        try {
            double saldo = Double.parseDouble(saldoStr);
            boolean ok = ServicioPersistencia.registrarUsuario(user, pass, saldo);
            if (ok) {
                out.println("REGISTRO_OK");
                System.out.println("Nuevo usuario registrado: " + user);
            } else {
                out.println("REGISTRO_ERROR");
            }
        } catch (NumberFormatException e) {
            out.println("REGISTRO_ERROR_FORMATO");
        }
    }

    private void procesarLogin() throws IOException {
        out.println("USUARIO:");
        String user = in.readLine();
        out.println("PASSWORD:");
        String pass = in.readLine();

        if (ServicioPersistencia.validarLogin(user, pass)) {
            out.println("LOGIN_OK");

            // Enviamos datos iniciales al BancoFrame
            CuentaBancaria cuenta = ServicioPersistencia.getCuenta(user);
            out.println("IBAN " + cuenta.getIban());
            out.println("SALDO " + cuenta.getSaldo());

            // --- BUCLE DE OPERACIONES BANCARIAS ---
            // Una vez logueado, este hilo se queda aquí gestionando ingresos/retiradas
            new OperacionBancaria(socket, in, out, user).gestionar();

        } else {
            out.println("LOGIN_ERROR");
        }
    }

    private void cerrarConexion() {
        try {
            if (socket != null) socket.close();
            System.out.println("Conexión cerrada con el cliente.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}