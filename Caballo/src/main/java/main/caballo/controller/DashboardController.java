package main.caballo.controller;

import main.caballo.CaballoApplication;
import main.caballo.model.Role;
import main.caballo.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class DashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Button reportsBtn;
    @FXML private Button usersBtn;

    private User currentUser;

    public void init(User user) {
        this.currentUser = user;
        welcomeLabel.setText("Welcome, " + user.getUsername() + " (" + user.getRole() + ")");
        boolean isAdmin = user.getRole() == Role.ADMIN;
        usersBtn.setVisible(isAdmin);
        reportsBtn.setVisible(isAdmin);
    }

    @FXML
    private void openMenu(ActionEvent e) {
        loadScene("/main/caballo/view/menu.fxml");
    }

    @FXML
    private void openOrders(ActionEvent e) {
        loadScene("/main/caballo/view/orders.fxml");
    }

    @FXML
    private void openReservations(ActionEvent e) {
        loadScene("/main/caballo/view/reservations.fxml");
    }

    @FXML
    private void openUsers(ActionEvent e) {
        loadScene("/main/caballo/view/users.fxml");
    }

    @FXML
    private void logout(ActionEvent e) {
        CaballoApplication.showLogin();
    }

    private void loadScene(String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Scene scene = welcomeLabel.getScene();
            scene.setRoot(root);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load: " + fxml, ex);
        }
    }

}
