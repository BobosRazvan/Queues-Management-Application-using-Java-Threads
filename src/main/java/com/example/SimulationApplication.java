package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class SimulationApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SimulationApplication.class.getResource("Simulation.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 605);
        stage.setTitle("Queues Management Application");
        String iconPath = "C:\\Users\\danie\\Desktop\\Razvi\\Altele\\queues.png";
        stage.getIcons().add(new Image(iconPath));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
