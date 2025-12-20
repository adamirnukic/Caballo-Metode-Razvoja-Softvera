package main.caballo;

import main.caballo.controller.DashboardController;
import main.caballo.controller.LoginController;
import main.caballo.model.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CaballoApplication extends Application {
    private static Stage primaryStage;
    private static User currentUser;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        primaryStage.setTitle("Caballo Restaurant");
        showLogin();
        primaryStage.show();
    }

    public static void showLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(CaballoApplication.class.getResource("/main/caballo/view/login.fxml"));
            Scene scene = new Scene(loader.load());
            LoginController controller = loader.getController();
            controller.setOnLoginSuccess(user -> {
                currentUser = user;
                showDashboard();
            });
            primaryStage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load login.fxml", e);
        }
    }

    public static void showDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(CaballoApplication.class.getResource("/main/caballo/view/dashboard.fxml"));
            Scene scene = new Scene(loader.load());
            DashboardController controller = loader.getController();
            controller.init(currentUser);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load dashboard.fxml", e);
        }
    }

    public static User getCurrentUser() {
        return currentUser;
    }
}

