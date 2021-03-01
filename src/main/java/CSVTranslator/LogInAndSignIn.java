package CSVTranslator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

public class LogInAndSignIn {

    private JPanel mainPanel;
    private JPanel signInPanel;
    private JPanel logInPanel;

    private JButton signInBtn;
    private JLabel signInUserNameLabel;
    private JLabel signInEmailLabel;
    private JLabel signInPasswordLabel;
    private JLabel linkToLogIn;
    private JTextField userNameTextField;
    private JTextField emailTextField;
    private JPasswordField passwordField1;
    private JPasswordField passwordField2;

    private JButton LogInBtn;
    private JLabel userNameOrEmailLabel;
    private JLabel logInPasswordLabel;
    private JLabel linkToSignIn;
    private JTextField userNameOrEmailTextField;
    private JPasswordField logInPasswordField;

    public LogInAndSignIn() {
        {
            LogInBtn.addActionListener(actionEvent -> {
                //TODO: call log in method
            });

            linkToSignIn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            linkToSignIn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    //TODO: see why the click doesn't always register
                    CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
                    cardLayout.next(mainPanel);
                    userNameOrEmailTextField.setText("");
                    logInPasswordField.setText("");
                }
            });
        }

        {
            signInBtn.addActionListener(actionEvent -> {
                //TODO: call sign in method
                String userName;
                String email;
                char[] password;

                if (userNameTextField.getText() != null && emailTextField.getText() != null) {
                    userName = userNameTextField.getText();
                    email = emailTextField.getText();

                    if (passwordField1.getPassword() != null && passwordField2.getPassword() != null) {
                        if (Arrays.equals(passwordField1.getPassword(), passwordField2.getPassword())) {
                            password = passwordField1.getPassword();
                            System.out.println(userName + "\n" + email + "\n" + Arrays.toString(password));
                        } else {
                            System.out.println("Passwords don't match!");
                            JOptionPane.showMessageDialog(mainPanel, "Passwords don't match!");
                        }
                    }
                }
            });

            linkToLogIn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            linkToLogIn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    //TODO: see why the click doesn't always register
                    CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
                    cardLayout.next(mainPanel);
                    userNameTextField.setText("");
                    emailTextField.setText("");
                    passwordField1.setText("");
                    passwordField2.setText("");
                }
            });
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("LogInAndSignIn");
        frame.setContentPane(new LogInAndSignIn().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
