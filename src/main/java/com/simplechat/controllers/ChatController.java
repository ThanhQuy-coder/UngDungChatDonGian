package com.simplechat.controllers;

import com.simplechat.dao.ChatSessionDAO;
import com.simplechat.dao.FriendDAO;
import com.simplechat.dao.MessageDAO;
import com.simplechat.dao.UserDAO;
import com.simplechat.database.Database;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ChatController {
    public Button contactsTabBtn;
    public Button requestsTabBtn;
    public Button sentTabBtn;
    public StackPane tabContent;
    public VBox contactsPane;
    public VBox requestsPane;
    public VBox sentPane;
    @FXML private TextField messageInput;
    public ListView requestsList;
    public ListView sentRequestsList;
    @FXML private ListView<String> contactsList;
    @FXML private ListView<String> messagesList;
    @FXML private Label usernameLabel;
    @FXML private Label currentChatLabel;
    @FXML private Label userProfileName;

    private String currentEmail;
    private String currentContact;
    private Timeline refreshTimeline;
    private String currentChatContactId; // ID của người bạn đang trò chuyện
    private String currentUserId; // ID của người dùng hiện tại


    private boolean sendFriendRequest(String receiverEmail) throws SQLException {
        try (Connection conn = Database.getConnection()){
            UserDAO userDAO = new UserDAO(conn);
            FriendDAO friendDAO = new FriendDAO(conn);
            String senderId = userDAO.getUserIdByEmail(currentEmail);
            String receiverId = userDAO.getUserIdByEmail(receiverEmail); // Lấy userId người nhận từ email

            System.out.println("Friend request sent from userID: " + senderId + " to userID: " + receiverId);
            return friendDAO.sendFriendRequest(senderId, receiverId);
        }
    }

    private boolean acceptFriendRequest(String receiverEmail) throws SQLException {
        try (Connection conn = Database.getConnection()){
            UserDAO userDAO = new UserDAO(conn);
            FriendDAO friendDAO = new FriendDAO(conn);
            String senderId = userDAO.getUserIdByEmail(receiverEmail);
            String receiverId = userDAO.getUserIdByEmail(currentEmail);

            System.out.println("Accept friend request from userID: " + senderId + " to userID: " + receiverId);
            return friendDAO.acceptRequest(senderId, receiverId);
        }
    }

    private boolean declineFriendRequest(String receiverEmail) throws SQLException {
        try (Connection conn = Database.getConnection()){
            UserDAO userDAO = new UserDAO(conn);
            FriendDAO friendDAO = new FriendDAO(conn);
            String senderId = userDAO.getUserIdByEmail(receiverEmail);
            String receiverId = userDAO.getUserIdByEmail(currentEmail);

            System.out.println("Decline friend request from userID: " + senderId + " to userID: " + receiverId);
            return friendDAO.declineRequest(senderId, receiverId);
        }
    }

    @FXML
    public void initialize() {
        // Tab mặc định
        showContactsTab();

        // Listener cho danh sách bạn bè (contactsList)
        contactsList.setOnMouseClicked(event -> {
            // Lấy giá trị được chọn
            String selectedContact = contactsList.getSelectionModel().getSelectedItem();

            if (selectedContact != null) {
                currentContact = selectedContact; // Cập nhật currentContact
                currentChatLabel.setText("Chat với: " + currentContact); // Hiển thị thông tin
                System.out.println("Current Contact Selected: " + currentContact); // Debug log
                try {
                    setUserContext();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        // Khởi tạo timeline chỉ khi currentContact hợp lệ
        refreshTimeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            if (currentContact != null && !currentContact.isEmpty()) {
                refreshChatMessages();
            }
        }));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    /**
     * Thiết lập thông tin người dùng hiện tại và danh bạ đang trò chuyện.
     */
    public void setUserContext() throws SQLException {
        Connection conn = Database.getConnection();
        UserDAO userDAO = new UserDAO(conn);
        this.currentUserId = userDAO.getUserIdByEmail(currentEmail);
        this.currentChatContactId = userDAO.getUserIdByEmail(userDAO.getEmailByUsername(currentContact));

        // Tải lại lịch sử tin nhắn ngay khi thay đổi danh bạ
        refreshChatMessages();
    }

    private void refreshChatMessages() {
        if (currentUserId == null || currentChatContactId == null) {
            System.err.println("User ID or Contact ID is not set.");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            MessageDAO messageDAO = new MessageDAO(conn);

            // Lấy tin nhắn trong cuộc trò chuyện giữa currentUserId và currentChatContactId
            List<String> messages = messageDAO.loadMessages(currentUserId, currentChatContactId);

            // Cập nhật giao diện với các tin nhắn
            messagesList.getItems().clear();
            messagesList.getItems().addAll(messages);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void stopAutoReload() {
        if (refreshTimeline != null) {
            refreshTimeline.stop();
        }
    }

    @FXML
    private void startAutoReload() {
        if (refreshTimeline != null) {
            refreshTimeline.play();
        }
    }

    public void setUsername(String username) {
        usernameLabel.setText(username);
        userProfileName.setText(username);
    }

    public void setEmail(String email){
        this.currentEmail = email;
        showFriends(currentEmail);
    }

    private void showFriendRequests(String currentEmail) {

        requestsList.getItems().clear();
        List<String> listFriendRequests;

        try (Connection conn = Database.getConnection()) {
            FriendDAO friendDAO = new FriendDAO(conn);
            UserDAO userDAO = new UserDAO(conn);
            listFriendRequests = friendDAO.getReceivedRequestsByEmail(userDAO.getUserIdByEmail(currentEmail));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (listFriendRequests.isEmpty()) {System.out.println("listFriendRequests is empty!");}

        for (String nameEmail : listFriendRequests){
            Label nameLabel = new Label(nameEmail);
            nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

            Button acceptBtn = new Button("Yes");
            acceptBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            acceptBtn.setOnAction(event -> {
                System.out.println("Yes: " + nameEmail);
                try {
                    boolean success = acceptFriendRequest(nameEmail);
                    if (success){
                        System.out.println("Friend request accepted successfully!");
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Friend request accepted successfully!");

                        // Tạo phiên trò chuyện
                        try (Connection conn = Database.getConnection()) {
                            ChatSessionDAO chatSessionDAO = new ChatSessionDAO(conn);
                            UserDAO userDAO = new UserDAO(conn);
                            if (chatSessionDAO.getOrCreateSession(userDAO.getUserIdByEmail(currentEmail), userDAO.getUserIdByEmail(nameEmail)) != null) {
                                System.out.println("Chat session created successfully!");
                            }
                            else {
                                System.out.println("Chat session creation failed!");
                            }
                        }

                        requestsList.getItems().remove(nameLabel.getParent());
                    }
                    else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Friend request accepted incorrectly!");
                        System.out.println("Friend request accepted incorrectly");
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            Button declineBtn = new Button("No");
            declineBtn.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
            declineBtn.setOnAction(event -> {
                System.out.println("No: " + nameEmail);
                try {
                    boolean success = declineFriendRequest(nameEmail);
                    if (success){
                        System.out.println("Friend request declined successfully!");
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Friend request declined successfully!");

                        requestsList.getItems().remove(nameLabel.getParent());
                    }
                    else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Friend request declined incorrectly!");
                        System.out.println("Friend request declined incorrectly!");
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            HBox row = new HBox(10, nameLabel, acceptBtn, declineBtn);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(5, 10, 5, 10));

            requestsList.getItems().add(row);
        }
    }
    
    private void showSentFriendRequests(String currentEmail) {
        sentRequestsList.getItems().clear();
        List<String> listSentRequests;

        try (Connection conn = Database.getConnection()) {
            FriendDAO friendDAO = new FriendDAO(conn);
            UserDAO userDAO = new UserDAO(conn);
            listSentRequests = friendDAO.getSentRequestsByEmail(userDAO.getUserIdByEmail(currentEmail));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (listSentRequests.isEmpty()) {System.out.println("listSentRequestsByID is empty!");}

        for (String nameEmail : listSentRequests) {
            /* Tạo một dòng giao diện cho mỗi yêu cầu gửi */
            Label nameLabel = new Label(nameEmail); // Tên người nhận yêu cầu kết bạn
            nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

            // Nút để hủy yêu cầu (tùy chọn thêm logic xử lý)
            Button cancelBtn = new Button("Cancel");
            cancelBtn.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
            cancelBtn.setOnAction(event -> {
                System.out.println("Canceled friend request to: " + nameEmail);
                try(Connection conn = Database.getConnection()){
                    FriendDAO friendDAO = new FriendDAO(conn);
                    UserDAO userDAO = new UserDAO(conn);
                    if(friendDAO.cancelSentRequest(userDAO.getUserIdByEmail(this.currentEmail), userDAO.getUserIdByEmail(nameEmail))) {
                        System.out.println("Friend request canceled successfully!");
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Friend request canceled successfully!");
                    }
                    else {
                        showAlert(Alert.AlertType.ERROR, "Error", "Friend request canceled incorrectly!");
                        System.out.println("Friend request canceled incorrectly!");
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                sentRequestsList.getItems().remove(nameLabel.getParent());
            });

            // Gộp nhãn và nút thành một dòng giao diện
            HBox row = new HBox(10, nameLabel, cancelBtn);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(5, 10, 5, 10));

            // Thêm dòng này vào danh sách giao diện (sentRequestsList)
            sentRequestsList.getItems().add(row);
        }
        System.out.println("Action case showSentFriendRequests");
    }

    private void showFriends(String currentEmail) {
        contactsList.getItems().clear();

        if (currentEmail == null || currentEmail.isBlank()) {
            System.err.println("Error: Invalid email. Cannot retrieve friends list.");
            return;
        }

        List<String> listFriends;

        try (Connection conn = Database.getConnection()){
            FriendDAO friendDAO = new FriendDAO(conn);
            UserDAO userDAO = new UserDAO(conn);

            // Fetch friends list
            String userId = String.valueOf(userDAO.getUserIdByEmail(currentEmail));
            if (userId == null) {
                System.err.println("Error: No user ID found for the provided email: " + currentEmail);
                return; // No user ID found, exit the method
            }

            listFriends = friendDAO.getFriendsList(userDAO.getUserIdByEmail(currentEmail));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (listFriends.isEmpty()) {System.out.println("listFriends is empty!");}
        else {
            for (String nameEmail : listFriends) {
                Label nameLabel = new Label(nameEmail);
                nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
                contactsList.getItems().add(nameEmail);
            }
            System.out.println("Action case showFriends");
        }
    }

    @FXML
    private void handleSendFriendRequest() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Send friend request");
        dialog.setHeaderText("Enter the email of the person you want to be friends with:");
        dialog.setContentText("Email:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(email -> {
            boolean success;
            try {
                success = sendFriendRequest(email); // Kiểm tra gửi kết bạn
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Invitation has been sent " + email);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Unable to send invitation!");
            }
        });
    }

    @FXML
    private void handleSendMessage() {
        String message = messageInput.getText().trim();

        if (!message.isEmpty() && currentContact != null) {
            messagesList.getItems().add(userProfileName + ": " + message);
            messageInput.clear();
            // TODO: Gửi tin nhắn
            try {
                sendMessage(message);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            messagesList.scrollTo(messagesList.getItems().size() - 1);
        }
    }

    private void sendMessage(String message) throws SQLException {
        try (Connection connection = Database.getConnection()){
            MessageDAO messageDAO = new MessageDAO(connection);
            UserDAO userDAO = new UserDAO(connection);

            String currentContactEmail = userDAO.getEmailByUsername(currentContact);

            if (messageDAO.insertMessage(message, currentEmail, currentContactEmail)){
                System.out.println("Message sent successfully!");
            }
            else {
                System.out.println("Message sent incorrectly!");
            }
        }
    }

    @FXML
    private void handleLogout() {
        stopAutoReload();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/simplechat/views/sign_in.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sign In");
            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading sign in screen: " + e.getMessage());
        }
    }

    @FXML
    private void handleChangeAvatar() {
        // TODO: Implement avatar change
        System.out.println("Change Avatar clicked");
    }

    @FXML
    public void showContactsTab() {
        if (currentEmail == null || currentEmail.isBlank()) {
            return; // Prevent further execution if the email is invalid
        }

        contactsPane.setVisible(true);
        contactsPane.setManaged(true);

        requestsPane.setVisible(false);
        requestsPane.setManaged(false);

        sentPane.setVisible(false);
        sentPane.setManaged(false);

        contactsTabBtn.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-border-color: transparent transparent #1A237E transparent; -fx-border-width: 0 0 2 0;");
        requestsTabBtn.setStyle("-fx-background-color: transparent; -fx-font-weight: normal; -fx-border-color: transparent; -fx-border-width: 0 0 2 0;");
        sentTabBtn.setStyle("-fx-background-color: transparent; -fx-font-weight: normal; -fx-border-color: transparent; -fx-border-width: 0 0 2 0;");

        showFriends(currentEmail);
        startAutoReload();
    }

    @FXML
    public void showRequestsTab() {
        contactsPane.setVisible(false);
        contactsPane.setManaged(false);

        requestsPane.setVisible(true);
        requestsPane.setManaged(true);

        sentPane.setVisible(false);
        sentPane.setManaged(false);
        contactsTabBtn.setStyle("-fx-background-color: transparent; -fx-font-weight: normal; -fx-border-color: transparent; -fx-border-width: 0 0 2 0;");
        requestsTabBtn.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-border-color: transparent transparent #1A237E transparent; -fx-border-width: 0 0 2 0;");
        sentTabBtn.setStyle("-fx-background-color: transparent; -fx-font-weight: normal; -fx-border-color: transparent; -fx-border-width: 0 0 2 0;");

        showFriendRequests(currentEmail);
        stopAutoReload();
    }

    public void showSentTab() {
        contactsPane.setVisible(false);
        contactsPane.setManaged(false);

        requestsPane.setVisible(false);
        requestsPane.setManaged(false);

        sentPane.setVisible(true);
        sentPane.setManaged(true);
        contactsTabBtn.setStyle("-fx-background-color: transparent; -fx-font-weight: normal; -fx-border-color: transparent; -fx-border-width: 0 0 2 0;");
        requestsTabBtn.setStyle("-fx-background-color: transparent; -fx-font-weight: normal; -fx-border-color: transparent; -fx-border-width: 0 0 2 0;");
        sentTabBtn.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-border-color: transparent transparent #1A237E transparent; -fx-border-width: 0 0 2 0;");

        // Gọi hàm hiển thị danh sách đã gửi (sử dụng email của người dùng hiện tại)
        showSentFriendRequests(currentEmail);
        stopAutoReload();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleRefreshRequests(ActionEvent actionEvent) {
        showFriendRequests(currentEmail);
    }

    public void handleRefreshSent(ActionEvent actionEvent) {
        showSentFriendRequests(currentEmail);
    }
}