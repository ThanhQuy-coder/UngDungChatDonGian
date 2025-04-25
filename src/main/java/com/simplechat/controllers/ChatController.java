package com.simplechat.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class ChatController {
    public VBox contactsPane;
    public VBox requestsPane;
    @FXML private Label usernameLabel;
    @FXML private ListView<String> contactsList;
    @FXML private Label currentChatLabel;
    @FXML private ListView<String> messagesList;
    @FXML private TextField messageInput;
    @FXML private Label userProfileName;

    // Mới thêm:
    @FXML private ListView<String> requestsList;
    @FXML private TextField searchField;
    @FXML private Button contactsTabBtn;
    @FXML private Button requestsTabBtn;
    @FXML private StackPane tabContent;

    private String currentContact;

    @FXML
    public void initialize() {
        // Tab mặc định
        showRequestsTab();

        // Lắng nghe click contact
        contactsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                currentContact = newVal.split(" ")[0];
                currentChatLabel.setText("Chat with " + currentContact);
                loadChatHistory(currentContact);
            }
        });
    }

    public void setUsername(String username) {
        usernameLabel.setText(username);
        userProfileName.setText(username);
    }

    @FXML
    private void handleSendMessage() {
        String message = messageInput.getText().trim();
        if (!message.isEmpty() && currentContact != null) {
            messagesList.getItems().add("You: " + message);
            messageInput.clear();
            // TODO: Gửi tin nhắn
            messagesList.scrollTo(messagesList.getItems().size() - 1);
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/web_chatdongian/views/sign_in.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Sign In");
        } catch (IOException e) {
            System.err.println("Error loading sign in screen: " + e.getMessage());
        }
    }

    @FXML
    private void handleChangeAvatar() {
        // TODO: Implement avatar change
        System.out.println("Change Avatar clicked");
    }

    private void loadChatHistory(String contact) {
        messagesList.getItems().clear();
        // TODO: Load từ database/service
        messagesList.getItems().add("Loaded chat history with " + contact);
    }

    public void receiveMessage(String sender, String message) {
        messagesList.getItems().add(sender + ": " + message);
        messagesList.scrollTo(messagesList.getItems().size() - 1);
    }

    @FXML
    private void handleSendFriendRequest(javafx.event.ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Gửi lời mời kết bạn");
        dialog.setHeaderText("Nhập email người bạn muốn kết bạn:");
        dialog.setContentText("Email:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(email -> {
            boolean success = sendFriendRequest(email);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Lời mời đã được gửi đến " + email);
            } else {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể gửi lời mời!");
            }
        });
    }

    private boolean sendFriendRequest(String email) {
        System.out.println("Đã gửi lời mời kết bạn đến: " + email);
        return true;
    }

    public void showFriendRequestDialog(String senderEmail) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Lời mời kết bạn");
        alert.setHeaderText(senderEmail + " muốn kết bạn với bạn!");
        alert.setContentText("Bạn có đồng ý không?");

        ButtonType buttonTypeYes = new ButtonType("Đồng ý", ButtonBar.ButtonData.YES);
        ButtonType buttonTypeNo = new ButtonType("Từ chối", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeYes) {
            acceptFriendRequest(senderEmail);
        } else {
            rejectFriendRequest(senderEmail);
        }
    }

    private void acceptFriendRequest(String email) {
        System.out.println("Đã chấp nhận lời mời từ: " + email);
        contactsList.getItems().add(email + " (online)");
        requestsList.getItems().remove(email + " - accept/decline");
    }

    private void rejectFriendRequest(String email) {
        System.out.println("Đã từ chối lời mời từ: " + email);
        requestsList.getItems().remove(email + " - accept/decline");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ============================
    // Tab Switching Logic
    // ============================
    @FXML
    public void showContactsTab() {
        contactsList.setVisible(true);
        requestsList.setVisible(false);
        searchField.setVisible(false);

        contactsTabBtn.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-border-color: transparent transparent #1A237E transparent; -fx-border-width: 0 0 2 0;");
        requestsTabBtn.setStyle("-fx-background-color: transparent; -fx-font-weight: normal; -fx-border-color: transparent; -fx-border-width: 0 0 2 0;");
    }

    @FXML
    public void showRequestsTab() {
        contactsList.setVisible(false);
        requestsList.setVisible(true);
        searchField.setVisible(true);

        contactsTabBtn.setStyle("-fx-background-color: transparent; -fx-font-weight: normal; -fx-border-color: transparent; -fx-border-width: 0 0 2 0;");
        requestsTabBtn.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-border-color: transparent transparent #1A237E transparent; -fx-border-width: 0 0 2 0;");
    }

    // (Optional) Add friend request to list
    public void addFriendRequest(String email) {
        requestsList.getItems().add(email + " - accept/decline");
    }
}
