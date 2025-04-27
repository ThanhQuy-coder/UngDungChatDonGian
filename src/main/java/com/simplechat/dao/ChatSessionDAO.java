package com.simplechat.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ChatSessionDAO {
    private final Connection conn;

    public ChatSessionDAO(Connection conn){
        this.conn = conn;
    }

    public String getOrCreateSession(String userID1, String userID2) throws SQLException {
        // Kiểm tra xem user1Id và user2Id có tồn tại trong Users
        String sql = "SELECT userID FROM Users WHERE userID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userID1);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new SQLException("User with ID " + userID1 + " does not exist.");
            }
        }
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userID2);
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                throw new SQLException("User with ID " + userID2 + " does not exist.");
            }
        }

        sql = "SELECT sessionID FROM ChatSessions WHERE (userID1 = ? AND userID2 = ?) OR (userID1 = ? AND userID2 = ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userID1);
            stmt.setString(2, userID2);
            stmt.setString(3, userID2);
            stmt.setString(4, userID1);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("sessionID");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String sessionId = UUID.randomUUID().toString();
        sql = "INSERT INTO ChatSessions (sessionID, userID1, userID2) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            stmt.setString(2, userID1);
            stmt.setString(3, userID2);
            stmt.executeUpdate();
            return sessionId;
        }
    }
}
