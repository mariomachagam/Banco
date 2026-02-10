package bancocliente.gui;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class BancoFrame extends JFrame {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private JLabel lblIban;
    private JLabel lblSaldo;

    public BancoFrame(Socket socket, BufferedReader in, PrintWriter out,
                      String iban, String saldo) {

        this.socket = socket;
        this.in = in;
        this.out = out;

        setTitle("Banco - Cliente");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponentes(iban, saldo);

        setVisible(true);
    }

    private void initComponentes(String iban, String saldo) {

        JPanel panel = new JPanel(new BorderLayout());

        // --- PANEL SUPERIOR ---
        JPanel panelInfo = new JPanel(new GridLayout(2, 1));
        lblIban = new JLabel("IBAN: " + iban);
        lblSaldo = new JLabel("Saldo: " + saldo);
        panelInfo.add(lblIban);
        panelInfo.add(lblSaldo);
        panel.add(panelInfo, BorderLayout.NORTH);

        // --- PANEL BOTONES ---
        JPanel panelBotones = new JPanel(new GridLayout(2, 3, 10, 10));
        JButton btnIngresar = new JButton("Ingresar");
        JButton btnRetirar = new JButton("Retirar");
        JButton btnConsultar = new JButton("Consultar saldo");
        JButton btnSalir = new JButton("Salir");
        JButton btnListarUsuarios = new JButton("Usuarios");

        panelBotones.add(btnIngresar);
        panelBotones.add(btnRetirar);
        panelBotones.add(btnConsultar);
        panelBotones.add(btnSalir);
        panelBotones.add(btnListarUsuarios);

        panel.add(panelBotones, BorderLayout.CENTER);
        add(panel);

        // --- ACCIONES ---
        btnIngresar.addActionListener(e -> operar("I"));
        btnRetirar.addActionListener(e -> operar("R"));
        btnConsultar.addActionListener(e -> enviarComando("C"));
        btnSalir.addActionListener(e -> cerrarSesion());
        btnListarUsuarios.addActionListener(e -> listarUsuarios());
    }

    private void operar(String tipo) {
        String cantidad = JOptionPane.showInputDialog(this, "Introduce la cantidad:");
        if (cantidad == null || cantidad.isEmpty()) return;
        enviarComando(tipo + " " + cantidad);
    }

    private void enviarComando(String comando) {
        new Thread(() -> {
            try {
                out.println(comando);
                String respuesta = in.readLine();

                if (respuesta.startsWith("SALDO")) {
                    SwingUtilities.invokeLater(() -> lblSaldo.setText(respuesta));
                } else if ("SALDO_INSUFICIENTE".equals(respuesta)) {
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(this, "Saldo insuficiente",
                                    "Error", JOptionPane.ERROR_MESSAGE));
                } else if ("FIN".equals(respuesta)) {
                    SwingUtilities.invokeLater(this::dispose);
                }

            } catch (Exception e) {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this, "Error de comunicación con el servidor",
                                "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }


    private void cerrarSesion() {
        try {
            out.println("EXIT");
            socket.close();
        } catch (Exception ignored) {}
        dispose();
        System.exit(0);
    }

    private void listarUsuarios() {
        new Thread(() -> {
            try {
                List<String> usuarios = bancoservidor.persistencia.ServicioPersistencia.obtenerUsuarios();
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this,
                                String.join("\n", usuarios),
                                "Usuarios en la BBDD",
                                JOptionPane.INFORMATION_MESSAGE));
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this,
                                "Error al obtener usuarios",
                                "Error",
                                JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }
}
