package bancocliente.gui;

import javax.swing.SwingUtilities;

public class MainLogin {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
}
