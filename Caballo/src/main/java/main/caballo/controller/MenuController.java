package main.caballo.controller;

import javafx.event.ActionEvent;
import main.caballo.CaballoApplication;
import main.caballo.dao.MenuItemDao;
import main.caballo.dao.ReportDao;
import main.caballo.dao.impl.MenuItemDaoImpl;
import main.caballo.dao.impl.ReportDaoImpl;
import main.caballo.model.MenuItem;
import main.caballo.model.Pice;
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

    @FXML private CheckBox drinkCheck;

    private final MenuItemDao dao = new MenuItemDaoImpl();
    private final ReportDao reportDao = new ReportDaoImpl();
    private final ObservableList<MenuItem> data = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        descCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));
        priceCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPrice()));
        catCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory()));
        qtyCol.setCellValueFactory(c -> {
            if (c.getValue() instanceof Pice p) {
                return new SimpleIntegerProperty(p.getCurrentQty());
            }
            return null;
        });
        qtyCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                    return;
                }
                setText(String.valueOf(item.intValue()));
            }
        });

        table.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(MenuItem item, boolean empty) {
                super.updateItem(item, empty);

                getStyleClass().removeAll("low-stock-row", "critical-stock-row");

                if (empty || item == null) {
                    return;
                }

                if (item instanceof Pice p) {
                    int qty = p.getCurrentQty();
                    if (qty <= 10) {
                        getStyleClass().add("critical-stock-row");
                    } else if (qty <= 20) {
                        getStyleClass().add("low-stock-row");
                    }
                }
            }
        });

        table.setItems(data);
        refresh();

        table.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) {
                nameField.setText(n.getName());
                priceField.setText(String.valueOf(n.getPrice()));
                categoryField.setText(n.getCategory());
                descArea.setText(n.getDescription());

                boolean isDrink = n instanceof Pice;
                drinkCheck.setSelected(isDrink);

                if (isDrink) {
                    Pice p = (Pice) n;
                    qtyField.setText(String.valueOf(p.getCurrentQty()));
                    qtyField.setDisable(false);
                    deliveryQtyField.setDisable(false);
                } else {
                    qtyField.setText("0");
                    qtyField.setDisable(true);
                    deliveryQtyField.setDisable(true);
                }
            } else {
                clearForm();
            }
        });

        drinkCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (table.getSelectionModel().getSelectedItem() != null) return;
            if (Boolean.TRUE.equals(newVal)) {
                qtyField.setDisable(false);
                deliveryQtyField.setDisable(false);
                if (qtyField.getText() == null || qtyField.getText().isBlank()) qtyField.setText("0");
            } else {
                qtyField.setText("0");
                qtyField.setDisable(true);
                deliveryQtyField.setDisable(true);
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
        data.setAll(dao.search(q));
        table.refresh();
    }

    @FXML
    private void addItem(ActionEvent e) {
        try {
            String category = categoryField.getText();
            MenuItem m;

            if (drinkCheck.isSelected()) {
                int currentQty = Integer.parseInt(qtyField.getText());
                if (currentQty < 0) throw new IllegalArgumentException("Količina mora biti >= 0");
                m = new Pice(
                        0,
                        nameField.getText(),
                        descArea.getText(),
                        Double.parseDouble(priceField.getText()),
                        category,
                        currentQty
                );
            } else {
                m = new MenuItem(
                        0,
                        nameField.getText(),
                        descArea.getText(),
                        Double.parseDouble(priceField.getText()),
                        category
                );
            }

            dao.create(m);
            refresh();
            clearForm();
        } catch (Exception ex) {
            showError("Neispravan unos: " + ex.getMessage());
        }
    }

    @FXML
    private void updateItem(ActionEvent e) {
        MenuItem sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Odaberite red.");
            return;
        }
        try {
            sel.setName(nameField.getText());
            sel.setDescription(descArea.getText());
            sel.setPrice(Double.parseDouble(priceField.getText()));
            sel.setCategory(categoryField.getText());

            if (sel instanceof Pice p) {
                p.setCurrentQty(Integer.parseInt(qtyField.getText()));
            }

            drinkCheck.setSelected(sel instanceof Pice);

            dao.update(sel);
            refresh();
        } catch (Exception ex) {
            showError("Ažuriranje nije uspjelo: " + ex.getMessage());
        }
    }

    @FXML
    private void deleteItem(ActionEvent e) {
        MenuItem sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Odaberite red za brisanje.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Obriši stavku: " + sel.getName() + " ?",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.showAndWait();

        if (confirm.getResult() != ButtonType.YES) {
            return;
        }

        try {
            boolean deleted = dao.delete(sel.getId());
            if (!deleted) {
                showError("Brisanje nije uspjelo u bazi (0 redova promijenjeno).");
                return;
            }

            data.remove(sel);
            table.getSelectionModel().clearSelection();
            clearForm();
            table.refresh();
        } catch (Exception ex) {
            showError("Greška pri brisanju: " + ex.getMessage());
        }
    }

    @FXML
    private void addDelivery(ActionEvent e) {
        MenuItem sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showError("Odaberite stavku.");
            return;
        }
        if (!(sel instanceof Pice drink)) {
            showError("Dostavu možete dodati samo za pića.");
            return;
        }
        if (deliveryQtyField == null) {
            showError("Polje za količinu dostave nije konfigurisano.");
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(deliveryQtyField.getText());
        } catch (NumberFormatException ex) {
            showError("Neispravna količina dostave.");
            return;
        }
        if (qty <= 0) {
            showError("Količina mora biti > 0.");
            return;
        }

        try {
            dao.addDelivery(sel.getId(), qty);
            reportDao.addReceivedQty(LocalDate.now(), sel.getId(), qty);

            drink.setCurrentQty(drink.getCurrentQty() + qty);
            table.refresh();
            deliveryQtyField.clear();
        } catch (Exception ex) {
            showError("Dostava nije uspjela: " + ex.getMessage());
        }
    }

    private void clearForm() {
        nameField.clear();
        priceField.clear();
        categoryField.clear();
        descArea.clear();

        drinkCheck.setSelected(false);

        qtyField.setText("0");
        qtyField.setDisable(true);

        deliveryQtyField.clear();
        deliveryQtyField.setDisable(true);
    }

    @FXML
    private void goBack(ActionEvent e) {
        CaballoApplication.showDashboard();
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }
}
