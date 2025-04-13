package com.simplechat.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private final static String URL = "jdbc:sqlserver://localhost:1433;databaseName=ChatApp;encrypt=true;trustServerCertificate=true";
    private final static String USER = "sa"; // Thay bằng username SQL Server
    private final static String PASSWORD = "htq@12a2sqlserver"; // Thay bằng mật khẩu SQL Server
    private static Connection connection;

    // Lấy kết nối
    public static Connection getConnection() {
        try{
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
            return connection;
        } catch (SQLException e){
            System.err.println("Error while getting the database connection: " + e.getMessage());
            e.printStackTrace(); // in chi tiết lỗi
            return null;
        }
    }

    // Đóng kết nối
    public static void closeConnection() {
        try{
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e){
            System.err.println("Error while closing the database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveUser(){

    }

    public void saveMessage(){

    }

    public void getUser(){

    }

    public void getMessage(){

    }
}
