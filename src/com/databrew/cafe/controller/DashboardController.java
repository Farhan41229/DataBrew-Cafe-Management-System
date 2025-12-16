package com.databrew.cafe.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.io.IOException;

public class DashboardController {

    @FXML
    private BorderPane mainLayout;

    @FXML
    private Button fullscreenButton;

    @FXML
    private void handleGoToPOS(ActionEvent event) {
        loadView("PosView.fxml");
    }

    @FXML
    private void handleGoToMenu(ActionEvent event) {
        loadView("MenuView.fxml");
    }

    @FXML
    private void handleGoToEmployees(ActionEvent event) {
        loadView("EmployeeView.fxml");
    }

    @FXML
    private void handleGoToInventory(ActionEvent event) {
        loadView("InventoryView.fxml");
    }

    @FXML
    private void handleMinimize(ActionEvent event) {
        Stage stage = (Stage) mainLayout.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleFullscreen(ActionEvent event) {
        Stage stage = (Stage) mainLayout.getScene().getWindow();
        boolean goingFullScreen = !stage.isFullScreen();
        stage.setFullScreen(goingFullScreen);
        if (fullscreenButton != null) {
            fullscreenButton.setText(goingFullScreen ? "Exit Fullscreen" : "Fullscreen");
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/LoginView.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/theme.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Login - DataBrew Cafe");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to switch the center content of the dashboard
    private void loadView(String fxmlFile) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/fxml/" + fxmlFile));
            mainLayout.setCenter(view);
        } catch (IOException e) {
            System.err.println("Could not load view: " + fxmlFile);
            e.printStackTrace();
        }
    }
}
