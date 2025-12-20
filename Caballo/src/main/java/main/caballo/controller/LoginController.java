package main.caballo.controller;

import main.caballo.dao.UserDao;
import main.caballo.dao.impl.UserDaoImpl;
import main.caballo.model.User;
import main.caballo.util.PasswordUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.function.Consumer;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginBtn;
    @FXML private Label statusLabel;

    private final UserDao userDao = new UserDaoImpl();
    private Consumer<User> onLoginSuccess;

    @FXML
    private void initialize() {
        loginBtn.setOnAction(e -> doLogin());
    }

    private void doLogin() {
        String u = usernameField.getText() != null ? usernameField.getText().trim() : "";
        String p = passwordField.getText() != null ? passwordField.getText().trim() : "";
        statusLabel.setText("");

        try {
            userDao.findByUsername(u).ifPresentOrElse(user -> {
                boolean ok = PasswordUtil.verify(p, user.getSalt(), user.getPasswordHash());
                if (ok) {
                    if (onLoginSuccess != null) onLoginSuccess.accept(user);
                } else {
                    // Debug info to console to help fix DB row during setup
                    String computed = PasswordUtil.hashSha256(p, user.getSalt());
                    System.out.println("[Login debug] username=" + u);
                    System.out.println("[Login debug] salt(hex)=" + user.getSalt() + " len=" + (user.getSalt() == null ? 0 : user.getSalt().length()));
                    System.out.println("[Login debug] stored hash(hex)=" + user.getPasswordHash() + " len=" + (user.getPasswordHash() == null ? 0 : user.getPasswordHash().length()));
                    System.out.println("[Login debug] computed hash(hex)=" + computed + " len=" + computed.length());
                    statusLabel.setText("Invalid credentials.");
                }
            }, () -> statusLabel.setText("User not found."));
        } catch (Exception ex) {
            // unwrap root cause so we see the actual DB problem
            Throwable root = ex;
            while (root.getCause() != null && root.getCause() != root) {
                root = root.getCause();
            }

            String msg = root.getMessage();
            if (msg == null || msg.isBlank()) {
                msg = root.getClass().getSimpleName();
            }

            statusLabel.setText("Database error: " + msg);

            // full stack trace to console for debugging
            ex.printStackTrace();
        }
    }

    public void setOnLoginSuccess(Consumer<User> onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }
}
