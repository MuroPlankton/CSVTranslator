package CSVTranslator;

import CSVTranslator.auth.AuthHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

public class LogInAndSignIn {

    private JPanel mainPanel;
    private JFrame frame;
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

    private final Runnable runUI = this::createUI;

    public final Runnable runUI() {
        return runUI;
    }

    private void createUI() {
        frame = new JFrame("LogInAndSignIn");
        frame.setContentPane(new LogInAndSignIn().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }

    public void dispose() { frame.dispose(); }

    public LogInAndSignIn() {
        AuthHelper authHelper = AuthHelper.getInstance();

        LogInBtn.addActionListener(actionEvent -> {
            String email = userNameOrEmailTextField.getText();
            String password = String.valueOf(logInPasswordField.getPassword());

            authHelper.logExistingUserIn(email, password);
        });

        linkToSignIn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//        linkToSignIn.addMouseListener(new MouseListener() {
//            @Override
//            public void mouseClicked(MouseEvent mouseEvent) {
//
//            }
//
//            @Override
//            public void mousePressed(MouseEvent mouseEvent) {
//
//            }
//
//            @Override
//            public void mouseReleased(MouseEvent mouseEvent) {
//
//            }
//
//            @Override
//            public void mouseEntered(MouseEvent mouseEvent) {
//
//            }
//
//            @Override
//            public void mouseExited(MouseEvent mouseEvent) {
//
//            }
//        });
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

        signInBtn.addActionListener(actionEvent -> {
            String userName;
            String email;
            String password;

            if (userNameTextField.getText() != null && emailTextField.getText() != null) {
                userName = userNameTextField.getText();
                email = emailTextField.getText();

                if (passwordField1.getPassword() != null && passwordField2.getPassword() != null) {
                    if (Arrays.equals(passwordField1.getPassword(), passwordField2.getPassword())) {
                        password = String.valueOf(passwordField1.getPassword());

                        authHelper.signNewUserIn(email, userName, password);
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
