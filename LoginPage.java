import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton registerStaffButton;

    public static class CurrentUser {
        private static CurrentUser instance;
        private String username;

        private CurrentUser() {
        }

        public static CurrentUser getInstance() {
            if (instance == null) {
                instance = new CurrentUser();
            }
            return instance;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    public LoginPage() {
        // Set up the frame
        setTitle("Parking Management System - Login");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        // Set light blue background
        getContentPane().setBackground(new Color(173, 216, 230)); // Light blue color

        // Create UI components
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = createTextField();

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = createPasswordField();

        // Use non-static createStyledButton
        loginButton = createStyledButton("Login");
        registerButton = createStyledButton("Register as Customer");
        registerStaffButton = createStyledButton("Register as Staff");

        // Set Font Styles
        Font font = new Font("Arial", Font.BOLD, 14);
        usernameLabel.setFont(font);
        passwordLabel.setFont(font);
        usernameField.setFont(font);
        passwordField.setFont(font);
        loginButton.setFont(font);
        registerButton.setFont(font);
        registerStaffButton.setFont(font);

        // Set GridBag Constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding for all sides
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Span across two columns
        add(loginButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2; // Span across two columns
        add(registerButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; // Span across two columns
        add(registerStaffButton, gbc);

        // Add action listener to the login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Authenticate the user
                boolean isAuthenticated = DatabaseHandler.authenticateCustomer(username, password);
                boolean isAuthenticatedStaff = DatabaseHandler.authenticateStaff(username,password);

                if (isAuthenticated) {
                    // Store the username in the CurrentUser singleton
                    CurrentUser.getInstance().setUsername(username);
                    JOptionPane.showMessageDialog(LoginPage.this, "Login successful! Welcome, " + username + "!");
                    new BookParkingPage(username).setVisible(true); // Pass username to BookParkingPage
                    dispose(); // Close login page
                } else if (isAuthenticatedStaff) {
                    // Store the username in the CurrentUser singleton
                    CurrentUser.getInstance().setUsername(username);
                    JOptionPane.showMessageDialog(LoginPage.this, "Login successful! Welcome, " + username + "!");
//                    DatabaseHandler.authenticateStaff( username,  password);
                    dispose(); // Close login page
                } else {
                    JOptionPane.showMessageDialog(LoginPage.this, "Login failed! Please check your username and password.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Add action listener to the register button
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RegisterPage().setVisible(true); // Open registration page
                dispose(); // Close login page
            }
        });

        // Add action listener to the register staff button
        registerStaffButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RegisterStaff().setVisible(true); // Open staff registration page
                dispose(); // Close login page
            }
        });
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField(20);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10) // Internal padding
        ));
        textField.setOpaque(true); // Make background visible
        textField.setBackground(Color.WHITE);
        return textField;
    }

    private JPasswordField createPasswordField() {
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10) // Internal padding
        ));
        passwordField.setOpaque(true); // Make background visible
        passwordField.setBackground(Color.WHITE);
        return passwordField;
    }

    public static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0, 0, 139)); // Dark blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false); // Remove focus outline
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1, true),
                BorderFactory.createEmptyBorder(10, 20, 10, 20) // Internal padding for button
        ));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(button.getBackground().brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 0, 139));
            }
        });
        return button;
    }


    public static void main(String[] args) {
        // Run the login interface
        SwingUtilities.invokeLater(() -> {
            LoginPage loginPage = new LoginPage();
            loginPage.setVisible(true);
        });
    }
}
