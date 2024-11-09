package com.sece;
import java.sql.*;

public class DBConnect {
	private static final String url = "jdbc:mysql://localhost:3306/banking_db";
    private static final String user = "root";
    private static final String password = "My@sql21";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
        
    }
}