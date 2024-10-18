package com.cs2212.cardbound;

import com.cs2212.cardbound.system.MainMenu;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * This class represents the application window, and is the class that runs the entire game.
 * @version 1.0
 * @author Jacob Couture
 * @since 3/3/2024
 */
public class MainStage extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("main-menu.fxml"));
        Parent root = loader.load();

        // Make window borderless
        stage.setResizable(false);
        stage.setMaximized(true);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setMaxWidth(3840);
        stage.setMaxHeight(2160);
        stage.setMinWidth(1280);
        stage.setMinHeight(720);
        stage.setWidth(MainMenu.SCREEN_WIDTH);
        stage.setHeight(MainMenu.SCREEN_HEIGHT);

        Scene scene = new Scene(root, MainMenu.SCREEN_WIDTH, MainMenu.SCREEN_HEIGHT, Color.BLACK);

        stage.setTitle("CARDBOUND");
        stage.setScene(scene);
        stage.show();
        MainMenu.loadGame();
    }

    public static void main(String[] args) {
        launch();
    }
}