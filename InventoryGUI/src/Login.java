import javax.swing.*;

public class Login {
    public static boolean authenticate() {
        String user = JOptionPane.showInputDialog("Enter Username:");
        String pass = JOptionPane.showInputDialog("Enter Password:");

        if ("admin".equals(user) && "1234".equals(pass)) {
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Invalid Credentials!");
            return false;
        }
    }
}