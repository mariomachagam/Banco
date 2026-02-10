package bancoservidor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GestorClientes {
    private Map<String, Cliente> clientesConectados = new ConcurrentHashMap<>();

    public GestorClientes() {}

    public void añadirCliente(String usuario, Cliente cliente) {
        clientesConectados.put(usuario, cliente);
    }

    public void eliminarCliente(String usuario) {
        clientesConectados.remove(usuario);
    }

    public boolean estaConectado(String usuario) {
        return clientesConectados.containsKey(usuario);
    }

    public int getTotalClientes() { return clientesConectados.size(); }
}
