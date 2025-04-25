package com.simplechat.controllers;

import com.simplechat.dao.userDAO;
import com.simplechat.database.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class SignInController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Text errorMessage;
    @FXML
    private Button signInButton;

    public void setRoundedButton() {
        if (signInButton != null) {
            signInButton.setStyle("-fx-background-radius: 20px;");
        } else {
            System.out.println("Button is null!");
        }
    }

    @FXML
    public void initialize() {
        // Bo góc cho các ô nhập liệu và nút đăng ký
        setRoundedStyle(usernameField);
        setRoundedStyle(passwordField);

        // Xóa thông báo lỗi khi nhập lại
        usernameField.setOnKeyTyped(e -> clearError());
        passwordField.setOnKeyTyped(e -> clearError());
    }

    private void clearError() {
        errorMessage.setText("");
    }

    private void setRoundedStyle(TextField textField) {
        textField.setStyle(
                "-fx-background-radius: 10px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-border-color: #ccc; " +
                        "-fx-padding: 8px;"
        );
    }

    private void setRoundedButton(Button button) {
        button.setStyle(
                "-fx-background-radius: 10px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-background-color: #3949AB; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16px; " +
                        "-fx-padding: 8px 16px;"
        );
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try (Connection conn = Database.getConnection()) {
            userDAO userDAO = new userDAO(conn);
            boolean success = userDAO.validateUser(username, password);

            if (success) {
                errorMessage.setText("Login successful!");
                errorMessage.setFill(Color.GREEN);

                // Load chat.fxml
                Stage stage = (Stage) usernameField.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/simplechat/views/chat.fxml"));

                // Check if FXML file exists
                if (fxmlLoader.getLocation() == null) {
                    throw new IOException("Cannot find chat.fxml");
                }

                Scene scene = new Scene(fxmlLoader.load());

                // Get ChatController instance to initialize it
                ChatController chatController = fxmlLoader.getController();
                if (chatController != null) {
                    chatController.setUsername(username); // Pass username to ChatController
                }

                stage.setScene(scene);
                stage.setTitle("App Chat");
                stage.show();
            } else {
                errorMessage.setText("Invalid username or password.");
                errorMessage.setFill(Color.RED);
            }
        } catch (IOException e) {
            errorMessage.setText("Error loading chat interface.");
            errorMessage.setFill(Color.RED);
            e.printStackTrace();
        } catch (Exception e) {
            errorMessage.setText("An error occurred during login.");
            errorMessage.setFill(Color.RED);
            e.printStackTrace();
        }
    }

    @FXML
    private void openSignUp() throws IOException {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/simplechat/views/sign_up.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setTitle("Sign Up");
    }
}
