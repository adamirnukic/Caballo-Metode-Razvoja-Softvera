package main.caballo.controller;

import main.caballo.dao.UserDao;
import main.caballo.dao.impl.UserDaoImpl;
import main.caballo.model.User;
import main.caballo.util.PasswordUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

import java.util.function.Consumer;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private final UserDao userDao = new UserDaoImpl();
    private Consumer<User> onLoginSuccess;

    @FXML
    private void doLogin(ActionEvent e) {
        String u = usernameField.getText() != null ? usernameField.getText().trim() : "";
        String p = passwordField.getText() != null ? passwordField.getText().trim() : "";
        statusLabel.setText("");

        try {
            userDao.findByUsername(u).ifPresentOrElse(user -> {
                boolean ok = PasswordUtil.verify(p, user.getSalt(), user.getPasswordHash());
                if (ok) {
                    if (onLoginSuccess != null) onLoginSuccess.accept(user);
                } else {
                    statusLabel.setText("Invalid credentials.");
                }
            }, () -> statusLabel.setText("User not found."));
        } catch (Exception ex) {
            statusLabel.setText("An error occurred during login.");
        }
    }

    public void setOnLoginSuccess(Consumer<User> onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }
}
