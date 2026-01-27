package main.caballo.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.caballo.Main;
import main.caballo.dao.MenuItemDao;
import main.caballo.dao.OrderDao;
import main.caballo.dao.impl.MenuItemDaoImpl;
import main.caballo.dao.impl.OrderDaoImpl;
import main.caballo.model.MenuItem;
import main.caballo.model.Order;
import main.caballo.model.OrderItem;
import main.caballo.model.Pice;

import java.time.format.DateTimeFormatter;

public class OrdersController {
    @FXML private TextField qtyField;

    @FXML private TableView<Order> ordersTable;
    @FXML private TableColumn<Order, String> orderDateCol;
    @FXML private TableColumn<Order, Number> orderTotalCol;

    @FXML private TableView<OrderItem> itemsTable;
    @FXML private TableColumn<OrderItem, String> itemNameCol;
    @FXML private TableColumn<OrderItem, Number> itemQtyCol;
    @FXML private TableColumn<OrderItem, Number> itemPriceCol;
    @FXML private TableColumn<OrderItem, Number> itemSumCol;

    @FXML private TextField searchMenuField;
    @FXML private TableView<MenuItem> menuTable;
    @FXML private TableColumn<MenuItem, String> menuNameCol;
    @FXML private TableColumn<MenuItem, String> menuCatCol;
    @FXML private TableColumn<MenuItem, Number> menuPriceCol;

    private final OrderDao orderDao = new OrderDaoImpl();
    private final MenuItemDao menuDao = new MenuItemDaoImpl();
    private final ObservableList<Order> orders = FXCollections.observableArrayList();
    private final ObservableList<OrderItem> items = FXCollections.observableArrayList();
    private final ObservableList<MenuItem> menu = FXCollections.observableArrayList();
    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    private void initialize() {
        orderDateCol.setCellValueFactory(c -> new SimpleStringProperty(DF.format(c.getValue().getDatum())));
        orderTotalCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getUkupno()));

        itemNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getItemName()));
        itemQtyCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getKolicina()));
        itemPriceCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getCijena()));
        itemSumCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getUkupno()));

        menuNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        menuCatCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory()));
        menuPriceCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPrice()));

        ordersTable.setItems(orders);
        itemsTable.setItems(items);
        menuTable.setItems(menu);

        refreshOrders();
        refreshMenu();

        searchMenuField.textProperty().addListener((obs, o, n) -> refreshMenu());

        ordersTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) {
                items.setAll(orderDao.findItems(n.getId()));
            } else {
                items.clear();
            }
        });
    }

    private void refreshOrders() { orders.setAll(orderDao.findRecent(50)); }
    private void refreshMenu() { menu.setAll(menuDao.search(searchMenuField.getText())); }

    @FXML
    private void newOrder(ActionEvent e) {
        Order o = new Order();
        o.setUserId(Main.getCurrentUser().getId());
        o.setUkupno(0);
        orderDao.create(o);
        refreshOrders();
        ordersTable.getSelectionModel().selectFirst();
    }

    @FXML
    private void addItem(ActionEvent e) {
        Order sel = ordersTable.getSelectionModel().getSelectedItem();
        if (sel == null) { alert("Prvo odaberite narudžbu."); return; }
        MenuItem chosen = menuTable.getSelectionModel().getSelectedItem();
        if (chosen == null) { alert("Odaberite stavku s menija."); return; }
        try {
            int qty = Integer.parseInt(qtyField.getText());
            if (qty <= 0) throw new IllegalArgumentException("Količina mora biti > 0");
            if (chosen instanceof Pice drink) {
                if (qty > drink.getCurrentQty()) {
                    alert("Nema dovoljno na stanju. Trenutno: " + drink.getCurrentQty());
                    return;
                }
            }

            OrderItem it = new OrderItem(0, sel.getId(), chosen.getId(), chosen.getName(), qty, chosen.getPrice());
            orderDao.addItem(it);
            refreshMenu();
            refreshOrders();
            ordersTable.getSelectionModel().select(sel);
            items.setAll(orderDao.findItems(sel.getId()));
            qtyField.clear();
        } catch (Exception ex) {
            alert("Dodavanje stavke nije uspjelo: " + ex.getMessage());
        }
    }

    @FXML
    private void goBack(ActionEvent e) {
        Main.showDashboard();
    }

    private void alert(String msg) { new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait(); }
}
