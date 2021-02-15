package CSVTranslator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
