package com.simplechat.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class ChatController {
    @FXML private Label usernameLabel;
    @FXML private ListView<String> contactsList;
    @FXML private Label currentChatLabel;
    @FXML private ListView<String> messagesList;
    @FXML private TextField messageInput;
    @FXML private Label userProfileName;

    private String currentContact;

    @FXML
    public void initialize() {
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
}