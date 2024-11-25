import javax.swing.*;
import java.awt.*;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    private static final String URL = "jdbc:mysql://localhost:3306/parking_management_system";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "admin";

    // Establish a connection to the database
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    // Authenticate user credentials
    public static boolean authenticateCustomer(String username, String password) {
        String query = "SELECT * FROM user INNER JOIN customer_has_account ON user.user_id = customer_has_account.user_id INNER JOIN customer ON customer_has_account.cust_id = customer.cust_id WHERE user.username = ? AND user.password = ?;";
        try (Connection con = getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password); // No hashing
            ResultSet result = preparedStatement.executeQuery();
            return result.next(); // Returns true if a record exists
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
            return false;
        }
    }

    public static boolean authenticateStaff(String username, String password) {
        String query = "SELECT user.*, staff.role FROM user " +
                "INNER JOIN staff_has_account ON user.user_id = staff_has_account.user_id " +
                "INNER JOIN staff ON staff_has_account.staff_id = staff.staff_id " +
                "WHERE user.username = ? AND user.password = ?;";

        try (Connection con = getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password); // No hashing
            ResultSet result = preparedStatement.executeQuery();

            if (result.next()) {
                // Fetch the role of the staff
                String role = result.getString("role");

                // Show different details based on the role
                switch (role.toLowerCase()) {
                    case "admin":
                        new showAllDetailsPage().setVisible(true);
                        break;
//                    case "manager":
//                        new showOccupiedAndVacantSlots().setVisible(true);
//                        break;
                    default:
                        System.out.println("No specific page for role: " + role);
                }
                return true; // Authentication successful
            } else {
                return false; // No matching record found
            }
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
            return false;
        }
    }

    // Register a new user
    public static boolean registerUser(String username, String password, String name, String phoneNo, String gender, String license_plate_no,
                                       String model, String color, String type) {
        String queryUser = "INSERT INTO user (username, password) VALUES (?, ?)";
        String queryCustomer = "INSERT INTO customer (name, phone_no, gender) VALUES (?, ?, ?)";
        String queryCustomerAccount =
                "INSERT INTO customer_has_account (user_id, cust_id) VALUES (" +
                        "(SELECT user_id FROM user WHERE username = ? LIMIT 1), " +
                        "(SELECT cust_id FROM customer WHERE name = ? LIMIT 1))";
        String queryVehicle = "INSERT INTO vehicle (licence, color, model, type) VALUES (?, ?, ?, ?)";
        String queryCustomerVehicle =
                "INSERT INTO customer_has_vehicle (cust_id, vehicle_id) VALUES (" +
                        "(SELECT cust_id FROM customer WHERE name = ? LIMIT 1), " +
                        "(SELECT vehicle_id FROM vehicle WHERE model = ? LIMIT 1))";

        try (Connection con = getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement preparedStatement1 = con.prepareStatement(queryUser);
                 PreparedStatement preparedStatement2 = con.prepareStatement(queryCustomer);
                 PreparedStatement preparedStatement3 = con.prepareStatement(queryCustomerAccount);
                 PreparedStatement preparedStatement4 = con.prepareStatement(queryVehicle);
                 PreparedStatement preparedStatement5 = con.prepareStatement(queryCustomerVehicle)) {

                // User registration
                preparedStatement1.setString(1, username);
                preparedStatement1.setString(2, password);
                int resultUser = preparedStatement1.executeUpdate();

                // Customer registration
                preparedStatement2.setString(1, name);
                preparedStatement2.setString(2, phoneNo);
                preparedStatement2.setString(3, gender);
                int resultCustomer = preparedStatement2.executeUpdate();

                // Associate customer with user account
                preparedStatement3.setString(1, username);
                preparedStatement3.setString(2, name);
                int resultCustomerAccount = preparedStatement3.executeUpdate();

                // Vehicle registration
                preparedStatement4.setString(1, license_plate_no);
                preparedStatement4.setString(2, color);
                preparedStatement4.setString(3, model);
                preparedStatement4.setString(4, type);
                int resultVehicle = preparedStatement4.executeUpdate();

                // Associate vehicle with customer
                preparedStatement5.setString(1, name);
                preparedStatement5.setString(2, model);
                int resultCustomerVehicle = preparedStatement5.executeUpdate();

                // Commit transaction if all queries succeed
                con.commit();
                return resultUser > 0 && resultCustomer > 0 && resultCustomerAccount > 0 && resultVehicle > 0 && resultCustomerVehicle > 0;

            } catch (SQLException e) {
                con.rollback();
                System.err.println("Registration failed: " + e.getMessage());
                return false;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            return false;
        }
    }


    // Register staff
    public static boolean registerStaff( String username, String password,String name, String phoneNo, String gender, String staffId, String role) {
        String queryUser = "INSERT INTO user (username, password) VALUES (?, ?)";
        String queryStaff = "INSERT INTO staff (staff_id, name, role, gender, phone_no) VALUES (?, ?, ?, ?, ?)";
        String queryStaffHasAccount = "INSERT INTO staff_has_account (user_id, staff_id) VALUES ((SELECT user_id FROM user WHERE username = ?), ?)";

        try (Connection con = getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement preparedStatement1 = con.prepareStatement(queryUser);
                 PreparedStatement preparedStatement2 = con.prepareStatement(queryStaff);
                 PreparedStatement preparedStatement3 = con.prepareStatement(queryStaffHasAccount)) {

                // User registration
                preparedStatement1.setString(1, username);
                preparedStatement1.setString(2, password); // No hashing
                int resultUser = preparedStatement1.executeUpdate();
                System.out.println("User registration result: " + resultUser);

                // Staff registration
                preparedStatement2.setString(1, staffId);
                preparedStatement2.setString(2, name);
                preparedStatement2.setString(3, role);
                preparedStatement2.setString(4, gender);
                preparedStatement2.setString(5, phoneNo);
                int resultStaff = preparedStatement2.executeUpdate();
                System.out.println("Staff registration result: " + resultStaff);

                // Staff has user account
                preparedStatement3.setString(1, username);
                preparedStatement3.setString(2, staffId);
                int resultStaffHasAccount = preparedStatement3.executeUpdate();
                System.out.println("Staff has account: " + resultStaffHasAccount);

                // Commit transaction if all queries succeed
                con.commit();
                return resultUser > 0 && resultStaff > 0 && resultStaffHasAccount > 0;

            } catch (SQLException e) {
                // Rollback if any query fails
                con.rollback();
                System.err.println("Registration failed: " + e.getMessage());
                return false;
            } finally {
                // Re-enable auto-commit mode
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            return false;
        }
    }

    public static boolean bookSlot(String userId, String time, String numberOfHours, String paymentMethod, Component parent) {
        // SQL Queries
        String findAvailableSpaceQuery = "SELECT space_no FROM parking_space WHERE status IS NULL OR status = 'available' LIMIT 1";
        String updateParkingQuery = "UPDATE parking_space SET status = 'occupied' WHERE space_no = ?";
        String getCustIdQuery = "SELECT cust_id FROM customer_has_account WHERE user_id = (SELECT user_id From user where username =?)";
        String getLicenseQuery = "SELECT licence FROM vehicle WHERE vehicle_id = (Select vehicle_id from customer_has_vehicle where cust_id = ?)";
        String insertTicketQuery = "INSERT INTO ticket (space_no, payment_status, licence_plate, in_time, out_time) VALUES (?, ?, (SELECT licence FROM vehicle WHERE vehicle_id = (Select vehicle_id from customer_has_vehicle where cust_id = ?)), ?, ?)";
        String insertPaymentQuery = "INSERT INTO payment (pay_method,amount) VALUES (?,?)";
        String insertCustomerPaymentQuery = "INSERT INTO customer_process_payment (cust_id, transaction_id) VALUES (?, ?)";
        String generateTicketQuery = "INSERT INTO generate_ticket (cust_id, transaction_id) VALUES (?, ?)";

        try (Connection con = getConnection()) {
            con.setAutoCommit(false);

            try (PreparedStatement findAvailableSpaceStmt = con.prepareStatement(findAvailableSpaceQuery);
                 PreparedStatement updateParkingStmt = con.prepareStatement(updateParkingQuery);
                 PreparedStatement getCustIdStmt = con.prepareStatement(getCustIdQuery);
                 PreparedStatement getLicenseStmt = con.prepareStatement(getLicenseQuery);
                 PreparedStatement insertTicketStmt = con.prepareStatement(insertTicketQuery);
                 PreparedStatement insertPaymentStmt = con.prepareStatement(insertPaymentQuery, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement insertCustomerPaymentStmt = con.prepareStatement(insertCustomerPaymentQuery);
                 PreparedStatement generateTicketStmt = con.prepareStatement(generateTicketQuery)) {

                // Step 1: Retrieve Cust_id for the current user
                getCustIdStmt.setString(1, userId);
                ResultSet custIdRs = getCustIdStmt.executeQuery();
                if (!custIdRs.next()) {
                    System.err.println("Customer ID not found for user ID: " + userId);
                    return false;
                }

                int custId = custIdRs.getInt("cust_id");

                // Step 2: Retrieve license for the customer
                getLicenseStmt.setInt(1, custId);
                ResultSet licenseRs = getLicenseStmt.executeQuery();
                if (!licenseRs.next()) {
                    System.err.println("License not found for customer ID: " + custId);
                    return false;
                }

                String licence = licenseRs.getString("licence");

                // Step 3: Find an available parking space
                ResultSet rs = findAvailableSpaceStmt.executeQuery();
                if (rs.next()) {
                    String spaceNo = rs.getString("space_no");


                    // Update the status to 'occupied'
                    updateParkingStmt.setString(1, spaceNo);

                    int rowsUpdated = updateParkingStmt.executeUpdate();

                    if (rowsUpdated > 0) {
                        // Calculate the amount based on the number of hours
                        int hours = Integer.parseInt(numberOfHours);
                        double amount = hours * 10.0; // Assuming a flat rate of $10 per hour

                        // Insert payment details
                        insertPaymentStmt.setString(1, paymentMethod);
                        insertPaymentStmt.setDouble(2, amount);
                        insertPaymentStmt.executeUpdate();

                        // Retrieve the generated transaction_id
                        ResultSet paymentRs = insertPaymentStmt.getGeneratedKeys();
                        if (!paymentRs.next()) {
                            con.rollback();
                            System.err.println("Failed to retrieve transaction ID.");
                            return false;
                        }
                        int transactionId = paymentRs.getInt(1);

                        // Insert customer payment relation
                        insertCustomerPaymentStmt.setInt(1, custId);
                        insertCustomerPaymentStmt.setInt(2, transactionId);
                        insertCustomerPaymentStmt.executeUpdate();

                        // Generate ticket
                        generateTicketStmt.setInt(1, custId);
                        generateTicketStmt.setInt(2, transactionId);
                        generateTicketStmt.executeUpdate();

                        // Convert the string time to a Timestamp
                        Timestamp inTime = Timestamp.valueOf(time); // Ensure time is in "yyyy-MM-dd HH:mm:ss" format

                        // Calculate out_time based on in_time and the number of hours
                        Timestamp outTime = new Timestamp(inTime.getTime() + hours * 3600 * 1000); // Add hours in milliseconds

                        // Insert a new ticket
                        insertTicketStmt.setString(1, spaceNo);
                        insertTicketStmt.setString(2,"unpaid");
                        insertTicketStmt.setInt(3, custId); // Pass custId to get licence
                        insertTicketStmt.setTimestamp(4, inTime); // in_time
                        insertTicketStmt.setTimestamp(5, outTime); // out_time
                        insertTicketStmt.executeUpdate();

                        con.commit();
                        JOptionPane.showMessageDialog(parent,"Booking confirmed for space " + spaceNo + ". Amount to be paid: "+amount);
                        return true;
                    } else {
                        con.rollback();
                        JOptionPane.showMessageDialog(parent,"Failed to update the parking space status.");
                        return false;
                    }
                } else {
                    System.out.println("No available parking spaces.");
                    return false;
                }
            } catch (Exception e) {
                con.rollback();
                System.err.println("Error during booking: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            return false;
        }
//        return null;
    }

    public static double getTotalEarnings(String date) {
        String query = "SELECT SUM(payment.amount) AS total_earnings " +
                "FROM payment " +
                "JOIN generate_ticket ON payment.transaction_id = generate_ticket.transaction_id " +
                "JOIN ticket ON generate_ticket.ticket_id = ticket.ticket_id " +
                "WHERE DATE(ticket.in_time) = ?";

        double totalEarnings = 0.0;

        try (Connection con = getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(query)) {

            preparedStatement.setString(1, date);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                totalEarnings = resultSet.getDouble("total_earnings");
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
        return totalEarnings;
    }


    public static List<Object[]> getBookingDetails(String date) {
        String query = "SELECT Payment.transaction_id, Payment.amount, Payment.pay_method, " +
                "Ticket.ticket_id, Ticket.in_time,ticket.out_time, Ticket.space_no, Ticket.payment_status " +
                "FROM Payment " +
                "JOIN generate_ticket ON Payment.transaction_id = generate_ticket.transaction_id " +
                "JOIN ticket ON generate_ticket.ticket_id = ticket.ticket_id " +
                "WHERE DATE(Ticket.in_time) = ?";
        List<Object[]> bookingDetails = new ArrayList<>();

        try (Connection con = getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(query)) {

            preparedStatement.setString(1, date);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Object[] row = new Object[8];
                row[0] = resultSet.getInt("transaction_id");
                row[1] = resultSet.getDouble("amount");
                row[2] = resultSet.getString("pay_method");
                row[3] = resultSet.getInt("ticket_id");
                row[4] = resultSet.getTimestamp("in_time");
                row[5] = resultSet.getString("out_time");
                row[6] = resultSet.getString("space_no");
                row[7] = resultSet.getString("payment_status");
                bookingDetails.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
        return bookingDetails;
    }


    public static double getTotalEarningsThisMonth(String date) {
        String query = "SELECT SUM(amount) AS total_earnings " +
                "FROM Payment " +
                "JOIN generate_Ticket ON Payment.transaction_id = generate_Ticket.transaction_id " +
                "JOIN ticket ON generate_ticket.ticket_id = ticket.ticket_id " +
                "WHERE MONTH(Ticket.in_time) = MONTH(?) AND YEAR(Ticket.in_time) = YEAR(?)";
        try (Connection con = getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(query)) {

            preparedStatement.setString(1, date);
            preparedStatement.setString(2, date);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("total_earnings");
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
        return 0.0;
    }

    public static int[] getParkingSpaceInfo() {
        String query = "SELECT " +
                "SUM(CASE WHEN status = 'available' OR status IS NULL THEN 1 ELSE 0 END) AS available, " +
                "SUM(CASE WHEN status = 'occupied' THEN 1 ELSE 0 END) AS occupied " +
                "FROM Parking_Space";
        try (Connection con = getConnection();
             Statement statement = con.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                int available = resultSet.getInt("available");
                int occupied = resultSet.getInt("occupied");
                return new int[]{available, occupied};
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
        return new int[]{0, 0};
    }


    public static boolean registerCustomer(String username, String password, String name, String phoneNo, String gender,
                                       String licensePlateNo, String model, String color, String vehicleType) {
        String queryUser = "INSERT INTO user (username, password) VALUES (?, ?)";
        String queryCustomer = "INSERT INTO customer (name, phone_no, gender) VALUES (?, ?, ?)";
        String queryCustomerAccount = "INSERT INTO customer_has_account (user_id, cust_id) VALUES " +
                "((SELECT user_id FROM user WHERE username = ?), " +
                "(SELECT cust_id FROM customer WHERE name = ? AND phone_no = ?))";
        String queryVehicle = "INSERT INTO vehicle (licence, color, model, type) VALUES (?, ?, ?, ?)";
        String queryCustomerVehicle = "INSERT INTO customer_has_vehicle (cust_id, vehicle_id) VALUES " +
                "((SELECT cust_id FROM customer WHERE name = ? AND phone_no = ?), " +
                "(SELECT vehicle_id FROM vehicle WHERE licence = ?))";

        try (Connection con = getConnection()) {
            con.setAutoCommit(false); // Start transaction

            try (PreparedStatement psUser = con.prepareStatement(queryUser);
                 PreparedStatement psCustomer = con.prepareStatement(queryCustomer);
                 PreparedStatement psCustomerAccount = con.prepareStatement(queryCustomerAccount);
                 PreparedStatement psVehicle = con.prepareStatement(queryVehicle);
                 PreparedStatement psCustomerVehicle = con.prepareStatement(queryCustomerVehicle)) {

                // Insert into user table
                psUser.setString(1, username);
                psUser.setString(2, password);
                int userResult = psUser.executeUpdate();

                // Insert into customer table
                psCustomer.setString(1, name);
                psCustomer.setString(2, phoneNo);
                psCustomer.setString(3, gender);
                int customerResult = psCustomer.executeUpdate();

                // Link user and customer
                psCustomerAccount.setString(1, username);
                psCustomerAccount.setString(2, name);
                psCustomerAccount.setString(3, phoneNo);
                int customerAccountResult = psCustomerAccount.executeUpdate();

                // Insert into vehicle table
                psVehicle.setString(1, licensePlateNo);
                psVehicle.setString(2, color);
                psVehicle.setString(3, model);
                psVehicle.setString(4, vehicleType);
                int vehicleResult = psVehicle.executeUpdate();

                // Link customer and vehicle
                psCustomerVehicle.setString(1, name);
                psCustomerVehicle.setString(2, phoneNo);
                psCustomerVehicle.setString(3, licensePlateNo);
                int customerVehicleResult = psCustomerVehicle.executeUpdate();

                // Commit the transaction if all operations are successful
                if (userResult > 0 && customerResult > 0 && customerAccountResult > 0 &&
                        vehicleResult > 0 && customerVehicleResult > 0) {
                    con.commit();
                    return true;
                } else {
                    con.rollback();
                    return false;
                }

            } catch (SQLException e) {
                con.rollback(); // Rollback transaction if something fails
                System.err.println("Registration failed: " + e.getMessage());
                return false;
            } finally {
                con.setAutoCommit(true); // Re-enable auto-commit mode
            }

        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            return false;
        }
    }

    public static void deleteCustomer(int custId) {
        String query = "DELETE FROM Customer WHERE cust_id = ?";
        try (Connection con = getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(query)) {

            preparedStatement.setInt(1, custId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
    }

    public static double getEarningsByTimeSlot(String date, String startTime, String endTime) {
        String query = "SELECT SUM(amount) AS total_earnings " +
                "FROM Payment " +
                "JOIN generate_Ticket ON Payment.transaction_id = generate_Ticket.transaction_id " +
                "JOIN Ticket ON ticket.ticket_id = generate_ticket.ticket_id " +
                "WHERE DATE(Ticket.in_time) = ? AND TIME(Ticket.in_time) BETWEEN ? AND ?";
        try (Connection con = getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(query)) {

            preparedStatement.setString(1, date);
            preparedStatement.setString(2, startTime);
            preparedStatement.setString(3, endTime);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("total_earnings");
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
        return 0.0; // Return 0 if no records are found or an error occurs
    }

    public static int[] getVehicleCount(String date) {
        String query = "SELECT Vehicle.type, COUNT(*) AS count " +
                "FROM Vehicle " +
                "JOIN ticket ON Ticket.licence_plate = Vehicle.licence " +
                "WHERE DATE(Ticket.in_time) = ? " +
                "GROUP BY Vehicle.type";
        int twoWheelerCount = 0;
        int fourWheelerCount = 0;
        try (Connection con = getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(query)) {

            preparedStatement.setString(1, date);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String type = resultSet.getString("type");
                int count = resultSet.getInt("count");
                if ("2 Wheeler".equalsIgnoreCase(type)) {
                    twoWheelerCount = count;
                } else if ("4 Wheeler".equalsIgnoreCase(type)) {
                    fourWheelerCount = count;
                }
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
        return new int[]{twoWheelerCount, fourWheelerCount};
    }

    public static double getProfitPercentage(String startDate, String endDate) {
        String revenueQuery = "SELECT SUM(amount) AS total_revenue " +
                "FROM Payment " +
                "JOIN generate_Ticket ON Payment.transaction_id = generate_Ticket.transaction_id " +
                "Join ticket on ticket.ticket_id = generate_ticket.ticket_id " +
                "WHERE DATE(Ticket.in_time) BETWEEN ? AND ?";

        String costQuery = "SELECT DATE(in_time) AS date, COUNT(DISTINCT space_no) AS occupied_spaces " +
                "FROM Ticket " +
                "WHERE DATE(in_time) BETWEEN ? AND ? " +
                "GROUP BY DATE(in_time)";

        double totalRevenue = 0.0;
        double totalCost = 0.0;
        final double costRatePerSpace = 20.0;

        try (Connection con = getConnection();
             PreparedStatement revenueStmt = con.prepareStatement(revenueQuery);
             PreparedStatement costStmt = con.prepareStatement(costQuery)) {

            // Set parameters for the date range in revenue query
            revenueStmt.setString(1, startDate);
            revenueStmt.setString(2, endDate);

            // Execute the query to get total revenue
            ResultSet revenueResult = revenueStmt.executeQuery();
            if (revenueResult.next()) {
                totalRevenue = revenueResult.getDouble("total_revenue");
            }

            // Set parameters for the date range in cost query
            costStmt.setString(1, startDate);
            costStmt.setString(2, endDate);

            // Execute the query to calculate daily costs based on occupied spaces
            ResultSet costResult = costStmt.executeQuery();
            while (costResult.next()) {
                int occupiedSpaces = costResult.getInt("occupied_spaces");
                totalCost += occupiedSpaces * costRatePerSpace;
            }

        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            return 0.0;
        }

        // Calculate profit percentage
        if (totalRevenue == 0) {
            return 0.0;  // Avoid division by zero if no revenue
        }

        return ((totalRevenue - totalCost) / totalRevenue) * 100;
    }

    public static void updateParkingStatus(int spaceId, String spaceNumber, String newStatus) throws SQLException {
        String query = "UPDATE parking_space SET status = ? WHERE space_id = ? AND space_no = ?";
        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, spaceId);
            stmt.setString(3, spaceNumber);
            stmt.executeUpdate();
        }
    }

    // Method to retrieve all parking details
    public static List<Object[]> getAllParkingDetails() throws SQLException {
        String query = "SELECT space_id, space_no, status FROM parking_space";
        List<Object[]> parkingDetails = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int spaceId = rs.getInt("space_id");
                String spaceNumber = rs.getString("space_no");
                String status = rs.getString("status");
                parkingDetails.add(new Object[]{spaceId, spaceNumber, status});
            }
        }
        return parkingDetails;
    }

    public static boolean updatePaymentStatus(int transactionId, String newStatus) {
        String updateQuery = "UPDATE ticket SET payment_status = ? WHERE ticket_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            // Set parameters for the prepared statement
            preparedStatement.setString(1, newStatus);
            preparedStatement.setInt(2, transactionId);

            // Execute the update
            int rowsAffected = preparedStatement.executeUpdate();

            // Return true if the update was successful, false otherwise
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating payment status: " + e.getMessage());
            return false;
        }
    }

    // Database method for fetching payment details
    public static List<Object[]> getPaymentDetails(String date) {
        String selectQuery = "SELECT payment.transaction_id, payment.amount, payment.pay_method, ticket.ticket_id, ticket.in_time, ticket.out_time, ticket.space_no, ticket.payment_status " +
                "FROM payment " +
                "JOIN generate_ticket gt ON payment.transaction_id = gt.transaction_id " +
                "JOIN ticket ON gt.ticket_id = ticket.ticket_id " +
                "WHERE DATE(ticket.in_time) = ?";

        List<Object[]> paymentDetails = new ArrayList<>();

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

            // Set the date parameter for the query
            preparedStatement.setString(1, date);

            // Execute the query and process the results
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Object[] row = new Object[8];
                    row[0] = resultSet.getInt("transaction_id");
                    row[1] = resultSet.getDouble("amount");
                    row[2] = resultSet.getString("pay_method");
                    row[3] = resultSet.getInt("ticket_id");
                    row[4] = resultSet.getTimestamp("in_time").toString();
                    row[5] = resultSet.getTimestamp("out_time").toString();
                    row[6] = resultSet.getString("space_no");
                    row[7] = resultSet.getString("payment_status");
                    paymentDetails.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching payment details: " + e.getMessage());
        }

        return paymentDetails;
    }


}