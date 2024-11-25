import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterPage extends JFrame {
    private JTextField fullNameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField mobileField;
    private JTextField licenseField;
    private JTextField modelField;
    private JTextField colorField;
    private JComboBox<String> typeComboBox;
    private JComboBox<String> genderComboBox; // Updated name for clarity
    private JButton registerButton;
    private JButton backButton;

    public RegisterPage() {
        // Frame setup
        setTitle("Parking Management System - Register");
        setSize(600, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(173, 216, 230)); // Light blue

        // Initialize components
        fullNameField = createTextField();
        usernameField = createTextField();
        passwordField = createPasswordField();
        mobileField = createTextField();
        licenseField = createTextField();
        modelField = createTextField();
        colorField = createTextField();

        // Dropdowns for vehicle type and gender
        String[] vehicleTypes = {"2 Wheeler", "4 Wheeler"};
        typeComboBox = new JComboBox<>(vehicleTypes);

        String[] genders = {"Male", "Female"};
        genderComboBox = new JComboBox<>(genders);

        registerButton = createStyledButton("Register");
        backButton = createStyledButton("Back");

        // Set Font Styles
        Font font = new Font("Arial", Font.BOLD, 14);
        setFontStyles(font);

        // Layout setup
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add components to the layout
        addComponent(gbc, 0, 0, "Full Name:", fullNameField);
        addComponent(gbc, 0, 1, "Username:", usernameField);
        addComponent(gbc, 0, 2, "Password:", passwordField);
        addComponent(gbc, 0, 3, "Mobile Number:", mobileField);
        addComponent(gbc, 0, 4, "License Plate No:", licenseField);
        addComponent(gbc, 0, 5, "Vehicle Model:", modelField);
        addComponent(gbc, 0, 6, "Vehicle Color:", colorField);
        addComponent(gbc, 0, 7, "Vehicle Type:", typeComboBox);
        addComponent(gbc, 0, 8, "Gender:", genderComboBox);

        gbc.gridx = 0;
        gbc.gridy = 9;
        add(registerButton, gbc);

        gbc.gridx = 1;
        add(backButton, gbc);

        // Register button action
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegisterAction();
            }
        });

        // Back button action
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginPage().setVisible(true);
                dispose();
            }
        });
    }

    private void handleRegisterAction() {
        // Retrieve inputs
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String mobile = mobileField.getText().trim();
        String license = licenseField.getText().trim();
        String model = modelField.getText().trim();
        String color = colorField.getText().trim();
        String type = (String) typeComboBox.getSelectedItem();
        String gender = (String) genderComboBox.getSelectedItem();

        // Validate inputs
        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || mobile.isEmpty() ||
                license.isEmpty() || model.isEmpty() || color.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Call database handler
        boolean isRegistered = DatabaseHandler.registerUser(username, password, fullName, mobile, gender, license, model, color, type);

        // Show appropriate message
        if (isRegistered) {
            JOptionPane.showMessageDialog(this, "Registration successful!");
            new LoginPage().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField(20);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        textField.setOpaque(true);
        textField.setBackground(Color.WHITE);
        return textField;
    }

    private JPasswordField createPasswordField() {
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        return passwordField;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0, 0, 139)); // Dark blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1, true),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
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

    private void setFontStyles(Font font) {
        fullNameField.setFont(font);
        usernameField.setFont(font);
        passwordField.setFont(font);
        mobileField.setFont(font);
        licenseField.setFont(font);
        modelField.setFont(font);
        colorField.setFont(font);
        typeComboBox.setFont(font);
        genderComboBox.setFont(font);
    }

    private void addComponent(GridBagConstraints gbc, int gridx, int gridy, String labelText, JComponent component) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        add(new JLabel(labelText), gbc);

        gbc.gridx = gridx + 1;
        add(component, gbc);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RegisterPage registerPage = new RegisterPage();
            registerPage.setVisible(true);
        });
    }
}
