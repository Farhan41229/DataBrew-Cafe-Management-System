package com.databrew.cafe.controller;

import com.databrew.cafe.model.User;
import com.databrew.cafe.service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        // Clear error message when user starts typing
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> clearError());
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> clearError());
    }

    @FXML
    public void onLogin(ActionEvent event) {
        // Clear previous error styling
        clearFieldErrors();

        // Validate inputs
        String validationError = validateInputs();
        if (validationError != null) {
            showError(validationError);
            return;
        }

        try {
            String username = usernameField.getText().trim();
            String password = passwordField.getText();

            User user = authService.authenticate(username, password);
            
            if (user == null) {
                showError("Invalid username or password. Please try again.");
                highlightFieldError(usernameField);
                highlightFieldError(passwordField);
                return;
            }

            // Successful login - navigate to dashboard
            navigateToDashboard(user);

        } catch (Exception e) {
            String errorMessage = "Login failed: ";
            if (e.getMessage() != null && e.getMessage().contains("Communications link failure")) {
                errorMessage += "Unable to connect to database. Please check your connection.";
            } else if (e.getMessage() != null) {
                errorMessage += e.getMessage();
            } else {
                errorMessage += "An unexpected error occurred. Please try again.";
            }
            showError(errorMessage);
            e.printStackTrace();
        }
    }

    /**
     * Validates login form inputs
     * @return error message if validation fails, null if validation passes
     */
    private String validateInputs() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Check for empty username
        if (username == null || username.trim().isEmpty()) {
            highlightFieldError(usernameField);
            return "Username is required.";
        }

        // Check for empty password
        if (password == null || password.isEmpty()) {
            highlightFieldError(passwordField);
            return "Password is required.";
        }

        // Check username length (minimum 3 characters)
        if (username.trim().length() < 3) {
            highlightFieldError(usernameField);
            return "Username must be at least 3 characters long.";
        }

        // Check password length (minimum 6 characters)
        if (password.length() < 6) {
            highlightFieldError(passwordField);
            return "Password must be at least 6 characters long.";
        }

        // Check for invalid characters in username
        if (!username.matches("^[a-zA-Z0-9_.-]+$")) {
            highlightFieldError(usernameField);
            return "Username can only contain letters, numbers, underscores, dots, and hyphens.";
        }

        return null; // Validation passed
    }

    /**
     * Highlights a field with error styling
     */
    private void highlightFieldError(TextField field) {
        field.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2px;");
    }

    /**
     * Clears error styling from all fields
     */
    private void clearFieldErrors() {
        usernameField.setStyle("");
        passwordField.setStyle("");
    }

    /**
     * Displays an error message
     */
    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        }
    }

    /**
     * Clears the error message
     */
    private void clearError() {
        if (errorLabel != null) {
            errorLabel.setText("");
        }
        clearFieldErrors();
    }

    /**
     * Navigates to the dashboard after successful login
     */
    private void navigateToDashboard(User user) throws Exception {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DashboardView.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());
        
        com.databrew.cafe.controller.DashboardController controller = loader.getController();
        if (controller != null) {
            controller.setCurrentUser(user);
        }
        
        stage.setScene(scene);
        stage.setTitle("DataBrew Cafe Dashboard");
    }
}
