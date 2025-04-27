package com.simplechat.dao;

import com.simplechat.models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDAO {
    private final Connection conn;

    public UserDAO(Connection conn){
        this.conn = conn;
    }

    public boolean addUser(User user) throws SQLException{
        String id = UUID.randomUUID().toString();
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

        String sql = "INSERT INTO Users (userID, username, email, password) VALUES (?,?,?,?)";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, id);
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, hashedPassword);
            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        }
    }

    public boolean validateUser(String username, String password) throws SQLException{
        String sql = "SELECT password FROM Users WHERE username = ?";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()){
                String hashedPassword = rs.getString("password");

                if (BCrypt.checkpw(password, hashedPassword)){
                    return true;
                }
            }
        }
        return false;
    }

    public String getUserIdByEmail(String email) throws SQLException {
        String sql = "SELECT userID FROM Users WHERE email=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) { // conn phải được khởi tạo từ DAO
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("userID"); // Trả về userID nếu có
            }
        }
        throw new SQLException("User ID not found with email: " + email); // Báo lỗi nếu không thấy email
    }

    public String getEmailByUsername(String username) throws SQLException {
        String sql = "SELECT email FROM Users WHERE username=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                return rs.getString("email");
            }
        }
        throw new SQLException("Email with username not found: " + username);
    }

    public String getUsernameByEmail(String email) throws SQLException {
        String sql = "SELECT username FROM Users WHERE email=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                return rs.getString("username");
            }
        }
        throw new SQLException("Username with email not found: " + email);
    }

    public String getUsernameByID(String userID) throws SQLException {
        String sql = "SELECT username FROM Users WHERE userID=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, userID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                return rs.getString("username");
            }
        }
        throw new SQLException("Username with email not found: " + userID);
    }
}
