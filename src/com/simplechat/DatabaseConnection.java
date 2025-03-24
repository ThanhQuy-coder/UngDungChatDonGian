package src.com.simplechat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=ChatDonGian;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa"; // Thay bằng username SQL Server
    private static final String PASSWORD = "123456789"; // Thay bằng mật khẩu SQL Server

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Kết nối thành công!");
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối: " + e.getMessage());
        }
        return connection;
    }
}
