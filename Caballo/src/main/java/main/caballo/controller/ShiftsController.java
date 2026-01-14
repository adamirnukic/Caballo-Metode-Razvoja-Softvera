package main.caballo.controller;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.caballo.CaballoApplication;
import main.caballo.dao.ShiftDao;
import main.caballo.dao.UserDao;
import main.caballo.dao.impl.ShiftDaoImpl;
import main.caballo.dao.impl.UserDaoImpl;
import main.caballo.model.Role;
import main.caballo.model.Shift;
import main.caballo.model.ShiftType;
import main.caballo.model.User;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ShiftsController {

    @FXML
    private TableView<User> usersTable;
    @FXML private TableColumn<User, Number> userIdCol;
    @FXML private TableColumn<User, String> usernameCol;
    @FXML private TableColumn<User, String> roleCol;

    @FXML private DatePicker datePicker;
    @FXML private ChoiceBox<ShiftType> shiftTypeChoice;
    @FXML private TextArea noteArea;
    @FXML private Button saveBtn;

    private final UserDao userDao = new UserDaoImpl();
    private final ShiftDao shiftDao = new ShiftDaoImpl();

    private final ObservableList<User> users = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        userIdCol.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().getId()));
        usernameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsername()));
        roleCol.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getRole())));

        usersTable.setItems(users);

        datePicker.setValue(LocalDate.now());
        shiftTypeChoice.setItems(FXCollections.observableArrayList(ShiftType.values()));

        users.setAll(userDao.findAll());
        usersTable.getSelectionModel().selectFirst();

        usersTable.getSelectionModel().selectedItemProperty().addListener((o, a, b) -> {
            if (o == null && a == null && b == null) return;
            loadForSelection();
        });
        datePicker.valueProperty().addListener((o, a, b) -> {
            if (o == null && a == null && b == null) return;
            loadForSelection();
        });

        loadForSelection();
    }

    @FXML
    private void goBack(ActionEvent e) {
        CaballoApplication.showDashboard();
    }

    private void loadForSelection() {
        User u = usersTable.getSelectionModel().getSelectedItem();
        LocalDate d = datePicker.getValue();

        if (u == null || d == null) {
            shiftTypeChoice.getSelectionModel().clearSelection();
            noteArea.clear();
            return;
        }

        Shift existing = shiftDao.findByUserAndDate(u.getId(), d).orElse(null);
        if (existing == null) {
            ShiftType def = defaultShiftForUserAndDate(u, d);
            shiftTypeChoice.getSelectionModel().select(def);
            noteArea.setText("");
        } else {
            shiftTypeChoice.getSelectionModel().select(existing.getType());
            noteArea.setText(existing.getNote() == null ? "" : existing.getNote());
        }
    }

    @FXML
    private void save(ActionEvent e) {
        User u = usersTable.getSelectionModel().getSelectedItem();
        LocalDate d = datePicker.getValue();
        ShiftType t = shiftTypeChoice.getValue();

        if (u == null) {
            alert("Odaberi korisnika.");
            return;
        }
        if (d == null) {
            alert("Odaberi datum.");
            return;
        }
        if (t == null) {
            alert("Odaberi tip smjene.");
            return;
        }

        try {
            shiftDao.upsert(u.getId(), d, t, noteArea.getText());
        } catch (Exception ex) {
            alert("Save failed: " + ex.getMessage());
        }
    }

    private void alert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }

    private ShiftType defaultShiftForUserAndDate(User user, LocalDate date) {
        if (user == null || date == null) return null;

        if (date.getDayOfWeek() == DayOfWeek.MONDAY) {
            return ShiftType.SLOBODAN;
        }

        LocalDate monday = date.with(DayOfWeek.MONDAY);
        long daysFromMonday = ChronoUnit.DAYS.between(monday, date);

        long dayIndexFromTuesday = daysFromMonday - 1;
        if (dayIndexFromTuesday < 0) dayIndexFromTuesday = 0;

        boolean userStartsMorningOnTuesday = (user.getId() % 2 == 0);
        boolean isEven = (dayIndexFromTuesday % 2 == 0);

        if (userStartsMorningOnTuesday) {
            return isEven ? ShiftType.JUTARNJA : ShiftType.PODNEVNA;
        } else {
            return isEven ? ShiftType.PODNEVNA : ShiftType.JUTARNJA;
        }
    }
}
