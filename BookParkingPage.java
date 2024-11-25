import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BookParkingPage extends JFrame {
    private JComboBox<String> hourComboBox;
    private JSpinner dateSpinner;
    private JSpinner timeSpinner;
    private JComboBox<String> paymentMethodComboBox; // Added payment method combo box
    private JButton bookButton;
    private String username;

    public BookParkingPage(String username) {
        this.username = username;

        // Set up the frame
        setTitle("Parking Management System - Book Parking");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Set a simple layout for booking parking
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(173, 216, 230)); // Light blue color

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Add components to the booking panel
        JLabel titleLabel = new JLabel("Book Your Parking Space");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, gbc);

        gbc.gridy++;
        JLabel welcomeLabel = new JLabel("Welcome " + username);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(welcomeLabel, gbc);

        gbc.gridy++;
        JLabel dateLabel = new JLabel("Select Date:");
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(dateLabel, gbc);

        gbc.gridx = 1;
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new Date()); // set the current date
        panel.add(dateSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel timeLabel = new JLabel("Select Time:");
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(timeLabel, gbc);

        gbc.gridx = 1;
        timeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(timeEditor);
        timeSpinner.setValue(new Date()); // set the current time
        panel.add(timeSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel hourLabel = new JLabel("Number of Hours:");
        hourLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(hourLabel, gbc);

        gbc.gridx = 1;
        hourComboBox = new JComboBox<>(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"});
        panel.add(hourComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        JLabel paymentLabel = new JLabel("Select Payment Method:");
        paymentLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(paymentLabel, gbc);

        gbc.gridx = 1;
        paymentMethodComboBox = new JComboBox<>(new String[]{"Credit Card", "Debit Card", "PayPal", "Cash"});
        panel.add(paymentMethodComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        bookButton = LoginPage.createStyledButton("Book Now");

        // Add action listener for the book button
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date selectedDate = (Date) dateSpinner.getValue();
                Date selectedTime = (Date) timeSpinner.getValue();
                String selectedHours = (String) hourComboBox.getSelectedItem();
                String selectedPaymentMethod = (String) paymentMethodComboBox.getSelectedItem();

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(selectedDate);
                Calendar timeCalendar = Calendar.getInstance();
                timeCalendar.setTime(selectedTime);
                calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY));
                calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE));
                Date finalDateTime = calendar.getTime();

                int hours = Integer.parseInt(selectedHours);

                // Format the final date and time into a String for the database
                SimpleDateFormat sdfTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String timestamp = sdfTimestamp.format(finalDateTime);

                // Attempt to book the slot
                boolean bookingDetails = DatabaseHandler.bookSlot(username, timestamp, selectedHours, selectedPaymentMethod,BookParkingPage.this);

                if (bookingDetails){
                    JOptionPane.showMessageDialog(BookParkingPage.this, "Booking confirmed for "+timestamp+" hours: "+selectedHours);
                    dispose(); // Close the booking page
                } else {
                    JOptionPane.showMessageDialog(BookParkingPage.this, "Booking failed. Please try again.");
                }

            }
        });

        panel.add(bookButton, gbc);

        add(panel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        String exampleUsername = "User"; // Placeholder for testing
        SwingUtilities.invokeLater(() -> {
            BookParkingPage bookParkingPage = new BookParkingPage(exampleUsername);
            bookParkingPage.setVisible(true);
        });
    }
}
