package bancocliente;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.sql.Connection;


/**
 * Cliente bancario que se conecta al servidor mediante TCP.
 * Permite enviar comandos y recibir respuestas.
 */
public class ClienteBanco {

    public static void main(String[] args) throws Exception {

        // Conexión al servidor
        Socket socket = new Socket("localhost", 6000);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(
                socket.getOutputStream(), true);

        Scanner sc = new Scanner(System.in);

        // Hilo que escucha respuestas del servidor
        new Thread(() -> {
            try {
                String respuesta;
                while ((respuesta = in.readLine()) != null) {
                    System.out.println("Servidor: " + respuesta);
                }
            } catch (IOException e) {
                System.out.println("Conexión cerrada");
            }
        }).start();

        // Envío de comandos al servidor
        while (true) {
            String comando = sc.nextLine();
            out.println(comando);

            if (comando.equalsIgnoreCase("SALIR")) {
                break;
            }
        }

        socket.close();
    }
}
