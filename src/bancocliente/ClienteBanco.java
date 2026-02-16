package bancocliente;

import bancocliente.gui.LoginFrame;
import java.io.*;
import java.net.Socket;

public class ClienteBanco {

    public static void main(String[] args) {
        try {
            // 1. Conexión al servidor
            Socket socket = new Socket("localhost", 6000);

            // 2. Creamos los flujos
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // 3. LANZAMOS LA GUI (LoginFrame)
            // IMPORTANTE: Pasamos los 3 parámetros que el nuevo constructor de LoginFrame exige
            new LoginFrame(socket, in, out);

            // NOTA: Hemos eliminado el Thread de consola y el Scanner.
            // Ahora la ventana de Login tiene el control total.

        } catch (IOException e) {
            System.err.println("No se pudo conectar con el servidor. Revisa que ServidorBanco esté corriendo.");
            e.printStackTrace();
        }
    }
}