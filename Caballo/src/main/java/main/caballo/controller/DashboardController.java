package main.caballo.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ListView;
import main.caballo.CaballoApplication;
import main.caballo.dao.MenuItemDao;
import main.caballo.dao.ReportDao;
import main.caballo.dao.impl.MenuItemDaoImpl;
import main.caballo.dao.impl.ReportDaoImpl;
import main.caballo.model.Pice;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class DashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Button usersBtn;

    @FXML private PieChart topItemsPieChart;
    @FXML private Label topItemsEmptyLabel;

    @FXML private ListView<String> lowStockList;
    @FXML private Label lowStockEmptyLabel;

    private static final int LOW_STOCK_THRESHOLD = 20;
    private static final int CRITICAL_STOCK_THRESHOLD = 10;

    private final ReportDao reportDao = new ReportDaoImpl();
    private final MenuItemDao menuItemDao = new MenuItemDaoImpl();

    public void init(User user) {
        String roleStr = user.getRole() == Role.ADMIN ? "ADMIN" : "KORISNIK";
        welcomeLabel.setText("Dobrodošli, " + user.getUsername() + " (" + roleStr + ")");
        boolean isAdmin = user.getRole() == Role.ADMIN;
        usersBtn.setVisible(isAdmin);

        if (lowStockList != null) {
            lowStockList.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    getStyleClass().removeAll("low-stock-item", "critical-stock-item");

                    if (empty || item == null) {
                        setText(null);
                        return;
                    }

                    setText(item);
                    if (item.startsWith("[KRITIČNO]")) {
                        getStyleClass().add("critical-stock-item");
                    } else if (item.startsWith("[MALO]")) {
                        getStyleClass().add("low-stock-item");
                    }
                }
            });
        }

        loadTopItemsForToday();
        loadLowStockItems();
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
            topItemsEmptyLabel.setText(empty ? "Danas još nema narudžbi." : "");
        } catch (Exception ex) {
            topItemsEmptyLabel.setText("Nije moguće učitati najprodavanije artikle.");
        }
    }

    private void loadLowStockItems() {
        try {
            var low = menuItemDao.findAll().stream()
                    .filter(i -> i instanceof Pice)
                    .map(i -> (Pice) i)
                    .filter(p -> p.getCurrentQty() <= LOW_STOCK_THRESHOLD)
                    .sorted(Comparator.comparingInt(Pice::getCurrentQty))
                    .map(p -> {
                        String prefix = (p.getCurrentQty() <= CRITICAL_STOCK_THRESHOLD) ? "[KRITIČNO] " : "[MALO] ";
                        return prefix + p.getName() + " (Kol: " + p.getCurrentQty() + ")";
                    })
                    .toList();

            lowStockList.setItems(FXCollections.observableArrayList(low));

            boolean empty = low.isEmpty();
            lowStockList.setVisible(!empty);
            lowStockList.setManaged(!empty);
            lowStockEmptyLabel.setText(empty ? "Sve je u redu — nema pića sa niskim stanjem." : "");
        } catch (Exception ex) {
            lowStockList.setVisible(false);
            lowStockList.setManaged(false);
            lowStockEmptyLabel.setText("Nije moguće učitati stavke sa niskim stanjem.");
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