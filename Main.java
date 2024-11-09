package com.sece;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        BankingServices accountService = new BankingServices();
        Scanner sc = new Scanner(System.in);

        boolean adminLoggedIn = false;
        boolean customerLoggedIn = false;

        while (true) {
            System.out.println("\n1. Admin Login \n2. Customer Login \n3. Logout \n4. Exit");
            System.out.print("Choose an Option: ");
            int choice;

            try {
                choice = sc.nextInt();
                sc.nextLine();  // Consume newline
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.nextLine();  // Clear invalid input
                continue;
            }

            switch (choice) {
                case 1 -> {
                    if (!adminLoggedIn && !customerLoggedIn) {
                        System.out.print("Enter Admin Username: ");
                        String adminUser = sc.nextLine();
                        System.out.print("Enter Admin Password: ");
                        String adminPass = sc.nextLine();

                        if (accountService.adminLogin(adminUser, adminPass)) {
                            adminLoggedIn = true;
                            adminActions(accountService); // Trigger admin actions
                        } else {
                            System.out.println("Invalid admin credentials.");
                        }
                    } else {
                        System.out.println("Cannot log in as Admin. Already logged in as " + (adminLoggedIn ? "Admin" : "Customer"));
                    }
                }
                case 2 -> {
                    if (!adminLoggedIn && !customerLoggedIn) {
                        System.out.print("Enter Customer Username: ");
                        String customerUser = sc.nextLine();
                        System.out.print("Enter Customer Password: ");
                        String customerPass = sc.nextLine();

                        if (accountService.customerLogin(customerUser, customerPass)) {
                            customerLoggedIn = true;
                        } else {
                            System.out.println("Invalid customer credentials.");
                        }
                    } else {
                        System.out.println("Cannot log in as Customer. Already logged in as " + (adminLoggedIn ? "Admin" : "Customer"));
                    }
                }
                case 3 -> {
                    accountService.logout();
                    adminLoggedIn = false;
                    customerLoggedIn = false;
                }
                case 4 -> {
                    System.out.println("Exiting application.");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public static void adminActions(BankingServices accountService) throws SQLException {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n1. Create Customer \n2. Credit Account \n3. Debit Account \n4. Logout");
            System.out.print("Choose an option: ");
            int option;

            try {
                option = sc.nextInt();
                sc.nextLine(); // Consume newline
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.nextLine(); // Clear invalid input
                continue;
            }

            switch (option) {
                case 1 -> {
                    // Gather necessary details for creating a customer and account
                    System.out.print("Enter Customer Username: ");
                    String username = sc.nextLine();
                    System.out.print("Enter Customer Password: ");
                    String password = sc.nextLine();
                    System.out.print("Enter Account Type (Savings/Current): ");
                    String accountType = sc.nextLine();
                    System.out.print("Enter Initial Balance: ");
                    double balance = sc.nextDouble();
                    sc.nextLine();  // Consume newline after double input
                    System.out.print("Enter Customer Email: ");
                    String email = sc.nextLine();
                    System.out.print("Enter Customer Phone: ");
                    String phone = sc.nextLine();
                    System.out.print("Enter Customer Address: ");
                    String address = sc.nextLine();

                    // Call createCustomerAndAccount to insert data into both tables
                    accountService.createCustomerAndAccount(username, password, accountType, balance, email, phone, address);
                }
                case 2 -> {
                    System.out.print("Enter Account ID for credit: ");
                    int accountId = sc.nextInt();
                    System.out.print("Enter Amount to Credit: ");
                    double amount = sc.nextDouble();
                    accountService.creditTransaction(accountId, amount);
                }
                case 3 -> {
                    System.out.print("Enter Account ID for debit: ");
                    int accountId = sc.nextInt();
                    System.out.print("Enter Amount to Debit: ");
                    double amount = sc.nextDouble();
                    accountService.debitTransaction(accountId, amount);
                }
                case 4 -> {
                    System.out.println("Logging out from admin.");
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }
}