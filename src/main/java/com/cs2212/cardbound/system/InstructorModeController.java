package com.cs2212.cardbound.system;

import com.cs2212.cardbound.SceneTools;
import com.cs2212.cardbound.gameplay.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

import static com.cs2212.cardbound.system.MainMenu.currentUser;
import static com.cs2212.cardbound.system.MainMenu.userData;

public class InstructorModeController {

    @FXML
    private StackPane rootPane;

    @FXML
    private TableView<User> userStatisticsTable;

    @FXML
    private TableColumn<User, String> usernameColumn, bestTimeColumn, totalPlaytimeColumn;

    @FXML
    private TableColumn<User, Integer> lifetimeGamesColumn, bestScoreColumn, totalLevelsClearedColumn;

    @FXML
    private TextField searchBar;

    @FXML
    private Label backButton;

    /**
     * Observable list of all the user data
     */
    private final ObservableList<User> obsData = FXCollections.observableArrayList(userData);
    /**
     * Whether the back button is selected
     */
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

        // Init back button functionality
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

        rootPane.requestFocus();
        // Deselect search bar if clicked off of it
        rootPane.getScene().setOnMousePressed(event -> {
            if (!searchBar.equals(event.getSource()))
                searchBar.getParent().requestFocus();
        });

        userStatisticsTable.setStyle("-fx-font-size: 14;");

        // Put the proper data in the table columns
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        lifetimeGamesColumn.setCellValueFactory(new PropertyValueFactory<>("lifetimeGames"));
        bestScoreColumn.setCellValueFactory(new PropertyValueFactory<>("bestScore"));
        bestTimeColumn.setCellValueFactory(new PropertyValueFactory<>("bestTimeString"));
        totalLevelsClearedColumn.setCellValueFactory(new PropertyValueFactory<>("mostLevelsCleared"));
        totalPlaytimeColumn.setCellValueFactory(new PropertyValueFactory<>("totalPlaytimeString"));

        // Add data to the table
        if (currentUser.getUsername().equals("instructor"))
            obsData.remove(currentUser);
        userStatisticsTable.getItems().addAll(obsData);

        // Initialize search bar
        initSearchBar();

        // Listener for key presses
        rootPane.getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                // Selects the back button if up or down is pressed
                case UP, DOWN:
                    if (!backButtonSelected) {
                        backButtonSelected = true;
                        backButton.setScaleX(backButton.getScaleX() + 0.1);
                        backButton.setScaleY(backButton.getScaleY() + 0.1);
                    }
                    break;
                // Presses the back button if space is pressed
                case SPACE:
                    SceneTools.fadeOutToScene(event, "main-menu.fxml", 0, rootPane);
                default:
                    break;
            }
        });
    }

    /**
     * Sets up the search functionality of the search bar
     */
    private void initSearchBar() {
        FilteredList<User> filteredData = new FilteredList<>(obsData, b -> true); //FIXME: change obsData to userData

        searchBar.textProperty().addListener(((observable, oldValue, newValue) -> filteredData.setPredicate(user -> {
            if (newValue == null || newValue.isEmpty())
                return true;

            String newValueLowerCase = newValue.toLowerCase();

            return user.getUsername().toLowerCase().contains(newValueLowerCase);
        })));

        SortedList<User> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(userStatisticsTable.comparatorProperty());
        userStatisticsTable.setItems(sortedData);
    }

}
