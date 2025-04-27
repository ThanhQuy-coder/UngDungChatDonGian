package com.simplechat.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendDAO {

    private final Connection conn;

    public FriendDAO(Connection conn) {
        this.conn = conn;
    }

    // xử lý gửi yêu cầu kết bạn
    public boolean sendFriendRequest(String senderId, String receiverId) throws SQLException{
        try {
            if (checkFriendStatus(senderId, receiverId) != null) {
                System.err.println("You have made friends with this person");
                return false;
            }

            String sql = "INSERT INTO Friendships (userID1, userID2, status) VALUES (?,?,'pending')";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, senderId);
                stmt.setString(2, receiverId);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log lỗi hoặc ném lại Exception sau khi xử lý
            throw e;
        }
    }

    // xử lý chấp nhận lời mời
    public boolean acceptRequest(String senderId, String receiverId) throws SQLException {
        String sql = "UPDATE Friendships SET status='accepted' WHERE userID1=? AND userID2=? AND status='pending'";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, senderId);
            stmt.setString(2, receiverId);
            return stmt.executeUpdate() > 0;
        }
    }

    // xử lý từ chối lời mời
    public boolean declineRequest(String senderId, String receiverId) throws SQLException {
        String sql = "DELETE Friendships WHERE userID1=? AND userID2=? AND status='pending'";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, senderId);
            stmt.setString(2, receiverId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Kiểm tra điều kiện kết bạn
    public String checkFriendStatus(String userId1, String userId2) throws SQLException {
        if (userId1.equals(userId2)) {
            System.err.println("You are friends with yourself");
            return "You are friends with yourself";
        }
        // Kiểm tra đã có kết bạn chưa
        String sql = "SELECT status FROM Friendships WHERE (userID1=? AND userID2=?) OR (userID1=? AND userID2=?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, userId1);
            stmt.setString(2, userId2);
            stmt.setString(3, userId2);
            stmt.setString(4, userId1);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getString("status");
            }
        }
        return null;
    }

    // Kiểm tra trạng thái kết bạn
    public String checkFriendshipStatus(String userId1, String userId2) throws SQLException {
        String sql = "SELECT status FROM Friendships WHERE (userID1=? AND userID2=?) OR (userID1=? AND userID2=?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, userId1);
            stmt.setString(2, userId2);
            stmt.setString(3, userId2);
            stmt.setString(4, userId1);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return rs.getString("status");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // Lấy yêu cầu kết bạn từ người gửi
    public List<String> getSentRequestsByEmail(String currentID) throws SQLException {
        String sql = "SELECT u.email " +
                "FROM Friendships f " +
                "JOIN Users u ON f.userID2 = u.userID " +
                "WHERE f.userID1 = (SELECT userID FROM Users WHERE userID = ?) AND f.status = 'pending'";
        List<String> ListSentRequestsByID = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, currentID); // Thay thế tham số ? bằng email của người dùng
            ResultSet rs = stmt.executeQuery();

            // Duyệt qua kết quả trả về để thêm vào danh sách
            while (rs.next()) {
                String receiverEmail = rs.getString("email");
                ListSentRequestsByID.add(receiverEmail); // Thêm thông tin (email người nhận) vào danh sách
            }
        }

        return ListSentRequestsByID; // Trả về danh sách
    }

    // Hủy yêu cầu đã gửi
    public boolean cancelSentRequest(String senderId, String receiverId) throws SQLException {
        String sql = "DELETE FROM Friendships WHERE userID1=? AND userID2=? AND status='pending'";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, senderId);
            stmt.setString(2, receiverId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Lấy yêu cầu kết bạn từ người nhận
    public List<String> getReceivedRequestsByEmail(String currentID) throws SQLException {
        String sql = "SELECT u.email " +
                "FROM Friendships f " +
                "JOIN Users u ON f.userID1 = u.userID " +
                "WHERE f.userID2 = (SELECT userID FROM Users WHERE userID = ?) AND f.status = 'pending'";
        List<String> ListReceivedRequests = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, currentID); // Thay thế tham số ? bằng email của người dùng
            ResultSet rs = stmt.executeQuery();

            // Duyệt qua kết quả trả về để thêm vào danh sách
            while (rs.next()) {
                String receiverEmail = rs.getString("email");
                ListReceivedRequests.add(receiverEmail); // Thêm thông tin (email người nhận) vào danh sách
            }
        }

        return ListReceivedRequests; // Trả về danh sách
    }

    // Lấy danh sách bạn
    public List<String> getFriendsList(String currentID) throws SQLException {
        String sql = "SELECT u.username " +
                "FROM Friendships f " +
                "JOIN Users u ON (f.userID1 = ? AND f.userID2 = u.userID) OR (f.userID2 = ? AND f.userID1 = u.userID) " +
                "WHERE f.status = 'accepted'";
        List<String> ListFriendsList = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, currentID);
            stmt.setString(2, currentID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String username = rs.getString("username");
                ListFriendsList.add(username);
            }
        }

        return ListFriendsList;
    }
}
