package com.simplechat.controllers;

import com.simplechat.dao.UserDAO;
import com.simplechat.database.Database;
import com.simplechat.models.User;
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
import java.sql.SQLException;

public class SignUpController {
    @FXML
    private Text errorMessage;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button signUpButton;

    @FXML
    public void initialize() {
        // Bo góc cho các ô nhập liệu và nút đăng ký
        setRoundedStyle(usernameField);
        setRoundedStyle(passwordField);
        setRoundedStyle(emailField);
        setRoundedStyle(confirmPasswordField);
        setRoundedButton(signUpButton);

        // Xóa thông báo lỗi khi nhập lại
        usernameField.setOnKeyTyped(e -> clearError());
        passwordField.setOnKeyTyped(e -> clearError());
        confirmPasswordField.setOnKeyTyped(e -> clearError());
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

    private void clearError() {
        errorMessage.setText("");
    }

    @FXML
    private void handleSignUp() throws SQLException {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String email = emailField.getText().trim();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        } else if (password.length() < 6) {
            showError("Password must be at least 6 characters.");
            return;
        } else if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        User user = new User(username, email, password);

        try(Connection conn = Database.getConnection()) {
            UserDAO UserDAO = new UserDAO(conn);
            boolean success = UserDAO.addUser(user);

            if(success){
                showSuccess("Sign in successful!");
                openSignIn();
            }
            else{
                showError("Sign in failed. Try again.");
            }
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorMessage.setText(message);
        errorMessage.setFill(Color.RED);
    }

    private void showSuccess(String message) {
        errorMessage.setText(message);
        errorMessage.setFill(Color.GREEN);
    }

    @FXML
    private void openSignIn() throws IOException {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/simplechat/views/sign_in.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setTitle("Sign In");
    }
}
