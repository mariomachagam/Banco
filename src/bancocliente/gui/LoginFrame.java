package bancocliente.gui;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class LoginFrame extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblEstado;

    public LoginFrame() {

        setTitle("Login Banco");
        setSize(350, 200);
        setLocationRelativeTo(null); // centrar ventana
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponentes();

        setVisible(true);
    }

    private void initComponentes() {

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2, 5, 5));

        panel.add(new JLabel("Usuario:"));
        txtUsuario = new JTextField();
        panel.add(txtUsuario);

        panel.add(new JLabel("Contraseña:"));
        txtPassword = new JPasswordField();
        panel.add(txtPassword);

        btnLogin = new JButton("Entrar");
        panel.add(btnLogin);

        lblEstado = new JLabel(" ");
        lblEstado.setForeground(Color.RED);
        panel.add(lblEstado);

        add(panel);

        // Acción del botón
        btnLogin.addActionListener(e -> hacerLogin());
    }

    private void hacerLogin() {

        String usuario = txtUsuario.getText();
        String password = new String(txtPassword.getPassword());

        if (usuario.isEmpty() || password.isEmpty()) {
            lblEstado.setText("Rellena todos los campos");
            return;
        }

        // Login en un hilo separado (NO bloquear la GUI)
        new Thread(() -> {

            try (
                    Socket socket = new Socket("localhost", 6000);
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
                    PrintWriter out = new PrintWriter(
                            socket.getOutputStream(), true)
            ) {

                // Espera "USUARIO:"
                in.readLine();
                out.println(usuario);

                // Espera "PASSWORD:"
                in.readLine();
                out.println(password);

                String respuesta = in.readLine();

                if ("LOGIN_OK".equals(respuesta)) {

                    String iban = in.readLine();
                    String saldo = in.readLine();

                    // Actualización SEGURA de la GUI
                    SwingUtilities.invokeLater(() -> {
                        dispose(); // cerrar login
                        new BancoFrame(socket, in, out, iban, saldo);
                    });

                } else {
                    SwingUtilities.invokeLater(() ->
                            lblEstado.setText("Usuario o contraseña incorrectos")
                    );
                }

            } catch (Exception ex) {
                SwingUtilities.invokeLater(() ->
                        lblEstado.setText("No se puede conectar al servidor")
                );
            }

        }).start();
    }
}

