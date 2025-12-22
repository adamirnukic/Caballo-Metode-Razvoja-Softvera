package main.caballo.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.caballo.CaballoApplication;
import main.caballo.dao.ReservationDao;
import main.caballo.dao.TableDao;
import main.caballo.dao.impl.ReservationDaoImpl;
import main.caballo.dao.impl.TableDaoImpl;
import main.caballo.model.DiningTable;
import main.caballo.model.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservationsController {
    @FXML
    private DatePicker datePicker;

    @FXML private TableView<Reservation> reservationsTable;
    @FXML private TableColumn<Reservation, Number> resIdCol;
    @FXML private TableColumn<Reservation, Number> resTableCol;
    @FXML private TableColumn<Reservation, String> resNameCol;
    @FXML private TableColumn<Reservation, String> resPhoneCol;
    @FXML private TableColumn<Reservation, String> resTimeCol;
    @FXML private TableColumn<Reservation, Number> resPeopleCol;

    @FXML private ComboBox<DiningTable> tableChoice;
    @FXML private TextField guestField;
    @FXML private TextField phoneField;
    @FXML private TextField timeField;
    @FXML private TextField peopleField;
    @FXML private TextField noteField;

    private final ReservationDao dao = new ReservationDaoImpl();
    private final TableDao tableDao = new TableDaoImpl();
    private final ObservableList<Reservation> data = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        datePicker.setValue(LocalDate.now());

        resIdCol.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().getId()));
        resTableCol.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().getTableId()));
        resNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getImeGosta()));
        resPhoneCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBrojTelefona()));
        resTimeCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getVrijemeDolaska())));
        resPeopleCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getBrojOsoba()));

        reservationsTable.setItems(data);
        reservationsTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) {
                tableChoice.getSelectionModel().select(tableDao.findAll().stream().filter(t -> t.getId() == n.getTableId()).findFirst().orElse(null));
                guestField.setText(n.getImeGosta());
                phoneField.setText(n.getBrojTelefona());
                timeField.setText(n.getVrijemeDolaska().toString());
                peopleField.setText(String.valueOf(n.getBrojOsoba()));
                noteField.setText(n.getNapomena());
            }
        });

        tableChoice.setItems(FXCollections.observableArrayList(tableDao.findAll()));
        load();
    }

    private void load() {
        data.setAll(dao.findByDate(datePicker.getValue()));
    }

    @FXML
    private void loadData(ActionEvent e) {
        load();
    }

    @FXML
    private void create(ActionEvent e) {
        try {
            DiningTable t = tableChoice.getSelectionModel().getSelectedItem();
            if (t == null) { alert("Choose a table."); return; }
            Reservation r = new Reservation(0,
                    t.getId(),
                    guestField.getText(),
                    phoneField.getText(),
                    datePicker.getValue(),
                    LocalTime.parse(timeField.getText()),
                    Integer.parseInt(peopleField.getText()),
                    noteField.getText());
            dao.create(r);
            load();
            clearForm();
        } catch (Exception ex) { alert("Create failed: " + ex.getMessage()); }
    }

    @FXML
    private void update(ActionEvent e) {
        Reservation sel = reservationsTable.getSelectionModel().getSelectedItem();
        if (sel == null) { alert("Select a reservation."); return; }
        try {
            DiningTable t = tableChoice.getSelectionModel().getSelectedItem();
            if (t == null) { alert("Choose a table."); return; }
            sel.setTableId(t.getId());
            sel.setImeGosta(guestField.getText());
            sel.setBrojTelefona(phoneField.getText());
            sel.setDatumRezervacije(datePicker.getValue());
            sel.setVrijemeDolaska(LocalTime.parse(timeField.getText()));
            sel.setBrojOsoba(Integer.parseInt(peopleField.getText()));
            sel.setNapomena(noteField.getText());
            dao.update(sel);
            load();
        } catch (Exception ex) { alert("Update failed: " + ex.getMessage()); }
    }

    @FXML
    private void delete(ActionEvent e) {
        Reservation sel = reservationsTable.getSelectionModel().getSelectedItem();
        if (sel == null) { alert("Select a reservation."); return; }
        try {
            dao.delete(sel.getId());
            load();
        } catch (Exception ex) { alert("Delete failed: " + ex.getMessage()); }
    }

    private void clearForm() {
        tableChoice.getSelectionModel().clearSelection();
        guestField.clear();
        phoneField.clear();
        timeField.clear();
        peopleField.clear();
        noteField.clear();
    }

    @FXML
    private void goBack(ActionEvent e) {
        CaballoApplication.showDashboard();
    }

    private void alert(String msg) { new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait(); }
}
