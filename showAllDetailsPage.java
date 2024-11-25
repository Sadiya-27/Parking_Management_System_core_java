import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class showAllDetailsPage extends JFrame {
    private JLabel earningsLabelToday;
    private JLabel earningsLabelMonth;
    private JLabel parkingSpaceLabel;
    private JTable bookingsTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> typeComboBox;
    private JComboBox<String> typeComboBox1;
    private DefaultTableModel parkingTableModel;

    public showAllDetailsPage() {
        setTitle("Admin Dashboard");
        setSize(1200, 820);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        getContentPane().setBackground(new Color(173, 216, 230));

        // Create components
        JLabel dateLabel = new JLabel("Enter Date (YYYY-MM-DD):");
        JTextField dateTextField = new JTextField(10);
        dateTextField.setPreferredSize(new Dimension(200, 30));
        JButton showBookingsButton = new JButton("Show Bookings");
        earningsLabelToday = new JLabel("Total Earnings Today: $0.00");
        earningsLabelMonth = new JLabel("Total Earnings This Month: $0.00");
        parkingSpaceLabel = new JLabel("Parking Space: 0 available, 0 vacant");

        JButton addCustomerButton = new JButton("Add Customer");
        JButton deleteCustomerButton = new JButton("Delete Customer");
        JButton earningsByTimeSlotButton = new JButton("Earnings by Time Slot");
        JButton profitPercentageButton = new JButton("Profit Percentage");
        JButton vehicleCountButton = new JButton("Vehicle Count");
        JButton updateParkingStatusButton = new JButton("Update Parking Status");
        JButton updatePaymentStatusButton = new JButton("Update Payment Status");

        // Initialize table model and table
        String[] columnNames = {"Transaction ID", "Amount", "Payment Method", "Ticket ID", "In Time", "Out Time", "Space No", "Payment Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        bookingsTable = new JTable(tableModel);

        // Set up layout and add components
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(dateLabel, gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Allow the text field to expand horizontally
        gbc.weightx = 1.0; // This makes sure the text field takes available horizontal space
        add(dateTextField, gbc);


        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(showBookingsButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(earningsLabelToday, gbc);
        gbc.gridy = 3;
        add(earningsLabelMonth, gbc);
        gbc.gridy = 4;
        add(parkingSpaceLabel, gbc);

        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(new JScrollPane(bookingsTable), gbc);

        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        add(addCustomerButton, gbc);
        gbc.gridx = 1;
        add(deleteCustomerButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        add(earningsByTimeSlotButton, gbc);
        gbc.gridx = 1;
        add(profitPercentageButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        add(vehicleCountButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        add(vehicleCountButton, gbc);
        gbc.gridx = 1;
        add(updateParkingStatusButton, gbc);

        gbc.gridy = 9;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        add(updatePaymentStatusButton, gbc);

        // Add action listeners
        showBookingsButton.addActionListener(e -> {
            String date = dateTextField.getText();
            try {
                double totalEarningsToday = DatabaseHandler.getTotalEarnings(date);
                earningsLabelToday.setText("Total Earnings Today: $" + totalEarningsToday);

                double totalEarningsMonth = DatabaseHandler.getTotalEarningsThisMonth(date);
                earningsLabelMonth.setText("Total Earnings This Month: $" + totalEarningsMonth);

                int[] parkingSpaceInfo = DatabaseHandler.getParkingSpaceInfo();
                parkingSpaceLabel.setText("Parking Space: " + parkingSpaceInfo[0] + " available, " + parkingSpaceInfo[1] + " occupied");

                List<Object[]> bookingDetails = DatabaseHandler.getBookingDetails(date);
                tableModel.setRowCount(0); // Clear existing rows
                for (Object[] row : bookingDetails) {
                    tableModel.addRow(row);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error fetching booking details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


        addCustomerButton.addActionListener(e -> {
            JTextField nameField = new JTextField(10);
            JTextField mobileField = new JTextField(10);
            JTextField modelField = new JTextField(10);
            JTextField colorField = new JTextField(10);
            JTextField usernameField = new JTextField(10);
            JTextField passwordField = new JPasswordField(10);
            JTextField vehicleNumberField = new JTextField(10);

            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Username:"));
            panel.add(usernameField);
            panel.add(new JLabel("Password:"));
            panel.add(passwordField);
            panel.add(new JLabel("Mobile No:"));
            panel.add(mobileField);

            String[] genderOptions = {"Male", "Female"};
            JComboBox<String> genderComboBox = new JComboBox<>(genderOptions);
            panel.add(new JLabel("Gender:"));
            panel.add(genderComboBox);

            panel.add(new JLabel("Vehicle Number:"));
            panel.add(vehicleNumberField);

            String[] vehicleTypeOptions = {"2 wheeler", "4 wheeler"};
            JComboBox<String> vehicleTypeComboBox = new JComboBox<>(vehicleTypeOptions);
            panel.add(new JLabel("Vehicle Type:"));
            panel.add(vehicleTypeComboBox);

            panel.add(new JLabel("Model:"));
            panel.add(modelField);
            panel.add(new JLabel("Color:"));
            panel.add(colorField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Add Customer", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    String name = nameField.getText().trim();
                    String username = usernameField.getText().trim();
                    String password = new String(((JPasswordField) passwordField).getPassword()).trim();
                    String mobile = mobileField.getText().trim();
                    String gender = (String) genderComboBox.getSelectedItem();
                    String vehicleNumber = vehicleNumberField.getText().trim();
                    String model = modelField.getText().trim();
                    String color = colorField.getText().trim();
                    String vehicleType = (String) vehicleTypeComboBox.getSelectedItem();

                    // Ensure all fields are filled
                    if (name.isEmpty() || username.isEmpty() || password.isEmpty() || mobile.isEmpty() || vehicleNumber.isEmpty() || model.isEmpty() || color.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Please fill in all fields.", "Input Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // Register the user using DatabaseHandler
                    boolean success = DatabaseHandler.registerCustomer(username, password, name, mobile, gender, vehicleNumber, model, color, vehicleType);
                    if (success) {
                        JOptionPane.showMessageDialog(null, "Customer registered successfully.");
                    } else {
                        JOptionPane.showMessageDialog(null, "Registration failed. Please check your inputs or try again.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error adding customer: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });



        deleteCustomerButton.addActionListener(e -> {
            JTextField customerIdField = new JTextField(10);
            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Customer ID:"));
            panel.add(customerIdField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Delete Customer", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    int customerId = Integer.parseInt(customerIdField.getText());
                    DatabaseHandler.deleteCustomer(customerId);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid Customer ID", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting customer: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        vehicleCountButton.addActionListener(e -> {
            JTextField dateField = new JTextField(10);
            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Date (YYYY-MM-DD):"));
            panel.add(dateField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Vehicle Count", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    String date = dateField.getText();
                    int[] vehicleCount = DatabaseHandler.getVehicleCount(date);
                    JOptionPane.showMessageDialog(null, "Vehicle Count on " + date + ":\n" +
                            "2-Wheelers: " + vehicleCount[0] + "\n4-Wheelers: " + vehicleCount[1]);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error fetching vehicle count: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        earningsByTimeSlotButton.addActionListener(e -> {
            JTextField dateField = new JTextField(10);
            JTextField startTimeField = new JTextField(5);
            JTextField endTimeField = new JTextField(5);

            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Date (YYYY-MM-DD):"));
            panel.add(dateField);
            panel.add(new JLabel("Start Time (HH:MM):"));
            panel.add(startTimeField);
            panel.add(new JLabel("End Time (HH:MM):"));
            panel.add(endTimeField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Earnings by Time Slot", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    String date = dateField.getText();
                    String startTime = startTimeField.getText();
                    String endTime = endTimeField.getText();
                    double earnings = DatabaseHandler.getEarningsByTimeSlot(date, startTime, endTime);
                    JOptionPane.showMessageDialog(null, "Earnings on " + date + " between " + startTime + " and " + endTime + ":\n" +
                            "Total Earnings: $" + earnings);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error fetching earnings: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        profitPercentageButton.addActionListener(e -> {
            // Prompt for input if necessary, for example a date range
            JTextField startDateField = new JTextField(10);
            JTextField endDateField = new JTextField(10);

            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Start Date (YYYY-MM-DD):"));
            panel.add(startDateField);
            panel.add(new JLabel("End Date (YYYY-MM-DD):"));
            panel.add(endDateField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Profit Percentage", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    String startDate = startDateField.getText();
                    String endDate = endDateField.getText();
                    double profitPercentage = DatabaseHandler.getProfitPercentage(startDate, endDate);
                    JOptionPane.showMessageDialog(null, "Profit Percentage from " + startDate + " to " + endDate + ":\n" +
                            "Profit Percentage: " + profitPercentage + "%");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error fetching profit percentage: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        updateParkingStatusButton.addActionListener(e -> {
            try {
                // Fetch parking details from the database
                List<Object[]> parkingDetails = DatabaseHandler.getAllParkingDetails();

                // Create a table model for displaying parking details
                String[] parkingColumns = {"Space ID", "Space Number", "Status"};
                parkingTableModel = new DefaultTableModel(parkingColumns, 0);
                JTable parkingTable = new JTable(parkingTableModel);

                // Populate the table with data
                for (Object[] parkingDetail : parkingDetails) {
                    parkingTableModel.addRow(parkingDetail);
                }

                // Add the table to a scroll pane
                JScrollPane parkingScrollPane = new JScrollPane(parkingTable);
                parkingScrollPane.setPreferredSize(new Dimension(600, 300));

                // Panel for containing table and button for updating status
                JPanel panel = new JPanel(new BorderLayout());
                panel.add(parkingScrollPane, BorderLayout.CENTER);

                JButton updateStatusButton = new JButton("Update Selected Status");
                panel.add(updateStatusButton, BorderLayout.SOUTH);

                // Show the table in a dialog
                int result = JOptionPane.showConfirmDialog(null, panel, "Update Parking Status", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    // Get selected row
                    int selectedRow = parkingTable.getSelectedRow();
                    if (selectedRow != -1) {
                        int spaceId = (int) parkingTableModel.getValueAt(selectedRow, 0);
                        String currentStatus = (String) parkingTableModel.getValueAt(selectedRow, 2);
                        String newStatus = currentStatus.equalsIgnoreCase("Available") ? "Occupied" : "Available";

                        // Update status in the database
                        DatabaseHandler.updateParkingStatus(spaceId, (String) parkingTableModel.getValueAt(selectedRow, 1), newStatus);

                        // Update status in the table model
                        parkingTableModel.setValueAt(newStatus, selectedRow, 2);

                        JOptionPane.showMessageDialog(null, "Parking status updated successfully.");
                    } else {
                        JOptionPane.showMessageDialog(null, "Please select a row to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error displaying parking details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add action listeners
        showBookingsButton.addActionListener(e -> {
            String date = dateTextField.getText();
            try {
                List<Object[]> bookingDetails = DatabaseHandler.getBookingDetails(date);
                tableModel.setRowCount(0); // Clear existing rows
                for (Object[] row : bookingDetails) {
                    tableModel.addRow(row);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error fetching booking details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        updatePaymentStatusButton.addActionListener(e -> {
            String date = dateTextField.getText();
            try {
                // Fetch payment details for the given date
                List<Object[]> paymentDetails = DatabaseHandler.getPaymentDetails(date);

                // Create a table model for displaying payment details
                String[] paymentColumns = {"Transaction ID", "Amount", "Pay Method", "Ticket ID", "In Time", "Out Time", "Space No", "Payment Status"};
                DefaultTableModel paymentTableModel = new DefaultTableModel(paymentColumns, 0);
                JTable paymentTable = new JTable(paymentTableModel);

                // Populate the table with data
                for (Object[] paymentDetail : paymentDetails) {
                    paymentTableModel.addRow(paymentDetail);
                }

                // Add the table to a scroll pane
                JScrollPane paymentScrollPane = new JScrollPane(paymentTable);
                paymentScrollPane.setPreferredSize(new Dimension(800, 500));

                // Panel for containing the table and button for updating payment status
                JPanel panel = new JPanel(new BorderLayout());
                panel.add(paymentScrollPane, BorderLayout.CENTER);

                JButton updateStatusButton = new JButton("Update Selected Payment Status");
                panel.add(updateStatusButton, BorderLayout.SOUTH);

                // Show the table in a dialog
                int result = JOptionPane.showConfirmDialog(null, panel, "Update Payment Status", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    // Get selected row
                    int selectedRow = paymentTable.getSelectedRow();
                    if (selectedRow != -1) {
                        int transactionId = (int) paymentTableModel.getValueAt(selectedRow, 0);
                        String currentStatus = (String) paymentTableModel.getValueAt(selectedRow, 7); // Corrected column index for "Payment Status"
                        String newStatus = currentStatus.equalsIgnoreCase("Paid") ? "Unpaid" : "Paid";

                        // Update payment status in the database
                        boolean updateSuccess = DatabaseHandler.updatePaymentStatus(transactionId, newStatus);

                        if (updateSuccess) {
                            // Update status in the table model
                            paymentTableModel.setValueAt(newStatus, selectedRow, 7);
                            JOptionPane.showMessageDialog(null, "Payment status updated successfully.");
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to update payment status. Please try again.", "Update Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Please select a row to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error displaying payment details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });


    }
}
