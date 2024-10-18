package com.cs2212.cardbound.system;

import com.cs2212.cardbound.SceneTools;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import javafx.scene.input.InputEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static com.cs2212.cardbound.system.Gameplay.player;
import static com.cs2212.cardbound.system.MainMenu.*;

/**
 * This class controls the GUI functionality of the Main Menu screen.
 * @version 1.0
 * @author Jacob Couture, Pranav Arora
 * @since 3/3/2024
 */
public class MainMenuController {

    @FXML
    private StackPane rootPane;

    @FXML
    private Button newGameConfirm, newGameCancel, instructorMode, set720p, set1080p;

    @FXML
    private CheckBox passwordVisible, passwordVisible1;

    @FXML
    private Label startGameButton, newGameButton, loadGameButton, tutorialButton, leaderboardButton, settingsButton, exitButton, invalidLogin, invalidCreate;

    @FXML
    private Pane mainMenuPane, newGameWarning, loginPane, createUserPane;

    @FXML
    private TitledPane debugMenu;

    @FXML
    private TextField usernameField, shownPasswordField, usernameField1, shownPasswordField1;

    @FXML
    private PasswordField hiddenPasswordField, hiddenPasswordField1;

    @FXML
    private Rectangle darkenBackground;

    /**
     * The sequence of inputs that must be pressed to activate debug mode
     */
    private final String[] DEBUG_CODE = {"UP", "UP", "DOWN", "DOWN", "UP", "UP", "UP", "UP"};    // Use this set of inputs to enable debug mode
    /**
     * ArrayList that keeps track of the current list of inputs for activating debug mode
     */
    private ArrayList<String> inputArray;
    /**
     * Whether the new game button is selected
     */
    private boolean newGameButtonSelected = false;
    /**
     * Whether the load game button is selected
     */
    private boolean loadButtonSelected = false;
    /**
     * Whether the tutorial button is selected
     */
    private boolean tutorialButtonSelected = false;
    /**
     * Whether the leaderboard button is selected
     */
    private boolean leaderboardButtonSelected = false;
    /**
     * Whether the settings button is selected
     */
    private boolean settingsButtonSelected = false;
    /**
     * Whether the exit button is selected
     */
    private boolean exitButtonSelected = false;


    /**
     * Initializes the default scene settings.
     */
    public void initialize() {
        // Run post initialization when the initialization finishes
        Platform.runLater(this::postInit);

        inputArray = new ArrayList<>();
        newGameWarning.setVisible(false);
        darkenBackground.setVisible(false);

        // If the player hasn't already logged in, perform the login process
        if (!MainMenu.isLoggedIn()) {
            mainMenuPane.setVisible(false);
            loginPane.setVisible(false);
            createUserPane.setVisible(false);
            startGameButton.setAlignment(Pos.CENTER);
            startGameButton.setVisible(true);
            return;
        }

        // Otherwise switch to the scene normally
        rootPane.setOpacity(0);
        mainMenuPane.setVisible(true);

        SceneTools.fadeInScene(rootPane, 1500);
    }

    /**
     * Code to run immediately after scene initialization
     */
    private void postInit() {
        SceneTools.setScreenScale(MainMenu.screenWidthProperty.get(), MainMenu.screenHeightProperty.get(), rootPane);

        // Fade in music
        if (!AudioPlayer.getMenuMusicPlayer().getStatus().equals(MediaPlayer.Status.PLAYING))
            AudioPlayer.fadeInMusic(AudioPlayer.getMenuMusicPlayer(), 2000);

        set720p.setOnAction(this::set720p);
        set1080p.setOnAction(this::set1080p);
        debugInputListener(rootPane.getScene());
        if (MainMenu.isDebugActive()) {
            MainMenu.initDebugMenu(debugMenu);
        } else
            debugMenu.setVisible(false);

        instructorMode.setVisible(MainMenu.instructorModeActive);

        darkenBackground.widthProperty().bind(MainMenu.screenWidthProperty);
        darkenBackground.heightProperty().bind(MainMenu.screenHeightProperty);
        loadGameButton.disableProperty().bind(MainMenu.hasSavedGameProperty().not());

        // Listener for key presses
        rootPane.getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                // Navigate up through menu buttons
                case UP:
                    if (!newGameButtonSelected && !loadButtonSelected && !tutorialButtonSelected && !leaderboardButtonSelected && !settingsButtonSelected && !exitButtonSelected) {
                        changeNewGameButtonSelected(true);
                        break;
                    }
                    if (newGameButtonSelected) {
                        break;
                    }
                    else if (loadButtonSelected) {
                        changeLoadButtonSelected(false);
                        changeNewGameButtonSelected(true);
                    } else if (tutorialButtonSelected) {
                        changeTutorialButtonSelected(false);

                        if (loadGameButton.isDisabled()) {
                            changeNewGameButtonSelected(true);
                        } else {
                            changeLoadButtonSelected(true);
                        }
                    } else if (leaderboardButtonSelected) {
                        changeLeaderboardButtonSelected(false);
                        changeTutorialButtonSelected(true);
                    } else if (settingsButtonSelected) {
                        changeSettingsButtonSelected(false);
                        changeLeaderboardButtonSelected(true);
                    } else {
                        changeExitButtonSelected(false);
                        changeSettingsButtonSelected(true);
                    }
                    break;
                // Navigate down through menu buttons
                case DOWN:
                    if (!newGameButtonSelected && !loadButtonSelected && !tutorialButtonSelected && !leaderboardButtonSelected && !settingsButtonSelected && !exitButtonSelected) {
                        changeNewGameButtonSelected(true);
                        break;
                    }

                    if (newGameButtonSelected) {
                        changeNewGameButtonSelected(false);

                        if (loadGameButton.isDisabled()) {
                            changeTutorialButtonSelected(true);
                        } else {
                            changeLoadButtonSelected(true);
                        }
                    } else if (loadButtonSelected) {
                        changeLoadButtonSelected(false);
                        changeTutorialButtonSelected(true);
                    } else if (tutorialButtonSelected) {
                        changeTutorialButtonSelected(false);
                        changeLeaderboardButtonSelected(true);
                    } else if (leaderboardButtonSelected) {
                        changeLeaderboardButtonSelected(false);
                        changeSettingsButtonSelected(true);
                    } else if (settingsButtonSelected) {
                        changeSettingsButtonSelected(false);
                        changeExitButtonSelected(true);
                    } else {
                        break;
                    }
                    break;
                // Confirm selection on any menu button
                case SPACE:
                    if (startGameButton.isVisible())
                        showLogin();
                    else {
                        if (newGameButtonSelected)
                            newGame(event);
                        else if (loadButtonSelected)
                            loadGame(event);
                        else if (tutorialButtonSelected)
                            tutorial(event);
                        else if (leaderboardButtonSelected)
                            leaderboard(event);
                        else if (settingsButtonSelected)
                            settings(event);
                        else if (exitButtonSelected)
                            exit();
                    }
                default:
                    break;
            }
        });
    }

    /**
     * Called when pressing the start game button. Shows the login screen.
     */
    public void showLogin() {
        startGameButton.setVisible(false);
        darkenBackground.setVisible(true);
        loginPane.setVisible(true);
        invalidLogin.setVisible(false);

        usernameField.setText("");
        shownPasswordField.setText("");
        hiddenPasswordField.setText("");

        shownPasswordField.setManaged(false);
        shownPasswordField.setVisible(false);

        shownPasswordField.managedProperty().bind(passwordVisible.selectedProperty());
        shownPasswordField.visibleProperty().bind(passwordVisible.selectedProperty());

        hiddenPasswordField.managedProperty().bind(passwordVisible.selectedProperty().not());
        hiddenPasswordField.visibleProperty().bind(passwordVisible.selectedProperty().not());

        shownPasswordField.textProperty().bindBidirectional(hiddenPasswordField.textProperty());
    }

    /**
     * Called when pressing the login button. Loads the main menu when the player successfully logs in.
     */
    public void login() {
        String username = usernameField.getText();
        String password = hiddenPasswordField.getText();

        if (MainMenu.login(username, password)) {
            loginPane.setVisible(false);
            darkenBackground.setVisible(false);
            mainMenuPane.setVisible(true);
            MainMenu.setLoggedIn(true);
            totalPlaytime = currentUser.getTotalPlaytime();
            totalPlaytimeStringProperty().set(currentUser.getTotalPlaytimeString());
            MainMenu.startTotalPlaytimeTimer();
            if (currentUser.getUsername().equals("instructor"))
                toggleInstructorMode();
        } else {
            invalidLogin.setVisible(true);
        }
    }

    /**
     * Called when pressing Create User button from login window.
     */
    public void showCreateNewUser() {
        // Make create user pane visible and hide invalid message
        createUserPane.setVisible(true);
        invalidCreate.setVisible(false);

        // Clear username and password fields
        usernameField1.setText("");
        shownPasswordField1.setText("");
        hiddenPasswordField1.setText("");

        // Hide or show password fields based on the selected state of passwordVisible1 checkbox
        if (!shownPasswordField1.managedProperty().isBound())
            shownPasswordField1.setManaged(false);
        if (!shownPasswordField1.visibleProperty().isBound())
            shownPasswordField1.setVisible(false);

        shownPasswordField1.managedProperty().bind(passwordVisible1.selectedProperty());
        shownPasswordField1.visibleProperty().bind(passwordVisible1.selectedProperty());

        hiddenPasswordField1.managedProperty().bind(passwordVisible1.selectedProperty().not());
        hiddenPasswordField1.visibleProperty().bind(passwordVisible1.selectedProperty().not());

        // Bind text property bidirectionally to display/hide password
        shownPasswordField1.textProperty().bindBidirectional(hiddenPasswordField1.textProperty());
    }

    /**
     * Creates a new user using the provided username and password.
     * If both username and password are provided, it creates a new user, logs them in,
     * and starts the total playtime timer.
     * If either username or password is empty, it displays an invalid message.
     */
    public void createNewUser() {
        // Retrieve username and password from input fields
        String username = usernameField1.getText();
        String password = hiddenPasswordField1.getText();

        // Check if both username and password are provided
        if (!password.isEmpty() && !username.isEmpty()) {
            // Create a new user and log them in
            MainMenu.createUser(username, password);

            // Hide login and create user panes, show main menu pane, and set logged-in status
            loginPane.setVisible(false);
            createUserPane.setVisible(false);
            darkenBackground.setVisible(false);
            mainMenuPane.setVisible(true);
            MainMenu.setLoggedIn(true);

            // Start the total playtime timer
            MainMenu.startTotalPlaytimeTimer();
        }
        else {
            // Display invalid message if either username or password is empty
            invalidCreate.setVisible(true);
        }
    }

    /**
     * Called when pressing the close button on the login window. Closes the login window.
     */
    public void closeLogin() {
        startGameButton.setVisible(true);
        loginPane.setVisible(false);
        darkenBackground.setVisible(false);

        shownPasswordField.managedProperty().unbind();
        shownPasswordField.visibleProperty().unbind();

        hiddenPasswordField.managedProperty().unbind();
        hiddenPasswordField.visibleProperty().unbind();

        shownPasswordField.textProperty().unbindBidirectional(hiddenPasswordField.textProperty());
    }

    /**
     * Removes Create User Menu when X button is clicked
     */
    public void closeUser() {
        createUserPane.setVisible(false);
    }

    /**
     * Called when pressing the new game button. Starts a new run, warning the user if they have a saved run already.
     * @param e An event representing the button firing.
     */
    public void newGame(InputEvent e) {
        if (MainMenu.getHasSavedGame()) {
            newGameWarning.setVisible(true);
            mainMenuPane.setDisable(true);
            newGameConfirm.setOnAction(event -> {
                MainMenu.deleteSaveGame();
                currentUser.setLifetimeGames(currentUser.getLifetimeGames() + 1);
                player.setHealth(3);
                Gameplay.setLevelsCleared(1);
                Gameplay.setScore(0);
                Gameplay.setScoreMultiplier(1);
                Gameplay.setTimerValue("00:00:00");
                AudioPlayer.fadeOutMusic(AudioPlayer.getMenuMusicPlayer(), 500);
                SceneTools.fadeOutToScene(e, "gameplay.fxml", 500, rootPane);
            });
            newGameCancel.setOnAction(event -> {
                newGameWarning.setVisible(false);
                mainMenuPane.setDisable(false);
            });
        } else {
            currentUser.setLifetimeGames(currentUser.getLifetimeGames() + 1);
            player.setHealth(3);
            Gameplay.setLevelsCleared(1);
            Gameplay.setScore(0);
            Gameplay.setScoreMultiplier(1);
            Gameplay.setTimerValue("00:00:00");
            AudioPlayer.fadeOutMusic(AudioPlayer.getMenuMusicPlayer(), 500);
            SceneTools.fadeOutToScene(e, "gameplay.fxml", 500, rootPane);
        }
    }

    /**
     * Called when pressing the load game button. Loads a saved run and deletes the save after.
     * @param e An event representing the button firing.
     */
    public void loadGame(InputEvent e) {
        MainMenu.loadGame();
        AudioPlayer.fadeOutMusic(AudioPlayer.getMenuMusicPlayer(), 500);
        SceneTools.fadeOutToScene(e, "gameplay.fxml", 500, rootPane);
    }

    /**
     * Called when pressing the tutorial button. Switches to the tutorial screen.
     * @param e An event representing the button firing
     */
    public void tutorial(InputEvent e) {
        SceneTools.fadeOutToScene(e, "tutorial.fxml", 500, rootPane);
    }

    /**
     * Called when pressing the leaderboards button. Switches to the leaderboards screen.
     * @param e An event representing the button firing
     */
    public void leaderboard(InputEvent e) {
        SceneTools.fadeOutToScene(e, "leaderboard.fxml", 500, rootPane);
    }

    /**
     * Called when pressing the settings button. Switches to the settings screen.
     * @param e An event representing the button firing
     */
    public void settings(InputEvent e) {
        SceneTools.fadeOutToScene(e, "settings.fxml", 500, rootPane);
    }

    /**
     * Called when pressing the exit button. Closes the game.
     */
    public void exit() {
        currentUser.setAsPlayer(false);
        MainMenu.saveGame();
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000), rootPane);
        fadeTransition.setOnFinished(e -> SceneTools.exitApplication());
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        AudioPlayer.fadeOutMusic(AudioPlayer.getMenuMusicPlayer(), 1000);
        fadeTransition.play();
    }

    /**
     * Called when pressing the instructor mode button. Switches to the instructor mode screen.
     * @param e An event representing the button firing
     */
    public void instructorMode(ActionEvent e) {
        SceneTools.fadeOutToScene(e, "instructor-mode.fxml", 500, rootPane);
    }

    /**
     * Enlarges buttons when hovering over them
     * @param e the mouse event triggering this
     */
    public void hoverEnterButton(InputEvent e) {
        setHoverSelection(false, false, false, false, false, false);
        Label menuButton = (Label) e.getSource();
        menuButton.setScaleX(menuButton.getScaleX() + 0.1);
        menuButton.setScaleY(menuButton.getScaleY() + 0.1);

        switch (menuButton.getText()) {
            case "NEW GAME" -> setHoverSelection(true, false, false, false, false, false);
            case "LOAD GAME" -> {
                if (!loadGameButton.isDisabled())
                    setHoverSelection(false, true, false, false, false, false);
            }
            case "TUTORIAL" -> setHoverSelection(false, false, true, false, false, false);
            case "LEADERBOARDS" -> setHoverSelection(false, false, false, true, false, false);
            case "SETTINGS" -> setHoverSelection(false, false, false, false, true, false);
            case "EXIT" -> setHoverSelection(false, false, false, false, false, true);
            default -> {
            }
        }
    }

    /**
     * Shrinks buttons when you stop hovering them
     * @param e the mouse event triggering this
     */
    public void hoverExitButton(InputEvent e) {
        Label menuButton = (Label) e.getSource();
        menuButton.setScaleX(menuButton.getScaleX() - 0.1);
        menuButton.setScaleY(menuButton.getScaleY() - 0.1);
        setHoverSelection(false, false, false, false, false, false);
    }

    /**
     * Sets the selection of the main menu buttons
     * @param button1 new value for the new game button
     * @param button2 new value for the load game button
     * @param button3 new value for the tutorial button
     * @param button4 new value for the leaderboard button
     * @param button5 new value for the settings button
     * @param button6 new value for the exit button
     */
    private void setHoverSelection(boolean button1, boolean button2, boolean button3, boolean button4, boolean button5, boolean button6) {
        changeNewGameButtonSelected(button1);
        changeLoadButtonSelected(button2);
        changeTutorialButtonSelected(button3);
        changeLeaderboardButtonSelected(button4);
        changeSettingsButtonSelected(button5);
        changeExitButtonSelected(button6);
    }

    /**
     * Change whether the new game button is selected
     * @param newValue new true/false value for whether the button is selected
     */
    private void changeNewGameButtonSelected(boolean newValue) {
        if (newGameButtonSelected == newValue)
            return;
        newGameButtonSelected = newValue;
        if (newGameButtonSelected) {
            newGameButton.setScaleX(newGameButton.getScaleX() + 0.1);
            newGameButton.setScaleY(newGameButton.getScaleY() + 0.1);
        } else {
            newGameButton.setScaleX(newGameButton.getScaleX() - 0.1);
            newGameButton.setScaleY(newGameButton.getScaleY() - 0.1);
        }
    }

    /**
     * Change whether the load game button is selected
     * @param newValue new true/false value for whether the button is selected
     */
    private void changeLoadButtonSelected(boolean newValue) {
        if (loadButtonSelected == newValue)
            return;
        loadButtonSelected = newValue;
        if (loadButtonSelected) {
            loadGameButton.setScaleX(loadGameButton.getScaleX() + 0.1);
            loadGameButton.setScaleY(loadGameButton.getScaleY() + 0.1);
        } else {
            loadGameButton.setScaleX(loadGameButton.getScaleX() - 0.1);
            loadGameButton.setScaleY(loadGameButton.getScaleY() - 0.1);
        }
    }

    /**
     * Change whether the tutorial button is selected
     * @param newValue new true/false value for whether the button is selected
     */
    private void changeTutorialButtonSelected(boolean newValue) {
        if (tutorialButtonSelected == newValue)
            return;
        tutorialButtonSelected = newValue;
        if (tutorialButtonSelected) {
            tutorialButton.setScaleX(tutorialButton.getScaleX() + 0.1);
            tutorialButton.setScaleY(tutorialButton.getScaleY() + 0.1);
        } else {
            tutorialButton.setScaleX(tutorialButton.getScaleX() - 0.1);
            tutorialButton.setScaleY(tutorialButton.getScaleY() - 0.1);
        }
    }

    /**
     * Change whether the leaderboard button is selected
     * @param newValue new true/false value for whether the button is selected
     */
    private void changeLeaderboardButtonSelected(boolean newValue) {
        if (leaderboardButtonSelected == newValue)
            return;
        leaderboardButtonSelected = newValue;
        if (leaderboardButtonSelected) {
            leaderboardButton.setScaleX(leaderboardButton.getScaleX() + 0.1);
            leaderboardButton.setScaleY(leaderboardButton.getScaleY() + 0.1);
        } else {
            leaderboardButton.setScaleX(leaderboardButton.getScaleX() - 0.1);
            leaderboardButton.setScaleY(leaderboardButton.getScaleY() - 0.1);
        }
    }

    /**
     * Change whether the settings button is selected
     * @param newValue new true/false value for whether the button is selected
     */
    private void changeSettingsButtonSelected(boolean newValue) {
        if (settingsButtonSelected == newValue)
            return;
        settingsButtonSelected = newValue;
        if (settingsButtonSelected) {
            settingsButton.setScaleX(settingsButton.getScaleX() + 0.1);
            settingsButton.setScaleY(settingsButton.getScaleY() + 0.1);
        } else {
            settingsButton.setScaleX(settingsButton.getScaleX() - 0.1);
            settingsButton.setScaleY(settingsButton.getScaleY() - 0.1);
        }
    }

    /**
     * Change whether the exit button is selected
     * @param newValue new true/false value for whether the button is selected
     */
    private void changeExitButtonSelected(boolean newValue) {
        if (exitButtonSelected == newValue)
            return;
        exitButtonSelected = newValue;
        if (exitButtonSelected) {
            exitButton.setScaleX(exitButton.getScaleX() + 0.1);
            exitButton.setScaleY(exitButton.getScaleY() + 0.1);
        } else {
            exitButton.setScaleX(exitButton.getScaleX() - 0.1);
            exitButton.setScaleY(exitButton.getScaleY() - 0.1);
        }
    }

    /**
     * Listens for key presses from the user while on the main menu to determine if they have inputted the code for debug mode.
     * @param scene the current scene
     */
    private void debugInputListener(Scene scene) {
        PauseTransition debugTimer = new PauseTransition(Duration.millis(2000));
        debugTimer.setOnFinished(e -> inputArray.clear());
        // Listener for key presses
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            String curInput = event.getCode().toString();
            inputArray.add(curInput);
            if (inputArray.size() == 1 && curInput.equals("UP")) {
                debugTimer.play();
            }

            if (!(curInput.equals("UP")) && !(curInput.equals("DOWN"))) {
                inputArray.clear();
                debugTimer.stop();
            }

            // Activates debug mode if the code has been inputted correctly in under 2 seconds
            if (inputArray.size() >= 8) {
                String[] arr = new String[inputArray.size()];
                inputArray.toArray(arr);
                if (Arrays.equals(arr, DEBUG_CODE) && MainMenu.isLoggedIn())
                    MainMenu.initDebugMenu(debugMenu);
                inputArray.clear();
                debugTimer.stop();
            }
        });
    }

    /**
     * Called when pressing the exit debug button in the debug menu. Exits debug mode and closes the debug menu.
     */
    public void toggleDebugWindow() {
        MainMenu.setDebugActive(false);
        debugMenu.setVisible(false);
    }

    /**
     * Toggles instructor mode on or off
     */
    public void toggleInstructorMode() {
        MainMenu.instructorModeActive = !MainMenu.instructorModeActive;
        instructorMode.setVisible(MainMenu.instructorModeActive);
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
            SceneTools.switchScene(e, "main-menu.fxml");
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