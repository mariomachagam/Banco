package bancoservidor;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServidorBanco {

    private static final int PUERTO = 6000;

    public static void main(String[] args) {

        System.out.println("=== SERVIDOR BANCO INICIADO ===");

        // Pool de hilos para atender clientes concurrentes
        ExecutorService pool = Executors.newCachedThreadPool();

        // Gestor de clientes compartido por todos los hilos
        GestorClientes gestorClientes = new GestorClientes();

        try (ServerSocket serverSocket = new ServerSocket(6000)) {

            System.out.println("Servidor escuchando en el puerto " + PUERTO);

            while (true) {
                Socket socketCliente = serverSocket.accept();
                System.out.println("Cliente conectado desde " +
                        socketCliente.getInetAddress());

                // Cada cliente se atiende en un hilo independiente
                pool.execute(new ClienteHandler(socketCliente,gestorClientes));
            }

        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }
}
