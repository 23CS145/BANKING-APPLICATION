package com.sece;

import java.sql.*;
import java.util.Scanner;

public class BankingServices {
    private Connection connection;
    private boolean adminLoggedIn = false;
    private boolean customerLoggedIn = false;

    public BankingServices() {
        try {
            connection = DBConnect.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean adminLogin(String userName, String password) {
        String query = "SELECT * FROM admin WHERE username = ? AND password = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                System.out.println("Admin logged in successfully.");
                adminLoggedIn = true;
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Invalid admin credentials.");
        return false;
    }

    public boolean customerLogin(String userName, String password) {
        String query = "SELECT * FROM Customer WHERE c_username = ? AND c_password = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                System.out.println("Customer logged in successfully.");
                customerLoggedIn = true;
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Invalid customer credentials.");
        return false;
    }

    public void logout() {
        adminLoggedIn = false;
        customerLoggedIn = false;
        System.out.println("Logged out successfully.");
    }

    public boolean isAdminLoggedIn() {
        return adminLoggedIn;
    }

    
    public void createCustomerAndAccount(String username, String password, String accountType, double balance, String email, String phone, String address) {
        Connection conn = null;
        PreparedStatement stmtCustomer = null;
        PreparedStatement stmtAccount = null;
        
        try {
            // Establish database connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/banking_db", "root","My@sql21");
            
            // Disable auto-commit for transaction management
            conn.setAutoCommit(false);
            
            // 1. Insert the customer data into the 'customer' table
            String sqlCustomer = "INSERT INTO customer (c_username, c_password, balance, accountType, email, phone, address, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            stmtCustomer = conn.prepareStatement(sqlCustomer, Statement.RETURN_GENERATED_KEYS);
            stmtCustomer.setString(1, username);
            stmtCustomer.setString(2, password);
            stmtCustomer.setDouble(3, balance);
            stmtCustomer.setString(4, accountType);
            stmtCustomer.setString(5, email);
            stmtCustomer.setString(6, phone);
            stmtCustomer.setString(7, address);
            stmtCustomer.setString(8, "active");
            
            int rowsInserted = stmtCustomer.executeUpdate();
            
            if (rowsInserted > 0) {
                // Retrieve the generated customer ID
                ResultSet rs = stmtCustomer.getGeneratedKeys();
                int customerId = -1;
                if (rs.next()) {
                    customerId = rs.getInt(1);  // Get the generated customer ID
                }
                
                // 2. Insert the account data into the 'accounts' table using the customer ID
                String sqlAccount = "INSERT INTO accounts (username, password, customer_name, balance, accountType, phone) VALUES (?, ?, ?, ?, ?, ?)";
                stmtAccount = conn.prepareStatement(sqlAccount);
                stmtAccount.setString(1, username);
                stmtAccount.setString(2, password);
                stmtAccount.setString(3, username);  // Using username for customer_name
                stmtAccount.setDouble(4, balance);
                stmtAccount.setString(5, accountType);
                stmtAccount.setString(6, phone);
                
                stmtAccount.executeUpdate();
                
                // Commit the transaction
                conn.commit();
                
                System.out.println("Customer and account created successfully.");
            } else {
                // Rollback if customer insertion fails
                conn.rollback();
                System.out.println("Failed to create customer.");
            }
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();  // Rollback the transaction in case of error
                }
            } catch (SQLException ex) {
                System.out.println("Error during rollback: " + ex.getMessage());
            }
            System.out.println("Error creating customer and account: " + e.getMessage());
        } finally {
            try {
                if (stmtCustomer != null) stmtCustomer.close();
                if (stmtAccount != null) stmtAccount.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    
    

    public void creditTransaction(int accountId, double amount) {
        if (!customerLoggedIn && !adminLoggedIn) {
            System.out.println("You need to be logged in to perform a transaction.");
            return;
        }

        TransactionServices transactionServices = new TransactionServices();
        transactionServices.creditTransaction(accountId, amount);
    }

    public void debitTransaction(int accountId, double amount) {
        if (!customerLoggedIn && !adminLoggedIn) {
            System.out.println("You need to be logged in to perform a transaction.");
            return;
        }

        TransactionServices transactionServices = new TransactionServices();
        transactionServices.debitTransaction(accountId, amount);
    }


    private double getCurrentBalance(int accountId) throws SQLException {
        String query = "SELECT balance FROM Customer WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        }
        return 0;
    }
}