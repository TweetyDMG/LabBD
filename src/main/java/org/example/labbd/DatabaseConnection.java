package org.example.labbd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=FitnessCenter;encrypt=true;trustServerCertificate=true";
    private static final String USER = "Artem";
    private static final String PASSWORD = "1";

    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
