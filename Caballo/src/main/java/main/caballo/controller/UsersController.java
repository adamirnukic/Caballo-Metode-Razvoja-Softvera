package main.caballo.controller;

import javafx.event.ActionEvent;
import main.caballo.Main;
import main.caballo.dao.UserDao;
import main.caballo.dao.impl.UserDaoImpl;
import main.caballo.model.Role;
import main.caballo.model.User;
import main.caballo.util.PasswordUtil;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class UsersController {
    @FXML private TableView<User> table;
    @FXML private TableColumn<User, Number> idCol;
    @FXML private TableColumn<User, String> usernameCol;
    @FXML private TableColumn<User, String> roleCol;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ChoiceBox<Role> roleChoice;

    private final UserDao userDao = new UserDaoImpl();
    private final ObservableList<User> data = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idCol.setCellValueFactory(c -> new SimpleLongProperty(c.getValue().getId()));
        usernameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsername()));
        roleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRole().name()));
        roleChoice.setItems(FXCollections.observableArrayList(Role.ADMIN, Role.USER));
        roleChoice.getSelectionModel().select(Role.USER);

        table.setItems(data);
        refresh();

        table.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) {
                usernameField.setText(n.getUsername());
                roleChoice.getSelectionModel().select(n.getRole());
            }
        });
    }

    private void refresh() { data.setAll(userDao.findAll()); }

    @FXML
    private void createUser(ActionEvent e) {
        try {
            String u = usernameField.getText();
            String p = passwordField.getText();
            Role r = roleChoice.getValue();
            if (u == null || u.isBlank() || p == null || p.isBlank() || r == null) {
                alert("Sva polja su obavezna za kreiranje korisnika.");
                return;
            }
            String salt = PasswordUtil.generateSalt(16);
            String hash = PasswordUtil.hashSha256(p, salt);
            User user = new User(0, u, hash, salt, r);
            userDao.create(user);
            refresh();
            usernameField.clear();
            passwordField.clear();
            roleChoice.getSelectionModel().select(Role.USER);
        } catch (Exception ex) {
            alert("Kreiranje nije uspjelo: " + ex.getMessage());
        }
    }

    @FXML
    private void resetPassword(ActionEvent e) {
        User sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { alert("Odaberite korisnika."); return; }
        String p = passwordField.getText();
        if (p == null || p.isBlank()) { alert("Unesite novu lozinku u polje za lozinku."); return; }
        try {
            String salt = PasswordUtil.generateSalt(16);
            String hash = PasswordUtil.hashSha256(p, salt);
            sel.setSalt(salt);
            sel.setPasswordHash(hash);
            boolean ok = userDao.resetPassword(sel);
            if (!ok) alert("Resetovanje lozinke nije uspjelo.");
            else { refresh(); passwordField.clear(); alert("Lozinka uspješno resetovana."); }
        } catch (Exception ex) {
            alert("Resetovanje lozinke nije uspjelo: " + ex.getMessage());
        }
    }

    @FXML
    private void deleteUser(ActionEvent e) {
        User sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { alert("Odaberite korisnika."); return; }
        User me = Main.getCurrentUser();
        if (me != null && me.getId() == sel.getId()) { alert("Ne možete obrisati vlastiti nalog dok ste prijavljeni."); return; }
        try {
            boolean ok = userDao.deleteById(sel.getId());
            if (!ok) alert("Brisanje nije uspjelo.");
            else refresh();
        } catch (Exception ex) {
            alert("Brisanje nije uspjelo: " + ex.getMessage());
        }
    }

    @FXML
    private void goBack(ActionEvent e) {
        Main.showDashboard();
    }


    private void alert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}
