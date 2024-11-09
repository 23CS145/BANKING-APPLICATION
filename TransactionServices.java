package com.sece;

import java.sql.*;
import java.io.FileWriter;
import java.io.IOException;

public class TransactionServices {
    private Connection connection;

    public TransactionServices() {
        try {
            connection = DBConnect.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean creditTransaction(int accountId, double amount) {
        if (amount <= 0) {
            System.out.println("Amount must be greater than zero.");
            return false;
        }

        String updateBalanceQuery = "UPDATE customer SET balance = balance + ? WHERE id = ?";
        String insertTransactionQuery = "INSERT INTO transaction (account_id, trans_date, trans_type, trans_amount, acc_balance) VALUES (?, NOW(), 'Credit', ?, ?)";

        try (PreparedStatement updateStmt = connection.prepareStatement(updateBalanceQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertTransactionQuery)) {

            // Update the balance
            updateStmt.setDouble(1, amount);
            updateStmt.setInt(2, accountId);
            int rowsAffected = updateStmt.executeUpdate();

            if (rowsAffected > 0) {
                double newBalance = getCurrentBalance(accountId); // Get the updated balance

                // Insert the transaction record
                insertStmt.setInt(1, accountId);
                insertStmt.setDouble(2, amount);
                insertStmt.setDouble(3, newBalance);
                insertStmt.executeUpdate();

                System.out.printf("Credit transaction successful! Amount credited: %.2f%nNew Balance: %.2f%n", amount, newBalance);

                // Write transaction details to file
                writeTransactionToFile("Amount " + amount + " credited. New Balance: " + newBalance);
                return true;
            } else {
                System.out.println("Credit transaction failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean debitTransaction(int accountId, double amount) {
        if (amount <= 0) {
            System.out.println("Amount must be greater than zero.");
            return false;
        }

        double currentBalance = getCurrentBalance(accountId);
        if (currentBalance < amount) {
            System.out.println("Insufficient balance for debit transaction.");
            return false;
        }

        String updateBalanceQuery = "UPDATE customer SET balance = balance - ? WHERE id = ?";
        String insertTransactionQuery = "INSERT INTO transaction (account_id, trans_date, trans_type, trans_amount, acc_balance) VALUES (?, NOW(), 'Debit', ?, ?)";

        try (PreparedStatement updateStmt = connection.prepareStatement(updateBalanceQuery);
             PreparedStatement insertStmt = connection.prepareStatement(insertTransactionQuery)) {

            // Update the balance
            updateStmt.setDouble(1, amount);
            updateStmt.setInt(2, accountId);
            int rowsAffected = updateStmt.executeUpdate();

            if (rowsAffected > 0) {
                double newBalance = getCurrentBalance(accountId); // Get the updated balance

                // Insert the transaction record
                insertStmt.setInt(1, accountId);
                insertStmt.setDouble(2, amount);
                insertStmt.setDouble(3, newBalance);
                insertStmt.executeUpdate();

                System.out.printf("Debit transaction successful! Amount debited: %.2f%nNew Balance: %.2f%n", amount, newBalance);

                // Write transaction details to file
                writeTransactionToFile("Amount " + amount + " debited. New Balance: " + newBalance);
                return true;
            } else {
                System.out.println("Debit transaction failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getCurrentBalance(int accountId) {
        String query = "SELECT balance FROM customer WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void writeTransactionToFile(String data) {
        try (FileWriter writer = new FileWriter("transaction.txt", true)) { // Append mode
            writer.write(data + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Error writing transaction details to file.");
            e.printStackTrace();
        }
    }

    public void getAccountStatement(int accountId) throws SQLException {
        String query = "SELECT trans_date, trans_type, trans_amount, acc_balance FROM transaction WHERE account_id = ? ORDER BY trans_date DESC";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            
            System.out.println("Transaction History for Account ID: " + accountId);
            while (rs.next()) {
                System.out.printf("Date: %s | Type: %s | Amount: %.2f | Balance: %.2f%n",
                        rs.getTimestamp("trans_date"),
                        rs.getString("trans_type"),
                        rs.getDouble("trans_amount"),
                        rs.getDouble("acc_balance"));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving account statement.");
            e.printStackTrace();
        }
    }
}
