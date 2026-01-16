package main.caballo.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import main.caballo.CaballoApplication;
import main.caballo.dao.ReportDao;
import main.caballo.dao.impl.ReportDaoImpl;
import main.caballo.model.Role;
import main.caballo.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class DashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Button usersBtn;

    @FXML private PieChart topItemsPieChart;
    @FXML private Label topItemsEmptyLabel;

    private final ReportDao reportDao = new ReportDaoImpl();

    public void init(User user) {
        welcomeLabel.setText("Welcome, " + user.getUsername() + " (" + user.getRole() + ")");
        boolean isAdmin = user.getRole() == Role.ADMIN;
        usersBtn.setVisible(isAdmin);
        loadTopItemsForToday();
    }

    private void loadTopItemsForToday() {
        try {
            LocalDate today = LocalDate.now();
            List<Map.Entry<String, Integer>> top = reportDao.topItemsForDate(today, 8);

            ObservableList<PieChart.Data> data = FXCollections.observableArrayList(
                    top.stream()
                            .map(e -> new PieChart.Data(e.getKey(), e.getValue()))
                            .toList()
            );

            topItemsPieChart.setData(data);

            boolean empty = data.isEmpty();
            topItemsPieChart.setVisible(!empty);
            topItemsPieChart.setManaged(!empty);
            topItemsEmptyLabel.setText(empty ? "No orders yet for today." : "");
        } catch (Exception ex) {
            topItemsEmptyLabel.setText("Unable to load top items.");
        }
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
    private void openReports(ActionEvent e) {
        loadScene("/main/caballo/view/reports.fxml");
    }

    @FXML
    private void openUsers(ActionEvent e) {
        loadScene("/main/caballo/view/users.fxml");
    }

    @FXML
    private void openShifts(ActionEvent e) {
        loadScene("/main/caballo/view/shifts.fxml");
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