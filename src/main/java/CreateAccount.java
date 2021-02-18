import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class CreateAccount {

    private JPanel MainPanel;
    private JTextField userNameTextField;
    private JTextField emailTextField;
    private JPasswordField passwordField2;
    private JPasswordField passwordField1;
    private JLabel userNameLabel;
    private JLabel emailLabel;
    private JLabel passwordLabel;
    private JButton SignInBtn;

    public CreateAccount() {
        String userName = userNameTextField.getText();
        String email = emailTextField.getText();

        if (Arrays.equals(passwordField1.getPassword(), passwordField2.getPassword())) {
            char[] password = passwordField1.getPassword();
        } else {
            System.out.println("Passwords don't match!");
        }

//        SignInBtn.setVisible(false);

        SignInBtn.addActionListener(e -> {

        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Create Account");
        frame.setContentPane(new CreateAccount().MainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
