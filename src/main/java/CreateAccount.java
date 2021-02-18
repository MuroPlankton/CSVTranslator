import javax.swing.*;
import java.util.Arrays;

public class CreateAccount{

    private JPanel mainPanel/* = new JPanel()*/;
    private JTextField userNameTextField;
    private JTextField emailTextField;
    private JPasswordField passwordField2;
    private JPasswordField passwordField1;
    private JLabel userNameLabel;
    private JLabel emailLabel;
    private JLabel passwordLabel;
    private JButton signInBtn;

    public CreateAccount() {
        signInBtn.addActionListener(e -> {
            String userName;
            String email;
            char[] password;

            if(userNameTextField.getText() != null && emailTextField.getText() != null) {
                userName = userNameTextField.getText();
                email = emailTextField.getText();

                if (passwordField1.getPassword() != null && passwordField2.getPassword() != null) {
                    if (Arrays.equals(passwordField1.getPassword(), passwordField2.getPassword())) {
                        password = passwordField1.getPassword();
                        System.out.println(userName + "\n" + email + "\n" + Arrays.toString(password));
                    } else {
                        System.out.println("Passwords don't match!");
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Create Account");
        frame.setContentPane(new CreateAccount().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
