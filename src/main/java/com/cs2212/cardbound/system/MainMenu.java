package com.cs2212.cardbound.system;

import com.cs2212.cardbound.gameplay.GameSave;
import com.cs2212.cardbound.gameplay.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import javafx.animation.PauseTransition;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.util.Duration;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A utility class that manages flags + User related methods for the Main Menu at runtime.
 * <br>
 * @version 1.0
 * @author Jacob Couture, Pranav Arora
 * @since 3/9/2024
 */
public class MainMenu {

    /**
     * The width of the user's screen in pixels
     */
    public static final double SCREEN_WIDTH = Screen.getPrimary().getBounds().getWidth();
    /**
     * The height of the user's screen in pixels
     */
    public static final double SCREEN_HEIGHT = Screen.getPrimary().getBounds().getHeight();
    /**
     * Modifiable screen width property
     */
    public static DoubleProperty screenWidthProperty = new SimpleDoubleProperty(SCREEN_WIDTH);
    /**
     * Modifiable screen height property
     */
    public static DoubleProperty screenHeightProperty = new SimpleDoubleProperty(SCREEN_HEIGHT);
    /**
     * Whether there is a user logged in
     */
    private static boolean isLoggedIn;
    /**
     * Whether the user has a saved run
     */
    private static final BooleanProperty hasSavedGame = new SimpleBooleanProperty();
    /**
     * Whether instructor mode has been activated
     */
    public static boolean instructorModeActive;
    /**
     * Whether debug mode has been activated
     */
    private static boolean debugActive;
    /**
     * Whether the user is currently confirming their resolution changes
     */
    public static boolean isConfirmingResolution;
    /**
     * List container holding the json converted user data
     */
    public static List<User> userData;
    /**
     * Container to store the current player's user object.
     */
    public static User currentUser;
    /**
     * List container (list of GameSave objects instantiated by gson()), holding
     * the json converted game data
     */
    public static List<GameSave> gameData;
    /**
     * Container to store the current player's game save.
     */
    public static GameSave currentGameSave;
    /**
     * Timer for the total overall playtime for this user
     */
    private static final PauseTransition totalPlaytimeTimer = new PauseTransition(Duration.millis(1000));
    /**
     * Integer representing the total overall playtime for this user
     */
    public static int totalPlaytime;
    /**
     * String representing the total overall playtime for this user
     */
    private static final StringProperty totalPlaytimeString = new SimpleStringProperty();

    private MainMenu() {
        throw new AssertionError();
    }

    /**
     * Gets the state of isLoggedIn.
     * @return True if a user is logged in, false otherwise.
     */
    public static boolean isLoggedIn() {
        return isLoggedIn;
    }

    /**
     * Sets the state of isLoggedIn
     * @param state The new given state.
     */
    public static void setLoggedIn(boolean state) {
        isLoggedIn = state;
    }

    /**
     * Searches the user database (userData) to see if the login credentials are valid.
     * If valid, sets static variable currentUser to the "logged in" user.
     * @param username The username of the user attempting to log in.
     * @param password The password of the user attempting to log in.
     * @return True if the user successfully logs in, false otherwise.
     */
    public static boolean login(String username, String password) {
        // Check if userData is not null
        if (userData != null) {
            Iterator<User> itr = userData.iterator();   // Iterate over users
            while (itr.hasNext()) {
                currentUser = itr.next(); // Point currentUser to the next user in the userData list each time
                if (currentUser.getUsername().equals(username)) {
                    if (currentUser.getPassword().equals(password)) { // If user/pass match in userData
                        currentUser.setAsPlayer(true);  // Set the user as the "Player"

                        int playerID = currentUser.getUserID();
                        retrieveSettings(currentUser);  // Retrieve saved settings and set them accordingly upon login
                        Iterator<GameSave> itr2 = gameData.iterator(); // Iterate over saved games
                        while (itr2.hasNext()) {
                            currentGameSave = itr2.next();
                            int checkId = currentGameSave.getUserId();
                            // Check if user ID matches in game data
                            if (playerID == checkId) {
                                // If user already has previously saved game (non-null cards list)
                                if (currentGameSave.getCardsList() != null)
                                    hasSavedGame.set(true);

                                return true;    // login successful
                            }
                        }
                        // If gameSave not found, create one and add to gameData
                        currentGameSave = new GameSave(currentUser.getUserID());
                        gameData.add(currentGameSave);
                        saveGame();
                        return true;    // login successful
                    }
                }
            }
        }
        return false;   // login unsuccessful
    }

    /**
     * Retrieves the user settings from the provided User object
     * and updates the corresponding settings in the game.
     * @param user The User object from which to retrieve the settings.
     */
    public static void retrieveSettings(User user) {
        // Retrieve and update Accessibility settings (Toggle Switches)
        Gameplay.levelTimerDisabled = user.getDisableLevelTimer();
        Gameplay.runTimerDisabled = user.getDisableRunTimer();
        Gameplay.unlimitedHealthToggled = user.getUnlHealth();

        // Retrieve and update Audio settings (Volume Sliders)
        AudioPlayer.setMasterVolume(user.getMasterVolume());
        AudioPlayer.setMusicVolume(user.getMusicVolume());
        AudioPlayer.setSfxVolume(user.getSfxVolume());

        AudioPlayer.fadeMusicTo(AudioPlayer.getMenuMusicPlayer(), (AudioPlayer.getMusicVolume() / 100f) * (AudioPlayer.getMasterVolume() / 100f), 250);
    }

    /**
     * Creates a new user with the provided username and password, adds it to userData (user database),
     * creates a corresponding game save, and saves the game data.
     * Sets the newly created user as the current player.
     * @param username The username of the new user.
     * @param password The password of the new user.
     */
    public static void createUser(String username, String password) {
        // Set created user's ID to increment from the previous one (consecutive)
        int ID = (userData != null) ? userData.size() : 0;

        // Creates corresponding User and GameSave objects and set them to current static variables
        currentUser = new User(username, password, ID);
        currentGameSave = new GameSave(currentUser.getUserID());

        // Add currentUser and currentGameSave to their respective databases
        userData.add(currentUser);
        gameData.add(currentGameSave);
        saveGame();

        // Set currentUser to be the "Player"
        currentUser.setAsPlayer(true);
    }

    /**
     * Gets the state of hasSavedGame.
     * @return True if this user has a saved run, false otherwise.
     */
    public static boolean getHasSavedGame() {
        return hasSavedGame.get();
    }

    /**
     * Sets hasSavedGame to a new value
     * @param value the new given value
     */
    public static void setHasSavedGame(boolean value) {
        hasSavedGame.set(value);
    }

    /**
     * Returns the hasSavedGame property
     * @return the hasSavedGame property
     */
    public static BooleanProperty hasSavedGameProperty() {
        return hasSavedGame;
    }

    /**
     * Saves the user's game data and settings to JSON files.
     * <br><br>
     * Updates the current user's statistics, volume settings,
     * and gameplay settings, and then writes the user data and game data
     * to JSON files to serve as a functioning database which can retrieve
     * data even after program termination.
     */
    public static void saveGame() {
        try {
            // Save current user's settings
            currentUser.setTotalPlaytimeString(totalPlaytime);
            currentUser.setTotalPlaytime(totalPlaytime);
            currentUser.setMusicVolume(AudioPlayer.getMusicVolume());
            currentUser.setMasterVolume(AudioPlayer.getMasterVolume());
            currentUser.setSfxVolume(AudioPlayer.getSfxVolume());
            currentUser.setDisableLevelTimer(Gameplay.levelTimerDisabled);
            currentUser.setDisableRunTimer(Gameplay.runTimerDisabled);
            currentUser.setUnlHealth(Gameplay.unlimitedHealthToggled);

            // Update game save data
            int playerID = currentUser.getUserID();
            for (GameSave save : gameData) {
                if (save.getUserId() == playerID) {
                    if (getHasSavedGame() && save.getCardsList() != null) {
                        save.setGameSave(currentGameSave.getNumLives(), currentGameSave.getLevel(), currentGameSave.getScore(), currentGameSave.getRunTimer(), currentGameSave.getEnemyHp(), currentGameSave.getCardsList());
                    }
                }
            }

            // Convert user data and game data to JSON strings
            String jsonData1 = new Gson().toJson(userData);
            String jsonData2 = new Gson().toJson(gameData);

            // Write user data to SaveData.json
            FileWriter jsonWriter1 = new FileWriter("./SaveData.json");
            jsonWriter1.write(jsonData1);
            jsonWriter1.close();

            // Write game data to GameSaveData.json
            FileWriter jsonWriter2 = new FileWriter("./GameSaveData.json");
            jsonWriter2.write(jsonData2);
            jsonWriter2.close();

        } catch (IOException e) {
            e.printStackTrace(); // catch any exceptions gracefully and log them to output console
        }
    }

    /**
     * Loads the user's saved game data from JSON files (database).
     * <br><br>
     * It reads the user data from SaveData.json and the game data from GameSaveData.json,
     * instantiates the JSON data to corresponding User/GameSave objects,
     * and then holds them as static lists for other methods/procedures to access.
     */
    public static void loadGame() {
        // Check if SaveData.json and GameSaveData.json files exist
        File saveDataJson = new File("./SaveData.json");
        File gameSaveDataJson = new File("./GameSaveData.json");

        // Initializes lists if it is the first user (if null)
        if (userData == null)
            userData = new ArrayList<>();
        if (gameData == null)
            gameData = new ArrayList<>();

        // Add instructor as user if there is no user data at all
        if (userData.isEmpty())
            userData.add(new User("instructor", "WorldsBestInstructor", 0));
        // Add instructor game data if there is no game data at all
        if (gameData.isEmpty())
            gameData.add(new GameSave(0));

        // Skip loading the json files if they don't exist
        if (!saveDataJson.isFile() || !gameSaveDataJson.isFile()) return;

        try {
            // Read user data from SaveData.json
            Gson gson1 = new Gson();
            JsonReader reader1 = new JsonReader(new FileReader(saveDataJson));

            // Create acceptable Type for users array (userData)
            Type listType1 = new TypeToken<List<User>>() {
            }.getType();

            // Convert JSON save data to List of User Objects
            userData = gson1.fromJson(reader1, listType1);

            // Read game data from GameSaveData.json
            Gson gson2 = new Gson();
            JsonReader reader2 = new JsonReader(new FileReader(gameSaveDataJson));

            // Create acceptable Type for GameSave array (gameData)
            Type listType2 = new TypeToken<List<GameSave>>() {}.getType();

            // Convert JSON game save data to List of GameSave Objects
            gameData = gson2.fromJson(reader2, listType2);
            if (gameData.isEmpty())
                gameData.add(new GameSave(0));

        } catch (IOException e) {
            e.printStackTrace(); // catch any exceptions gracefully and log them to output console
        }
    }

    /**
     * Starts the timer for total playtime
     */
    public static void startTotalPlaytimeTimer() {
        totalPlaytimeTimer.setOnFinished(e -> {
            updateTotalPlaytime();
            totalPlaytimeTimer.play();
        });
        totalPlaytimeTimer.play();
    }

    /**
     * Updates the total playtime value every second
     */
    private static void updateTotalPlaytime() {
        totalPlaytime++;
        int hours = totalPlaytime / 10000;
        int minutes = ((totalPlaytime - hours * 10000) / 100);
        int seconds = (totalPlaytime - hours * 10000 - minutes * 100);

        if (seconds > 59) {
            minutes++;
            seconds -= 60;
        }
        if (minutes > 59) {
            hours++;
            minutes -= 60;
        }

        String hoursString = String.format("%02d", hours);
        String minutesString = String.format("%02d", minutes);
        String secondsString = String.format("%02d", seconds);

        setTotalPlaytimeString(hoursString + ":" + minutesString + ":" + secondsString);
    }

    /**
     * Deletes the user's saved game.
     */
    public static void deleteSaveGame() {
        hasSavedGame.set(false);
        int playerID = currentUser.getUserID();
        for (GameSave save : gameData) {
            if (save.getUserId() == playerID) {
                save.setGameSave(3, 0, 0, "00:00:00", -1, null);
            }
        }
        saveGame();
    }

    /**
     * Gets the state of debugActive.
     * @return True if debug mode has been activated, false otherwise.
     */
    public static boolean isDebugActive() {
        return debugActive;
    }

    /**
     * Sets the new state of debugActive
     * @param state The new given state.
     */
    public static void setDebugActive(boolean state) {
        debugActive = state;
    }

    /**
     * Called when pressing the exit debug button in the debug menu. Exits debug mode and closes the debug menu.
     */
    public static void toggleDebugWindow(TitledPane debugMenu) {
        MainMenu.setDebugActive(false);
        debugMenu.setVisible(false);
    }

    /**
     * Initializes the debugMenu in a scene.
     * @param debugMenu the given debugMenu
     */
    public static void initDebugMenu(TitledPane debugMenu) {
        MainMenu.setDebugActive(true);
        ObservableList<Node> contents = ((AnchorPane) (debugMenu.getContent())).getChildren();
        ((Button) contents.get(0)).setOnAction(event -> MainMenu.saveGame());
        ((Button) contents.get(1)).setOnAction(event -> MainMenu.loadGame());
        ((Button) contents.get(3)).setOnAction(event -> MainMenu.toggleDebugWindow(debugMenu));
        debugMenu.setVisible(true);
        debugMenu.setDisable(false);

        Pane titleBar = (Pane) debugMenu.lookup(".title");
        Text title = (Text) titleBar.lookup(".text");

        debugMenu.setRotate(180);
        debugMenu.getContent().setRotate(180);
        title.setRotate(180);
        debugMenu.setLayoutY(411);
    }

    /**
     * Enlarges buttons when hovering over them
     * @param e the mouse event triggering this
     */
    public static void hoverEnterButton(MouseEvent e) {
        Label menuButton = (Label) e.getSource();
        menuButton.setScaleX(menuButton.getScaleX() + 0.1);
        menuButton.setScaleY(menuButton.getScaleY() + 0.1);
    }

    /**
     * Shrinks buttons when you stop hovering them
     * @param e the mouse event triggering this
     */
    public static void hoverExitButton(MouseEvent e) {
        Label menuButton = (Label) e.getSource();
        menuButton.setScaleX(menuButton.getScaleX() - 0.1);
        menuButton.setScaleY(menuButton.getScaleY() - 0.1);
    }

    /**
     * Gets the total playtime as a string
     * @return the total playtime as a string
     */
    public static String getTotalPlaytimeString() {
        return totalPlaytimeString.get();
    }

    /**
     * Gets the total playtime string property
     * @return the total playtime string property
     */
    public static StringProperty totalPlaytimeStringProperty() {
        return totalPlaytimeString;
    }

    /**
     * Sets the total playtime string property
     * @param totalPlaytimeString the new total playtime value
     */
    public static void setTotalPlaytimeString(String totalPlaytimeString) {
        MainMenu.totalPlaytimeString.set(totalPlaytimeString);
    }
}
