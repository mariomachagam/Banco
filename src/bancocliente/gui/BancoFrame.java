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
    private JTextArea txtListaUsuarios; // Para el refresco automático
    private Timer timerRefresco;

    public BancoFrame(Socket socket, BufferedReader in, PrintWriter out,
                      String iban, String saldo) {

        this.socket = socket;
        this.in = in;
        this.out = out;

        setTitle("Banco - Cliente");
        setSize(600, 350); // Aumentamos un poco el ancho para la lista lateral
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponentes(iban, saldo);
        iniciarRefrescoAutomatico(); // Activamos el temporizador

        setVisible(true);
    }

    private void initComponentes(String iban, String saldo) {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- PANEL CENTRAL (INFO Y BOTONES) ---
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));

        JPanel panelInfo = new JPanel(new GridLayout(2, 1));
        lblIban = new JLabel("IBAN: " + iban);
        lblIban.setFont(new Font("Arial", Font.BOLD, 14));
        lblSaldo = new JLabel("Saldo: " + saldo);
        lblSaldo.setForeground(new Color(0, 128, 0));
        panelInfo.add(lblIban);
        panelInfo.add(lblSaldo);
        panelCentral.add(panelInfo, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new GridLayout(2, 3, 10, 10));
        JButton btnIngresar = new JButton("Ingresar");
        JButton btnRetirar = new JButton("Retirar");
        JButton btnConsultar = new JButton("Consultar");
        JButton btnUsuarios = new JButton("Refrescar Ya");
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        JButton btnSalir = new JButton("Salir");

        panelBotones.add(btnIngresar);
        panelBotones.add(btnRetirar);
        panelBotones.add(btnConsultar);
        panelBotones.add(btnUsuarios);
        panelBotones.add(btnCerrarSesion);
        panelBotones.add(btnSalir);
        panelCentral.add(panelBotones, BorderLayout.CENTER);

        // --- PANEL LATERAL (LISTA AUTOMÁTICA) ---
        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.setPreferredSize(new Dimension(150, 0));
        panelDerecho.add(new JLabel("Usuarios Online:"), BorderLayout.NORTH);

        txtListaUsuarios = new JTextArea();
        txtListaUsuarios.setEditable(false);
        txtListaUsuarios.setBackground(new Color(240, 240, 240));
        panelDerecho.add(new JScrollPane(txtListaUsuarios), BorderLayout.CENTER);

        panelPrincipal.add(panelCentral, BorderLayout.CENTER);
        panelPrincipal.add(panelDerecho, BorderLayout.EAST);
        add(panelPrincipal);

        // --- ACCIONES ---
        btnIngresar.addActionListener(e -> operar("I"));
        btnRetirar.addActionListener(e -> operar("R"));
        btnConsultar.addActionListener(e -> enviarComando("C"));
        btnUsuarios.addActionListener(e -> listarUsuarios()); // Refresco manual si se desea
        btnCerrarSesion.addActionListener(e -> cerrarSesionYVolver());
        btnSalir.addActionListener(e -> salirCompletamente());
    }

    private void iniciarRefrescoAutomatico() {
        // Cada 5 segundos actualiza la lista lateral
        timerRefresco = new Timer(5000, e -> {
            new Thread(() -> {
                try {
                    List<String> usuarios = bancoservidor.persistencia.ServicioPersistencia.obtenerUsuarios();
                    String texto = String.join("\n", usuarios);
                    SwingUtilities.invokeLater(() -> txtListaUsuarios.setText(texto));
                } catch (Exception ignored) {}
            }).start();
        });
        timerRefresco.start();
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
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Saldo insuficiente"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void cerrarSesionYVolver() {
        if (timerRefresco != null) timerRefresco.stop();
        try {
            out.println("SALIR");
            socket.close();
        } catch (Exception ignored) {}
        dispose();
        try {
            Socket nuevoSocket = new Socket("localhost", 6000);
            BufferedReader nuevoIn = new BufferedReader(new java.io.InputStreamReader(nuevoSocket.getInputStream()));
            PrintWriter nuevoOut = new PrintWriter(nuevoSocket.getOutputStream(), true);
            new LoginFrame(nuevoSocket, nuevoIn, nuevoOut);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void salirCompletamente() {
        if (timerRefresco != null) timerRefresco.stop();
        try {
            out.println("SALIR");
            socket.close();
        } catch (Exception ignored) {}
        System.exit(0);
    }

    private void listarUsuarios() {
        new Thread(() -> {
            try {
                List<String> usuarios = bancoservidor.persistencia.ServicioPersistencia.obtenerUsuarios();
                String texto = String.join("\n", usuarios);
                SwingUtilities.invokeLater(() -> txtListaUsuarios.setText(texto));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
}