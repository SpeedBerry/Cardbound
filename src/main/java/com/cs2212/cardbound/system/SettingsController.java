package com.cs2212.cardbound.system;

import com.cs2212.cardbound.MainStage;
import com.cs2212.cardbound.SceneTools;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

import static com.cs2212.cardbound.system.Gameplay.*;
import static com.cs2212.cardbound.system.MainMenu.*;

public class SettingsController {

    @FXML
    StackPane rootPane;

    @FXML
    MenuButton resolutionDropdown;

    @FXML
    Slider masterVolumeSlider, musicVolumeSlider, sfxVolumeSlider;

    @FXML
    Pane resolutionWarning, resolutionWarningBackground;

    @FXML
    Button confirmResolution, revertResolution, levelTimerToggle, runTimerToggle, unlimitedHealthToggle;

    @FXML
    Label resolutionWarningText, backButton;

    /**
     * Whether the back button is selected
     */
    private boolean backButtonSelected = false;

    /**
     * Observable list of all the built-in selectable resolutions
     */
    private final ObservableList<MenuItem> resolutions = FXCollections.observableArrayList(
            new MenuItem((int) MainMenu.screenWidthProperty.get() + " x " + (int) MainMenu.screenHeightProperty.get()),
            new MenuItem("3840 x 2160"),
            new MenuItem("2560 x 1440"),
            new MenuItem("1920 x 1080"),
            new MenuItem("1600 x 900"),
            new MenuItem("1366 x 768"),
            new MenuItem("1280 x 720")
    );
    /**
     * The timer for counting down the confirmation timer after changing your resolution
     */
    private final PauseTransition countdownTimer = new PauseTransition(Duration.millis(1000));
    /**
     * The number of seconds remaining on the countdown tier
     */
    private int secondsRemaining = 15;
    /**
     * The previous X resolution before changing resolutions
     */
    private static double prevResX;
    /**
     * The previous Y resolution before changing resolutions
     */
    private static double prevResY;

    /**
     * Initializes the default scene settings.
     */
    public void initialize() {
        // Run post initialization when the initialization finishes
        Platform.runLater(this::postInit);

        if (isConfirmingResolution) {
            resolutionWarningBackground.setVisible(true);
            resolutionWarning.setVisible(true);
            rootPane.setOpacity(1.0);
        } else {
            resolutionWarningBackground.setVisible(false);
            resolutionWarning.setVisible(false);
            rootPane.setOpacity(0);
            SceneTools.fadeInScene(rootPane, 1500);
        }
    }

    /**
     * Code to run immediately after scene initialization
     */
    private void postInit() {
        // Set proper screen scale
        SceneTools.setScreenScale(MainMenu.screenWidthProperty.get(), MainMenu.screenHeightProperty.get(), rootPane);
        // Initialize back button
        backButton.setOnMouseClicked(e -> {
            saveGame();
            SceneTools.fadeOutToScene(e, "main-menu.fxml", 500, rootPane);
        });
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

        // Initialize accessibility toggle states
        levelTimerToggle.setBackground(null);
        toggleSwitch(levelTimerToggle, currentUser.getDisableLevelTimer());
        runTimerToggle.setBackground(null);
        toggleSwitch(runTimerToggle, currentUser.getDisableRunTimer());
        unlimitedHealthToggle.setBackground(null);
        toggleSwitch(unlimitedHealthToggle, currentUser.getUnlHealth());

        // Initialize resolution dropdown
        initResolutionDropdown();

        // Initialize volume sliders
        initVolumeSliders();

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
     * Initializes the options in the resolution dropdown menu
     */
    private void initResolutionDropdown() {
        // Initialize resolution dropdown text
        resolutionDropdown.setText((int) MainMenu.screenWidthProperty.get() + " x " + (int) MainMenu.screenHeightProperty.get());
        MenuItem curResolution = new MenuItem((int) MainMenu.screenWidthProperty.get() + " x " + (int) MainMenu.screenHeightProperty.get());

        // Set the actions for the options in the resolution dropdown
        for (int i = 1; i < resolutions.size(); i++) {
            MenuItem menuItem = resolutions.get(i);

            /* If the current menu item is for the current screen resolution, indicate that it is by adding the text "(current)" beside it.
            Doesn't add an action to the menu item in this case to avoid reloading the screen without changing the resolution */
            if (menuItem.getText().equals(curResolution.getText())) {
                menuItem.setText(menuItem.getText() + " (current)");
                resolutions.getFirst().setText("");
                continue;
            }

            menuItem.setOnAction(e -> {
                isConfirmingResolution = true;
                updateResolution(menuItem.getText());
            });
        }

        /* If the first menu item in the dropdown is for the current screen resolution, this means that the user's monitor resolution is not
        in the dropdown list. It will be displayed at the top of the list instead. */
        if (resolutions.getFirst().getText().equals(curResolution.getText()))
            resolutions.getFirst().setText(curResolution.getText() + " (current)");
        else
            resolutions.removeFirst();

        // Add all the resolutions to the dropdown menu
        resolutionDropdown.getItems().addAll(resolutions);

        // If the user has changed resolutions, begin the countdown timer for reverting the resolution if the user does not confirm it
        if (isConfirmingResolution) {
            confirmResolution.setOnAction(e -> {
                countdownTimer.stop();
                isConfirmingResolution = false;
                resolutionWarning.setVisible(false);
                resolutionWarningBackground.setVisible(false);
            });

            revertResolution.setOnAction(e -> {
                countdownTimer.stop();
                isConfirmingResolution = false;
                updateResolution(((int) prevResX + " x " + (int) prevResY));
            });
            showResolutionWarning();
        }
    }

    /**
     * Initializes the volume sliders and binds their values to the actual numerical volume property they are supposed to be changing
     */
    private void initVolumeSliders() {
        masterVolumeSlider.valueProperty().bindBidirectional(AudioPlayer.masterVolumeProperty());
        musicVolumeSlider.valueProperty().bindBidirectional(AudioPlayer.musicVolumeProperty());
        sfxVolumeSlider.valueProperty().bindBidirectional(AudioPlayer.sfxVolumeProperty());

        // Listens to see if the master volume slider has changed and updates the volume of the main menu music in real time
        masterVolumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> AudioPlayer.getMenuMusicPlayer().setVolume((AudioPlayer.getMusicVolume() / 100f) * (AudioPlayer.getMasterVolume() / 100f)));
        // Listens to see if the music volume slider has changed and updates the volume of the main menu music in real time
        musicVolumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> AudioPlayer.getMenuMusicPlayer().setVolume((AudioPlayer.getMusicVolume() / 100f) * (AudioPlayer.getMasterVolume() / 100f)));
        // Plays the card click sound effect after changing the SFX volume slider, so you can hear the effects of the changes
        sfxVolumeSlider.setOnMouseReleased(e -> AudioPlayer.playCardClick());
    }

    /**
     * Updates the screen resolution.
     * @param menuItemResolution the resolution listed in the given menu item
     */
    private void updateResolution(String menuItemResolution) {
        prevResX = MainMenu.screenWidthProperty.get();
        prevResY = MainMenu.screenHeightProperty.get();

        // Get the resolution listed in the menu item as doubles
        menuItemResolution = menuItemResolution.replaceAll("[^0-9]", " ");
        menuItemResolution = menuItemResolution.replaceAll(" +", " ");
        String[] resolutions = menuItemResolution.split(" ");

        double resX = Double.parseDouble(resolutions[0]);
        double resY = Double.parseDouble(resolutions[1]);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(0), rootPane);
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(750));

        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);

        // When the fade transition has ended, change the game resolution and begin the pause transition
        fadeTransition.setOnFinished(event -> {
            rootPane.getScene().getWindow().setHeight(resY);
            rootPane.getScene().getWindow().setWidth(resX);
            MainMenu.screenHeightProperty.set(resY);
            MainMenu.screenWidthProperty.set(resX);
            pauseTransition.play();
        });

        // When the pause transition has ended, reload the current scene
        pauseTransition.setOnFinished(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(MainStage.class.getResource("settings.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) rootPane.getScene().getWindow();
                Scene scene = new Scene(root, MainMenu.SCREEN_WIDTH, MainMenu.SCREEN_HEIGHT, Color.BLACK);
                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        fadeTransition.play();
    }

    /**
     * Controls the timer on the changed resolution warning.
     */
    private void showResolutionWarning() {
        if (secondsRemaining == 1)
            resolutionWarningText.setText(secondsRemaining + " second.");
        else
            resolutionWarningText.setText(secondsRemaining + " seconds.");

        // If the number of seconds remaining on the timer is greater than 0, decrement the timer by 1 second. Otherwise, revert the resolution to
        if (secondsRemaining > 0)
            secondsRemaining--;
        else {
            isConfirmingResolution = false;
            resolutionWarning.setVisible(false);
            resolutionWarningBackground.setVisible(false);
            updateResolution(((int) prevResX + " x " + (int) prevResY));
            countdownTimer.stop();
            return;
        }

        countdownTimer.setOnFinished(e -> showResolutionWarning());
        countdownTimer.play();
    }

    /**
     * Toggles the state of the toggleable buttons
     * @param toggleButton the given button
     * @param toggleState the new state to change the button to
     */
    private void toggleSwitch(Button toggleButton, boolean toggleState) {
        if (!toggleState)
            ((ImageView) toggleButton.getGraphic()).setImage(new Image(Objects.requireNonNull(MainStage.class.getResourceAsStream("sprites/switch-off.png"))));
        else
            ((ImageView) toggleButton.getGraphic()).setImage(new Image(Objects.requireNonNull(MainStage.class.getResourceAsStream("sprites/switch-on.png"))));
    }

    /**
     * Toggles the level timer
     */
    public void setLevelTimerToggle() {
        levelTimerDisabled = !levelTimerDisabled;
        currentUser.setDisableLevelTimer(levelTimerDisabled);
        saveGame();
        toggleSwitch(levelTimerToggle, levelTimerDisabled);
    }

    /**
     * Toggles the run timer
     */
    public void setRunTimerToggle() {
        runTimerDisabled = !runTimerDisabled;
        currentUser.setDisableRunTimer(runTimerDisabled);
        saveGame();
        toggleSwitch(runTimerToggle, runTimerDisabled);
    }

    /**
     * Toggles unlimited health
     */
    public void setUnlimitedHealthToggle() {
        unlimitedHealthToggled = !unlimitedHealthToggled;
        currentUser.setUnlHealth(unlimitedHealthToggled);
        saveGame();
        toggleSwitch(unlimitedHealthToggle, unlimitedHealthToggled);
    }

}
