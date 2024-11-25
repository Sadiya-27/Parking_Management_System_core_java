import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterStaff extends JFrame {
    private JTextField fullNameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField mobileField;
    private JTextField staffIdField;
    private JTextField roleField;
    private JRadioButton maleButton;
    private JRadioButton femaleButton;
    private JButton registerButton;
    private JButton backButton;

    public RegisterStaff() {
        // Set up the frame
        setTitle("Parking Management System - Register Staff");
        setSize(600, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        // Set light blue background
        getContentPane().setBackground(new Color(173, 216, 230)); // Light blue color

        // Create UI components
        fullNameField = createTextField("Full Name");
        usernameField = createTextField("Username");

        passwordField = new JPasswordField(20);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10) // Internal padding
        ));

        mobileField = createTextField("Mobile Number");
        staffIdField = createTextField("Staff id");
        roleField = createTextField("Staff role");


        // Gender options
        maleButton = new JRadioButton("Male");
        femaleButton = new JRadioButton("Female");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);

        // Set button styles
        registerButton = createStyledButton("Register");
        backButton = createStyledButton("Back");

        // Set Font Styles
        Font font = new Font("Arial", Font.BOLD, 14);
        setFontStyles(font);

        // Set GridBag Constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding for all sides
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add fields and buttons to the layout
        addComponent(gbc, 0, 0, "Full Name:", fullNameField);
        addComponent(gbc, 0, 1, "Username:", usernameField);
        addComponent(gbc, 0, 2, "Password:", passwordField);
        addComponent(gbc, 0, 3, "Mobile Number:", mobileField);
        addComponent(gbc, 0, 4, "Staff Id:", staffIdField);
        addComponent(gbc, 0, 5, "Staff Role:", roleField);

        gbc.gridx = 0;
        gbc.gridy = 8;
        add(new JLabel("Gender:"), gbc);

        gbc.gridx = 1;
        add(maleButton, gbc);

        gbc.gridx = 2;
        add(femaleButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 9;
        add(registerButton, gbc);

        gbc.gridx = 1;
        add(backButton, gbc); // Add the back button

        // Add action listener to the register button
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fullName = fullNameField.getText();
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String mobile = mobileField.getText();
                String staffId = staffIdField.getText();
                String role = roleField.getText();
                String gender = maleButton.isSelected() ? "Male" : "Female";

                // Perform registration logic
                boolean isRegistered = DatabaseHandler.registerStaff(username, password, fullName, mobile, gender, staffId, role);

                if (isRegistered) {
                    JOptionPane.showMessageDialog(RegisterStaff.this, "Registration successful!");
                    new LoginPage().setVisible(true); // Open login page
                    dispose(); // Close registration page
                } else {
                    JOptionPane.showMessageDialog(RegisterStaff.this, "Registration failed! Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Add action listener to the back button
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginPage().setVisible(true); // Open login page
                dispose(); // Close registration page
            }
        });
    }

    private JTextField createTextField(String placeholder) {
        JTextField textField = new JTextField(20);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10) // Internal padding
        ));
        textField.setOpaque(true); // Make background visible
        textField.setBackground(Color.WHITE);
        return textField;
    }

    private JButton createStyledButton(String text) {
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

    private void setFontStyles(Font font) {
        maleButton.setFont(font);
        femaleButton.setFont(font);
        fullNameField.setFont(font);
        usernameField.setFont(font);
        mobileField.setFont(font);
        staffIdField.setFont(font);
        roleField.setFont(font);
        passwordField.setFont(font);
    }

    private void addComponent(GridBagConstraints gbc, int gridx, int gridy, String labelText, JComponent component) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        add(new JLabel(labelText), gbc);

        gbc.gridx = gridx + 1;
        add(component, gbc);
    }

    public static void main(String[] args) {
        // Run the registration interface
        SwingUtilities.invokeLater(() -> {
            RegisterStaff registerStaff = new RegisterStaff();
            registerStaff.setVisible(true);
        });
    }
}
