package bancocliente.gui;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class LoginFrame extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnRegistro;
    private JLabel lblEstado;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public LoginFrame(Socket socket, BufferedReader in, PrintWriter out) {
        this.socket = socket;
        this.in = in;
        this.out = out;

        setTitle("Login Banco - SQL");
        setSize(350, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initComponentes();
        setVisible(true);
    }

    private void initComponentes() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Usuario:"));
        txtUsuario = new JTextField();
        panel.add(txtUsuario);

        panel.add(new JLabel("Contraseña:"));
        txtPassword = new JPasswordField();
        panel.add(txtPassword);

        btnLogin = new JButton("Entrar");
        btnRegistro = new JButton("Registrar Nuevo");

        panel.add(btnLogin);
        panel.add(btnRegistro);

        lblEstado = new JLabel(" ");
        lblEstado.setForeground(Color.RED);
        panel.add(lblEstado);

        add(panel);

        btnLogin.addActionListener(e -> hacerLogin());
        btnRegistro.addActionListener(e -> hacerRegistro());
    }

    private void hacerLogin() {
        String user = txtUsuario.getText();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            lblEstado.setText("Rellena todos los campos");
            return;
        }

        new Thread(() -> {
            try {
                in.readLine(); // "OPCION..."
                out.println("LOGIN");

                in.readLine(); // "USUARIO:"
                out.println(user);

                in.readLine(); // "PASSWORD:"
                out.println(pass);

                String respuesta = in.readLine();

                if ("LOGIN_OK".equals(respuesta)) {
                    String ibanRaw = in.readLine();
                    String saldoRaw = in.readLine();

                    String iban = ibanRaw.replace("IBAN ", "");
                    String saldo = saldoRaw.replace("SALDO ", "");

                    SwingUtilities.invokeLater(() -> {
                        new BancoFrame(socket, in, out, iban, saldo);
                        this.dispose();
                    });
                } else {
                    SwingUtilities.invokeLater(() -> lblEstado.setText("Credenciales incorrectas"));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> lblEstado.setText("Error de conexión"));
            }
        }).start();
    }

    private void hacerRegistro() {
        String user = txtUsuario.getText();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            lblEstado.setText("Introduce datos para registrar");
            return;
        }

        // Pedir el saldo solo para el registro mediante un JOptionPane
        String saldoInicial = JOptionPane.showInputDialog(this, "Introduce el saldo inicial para la cuenta:");

        if (saldoInicial == null || saldoInicial.isEmpty()) {
            lblEstado.setText("Registro cancelado: falta saldo");
            return;
        }

        new Thread(() -> {
            try {
                // 1. Enviar Opción
                in.readLine();
                out.println("REGISTRO");

                // 2. Enviar Usuario
                in.readLine();
                out.println(user);

                // 3. Enviar Password
                in.readLine();
                out.println(pass);

                // 4. Enviar Saldo (Nueva interacción con el Handler modificado)
                in.readLine(); // Lee "SALDO:" enviado por el Handler
                out.println(saldoInicial);

                // 5. Leer resultado
                String res = in.readLine();

                if ("REGISTRO_OK".equals(res)) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "¡Usuario " + user + " registrado con " + saldoInicial + "€!");
                        txtUsuario.setText("");
                        txtPassword.setText("");
                        lblEstado.setText("Registrado correctamente");
                    });
                } else {
                    SwingUtilities.invokeLater(() -> lblEstado.setText("Error: El usuario ya existe"));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> lblEstado.setText("Error de comunicación"));
            }
        }).start();
    }
}