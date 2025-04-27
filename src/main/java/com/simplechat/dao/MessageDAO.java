package com.simplechat.dao;

import com.simplechat.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessageDAO {

    private final Connection conn;

    public MessageDAO(Connection conn){
        this.conn = conn;
    }

    public boolean insertMessage(String message, String currentEmail, String currentContact){
        try (Connection conn = Database.getConnection()) {
            UserDAO userDAO = new UserDAO(conn);
            ChatSessionDAO sessionDAO = new ChatSessionDAO(conn);
            String senderId = userDAO.getUserIdByEmail(currentEmail);
            String receiverId = userDAO.getUserIdByEmail(currentContact);
            String sessionId = sessionDAO.getOrCreateSession(senderId, receiverId);

            String sql = "INSERT INTO Messages (messageID, sessionID, senderID, receiverID, content) VALUES (?, ?, ?, ?, ?)";
            assert conn != null;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, UUID.randomUUID().toString());
                stmt.setString(2, sessionId);
                stmt.setString(3, senderId);
                stmt.setString(4, receiverId);
                stmt.setString(5, message);
                return stmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Trong class MessageDAO
    public List<String> loadMessages(String senderId, String receiverId) throws SQLException {
        List<String> messages = new ArrayList<>();

        // Tìm session giữa sender và receiver
        String sessionId = new ChatSessionDAO(conn).getOrCreateSession(senderId, receiverId);

        // Lấy tin nhắn dựa trên sessionID
        String sql = "SELECT content, senderID FROM Messages WHERE sessionID = ? ORDER BY timestamp";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sessionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String content = rs.getString("content");
                String sender = rs.getString("senderID");

                // Format tin nhắn - phân biệt sender và receiver
                String display = sender.equals(senderId) ? "You: " + content : "Friend: " + content;

                // Thêm tin nhắn vào danh sách
                messages.add(display);
            }
        }
        return messages; // Trả về danh sách tin nhắn
    }
}
