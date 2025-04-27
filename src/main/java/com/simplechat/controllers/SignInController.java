package com.simplechat.controllers;

import com.simplechat.dao.UserDAO;
import com.simplechat.database.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
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

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try (Connection conn = Database.getConnection()) {
            UserDAO userDAO = new UserDAO(conn);
            boolean success = userDAO.validateUser(username, password);

            if (success) {
                errorMessage.setText("Login successful!");
                errorMessage.setFill(Color.GREEN);

                // Chuyển đến chat.fxml
                Stage stage = (Stage) usernameField.getScene().getWindow(); // Lấy stage hiện tại
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/simplechat/views/chat.fxml"));
                Scene scene = new Scene(fxmlLoader.load());

                // Gửi username, currentEmail vào ChatController (nếu cần)
                ChatController chatController = fxmlLoader.getController();

                chatController.setEmail(userDAO.getEmailByUsername(username));
                chatController.setUsername(username);

                // Đặt giao diện mới
                stage.setScene(scene);
                stage.setTitle("Chat Application");
                stage.show();
            } else {
                errorMessage.setText("Invalid username or password.");
                errorMessage.setFill(Color.RED);
            }
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

    public void handleEnterPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().toString().equals("ENTER")) {
            handleLogin(); // Gọi phương thức đăng nhập khi nhấn Enter
        }
    }
}
