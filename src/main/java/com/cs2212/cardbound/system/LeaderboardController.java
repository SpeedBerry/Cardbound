package com.cs2212.cardbound.system;

import com.cs2212.cardbound.SceneTools;
import com.cs2212.cardbound.gameplay.User;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

import static com.cs2212.cardbound.system.MainMenu.*;

public class LeaderboardController {

    @FXML
    private StackPane rootPane;

    @FXML
    private TitledPane debugMenu;

    @FXML
    private Button set720p, set1080p;

    @FXML
    private TableView<User> highScoreTable;

    @FXML
    private TableColumn<User, String> usernameColumn, bestTimeColumn;

    @FXML
    private TableColumn<User, Integer> bestScoreColumn, rankColumn;

    @FXML
    private Label lifetimeGames, bestScore, fastestTime, mostLevelsCleared, totalPlaytime, backButton;

    private boolean backButtonSelected = false;

    /**
     * Initializes the default scene settings.
     */
    public void initialize() {
        // Run post initialization when the initialization finishes
        Platform.runLater(this::postInit);

        rootPane.setOpacity(0);
        SceneTools.fadeInScene(rootPane, 1500);
    }

    /**
     * Code to run immediately after scene initialization
     */
    private void postInit() {
        SceneTools.setScreenScale(MainMenu.screenWidthProperty.get(), MainMenu.screenHeightProperty.get(), rootPane);

        backButton.setOnMouseClicked(e -> SceneTools.fadeOutToScene(e, "main-menu.fxml", 500, rootPane));
        backButton.setOnMouseEntered(e -> {
            if (!backButtonSelected) {
                MainMenu.hoverEnterButton(e);
                backButtonSelected = true;
            }
        });
        backButton.setOnMouseExited(e -> {
            if (backButtonSelected) {
                MainMenu.hoverExitButton(e);
                backButtonSelected = false;
            }
        });

        if (MainMenu.isDebugActive()) {
            MainMenu.initDebugMenu(debugMenu);
            set720p.setOnAction(this::set720p);
            set1080p.setOnAction(this::set1080p);
        }
        else
            debugMenu.setVisible(false);

        highScoreTable.setStyle("-fx-font-size: 14;");

        // Link the proper data in the table columns
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        bestScoreColumn.setCellValueFactory(new PropertyValueFactory<>("bestScore"));
        bestTimeColumn.setCellValueFactory(new PropertyValueFactory<>("bestTimeString"));

        // Add data to the table
        highScoreTable.getItems().addAll(FXCollections.observableArrayList(userData));
        bestScoreColumn.setSortType(TableColumn.SortType.DESCENDING);
        highScoreTable.getSortOrder().add(bestScoreColumn);
        rankColumn.setSortable(false);
        usernameColumn.setSortable(false);
        bestScoreColumn.setSortable(false);
        bestTimeColumn.setSortable(false);

        // Init rank column
        rankColumn.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(highScoreTable.getItems().indexOf(column.getValue()) + 1));

        lifetimeGames.setText(Integer.toString(currentUser.getLifetimeGames()));
        bestScore.setText(Integer.toString(currentUser.getBestScore()));
        fastestTime.setText(currentUser.getBestTimeString());
        mostLevelsCleared.setText(Integer.toString(currentUser.getMostLevelsCleared()));
        totalPlaytime.textProperty().bind(MainMenu.totalPlaytimeStringProperty());

        // Listener for key presses
        rootPane.getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case UP, DOWN:
                    if (!backButtonSelected) {
                        backButtonSelected = true;
                        backButton.setScaleX(backButton.getScaleX() + 0.1);
                        backButton.setScaleY(backButton.getScaleY() + 0.1);
                    }
                    break;
                case SPACE:
                    SceneTools.fadeOutToScene(event, "main-menu.fxml", 0, rootPane);
                default:
                    break;
            }
        });
    }

    /**
     * Updates the screen resolution.
     * @param resX the new given horizontal resolution
     * @param resY the new given vertical resolution
     * @param e the action that invoked this method
     */
    private void updateResolution(double resX, double resY, ActionEvent e) {
        rootPane.getScene().getWindow().setHeight(resY);
        rootPane.getScene().getWindow().setWidth(resX);
        MainMenu.screenHeightProperty.set(resY);
        MainMenu.screenWidthProperty.set(resX);
        try {
            SceneTools.switchScene(e, "leaderboard.fxml");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Reloads the current scene and sets the resolution to 720p.
     */
    public void set720p(ActionEvent e) {
        updateResolution(1280, 720, e);
    }

    /**
     * Reloads the current scene and sets the resolution to 1080p.
     */
    public void set1080p(ActionEvent e) {
        updateResolution(1920, 1080, e);
    }

}