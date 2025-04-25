package com.simplechat.controllers;

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
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ChatController {
    public Button contactsTabBtn;
    public Button requestsTabBtn;
    public StackPane tabContent;
    public VBox contactsPane;
    public VBox requestsPane;
    public TextField searchField;
    public ListView requestsList;
    public VBox sentPane;
    public ListView sentRequestsList;
    public Button sentTabBtn;
    @FXML private Label usernameLabel;
    @FXML private ListView<String> contactsList;
    @FXML private Label currentChatLabel;
    @FXML private ListView<String> messagesList;
    @FXML private TextField messageInput;
    @FXML private Label userProfileName;

    private String currentContact;

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

    private boolean sendFriendRequest(String email) {
        System.out.println("Đã gửi lời mời kết bạn đến: " + email);
        return true;
    }

    private void acceptFriendRequest(String email) {
        System.out.println("Đã chấp nhận lời mời kết bạn từ: " + email);
        contactsList.getItems().add(email + " (online)");
        requestsList.getItems().remove(email + " - accept/decline");
    }

    private void rejectFriendRequest(String email) {
        System.out.println("Đã từ chối lời mời kết bạn từ: " + email);
        requestsList.getItems().remove(email + " - accept/decline");
    }

    @FXML
    public void initialize() {
        // Tab mặc định
        showContactsTab();

        contactsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                currentContact = newVal.split(" ")[0];
                currentChatLabel.setText("Chat with " + currentContact);
                loadChatHistory(currentContact);
            }
        });
        List<String> friendRequests = List.of("Anna Taylor", "Chris Evans");

        for (String name : friendRequests) {
            Label nameLabel = new Label(name);
            nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

            Button acceptBtn = new Button("Đồng ý");
            acceptBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            acceptBtn.setOnAction(event -> {
                System.out.println("Đồng ý: " + name);
                // Xử lý accept ở đây
            });

            Button declineBtn = new Button("Từ chối");
            declineBtn.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
            declineBtn.setOnAction(event -> {
                System.out.println("Từ chối: " + name);
                // Xử lý decline ở đây
            });

            HBox row = new HBox(10, nameLabel, acceptBtn, declineBtn);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(5, 10, 5, 10));

            requestsList.getItems().add(row);
        }
    }

    public void setUsername(String username) {
        usernameLabel.setText(username);
        userProfileName.setText(username);
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

    private void loadChatHistory(String contact) {
        messagesList.getItems().clear();
        // TODO: Load từ database/service
        messagesList.getItems().add("Loaded chat history with " + contact);
    }

    @FXML
    public void showContactsTab() {
        contactsPane.setVisible(true);
        contactsPane.setManaged(true);

        requestsPane.setVisible(false);
        requestsPane.setManaged(false);

        sentPane.setVisible(false);
        sentPane.setManaged(false);

        contactsTabBtn.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-border-color: transparent transparent #1A237E transparent; -fx-border-width: 0 0 2 0;");
        requestsTabBtn.setStyle("-fx-background-color: transparent; -fx-font-weight: normal; -fx-border-color: transparent; -fx-border-width: 0 0 2 0;");
        sentTabBtn.setStyle("-fx-background-color: transparent; -fx-font-weight: normal; -fx-border-color: transparent; -fx-border-width: 0 0 2 0;");
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
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}