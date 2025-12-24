package main.caballo.controller;

import javafx.event.ActionEvent;
import main.caballo.CaballoApplication;
import main.caballo.dao.MenuItemDao;
import main.caballo.dao.ReportDao;
import main.caballo.dao.impl.MenuItemDaoImpl;
import main.caballo.dao.impl.ReportDaoImpl;
import main.caballo.model.MenuItem;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

public class MenuController {
    @FXML private TableView<MenuItem> table;
    @FXML private TableColumn<MenuItem, String> nameCol;
    @FXML private TableColumn<MenuItem, String> descCol;
    @FXML private TableColumn<MenuItem, Number> priceCol;
    @FXML private TableColumn<MenuItem, String> catCol;
    @FXML private TableColumn<MenuItem, Number> qtyCol;

    @FXML private TextField nameField;
    @FXML private TextField priceField;
    @FXML private TextField categoryField;
    @FXML private TextField qtyField;
    @FXML private TextField deliveryQtyField;
    @FXML private TextArea descArea;
    @FXML private TextField searchField;

    private final MenuItemDao dao = new MenuItemDaoImpl();
    private final ReportDao reportDao = new ReportDaoImpl();
    private final ObservableList<MenuItem> data = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        descCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));
        priceCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPrice()));
        catCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory()));
        qtyCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCurrentQty()));

        table.setItems(data);
        refresh();

        table.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) {
                nameField.setText(n.getName());
                priceField.setText(String.valueOf(n.getPrice()));
                categoryField.setText(n.getCategory());
                descArea.setText(n.getDescription());
                qtyField.setText(String.valueOf(n.getCurrentQty()));
            } else {
                clearForm();
            }
        });

        searchField.textProperty().addListener((obs, o, n) -> search());
    }

    private void refresh() {
        data.setAll(dao.findAll());
        table.refresh();
    }

    private void search() {
        String q = searchField.getText();
        data.setAll(dao.search(q, null));
        table.refresh();
    }

    @FXML
    private void addItem(ActionEvent e) {
        try {
            int currentQty = Integer.parseInt(qtyField.getText());
            if (currentQty < 0) throw new IllegalArgumentException("Qty must be >= 0");

            MenuItem m = new MenuItem(
                    0,
                    nameField.getText(),
                    descArea.getText(),
                    Double.parseDouble(priceField.getText()),
                    categoryField.getText(),
                    currentQty
            );
            dao.create(m);
            refresh();
            clearForm();
        } catch (Exception ex) {
            showError("Invalid input: " + ex.getMessage());
        }
    }

    @FXML
    private void updateItem(ActionEvent e) {
        MenuItem sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Select a row.");
            return;
        }
        try {
            sel.setName(nameField.getText());
            sel.setDescription(descArea.getText());
            sel.setPrice(Double.parseDouble(priceField.getText()));
            sel.setCategory(categoryField.getText());
            sel.setCurrentQty(Integer.parseInt(qtyField.getText()));
            dao.update(sel);
            refresh();
        } catch (Exception ex) {
            showError("Update failed: " + ex.getMessage());
        }
    }

    @FXML
    private void deleteItem(ActionEvent e) {
        MenuItem sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Select a row to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete item: " + sel.getName() + " ?",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait();

        if (confirm.getResult() != ButtonType.YES) {
            return;
        }

        try {
            boolean deleted = dao.delete(sel.getId());
            if (!deleted) {
                showError("Delete failed in database (0 rows affected).");
                return;
            }

            data.remove(sel);
            table.getSelectionModel().clearSelection();
            clearForm();
            table.refresh();
        } catch (Exception ex) {
            showError("Delete error: " + ex.getMessage());
        }
    }

    @FXML
    private void addDelivery(ActionEvent e) {
        MenuItem sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Select an item.");
            return;
        }
        if (deliveryQtyField == null) {
            showError("Delivery quantity field not configured.");
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(deliveryQtyField.getText());
        } catch (NumberFormatException ex) {
            showError("Invalid delivery quantity.");
            return;
        }
        if (qty <= 0) {
            showError("Quantity must be > 0.");
            return;
        }

        try {
            dao.addDelivery(sel.getId(), qty);
            reportDao.addReceivedQty(LocalDate.now(), sel.getId(), qty);

            sel.setCurrentQty(sel.getCurrentQty() + qty);
            table.refresh();
            deliveryQtyField.clear();
        } catch (Exception ex) {
            showError("Delivery failed: " + ex.getMessage());
        }
    }

    private void clearForm() {
        nameField.clear();
        priceField.clear();
        categoryField.clear();
        qtyField.clear();
        descArea.clear();
        deliveryQtyField.clear();
    }

    @FXML
    private void goBack(ActionEvent e) {
        CaballoApplication.showDashboard();
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }
}
