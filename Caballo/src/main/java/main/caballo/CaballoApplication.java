package main.caballo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CaballoApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CaballoApplication.class.getResource("/main/caballo/view/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Caballo");
        stage.setScene(scene);
        stage.show();
    }
}
