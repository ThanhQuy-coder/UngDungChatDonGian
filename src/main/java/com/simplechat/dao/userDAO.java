package com.simplechat.dao;

import com.simplechat.models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class userDAO {
    private Connection conn;

    public userDAO(Connection conn){
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
        String sql = "SELECT password FROM users WHERE username = ?";
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
}
