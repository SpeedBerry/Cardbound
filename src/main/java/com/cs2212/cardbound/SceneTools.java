package com.cs2212.cardbound;

import com.cs2212.cardbound.system.MainMenu;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.InputEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

/**
 * A utility class that helps with managing and controlling scenes in a JavaFX project.
 * @version 1.0
 * @author Jacob Couture
 * @since 3/10/2024
 */
public class SceneTools {

    /**
     * The current X resolution scale. Default is 1.0, which is at 1280
     */
    public static double currentScaleX = 1.0;
    /**
     * The current Y resolution scale. Default is 1.0, which is at 720
     */
    public static double currentScaleY = 1.0;

    private SceneTools() {
        throw new AssertionError();
    }

    /**
     * Fades out a scene and switches to a different one.
     * @param e The event representing the action that led to this method being called.
     * @param sceneName The name of the fxml file belonging to the scene to be switched to.
     * @param milliseconds The amount of time in milliseconds it will take to fade out.
     * @param rootPane The root pane of the current scene.
     */
    public static void fadeOutToScene(ActionEvent e, String sceneName, int milliseconds, Node rootPane) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(milliseconds), rootPane);
        fadeTransition.setOnFinished(event -> {
            try {
                switchScene(e, sceneName);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.play();
    }

    public static void fadeOutToScene(InputEvent e, String sceneName, int milliseconds, Node rootPane) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(milliseconds), rootPane);
        fadeTransition.setOnFinished(event -> {
            try {
                switchScene(e, sceneName);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.play();
    }

    /**
     * Switches to a different scene.
     * @param e The event representing the action that led to this method being called.
     * @param sceneName The name of the fxml file belonging to the scene to be switched to.
     * @throws IOException If an error occurs during loading.
     */
    public static void switchScene(ActionEvent e, String sceneName) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneTools.class.getResource(sceneName));
        Parent root = loader.load();
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, MainMenu.SCREEN_WIDTH, MainMenu.SCREEN_HEIGHT, Color.BLACK);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @see SceneTools#switchScene(ActionEvent, String)
     */
    public static void switchScene(InputEvent e, String sceneName) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneTools.class.getResource(sceneName));
        Parent root = loader.load();
        Stage stage;
        if (e.getSource().getClass().getSimpleName().equals("Scene"))
            stage = (Stage) ((Scene) e.getSource()).getWindow();
        else
            stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, MainMenu.SCREEN_WIDTH, MainMenu.SCREEN_HEIGHT, Color.BLACK);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Fades in a scene.
     * @param rootPane The root pane of the current scene.
     * @param milliseconds The amount of time in milliseconds it will take to fade in.
     */
    public static void fadeInScene(Node rootPane, int milliseconds) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(milliseconds), rootPane);
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(750));
        SequentialTransition sequentialTransition = new SequentialTransition(pauseTransition, fadeTransition);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        sequentialTransition.play();
    }

    /**
     * Updates the scale of all the objects in the current scene.
     * @param screenResolutionX the new horizontal screen resolution (e.g. 1280, 1920, etc)
     * @param screenResolutionY the new vertical screen resolution (e.g. 720, 1080, etc)
     * @param rootPane the root pane of the current scene
     */
    public static void setScreenScale(double screenResolutionX, double screenResolutionY, StackPane rootPane) {

        // Sets the stage size to match the new resolution
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.setX((MainMenu.SCREEN_WIDTH - stage.getWidth()) / 2);
        stage.setY((MainMenu.SCREEN_HEIGHT - stage.getHeight()) / 2);

        // Calculate the new scale
        currentScaleX = screenResolutionX / 1280;
        currentScaleY = screenResolutionY / 720;

        // Apply the new scale to all the nodes in the scene
        for (Node node : rootPane.getChildren()) {
            node.setScaleX(currentScaleX);
            node.setScaleY(currentScaleY);
            Insets nodeInsets = StackPane.getMargin(node);
            if (nodeInsets != null) {
                StackPane.setMargin(node, new Insets(nodeInsets.getTop() * currentScaleY, nodeInsets.getRight() * currentScaleX, nodeInsets.getBottom() * currentScaleY, nodeInsets.getLeft() * currentScaleX));
            }
        }
    }

    /**
     * Exits the application upon being called.
     */
    public static void exitApplication() {
        Platform.exit();
        System.exit(0);
    }

}
