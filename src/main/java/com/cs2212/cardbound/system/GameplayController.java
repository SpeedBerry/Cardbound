package com.cs2212.cardbound.system;

import com.cs2212.cardbound.MainStage;
import com.cs2212.cardbound.SceneTools;
import com.cs2212.cardbound.gameplay.Card;
import com.cs2212.cardbound.gameplay.Enemy;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Collections;

//IMPORTS FOR CARD LOGIC
import java.util.ArrayList;

import static com.cs2212.cardbound.gameplay.CardLogic.*;
import static com.cs2212.cardbound.system.Gameplay.*;
import static com.cs2212.cardbound.system.MainMenu.*;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A class that controls the GUI functionality of the Gameplay screen.
 * @version 1.0
 * @author Jacob Couture, Abhinav Bhati
 * @since 3/3/2024
 */
public class GameplayController {

    @FXML
    private StackPane rootPane;

    @FXML
    private Rectangle darkenBackground, attackBar, enemyHealthBar, whiteOut;

    @FXML
    private Pane pauseMenu, cardsPane, attackButton, enemyHealthPane, battlePane, statisticsPane, resultsScreen, resultButtons, victoryScreen, retryPane;

    @FXML
    private Card card0, card1, card2, card3;

    @FXML
    private TitledPane debugMenu;

    @FXML
    private Button set720p, set1080p, debugResults;

    // General labels
    @FXML
    private Label attackText, levelTimerLabel, currentScore, currentLevel, runTimerLabel, runTimerValue, enemyHealthLabel, retryMessage, levelClearedText, victoryPoints;

    // Results screen labels
    @FXML
    private Label resultScoreLabel, resultScoreValue, resultLevelsClearedLabel, resultLevelsClearedValue, resultTimeSpentLabel, resultTimeSpentValue, resultBossesDefeatedLabel, resultBossesDefeatedValue;

    // Pause screen buttons
    @FXML
    private Label resumeButton, pauseRestartButton, pauseMainMenuButton, pauseExitButton;

    // Retry screen buttons
    @FXML
    private Label retryButton, abandonRunButton;

    // Results screen buttons
    @FXML
    private Label resultRestartButton, resultMainMenuButton, resultExitButton;

    @FXML
    private ImageView background, pauseButton, playerSprite, playerDown, enemySprite, heartContainer1, heartContainer2, heartContainer3, continueButton;

    /**
     * Red gradient color for the attack bar
     */
    private final Stop[] redStops = new Stop[] {new Stop(0, Color.color(1.0f, 97f / 255f, 94f / 255f)), new Stop(1, Color.color(213f / 255f, 23f / 255f, 20f / 255f))};
    private final LinearGradient RED_BAR = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, redStops);
    /**
     * Yellow gradient color for the attack bar
     */
    private final Stop[] yellowStops = new Stop[] {new Stop(0, Color.color(252f / 255f, 223f / 255f, 149f / 255f)), new Stop(1, Color.color(254f / 255f, 192f / 255f, 35f / 255f))};
    private final LinearGradient YELLOW_BAR = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, yellowStops);
    /**
     * Scales the attack button up when it is hovered
     */
    private ScaleTransition attackButtonSize;
    /**
     * The glow around the attack button
     */
    private DropShadow borderGlow;
    /**
     * Pulsate glow animation for the attack button
     */
    private Timeline pulsateGlow;
    /**
     * Animation for the attack bar moving
     */
    private Timeline progressBar;
    /**
     * The timer for the level timer
     */
    private final PauseTransition levelTimer = new PauseTransition(Duration.millis(1000));
    /**
     * The pause in between the cards pane hiding and appearing again
     */
    private final PauseTransition pauseHideCards = new PauseTransition(Duration.millis(300));
    /**
     * The animation of the cards pane flying off-screen
     */
    private TranslateTransition hideCardsPane;
    /**
     * The animation of the attack button flying off-screen
     */
    private TranslateTransition hideAttackButton;
    /**
     * The animation of the screen fading to white after retrying a level
     */
    private FadeTransition fadeWhiteOut = new FadeTransition(Duration.millis(5000), whiteOut);
    /**
     * The animation of the screen fading back in after retrying a level
     */
    private FadeTransition fadeWhiteIn = new FadeTransition(Duration.millis(1000), whiteOut);
    /**
     * The pause in between the screen fading to white and the screen fading back in
     */
    private PauseTransition retryPause = new PauseTransition(Duration.millis(250));
    /**
     * The amount of time remaining on the level timer
     */
    private int timeRemaining = Gameplay.getBaseLevelTimer();
    /**
     * The object containing the enemy's health and assets
     */
    private final Enemy enemy = new Enemy(-1, "enemy");
    /**
     * Whether the enemy is currently attacking
     */
    public static boolean enemyAttacking = false;
    /**
     * Whether the player has taken damage
     */
    private boolean tookDamage = false;
    /**
     * Whether the player overkilled the enemy
     */
    private boolean overkill = false;
    /**
     * Whether any of the retry screen buttons are selected
     */
    private boolean retryButtonSelected, abandonRunButtonSelected, continueButtonSelected;
    /**
     * Whether any of the pause menu buttons are selected
     */
    private boolean resumeButtonSelected, pauseRestartButtonSelected, pauseMainMenuButtonSelected, pauseExitButtonSelected;
    /**
     * Whether any of the results screen buttons are selected
     */
    private boolean resultRestartButtonSelected, resultMainMenuButtonSelected, resultExitButtonSelected;
    /**
     * The icon for the full heart
     */
    private final Image fullHeart = new Image(Objects.requireNonNull(MainStage.class.getResourceAsStream("sprites/full_heart.png")));
    /**
     * The icon for the broken heart
     */
    private final Image brokenHeart = new Image(Objects.requireNonNull(MainStage.class.getResourceAsStream("sprites/broken_heart.png")));
    /**
     * The list of card data and information
     */
    private String[][] cardsDataList;
    /**
     * The list of cards in the current hand
     */
    private final ArrayList<Card> cardList = new ArrayList<>();

    /**
     * Initializes the default scene settings.
     */
    public void initialize() {
        // Run post initialization when the initialization finishes
        Platform.runLater(this::postInit);

        // Load game protocol
        if (MainMenu.getHasSavedGame()) {
            MainMenu.setHasSavedGame(false);
            player.setHealth(currentGameSave.getNumLives());
            enemy.setMaxHealth(currentGameSave.getEnemyHp());
            enemy.setHealth(currentGameSave.getEnemyHp());
            Gameplay.setLevelsCleared(currentGameSave.getLevel());
            if (Gameplay.getLevelsCleared() > 5) {
                Gameplay.setScoreMultiplier(1 * (1.5 * (int) (Gameplay.getLevelsCleared() / 5f)));
                Gameplay.setCardMultiplier(1 + ((int) (getLevelsCleared() / 5f)));
            }
            else {
                Gameplay.setScoreMultiplier(1);
                Gameplay.setCardMultiplier(1);
            }
            Gameplay.setScore(currentGameSave.getScore());
            Gameplay.setTimePassed(currentGameSave.getRunTimer());
            Gameplay.setTimerValue(currentGameSave.getRunTimer());
            cardsDataList = currentGameSave.getCardsList();
        } else {
            // Card data
            int[] cardPoolChoices = generateCardPools(); // Get a list of which card pools to draw from
            int[] cardIndexList = generateCardIndexList(cardPoolChoices); // Creates a list of indexes to draw from the card pool
            cardsDataList = cardDataReader(cardIndexList); // Draws cards and their data from the pool
        }

        // Set the player health to 4 if unlimited health is enabled
        if (unlimitedHealthToggled)
            player.setHealth(4);

        List<Integer> shuffledLocationList = shuffleLocations();
        // Test cards, keep locations though
        buildCard(card0, cardsDataList[0], shuffledLocationList.get(0), -11);
        buildCard(card1, cardsDataList[1], shuffledLocationList.get(1), -11);
        buildCard(card2, cardsDataList[2], shuffledLocationList.get(2), -11);
        buildCard(card3, cardsDataList[3], shuffledLocationList.get(3), -11);

        // Add all the current cards to the card list
        cardList.add(card0);
        cardList.add(card1);
        cardList.add(card2);
        cardList.add(card3);

        // Initialize selected cards
        Gameplay.cardCount.set(0);
        Gameplay.selectedCards.clear();

        // Make the enemy a boss if the level is a multiple of 5
        if (Gameplay.getLevelsCleared() % 5 == 0) {
            enemy.setType("boss");
        }

        // Init enemy health and sprite
        enemySprite.setImage(enemy.getIdle());
        enemy.setMaxHealth(calculateTotalDamage(card0,card1));

        // Show scene
        rootPane.setOpacity(0.0);
        SceneTools.fadeInScene(rootPane, 1500);
        darkenBackground.setVisible(false);
        pauseMenu.setVisible(false);
    }

    /**
     * Code to run immediately after scene initialization
     */
    private void postInit() {
        // Set the proper screen scale
        SceneTools.setScreenScale(MainMenu.screenWidthProperty.get(), MainMenu.screenHeightProperty.get(), rootPane);

        // Allow the game to be paused
        Gameplay.isAttacking = false;

        // Choose background
        background.setImage(new Image(Objects.requireNonNull(MainStage.class.getResourceAsStream("sprites/backgrounds/battleback" + ((((getLevelsCleared() - 1) / 5) % 10) + 1) + ".png"))));

        // Make cards fly in on level start
        hideCardPaneAnimation(1);
        showCardPaneAnimation(1000);

        // Bind the dark overlay found when the pause menu is open to the screen size to ensure it always covers the entire screen
        darkenBackground.widthProperty().bind(MainMenu.screenWidthProperty);
        darkenBackground.heightProperty().bind(MainMenu.screenHeightProperty);

        // Initialize level timer
        startLevelTimer();
        // Initialize all the statistics counters
        initStatCounters();

        // Initialize run timer
        if (!Gameplay.runTimerDisabled) {
            runTimerValue.textProperty().bind(Gameplay.timerValueProperty());
            Gameplay.startRunTimer();
        } else {
            runTimerLabel.setVisible(false);
            runTimerValue.setVisible(false);
        }

        // Initialize attack button
        initAttackButton();
        // Update player health
        updatePlayerHearts();
        // Initialize enemy health bar text
        enemyHealthLabel.setText(enemy.getMaxHealth() + " HP");

        levelClearedText.setFont(Font.font("System", FontWeight.BOLD, 48));

        // Start or resume gameplay music
        if (!AudioPlayer.getGameplayMusicPlayer().getStatus().equals(MediaPlayer.Status.PLAYING))
            AudioPlayer.fadeInMusic(AudioPlayer.getGameplayMusicPlayer(), 1000);
        else
            AudioPlayer.fadeMusicTo(AudioPlayer.getGameplayMusicPlayer(), (AudioPlayer.getMusicVolume() / 100f) * (AudioPlayer.getMasterVolume() / 100f), 250);
        // Stop game over music if it is not stopped
        if (!AudioPlayer.getGameOverMusicPlayer().getStatus().equals(MediaPlayer.Status.STOPPED))
            AudioPlayer.stopGameOverMusic();

        // Listener for key presses
        rootPane.getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                // Select card 1
                case DIGIT1:
                    for (Card card : cardList) {
                        if (card.getLayoutX() == 104) {
                            card.toggleSelect();
                            break;
                        }
                    }
                    break;
                // Select card 2
                case DIGIT2:
                    for (Card card : cardList) {
                        if (card.getLayoutX() == 384) {
                            card.toggleSelect();
                            break;
                        }
                    }
                    break;
                // Select card 3
                case DIGIT3:
                    for (Card card : cardList) {
                        if (card.getLayoutX() == 664) {
                            card.toggleSelect();
                            break;
                        }
                    }
                    break;
                // Select card 4
                case DIGIT4:
                    for (Card card : cardList) {
                        if (card.getLayoutX() == 944) {
                            card.toggleSelect();
                            break;
                        }
                    }
                    break;
                // Navigate up through selectable UI buttons
                case UP:
                    // Retry screen buttons
                    if (retryPane.isVisible()) {
                        if (!retryButtonSelected && !abandonRunButtonSelected) {
                            changeRetryButtonSelected(true);
                            break;
                        }
                        if (retryButtonSelected) {
                            break;
                        } else {
                            changeAbandonRunButtonSelected(false);
                            changeRetryButtonSelected(true);
                        }
                    }
                    // Pause menu buttons
                    else if (pauseMenu.isVisible()) {
                        if (!resumeButtonSelected && !pauseRestartButtonSelected && !pauseMainMenuButtonSelected && !pauseExitButtonSelected) {
                            changeResumeButtonSelected(true);
                            break;
                        }
                        if (resumeButtonSelected) {
                            break;
                        } else if (pauseRestartButtonSelected) {
                            changePauseRestartButtonSelected(false);
                            changeResumeButtonSelected(true);
                        } else if (pauseMainMenuButtonSelected) {
                            changePauseMainMenuButtonSelected(false);
                            changePauseRestartButtonSelected(true);
                        } else {
                            changePauseExitButtonSelected(false);
                            changePauseMainMenuButtonSelected(true);
                        }
                    }
                    // Results screen buttons
                    else if (resultButtons.isVisible()) {
                        if (!resultRestartButtonSelected && !resultMainMenuButtonSelected && !resultExitButtonSelected) {
                            changeResultRestartButtonSelected(true);
                            break;
                        }
                        if (resultRestartButtonSelected) {
                            break;
                        } else if (resultMainMenuButtonSelected) {
                            changeResultMainMenuButtonSelected(false);
                            changeResultRestartButtonSelected(true);
                        } else {
                            changeResultExitButtonSelected(false);
                            changeResultMainMenuButtonSelected(true);
                        }
                    }
                    break;
                // Navigate down through selectable UI buttons
                case DOWN:
                    // Retry screen buttons
                    if (retryPane.isVisible()) {
                        if (!retryButtonSelected && !abandonRunButtonSelected) {
                            changeRetryButtonSelected(true);
                            break;
                        }
                        if (retryButtonSelected) {
                            changeRetryButtonSelected(false);
                            changeAbandonRunButtonSelected(true);
                        } else {
                            break;
                        }
                    }
                    // Pause menu buttons
                    else if (pauseMenu.isVisible()) {
                        if (!resumeButtonSelected && !pauseRestartButtonSelected && !pauseMainMenuButtonSelected && !pauseExitButtonSelected) {
                            changeResumeButtonSelected(true);
                            break;
                        }
                        if (resumeButtonSelected) {
                            changeResumeButtonSelected(false);
                            changePauseRestartButtonSelected(true);
                        } else if (pauseRestartButtonSelected) {
                            changePauseRestartButtonSelected(false);
                            changePauseMainMenuButtonSelected(true);
                        } else if (pauseMainMenuButtonSelected) {
                            changePauseMainMenuButtonSelected(false);
                            changePauseExitButtonSelected(true);
                        } else {
                            break;
                        }
                    }
                    // Results screen buttons
                    else if (resultButtons.isVisible()) {
                        if (!resultRestartButtonSelected && !resultMainMenuButtonSelected && !resultExitButtonSelected) {
                            changeResultRestartButtonSelected(true);
                            break;
                        }
                        if (resultRestartButtonSelected) {
                            changeResultRestartButtonSelected(false);
                            changeResultMainMenuButtonSelected(true);
                        } else if (resultMainMenuButtonSelected) {
                            changeResultMainMenuButtonSelected(false);
                            changeResultExitButtonSelected(true);
                        } else {
                            break;
                        }
                    }
                    break;
                // Confirm selection on any button
                case SPACE:
                    // Attack button
                    if (!Gameplay.isPaused)
                        clickAttack();
                    // Continue button
                    if (continueButton.isVisible()) {
                        changeContinueButtonSelected(true);
                        SceneTools.fadeOutToScene(event, "gameplay.fxml", 1000, rootPane);
                    }
                    // Retry screen buttons
                    else if (retryPane.isVisible()) {
                        if (retryButtonSelected) {
                            whiteOut.setOpacity(0.0);
                            whiteOut.setVisible(true);
                            fadeWhiteOut.play();
                            AudioPlayer.stopClockTick();
                            AudioPlayer.playClockTickAccelerate();
                        } else if (abandonRunButtonSelected) {
                            showResults();
                        }
                    }
                    // Pause menu buttons
                    else if (pauseMenu.isVisible()) {
                        if (resumeButtonSelected) {
                            resumeGame();
                        } else if (pauseRestartButtonSelected) {
                            restartGame(event);
                        } else if (pauseMainMenuButtonSelected) {
                            returnToMainMenu(event);
                        } else if (pauseExitButtonSelected) {
                            exitGame();
                        }
                    }
                    // Results screen buttons
                    else if (resultButtons.isVisible()) {
                        if (pauseRestartButtonSelected) {
                            restartGame(event);
                        } else if (pauseMainMenuButtonSelected) {
                            returnToMainMenu(event);
                        } else if (pauseExitButtonSelected) {
                            exitGame();
                        }
                    }
                    break;
                // Open pause menu
                case ESCAPE:
                    if (!Gameplay.isAttacking) {
                        if (!Gameplay.isPaused)
                            pauseGame();
                        else
                            resumeGame();
                    }
                default:
                    break;
            }
        });

        // If debug mode is enabled then initialize and show the menu
        if (MainMenu.isDebugActive()) {
            set720p.setOnAction(this::set720p);
            set1080p.setOnAction(this::set1080p);
            debugResults.setOnAction(e -> showResults());
            MainMenu.initDebugMenu(debugMenu);
        } else
            debugMenu.setVisible(false);
    }

    /**
     * This method is a helper method to create a random variation of the 4 card positions
     * @return the list in a shuffled state
     */
    private List<Integer> shuffleLocations() {
        List<Integer> locationsList = new ArrayList<>(List.of(104, 384, 664, 944));
        Collections.shuffle(locationsList);
        return locationsList; // Returns the shuffled list
    }


    /**
     * Makes the cards pane and the attack button fly off towards the bottom of the screen
     * @param duration number of milliseconds the animation will play for
     */
    private void hideCardPaneAnimation(int duration) {
        hideCardsPane = new TranslateTransition(Duration.millis(duration), cardsPane);
        hideAttackButton = new TranslateTransition(Duration.millis(duration), attackButton);

        // Makes the cards pane fly off-screen
        cardsPane.setDisable(true);
        hideCardsPane.setByY(cardsPane.getHeight() * SceneTools.currentScaleY);
        hideCardsPane.setInterpolator(Interpolator.SPLINE(0.5, 0, 0, 1));

        // Make the attack button fly off-screen
        hideAttackButton.setByY(cardsPane.getHeight() * SceneTools.currentScaleY);
        hideAttackButton.setInterpolator(Interpolator.SPLINE(0.5, 0, 0, 1));

        hideCardsPane.play();
        hideAttackButton.play();
    }

    /**
     * Makes the cards pane and the attack button fly up from the bottom of the screen back to their original position
     * @param duration number of milliseconds the animation will play for
     */
    private void showCardPaneAnimation(int duration) {
        // Make the cards pane and attack button fly back on-screen
        hideCardsPane.setByY(-(cardsPane.getHeight() * SceneTools.currentScaleY));
        hideAttackButton.setByY(-(cardsPane.getHeight() * SceneTools.currentScaleY));
        hideCardsPane.setDuration(Duration.millis(duration));
        hideAttackButton.setDuration(Duration.millis(duration));

        pauseHideCards.setOnFinished(e -> {
            hideCardsPane.setOnFinished(event -> cardsPane.setDisable(false));
            hideCardsPane.play();
            hideAttackButton.play();
        });
        pauseHideCards.play();
    }

    /**
     * Initializes and starts the level timer.
     */
    private void startLevelTimer() {
        if (!Gameplay.levelTimerDisabled) {
            levelTimerLabel.setText("1:00");
            levelTimer.setOnFinished(e -> {
                updateLevelTimer();
                levelTimer.play();
            });
            levelTimer.play();
        } else {
            levelTimerLabel.setVisible(false);
        }
    }

    /**
     * Updates the level timer every second
     */
    private void updateLevelTimer() {
        timeRemaining--;
        levelTimerLabel.setText("0:" + String.format("%02d", timeRemaining));

        if (timeRemaining == 0) {
            levelTimer.stop();
            levelTimer.setOnFinished(null);
            player.setHealth(player.getHealth() - 1);
            updatePlayerHearts();
            hideCardPaneAnimation(1000);
            retryLevel();
        }
    }

    /**
     * Initializes the statistics counters in the top right of the screen
     */
    private void initStatCounters() {
        currentScore.setText(String.valueOf(Gameplay.getScore()));
        currentLevel.setText(String.valueOf(Gameplay.getLevelsCleared()));
    }

    /**
     * Initializes the attack button
     */
    private void initAttackButton() {
        // Disable attack button text on scene start
        attackText.setDisable(true);

        // Set up the functions of the attack button and its initial state
        attackButton.setOnMouseEntered(event -> hoverSelectAttack());
        attackButton.setOnMouseExited(event -> hoverDeselectAttack());
        attackButton.setOnMouseClicked(event -> clickAttack());
        attackBar.setWidth(0);

        // Set up the attack button colors and effects
        attackBar.setFill(YELLOW_BAR);
        attackButtonSize = new ScaleTransition(Duration.millis(75), attackButton);
        borderGlow = new DropShadow();
        borderGlow.setColor(Color.SILVER);
        borderGlow.setOffsetX(0f);
        borderGlow.setOffsetY(0f);
        borderGlow.setSpread(0.5);

        // Animation for the attack button outer glow
        pulsateGlow = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(borderGlow.widthProperty(), 0),
                        new KeyValue(borderGlow.heightProperty(), 0),
                        new KeyValue(attackButton.scaleXProperty(), 1.0),
                        new KeyValue(attackButton.scaleYProperty(), 1.0)
                ),
                new KeyFrame(Duration.millis(2000),
                        new KeyValue(borderGlow.widthProperty(), 75),
                        new KeyValue(borderGlow.heightProperty(), 75),
                        new KeyValue(attackButton.scaleXProperty(), 1.1),
                        new KeyValue(attackButton.scaleYProperty(), 1.1)
                ),
                new KeyFrame(Duration.millis(4000),
                        new KeyValue(borderGlow.widthProperty(), 0),
                        new KeyValue(borderGlow.heightProperty(), 0),
                        new KeyValue(attackButton.scaleXProperty(), 1.0),
                        new KeyValue(attackButton.scaleYProperty(), 1.0)
                )
        );
        pulsateGlow.setCycleCount(Animation.INDEFINITE);


        // Listens to the card count variable to see if it has changed. If so, animate the attack bar accordingly
        Gameplay.cardCount.addListener((observable, oldValue, newValue) -> animateAttackBar((double) Gameplay.cardCount.get() / 2));

        // Listens to the width of the attack bar to see if it has changed. If so, various effects are applied or removed to the attack button.
        attackBar.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (attackBar.getWidth() < 154) {
                attackBar.setFill(YELLOW_BAR);
                attackText.setDisable(true);
                attackText.setTextFill(Color.BLACK);
                attackButton.setEffect(null);
                pulsateGlow.stop();
            }
            else {
                attackBar.setFill(RED_BAR);
                attackText.setDisable(false);
                attackText.setTextFill(Color.color(0.9, 0.9, 0.9));
                attackButton.setEffect(borderGlow);
                pulsateGlow.play();
            }
        });
    }

    /**
     * Animate the attack bar when cards are selected/deselected
     * @param percent a double value from 0.0 to 1.0 to determine how much to fill the progress bar
     */
    public void animateAttackBar(double percent) {
        percent *= 154;
        progressBar = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(attackBar.widthProperty(), attackBar.getWidth())),
                // The numbers in the interpolator are the values of the spline control points. It is what gives the smooth animation effects.
                new KeyFrame(Duration.millis(400), new KeyValue(attackBar.widthProperty(), percent, Interpolator.SPLINE(0.68, 0, 0.32, 0.7)))
        );
        progressBar.play();
    }

    /**
     * Animates the enemy health bar depleting
     * @param value the new value to set the enemy's health to
     */
    private void animateEnemyHealthBar(double value) {
        enemyHealthLabel.setText((int) value + " HP");

        //TODO: Make enemy health actually work
        if (value < 0)
            value = 0;
        else
            value = (value / enemy.getMaxHealth()) * 126;

        Timeline enemyHealthAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(enemyHealthBar.widthProperty(), enemyHealthLabel.getWidth())),
                // The numbers in the interpolator are the values of the spline control points. It is what gives the smooth animation effects.
                new KeyFrame(Duration.millis(400), new KeyValue(enemyHealthBar.widthProperty(), value, Interpolator.SPLINE(0.68, 0, 0.32, 0.7)))
        );
        enemyHealthAnimation.play();
    }

    /**
     * All the logic for attacking
     */
    private void attackLogic() {
        // Set enemy health to new value
        Card cardA = Gameplay.selectedCards.getFirst();
        Card cardB = Gameplay.selectedCards.getLast();

        // Ensure that an attack card is always the first parameter
        if (cardA.getType().getText().equals("attack")) {
            enemy.setHealth(enemy.getMaxHealth() - calculateTotalDamage(cardA, cardB));
        } else {
            enemy.setHealth(enemy.getMaxHealth() - calculateTotalDamage(cardB, cardA));
        }

        // Change player health if enemy is still alive
        if (enemy.getHealth() > 0) {
            tookDamage = true;
            player.setHealth(player.getHealth() - 1);
        } else if (enemy.getHealth() < 0) {
            overkill = true;
            player.setHealth(player.getHealth() - 1);
        }
    }

    /**
     * Updates the player's hearts if they take damage
     */
    private void updatePlayerHearts() {
        switch (player.getHealth()) {
            case 2 -> heartContainer3.setImage(brokenHeart);
            case 1 -> {
                heartContainer3.setImage(brokenHeart);
                heartContainer2.setImage(brokenHeart);
            }
            case 0 -> {
                heartContainer3.setImage(brokenHeart);
                heartContainer2.setImage(brokenHeart);
                heartContainer1.setImage(brokenHeart);
            }
            default -> {
                if (player.getHealth() >= 3) {
                    heartContainer3.setImage(fullHeart);
                    heartContainer2.setImage(fullHeart);
                    heartContainer1.setImage(fullHeart);
                } else {
                    heartContainer3.setImage(brokenHeart);
                    heartContainer2.setImage(brokenHeart);
                    heartContainer1.setImage(brokenHeart);
                }
            }
        }
    }

    /**
     * Performs all the animations during the attack phase
     */
    private void attackPhaseAnimations() {
        levelTimer.stop();
        Gameplay.pauseRunTimer();
        pauseButton.setDisable(true);
        pauseButton.setOpacity(0.5);

        hideCardPaneAnimation(1500);

        // Calculate attack logic
        attackLogic();
        attackAnimation(playerSprite, enemySprite);
    }

    /**
     * The animations for attacking
     * @param attacker the sprite performing the attack animations
     * @param defender the sprite performing the take damage animations
     */
    private void attackAnimation(ImageView attacker, ImageView defender) {
        if (pauseHideCards.getStatus().equals(Animation.Status.RUNNING))
            pauseHideCards.pause();

        PauseTransition pause = new PauseTransition(Duration.millis(300));

        final double ATTACKER_BASE_X = attacker.getLayoutX();
        final double DEFENDER_BASE_X = defender.getLayoutX();
        double ATTACKER_MOVE_DISTANCE = 60;
        double DEFENDER_MOVE_DISTANCE = 10;

        // If the attacker is on the right side of the screen (meaning it is the enemy), negate the attacker and defender move distances
        if (ATTACKER_BASE_X > battlePane.getWidth() / 2) {
            ATTACKER_MOVE_DISTANCE = -ATTACKER_MOVE_DISTANCE;
            DEFENDER_MOVE_DISTANCE = -DEFENDER_MOVE_DISTANCE;
        }

        Timeline attack;

        // Attacker animation for player
        if (!enemyAttacking) {
            attack = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(attacker.layoutXProperty(), ATTACKER_BASE_X),
                            new KeyValue(attacker.imageProperty(), player.getAttackWindUp())
                    ),
                    new KeyFrame(Duration.millis(750),
                            new KeyValue(attacker.layoutXProperty(), ATTACKER_BASE_X - ATTACKER_MOVE_DISTANCE / 2, Interpolator.EASE_BOTH),
                            new KeyValue(attacker.imageProperty(), player.getAttack())
                    ),
                    new KeyFrame(Duration.millis(1000),
                            new KeyValue(attacker.layoutXProperty(), ATTACKER_BASE_X + ATTACKER_MOVE_DISTANCE * 2, Interpolator.SPLINE(0.1, 0.2, 0, 0.02))
                    )
            );
        }
        // Attacker animation for enemy
        else {
            attack = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(attacker.layoutXProperty(), ATTACKER_BASE_X),
                            new KeyValue(attacker.imageProperty(), enemy.getAttackWindUp())
                    ),
                    new KeyFrame(Duration.millis(750),
                            new KeyValue(attacker.layoutXProperty(), ATTACKER_BASE_X - ATTACKER_MOVE_DISTANCE / 2, Interpolator.EASE_BOTH),
                            new KeyValue(attacker.imageProperty(), enemy.getAttack())
                    ),
                    new KeyFrame(Duration.millis(1000),
                            new KeyValue(attacker.layoutXProperty(), ATTACKER_BASE_X + ATTACKER_MOVE_DISTANCE * 2, Interpolator.SPLINE(0.1, 0.2, 0, 0.02))
                    )
            );
        }

        // Defender animation
        Timeline shake = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(defender.layoutXProperty(), DEFENDER_BASE_X)),
                new KeyFrame(Duration.millis(50), new KeyValue(defender.layoutXProperty(), DEFENDER_BASE_X + DEFENDER_MOVE_DISTANCE)),
                new KeyFrame(Duration.millis(150), new KeyValue(defender.layoutXProperty(), DEFENDER_BASE_X - DEFENDER_MOVE_DISTANCE)),
                new KeyFrame(Duration.millis(200), new KeyValue(defender.layoutXProperty(), DEFENDER_BASE_X))
        );
        shake.setCycleCount(3);

        // Return attacker to initial position
        Timeline endAttack = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(attacker.layoutXProperty(), ATTACKER_BASE_X + ATTACKER_MOVE_DISTANCE * 2)),
                new KeyFrame(Duration.millis(1000), new KeyValue(attacker.layoutXProperty(), ATTACKER_BASE_X, Interpolator.EASE_BOTH))
        );

        // When the attacker's attack connects with the defender, play the take damage animation for the defender
        attack.setOnFinished(e -> {
            playerSprite.setImage(player.getIdle());
            enemySprite.setImage(enemy.getIdle());
            // If the enemy is the one attacking, update the player's health. Otherwise, update the enemy's health
            if (enemyAttacking) {
                updatePlayerHearts();
                if (enemy.getType().equals("boss"))
                    AudioPlayer.playBossAttack();
                else
                    AudioPlayer.playEnemyAttack();
                AudioPlayer.playPlayerHurt();
            }
            else {
                animateEnemyHealthBar(enemy.getHealth());
                AudioPlayer.playPlayerAttack();
                if (enemy.getType().equals("boss"))
                    AudioPlayer.playBossHurt();
                else
                    AudioPlayer.playEnemyHurt();
            }
            shake.play();
        });

        // When the defender's take damage animation has finished, return the attacker to their initial position
        shake.setOnFinished(e -> endAttack.play());

        // When the attacker has returned to their initial position, determine what the outcome of the attack was
        endAttack.setOnFinished(e -> {
            pause.play();

            // If the player defeated the enemy, show the victory screen
            if (enemy.getHealth() == 0) {
                enemySprite.setImage(enemy.getDead());
                if (enemy.getType().equals("boss"))
                    AudioPlayer.playBossDeath();
                else
                    AudioPlayer.playEnemyDeath();
                showVictory();
            }
            // If overkill occurs, deal damage to the player and allow them to progress to the next level if they are still alive
            else if (overkill) {
                overkill = false;
                enemySprite.setImage(enemy.getDead());
                if (enemy.getType().equals("boss"))
                    AudioPlayer.playBossDeath();
                else
                    AudioPlayer.playEnemyDeath();
                updatePlayerHearts();
                levelClearedText.setFont(Font.font("Cambria", FontWeight.BOLD, FontPosture.ITALIC, 48));
                levelClearedText.setText("Do not waste our power.");
                if (player.getHealth() == 0)
                    showResults();
                else
                    showVictory();
            } else if (!enemyAttacking && !tookDamage)
                pauseHideCards.play();


            if (enemyAttacking) {
                enemyAttacking = false;
                tookDamage = false;

                // If the player's health is 0, show the results screen. Otherwise, prompt the player to retry the level
                if (player.getHealth() == 0) {
                    showResults();
                } else {
                    hideCardsPane.pause();
                    hideAttackButton.pause();
                    retryLevel();
                }
            }

            // If the player took damage, begin the enemy jump animation
            if (tookDamage)
                pause.setOnFinished(event -> enemyJumpAnimation());
        });
        attack.play();
    }

    /**
     * The animation for the enemy jumping before their attack, indicating that the player failed to defeat them
     */
    private void enemyJumpAnimation() {
        enemyAttacking = true;
        final double ENEMY_BASE_Y = enemySprite.getLayoutY();
        final double ENEMY_HEALTH_BASE_Y = enemyHealthPane.getLayoutY();
        final double ENEMY_JUMP_DISTANCE = -40;
        final double ENEMY_SCALE_X = 0.8;
        final double ENEMY_SCALE_Y = 1.2;
        final Interpolator start = Interpolator.SPLINE(0.1, 0.2, 0, 0);
        final Interpolator end = Interpolator.SPLINE(0, 0, 0.2, 0.1);

        Timeline enemyJump = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(enemySprite.layoutYProperty(), ENEMY_BASE_Y), new KeyValue(enemyHealthPane.layoutYProperty(), ENEMY_HEALTH_BASE_Y)),
                new KeyFrame(Duration.millis(100),
                        new KeyValue(enemySprite.layoutYProperty(), (ENEMY_BASE_Y + ENEMY_JUMP_DISTANCE / 2) / (5f/6f), start),
                        new KeyValue(enemyHealthPane.layoutYProperty(), (ENEMY_HEALTH_BASE_Y + ENEMY_JUMP_DISTANCE / 2) / (5f/6f), start),
                        new KeyValue(enemySprite.scaleXProperty(), ENEMY_SCALE_X, start),
                        new KeyValue(enemySprite.scaleYProperty(), ENEMY_SCALE_Y, start)
                ),
                new KeyFrame(Duration.millis(200),
                        new KeyValue(enemySprite.layoutYProperty(), ENEMY_BASE_Y + ENEMY_JUMP_DISTANCE, end),
                        new KeyValue(enemyHealthPane.layoutYProperty(), ENEMY_HEALTH_BASE_Y + ENEMY_JUMP_DISTANCE, end),
                        new KeyValue(enemySprite.scaleXProperty(), 1.0, end),
                        new KeyValue(enemySprite.scaleYProperty(), 1.0, end)
                ),
                new KeyFrame(Duration.millis(300),
                        new KeyValue(enemySprite.layoutYProperty(), (ENEMY_BASE_Y + ENEMY_JUMP_DISTANCE) / 1.25f),
                        new KeyValue(enemyHealthPane.layoutYProperty(), (ENEMY_HEALTH_BASE_Y + ENEMY_JUMP_DISTANCE) / 1.25f, start),
                        new KeyValue(enemySprite.scaleXProperty(), 1.2),
                        new KeyValue(enemySprite.scaleYProperty(), 0.8)
                ),
                new KeyFrame(Duration.millis(400),
                        new KeyValue(enemySprite.layoutYProperty(), (ENEMY_BASE_Y + ENEMY_JUMP_DISTANCE / 2) / (5f/6f), start),
                        new KeyValue(enemyHealthPane.layoutYProperty(), (ENEMY_HEALTH_BASE_Y + ENEMY_JUMP_DISTANCE / 2) / (5f/6f), start),
                        new KeyValue(enemySprite.scaleXProperty(), ENEMY_SCALE_X, start),
                        new KeyValue(enemySprite.scaleYProperty(), ENEMY_SCALE_Y, start)
                ),
                new KeyFrame(Duration.millis(500),
                        new KeyValue(enemySprite.layoutYProperty(), ENEMY_BASE_Y, end),
                        new KeyValue(enemyHealthPane.layoutYProperty(), ENEMY_HEALTH_BASE_Y),
                        new KeyValue(enemySprite.scaleXProperty(), 1.0, end),
                        new KeyValue(enemySprite.scaleYProperty(), 1.0, end)
                )
        );
        enemyJump.setCycleCount(3);
        enemyJump.setOnFinished(e -> attackAnimation(enemySprite, playerSprite));
        enemyJump.play();
    }

    /**
     * Displays the time stop effect for when the player takes damage
     */
    private void retryLevel() {
        levelTimer.stop();
        Gameplay.pauseRunTimer();
        AudioPlayer.fadeMusicTo(AudioPlayer.getGameplayMusicPlayer(), 0, 1000);
        playerSprite.setImage(player.getDowned());
        setHoverSelectionRetry(false, false);
        setHoverSelection(false, false, false, false);

        TranslateTransition hideTimer = new TranslateTransition(Duration.millis(1500), levelTimerLabel);
        hideTimer.setByY(-statisticsPane.getHeight() * SceneTools.currentScaleY);
        hideTimer.setInterpolator(Interpolator.SPLINE(0.5, 0, 0, 1));
        hideTimer.play();

        final String[] retryMessagesYellow = {
                "Fate is unkind...\nTurn back time and try again?",
                "The sands of time march onward.\nWill you revolt?",
                "Failure is the first step.\nWill you take the second?",
                "You can't fall here.\nShow them just what you're made of.",
                "Your end hasn't come just yet.\nWith us, time is no obstacle.",
                "It's not over.\nWe can help you get back up."
        };
        final String[] retryMessagesRed = {
                "Get back up.\nYou are not done here.",
                "This is not the end.\nGet up and FIGHT.",
                "Do not let them win.\nFight harder, better, faster, and stronger.",
                "We will not let your light die out.\nDo not let us down.",
                "Do not let their power overwhelm you.\nFailure is no longer an option.",
                "You cannot let them gain our power.\nFight with everything you have!"
        };
        final int MIN_MESSAGE = 0;
        final int MAX_MESSAGE = 5;

        // Darken the background and add a glow around the player
        FadeTransition freezeFrame = new FadeTransition(Duration.millis(500), darkenBackground);
        DropShadow playerGlow = new DropShadow();

        /*
        If the player has 2 or more health, make the glow animation around their body yellow. Otherwise, make it red.
        Also display a random retry message from the list of retry messages.
         */
        if (player.getHealth() >= 2) {
            playerGlow.setColor(Color.color(227f/255f, 180f/255f, 52f/255f, 0.7));
            retryMessage.setText(retryMessagesYellow[ThreadLocalRandom.current().nextInt(MIN_MESSAGE, MAX_MESSAGE + 1)]);
        }
        else {
            playerGlow.setColor(Color.color(242f/255f, 78f/255f, 78f/255f, 0.7));
            retryMessage.setText(retryMessagesRed[ThreadLocalRandom.current().nextInt(MIN_MESSAGE, MAX_MESSAGE + 1)]);
        }

        playerGlow.setOffsetX(0f);
        playerGlow.setOffsetY(0f);
        playerGlow.setSpread(0.5);

        retryPane.setVisible(true);
        playerDown.setVisible(true);
        darkenBackground.setOpacity(0.0);
        darkenBackground.setVisible(true);
        retryMessage.setOpacity(0.0);
        retryMessage.setVisible(true);

        AudioPlayer.playClockTick();

        freezeFrame.setToValue(1.0);
        freezeFrame.setOnFinished(e -> {
            retryButton.setOpacity(0.0);
            retryButton.setVisible(true);
            abandonRunButton.setOpacity(0.0);
            abandonRunButton.setVisible(true);

            // Animation for showing the retry and quit buttons
            Timeline fadeButtons = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(retryMessage.opacityProperty(), 0.0)
                    ),
                    new KeyFrame(Duration.millis(300),
                            new KeyValue(retryMessage.opacityProperty(), 0.0)
                    ),
                    new KeyFrame(Duration.millis(1050),
                            new KeyValue(retryMessage.opacityProperty(), 1.0)
                    ),
                    new KeyFrame(Duration.millis(1550),
                            new KeyValue(retryButton.opacityProperty(), 0.0),
                            new KeyValue(abandonRunButton.opacityProperty(), 0.0)
                    ),
                    new KeyFrame(Duration.millis(1800),
                            new KeyValue(retryButton.opacityProperty(), 1.0),
                            new KeyValue(abandonRunButton.opacityProperty(), 1.0)
                    )
            );
            fadeButtons.play();
        });
        freezeFrame.play();

        // Animation for the player sprite outer glow
        Timeline glow = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(playerGlow.widthProperty(), 0),
                        new KeyValue(playerGlow.heightProperty(), 0)
                ),
                new KeyFrame(Duration.millis(500),
                        new KeyValue(playerGlow.widthProperty(), 0),
                        new KeyValue(playerGlow.heightProperty(), 0)
                ),
                new KeyFrame(Duration.millis(2500),
                        new KeyValue(playerGlow.widthProperty(), 125),
                        new KeyValue(playerGlow.heightProperty(), 125)
                ),
                new KeyFrame(Duration.millis(4500),
                        new KeyValue(playerGlow.widthProperty(), 0),
                        new KeyValue(playerGlow.heightProperty(), 0)
                ),
                new KeyFrame(Duration.millis(5000),
                        new KeyValue(playerGlow.widthProperty(), 0),
                        new KeyValue(playerGlow.heightProperty(), 0)
                )
        );
        glow.setCycleCount(Animation.INDEFINITE);
        glow.play();

        playerDown.setEffect(playerGlow);

        // Effects for having the screen fade to white upon retrying
        fadeWhiteOut = new FadeTransition(Duration.millis(5000), whiteOut);
        fadeWhiteIn = new FadeTransition(Duration.millis(1000), whiteOut);
        retryPause = new PauseTransition(Duration.millis(250));
        fadeWhiteOut.setToValue(1.0);
        fadeWhiteIn.setToValue(0.0);

        fadeWhiteOut.setOnFinished(e -> {
            reloadLevel();
            retryPause.play();
        });
        retryPause.setOnFinished(e -> {
            AudioPlayer.playGameplayMusic();
            AudioPlayer.fadeMusicTo(AudioPlayer.getGameplayMusicPlayer(), ((AudioPlayer.getMusicVolume() / 100f) * (AudioPlayer.getMasterVolume() / 100f)), 1000);
            fadeWhiteIn.play();
        });
        fadeWhiteIn.setOnFinished(e -> {
            startLevelTimer();
            Gameplay.startRunTimer();
            Gameplay.isAttacking = false;
            pauseButton.setDisable(false);
            pauseButton.setOpacity(1.0);
            whiteOut.setVisible(false);
        });

        retryButton.setOnMouseClicked(e -> {
            whiteOut.setOpacity(0.0);
            whiteOut.setVisible(true);
            fadeWhiteOut.play();
            AudioPlayer.stopClockTick();
            AudioPlayer.playClockTickAccelerate();
            AudioPlayer.pauseGameplayMusic();
        });
    }

    /**
     * Reinitialize the current level without performing a full reload of the scene.
     * This is to prevent the cards and statistics from being updated, since they are updated when this scene is initialized.
     */
    private void reloadLevel() {
        playerDown.setVisible(false);
        darkenBackground.setVisible(false);
        retryPane.setVisible(false);
        retryMessage.setVisible(false);
        retryButton.setVisible(false);
        abandonRunButton.setVisible(false);
        showCardPaneAnimation(1500);
        timeRemaining = Gameplay.getBaseLevelTimer();
        enemy.setHealth(enemy.getMaxHealth());
        animateEnemyHealthBar(enemy.getMaxHealth());
        tookDamage = false;
        levelTimerLabel.setText("1:00");
        playerDown.setEffect(null);
        playerSprite.setImage(player.getIdle());

        TranslateTransition hideTimer = new TranslateTransition(Duration.millis(1), levelTimerLabel);
        hideTimer.setByY(statisticsPane.getHeight() * SceneTools.currentScaleY);
        hideTimer.setInterpolator(Interpolator.SPLINE(0.5, 0, 0, 1));
        hideTimer.play();
    }

    /**
     * Shows the victory screen when the player defeats the enemy by dealing exactly enough damage to them.
     */
    private void showVictory() {
        /*
        Increase score.
        If the level completed was a boss level, reward the player with double points and multiply the score multiplier by 1.5
        Otherwise, add points normally.
         */
        if (Gameplay.getLevelsCleared() % 5 == 0) {
            levelClearedText.setText("BOSS DEFEATED!");
            victoryPoints.setText("+" + (int) (Gameplay.BASE_SCORE_MODIFIER * Gameplay.getScoreMultiplier()) * 2 + " POINTS");
            Gameplay.setScore((int) (Gameplay.getScore() + (Gameplay.BASE_SCORE_MODIFIER * Gameplay.getScoreMultiplier()) * 2));
            Gameplay.setScoreMultiplier(Gameplay.getScoreMultiplier() * 1.5);
            Gameplay.setCardMultiplier(Gameplay.getCardMultiplier() + 1);
        } else {
            Gameplay.setScore((int) (Gameplay.getScore() + Gameplay.BASE_SCORE_MODIFIER * Gameplay.getScoreMultiplier()));
            victoryPoints.setText("+" + (int) (Gameplay.BASE_SCORE_MODIFIER * Gameplay.getScoreMultiplier()) + " POINTS");
        }

        // Set current score text
        currentScore.setText(String.valueOf(Gameplay.getScore()));
        // Increase levels cleared by 1
        Gameplay.setLevelsCleared(Gameplay.getLevelsCleared() + 1);
        AudioPlayer.fadeMusicTo(AudioPlayer.getGameplayMusicPlayer(), AudioPlayer.getGameplayMusicPlayer().getVolume() * 0.60, 250);

        darkenBackground.setOpacity(0.0);
        darkenBackground.setVisible(true);
        victoryScreen.setOpacity(0.0);
        victoryScreen.setVisible(true);
        continueButton.setOpacity(0.0);
        continueButton.setOnMouseClicked(e -> {
            continueButton.setDisable(true);
            SceneTools.fadeOutToScene(e, "gameplay.fxml", 1000, rootPane);
        });

        // Animation for fading in the victory screen
        Timeline fadeInVictory = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(darkenBackground.opacityProperty(), 0.0),
                        new KeyValue(victoryScreen.opacityProperty(), 0.0)
                ),
                new KeyFrame(Duration.millis(300),
                        new KeyValue(darkenBackground.opacityProperty(), 0.0),
                        new KeyValue(victoryScreen.opacityProperty(), 0.0)
                ),
                new KeyFrame(Duration.millis(600),
                        new KeyValue(darkenBackground.opacityProperty(), 1.0),
                        new KeyValue(victoryScreen.opacityProperty(), 1.0)
                ),
                new KeyFrame(Duration.millis(900),
                        new KeyValue(darkenBackground.opacityProperty(), 1.0),
                        new KeyValue(victoryScreen.opacityProperty(), 1.0),
                        new KeyValue(continueButton.opacityProperty(), 0.0)
                ),
                new KeyFrame(Duration.millis(1200),
                        new KeyValue(continueButton.opacityProperty(), 1.0),
                        new KeyValue(continueButton.visibleProperty(), true)
                )
        );
        fadeInVictory.play();
    }

    /**
     * Show the results screen
     */
    public void showResults() {
        player.setHealth(0);
        updatePlayerHearts();

        setHoverSelectionRetry(false, false);
        setHoverSelection(false, false, false, false);

        // Update relevant player stats
        if (Gameplay.getScore() > currentUser.getBestScore())
            currentUser.setBestScore(Gameplay.getScore());
        if (Gameplay.getTimePassed() > currentUser.getBestTime())
            currentUser.setBestTime(Gameplay.getTimePassed());
        if (Gameplay.getLevelsCleared() > currentUser.getMostLevelsCleared())
            currentUser.setMostLevelsCleared(Gameplay.getLevelsCleared());

        FadeTransition fadeBackground = new FadeTransition(Duration.millis(1500), darkenBackground);
        fadeBackground.setFromValue(1.0);
        fadeBackground.setToValue(0.0);
        fadeBackground.setOnFinished(e -> darkenBackground.setVisible(false));

        retryMessage.setVisible(false);
        retryButton.setVisible(false);
        abandonRunButton.setVisible(false);
        levelTimer.stop();
        Gameplay.pauseRunTimer();
        AudioPlayer.stopClockTick();

        // Make all UI elements fly off-screen except the player, player health, and enemy
        TranslateTransition hideStats = new TranslateTransition(Duration.millis(1500), statisticsPane);
        hideStats.setByY(0 - statisticsPane.getHeight() * SceneTools.currentScaleY);
        hideStats.setInterpolator(Interpolator.SPLINE(0.5, 0, 0, 1));

        TranslateTransition hideTimer = new TranslateTransition(Duration.millis(1500), levelTimerLabel);
        hideTimer.setByY(0 - statisticsPane.getHeight() * SceneTools.currentScaleY);
        hideTimer.setInterpolator(Interpolator.SPLINE(0.5, 0, 0, 1));

        TranslateTransition hidePauseButton = new TranslateTransition(Duration.millis(1500), pauseButton);
        hidePauseButton.setByY(0 - statisticsPane.getHeight() * SceneTools.currentScaleY);
        hidePauseButton.setInterpolator(Interpolator.SPLINE(0.5, 0, 0, 1));

        hideStats.play();
        hideTimer.play();
        hidePauseButton.play();

        // Set results screen text values
        resultScoreValue.setText(currentScore.getText());
        resultLevelsClearedValue.setText(String.valueOf(Gameplay.getLevelsCleared() - 1));
        resultTimeSpentValue.setText(runTimerValue.getText());
        resultBossesDefeatedValue.setText(String.valueOf(Gameplay.getLevelsCleared() / 5));

        resultsScreen.setOpacity(0.0);
        resultsScreen.setVisible(true);

        // Animate results screen elements fading in
        Timeline fadeInResults = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(resultsScreen.opacityProperty(), 0.0)),
                new KeyFrame(Duration.millis(2000), new KeyValue(resultsScreen.opacityProperty(), 1.0)),
                new KeyFrame(Duration.millis(2300), new KeyValue(resultsScreen.opacityProperty(), 1.0)),

                // Score
                new KeyFrame(Duration.millis(2595),
                        new KeyValue(resultScoreLabel.opacityProperty(), 0.0),
                        new KeyValue(resultScoreLabel.visibleProperty(), true),
                        new KeyValue(resultScoreValue.opacityProperty(), 0.0),
                        new KeyValue(resultScoreValue.visibleProperty(), true)
                ),
                new KeyFrame(Duration.millis(2800),
                        new KeyValue(resultScoreLabel.opacityProperty(), 1.0),
                        new KeyValue(resultScoreValue.opacityProperty(), 1.0)
                ),

                // Levels Cleared
                new KeyFrame(Duration.millis(3415),
                        new KeyValue(resultLevelsClearedLabel.opacityProperty(), 0.0),
                        new KeyValue(resultLevelsClearedLabel.visibleProperty(), true),
                        new KeyValue(resultLevelsClearedValue.opacityProperty(), 0.0),
                        new KeyValue(resultLevelsClearedValue.visibleProperty(), true)
                ),
                new KeyFrame(Duration.millis(3600),
                        new KeyValue(resultLevelsClearedLabel.opacityProperty(), 1.0),
                        new KeyValue(resultLevelsClearedValue.opacityProperty(), 1.0)
                ),

                // Time Spent
                new KeyFrame(Duration.millis(4235),
                        new KeyValue(resultTimeSpentLabel.opacityProperty(), 0.0),
                        new KeyValue(resultTimeSpentLabel.visibleProperty(), true),
                        new KeyValue(resultTimeSpentValue.opacityProperty(), 0.0),
                        new KeyValue(resultTimeSpentValue.visibleProperty(), true)
                ),
                new KeyFrame(Duration.millis(4400),
                        new KeyValue(resultTimeSpentLabel.opacityProperty(), 1.0),
                        new KeyValue(resultTimeSpentValue.opacityProperty(), 1.0)
                ),

                // Bosses Defeated
                new KeyFrame(Duration.millis(5050),
                        new KeyValue(resultBossesDefeatedLabel.opacityProperty(), 0.0),
                        new KeyValue(resultBossesDefeatedLabel.visibleProperty(), true),
                        new KeyValue(resultBossesDefeatedValue.opacityProperty(), 0.0),
                        new KeyValue(resultBossesDefeatedValue.visibleProperty(), true)
                ),
                new KeyFrame(Duration.millis(5200),
                        new KeyValue(resultBossesDefeatedLabel.opacityProperty(), 1.0),
                        new KeyValue(resultBossesDefeatedValue.opacityProperty(), 1.0)
                ),

                // Buttons
                new KeyFrame(Duration.millis(6000),
                        new KeyValue(resultButtons.opacityProperty(), 0.0),
                        new KeyValue(resultButtons.visibleProperty(), true)
                ),
                new KeyFrame(Duration.millis(7000),
                        new KeyValue(resultButtons.opacityProperty(), 1.0),
                        new KeyValue(resultButtons.opacityProperty(), 1.0)
                )
        );

        // Play death animation
        playerSprite.setImage(player.getDowned());
        AudioPlayer.playPlayerDeath();
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(1000));
        pauseTransition.setOnFinished(e -> {
            if (darkenBackground.isVisible()) {
                fadeBackground.play();
            }

            retryPane.setVisible(false);
            playerSprite.setImage(player.getDead());
            playerSprite.setRotate(-4);
            playerSprite.setLayoutY(123);
            AudioPlayer.stopGameplayMusic();
            AudioPlayer.playGameOverMusic();
            fadeInResults.play();
        });
        pauseTransition.play();
    }

    /**
     * Slightly enlarges the attack button when the mouse is hovering over it
     */
    public void hoverSelectAttack() {
        if (Gameplay.cardCount.get() == 2) {
            attackButton.setCursor(Cursor.HAND);
            pulsateGlow.stop();
            attackButtonSize.setFromX(attackButton.getScaleX());
            attackButtonSize.setFromY(attackButton.getScaleY());
            attackButtonSize.setToX(1.2);
            attackButtonSize.setToY(1.2);
            borderGlow.setWidth(0);
            borderGlow.setHeight(0);
            attackButtonSize.setOnFinished(null);
            attackButtonSize.play();
        }
    }

    /**
     * Resets the attack button size when the mouse stops hovering over it
     */
    public void hoverDeselectAttack() {
        if (Gameplay.cardCount.get() == 2) {
            attackButton.setCursor(null);
            attackButtonSize.setFromX(attackButton.getScaleX());
            attackButtonSize.setFromY(attackButton.getScaleY());
            attackButtonSize.setToX(1.0);
            attackButtonSize.setToY(1.0);
            attackButtonSize.setOnFinished(e -> pulsateGlow.play());
            attackButtonSize.play();
        }
    }

    /**
     * Drains the attack bar and begins the attack phase when the attack button is clicked
     */
    public void clickAttack() {
        if (Gameplay.cardCount.get() == 2) {
            hoverDeselectAttack();
            attackPhaseAnimations();
            Gameplay.isAttacking = true;
            Gameplay.selectedCards.getFirst().toggleSelect();
            Gameplay.selectedCards.getLast().toggleSelect();
            Gameplay.selectedCards.clear();
        }
    }

    /**
     * Called when the pause button is pressed. Pauses the game and opens the pause menu.
     */
    public void pauseGame() {
        Gameplay.isPaused = true;
        AudioPlayer.fadeMusicTo(AudioPlayer.getGameplayMusicPlayer(), AudioPlayer.getGameplayMusicPlayer().getVolume() * 0.3, 250);
        setHoverSelectionRetry(false, false);
        setHoverSelection(false, false, false, false);
        darkenBackground.setVisible(true);
        pauseMenu.setVisible(true);
        levelTimer.pause();
        Gameplay.pauseRunTimer();
        if (pulsateGlow.getStatus() == Animation.Status.RUNNING)
            pulsateGlow.pause();
        if (progressBar != null && progressBar.getStatus() == Animation.Status.RUNNING)
            progressBar.pause();
    }

    /**
     * Called when the resume button is pressed from the pause menu. Resumes the game.
     */
    public void resumeGame() {
        setHoverSelectionRetry(false, false);
        setHoverSelection(false, false, false, false);
        Gameplay.isPaused = false;
        AudioPlayer.fadeMusicTo(AudioPlayer.getGameplayMusicPlayer(), (AudioPlayer.getMusicVolume() / 100f) * (AudioPlayer.getMasterVolume() / 100f), 250);
        pauseMenu.setVisible(false);
        darkenBackground.setVisible(false);
        levelTimer.play();
        Gameplay.startRunTimer();
        if (pulsateGlow != null && progressBar != null) {
            if (pulsateGlow.getStatus() == Animation.Status.PAUSED)
                pulsateGlow.pause();
            if (progressBar.getStatus() == Animation.Status.PAUSED)
                progressBar.play();
        }
    }

    /**
     * Called when the restart game button is pressed. Restarts the run and initializes a new level.
     * @param e An event representing the button firing.
     */
    public void restartGame(InputEvent e) {
        Gameplay.stopRunTimer();
        Gameplay.setLevelsCleared(1);
        Gameplay.setScoreMultiplier(1);
        Gameplay.setCardMultiplier(1);
        Gameplay.setScore(0);
        Gameplay.setTimePassed("00:00:00");
        Gameplay.setTimerValue("00:00:00");
        deleteSaveGame();
        player.setHealth(3);
        AudioPlayer.fadeOutMusic(AudioPlayer.getGameplayMusicPlayer(), 500);
        SceneTools.fadeOutToScene(e, "gameplay.fxml", 500, rootPane);
    }

    /**
     * Called when the return to main menu button is pressed. Exits the run without saving and returns to the main menu.
     * @param e An event representing the button firing.
     */
    public void returnToMainMenu(InputEvent e) {
        Gameplay.stopRunTimer();
        if (player.getHealth() != 0) {
            setHasSavedGame(true);
            currentGameSave.setGameSave(player.getHealth(), Gameplay.getLevelsCleared(), Gameplay.getScore(), Gameplay.getTimerValue(), enemy.getMaxHealth(), cardsDataList);
            MainMenu.saveGame();
        }
        else {
            deleteSaveGame();
        }
        AudioPlayer.fadeOutMusic(AudioPlayer.getGameplayMusicPlayer(), 500);
        SceneTools.fadeOutToScene(e, "main-menu.fxml", 500, rootPane);
    }

    /**
     * Called when the exit button is pressed from the pause menu. Returns to the main menu.
     */
    public void exitGame() {
        if (player.getHealth() != 0) {
            setHasSavedGame(true);
            currentGameSave.setGameSave(player.getHealth(), Gameplay.getLevelsCleared(), Gameplay.getScore(), Gameplay.getTimerValue(), enemy.getMaxHealth(), cardsDataList);
            currentUser.setAsPlayer(false);
        }
        else {
            deleteSaveGame();
        }
        MainMenu.saveGame();
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000), rootPane);
        fadeTransition.setOnFinished(e -> SceneTools.exitApplication());
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        AudioPlayer.fadeOutMusic(AudioPlayer.getGameplayMusicPlayer(), 1000);
        fadeTransition.play();
    }

    /**
     * Enlarges buttons when hovering over them
     * @param e the mouse event triggering this
     */
    public void hoverEnterButton(InputEvent e) {
        setHoverSelection(false, false, false, false);
        Label menuButton = (Label) e.getSource();
        menuButton.setTextFill(Color.color(200f/255f, 200f/255f, 200f/255f));
        menuButton.setScaleX(menuButton.getScaleX() + 0.1);
        menuButton.setScaleY(menuButton.getScaleY() + 0.1);

        switch (menuButton.getText()) {
            case "RETRY" -> setHoverSelectionRetry(true, false);
            case "ABANDON RUN" -> setHoverSelectionRetry(false, true);
            case "RESUME" -> setHoverSelection(true, false, false, false);
            case "RESTART" -> setHoverSelection(false, true, false, false);
            case "RETURN TO MAIN MENU" -> setHoverSelection(false, false, true, false);
            case "EXIT GAME" -> setHoverSelection(false, false, false, true);
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
        menuButton.setTextFill(Color.color(88f/255f, 88f/255f, 88f/255f));
        menuButton.setScaleX(menuButton.getScaleX() - 0.1);
        menuButton.setScaleY(menuButton.getScaleY() - 0.1);
        setHoverSelection(false, false, false, false);
    }

    /**
     * Enlarges the pause button when hovering over them
     * @param e the mouse event triggering this
     */
    public void hoverEnterImageButton(InputEvent e) {
        changeContinueButtonSelected(false);
        ImageView imageButton = (ImageView) e.getSource();
        imageButton.setScaleX(imageButton.getScaleX() + 0.1);
        imageButton.setScaleY(imageButton.getScaleY() + 0.1);
        changeContinueButtonSelected(true);
    }

    /**
     * Shrinks the pause button when you stop hovering them
     * @param e the mouse event triggering this
     */
    public void hoverExitImageButton(InputEvent e) {
        ImageView imageButton = (ImageView) e.getSource();
        imageButton.setScaleX(imageButton.getScaleX() - 0.1);
        imageButton.setScaleY(imageButton.getScaleY() - 0.1);
        changeContinueButtonSelected(false);
    }

    /**
     * Set the selection of the retry screen buttons
     * @param retryValue new value for retry button
     * @param abandonRunValue new value abandon run button
     */
    private void setHoverSelectionRetry(boolean retryValue, boolean abandonRunValue) {
        changeRetryButtonSelected(retryValue);
        changeAbandonRunButtonSelected(abandonRunValue);
    }

    /**
     * Set the selection of the pause screen buttons
     * @param resumeValue new value for resume button
     * @param restartValue new value for restart button
     * @param mainMenuValue new value for main menu button
     * @param exitValue new value for exit button
     */
    private void setHoverSelection(boolean resumeValue, boolean restartValue, boolean mainMenuValue, boolean exitValue) {
        changeResumeButtonSelected(resumeValue);
        changePauseRestartButtonSelected(restartValue);
        changeResultRestartButtonSelected(restartValue);
        changePauseMainMenuButtonSelected(mainMenuValue);
        changeResultMainMenuButtonSelected(mainMenuValue);
        changePauseExitButtonSelected(exitValue);
        changeResultExitButtonSelected(exitValue);
    }

    /**
     * Change whether the retry button is selected
     * @param value new true/false value for whether the button is selected
     */
    private void changeRetryButtonSelected(boolean value) {
        if (retryButtonSelected == value)
            return;
        retryButtonSelected = value;
        if (retryButtonSelected) {
            retryButton.setScaleX(retryButton.getScaleX() + 0.1);
            retryButton.setScaleY(retryButton.getScaleY() + 0.1);
        } else {
            retryButton.setScaleX(retryButton.getScaleX() - 0.1);
            retryButton.setScaleY(retryButton.getScaleY() - 0.1);
        }
    }

    /**
     * Change whether the abandon run button is selected
     * @param value new true/false value for whether the button is selected
     */
    private void changeAbandonRunButtonSelected(boolean value) {
        if (abandonRunButtonSelected == value)
            return;
        abandonRunButtonSelected = value;
        if (abandonRunButtonSelected) {
            abandonRunButton.setScaleX(abandonRunButton.getScaleX() + 0.1);
            abandonRunButton.setScaleY(abandonRunButton.getScaleY() + 0.1);
        } else {
            abandonRunButton.setScaleX(abandonRunButton.getScaleX() - 0.1);
            abandonRunButton.setScaleY(abandonRunButton.getScaleY() - 0.1);
        }
    }

    /**
     * Change whether the resume button is selected
     * @param value new true/false value for whether the button is selected
     */
    private void changeResumeButtonSelected(boolean value) {
        if (resumeButtonSelected == value)
            return;
        resumeButtonSelected = value;
        if (resumeButtonSelected) {
            resumeButton.setScaleX(resumeButton.getScaleX() + 0.1);
            resumeButton.setScaleY(resumeButton.getScaleY() + 0.1);
            resumeButton.setTextFill(Color.color(200f/255f, 200f/255f, 200f/255f));
        } else {
            resumeButton.setScaleX(resumeButton.getScaleX() - 0.1);
            resumeButton.setScaleY(resumeButton.getScaleY() - 0.1);
            resumeButton.setTextFill(Color.color(88f/255f, 88f/255f, 88f/255f));
        }
    }

    /**
     * Change whether the restart button is selected on the pause menu
     * @param value new true/false value for whether the button is selected
     */
    private void changePauseRestartButtonSelected(boolean value) {
        if (pauseRestartButtonSelected == value)
            return;
        pauseRestartButtonSelected = value;
        if (pauseRestartButtonSelected) {
            pauseRestartButton.setScaleX(pauseRestartButton.getScaleX() + 0.1);
            pauseRestartButton.setScaleY(pauseRestartButton.getScaleY() + 0.1);
            pauseRestartButton.setTextFill(Color.color(200f/255f, 200f/255f, 200f/255f));
        } else {
            pauseRestartButton.setScaleX(pauseRestartButton.getScaleX() - 0.1);
            pauseRestartButton.setScaleY(pauseRestartButton.getScaleY() - 0.1);
            pauseRestartButton.setTextFill(Color.color(88f/255f, 88f/255f, 88f/255f));
        }
    }

    /**
     * Change whether the return to main menu button is selected on the pause menu
     * @param value new true/false value for whether the button is selected
     */
    private void changePauseMainMenuButtonSelected(boolean value) {
        if (pauseMainMenuButtonSelected == value)
            return;
        pauseMainMenuButtonSelected = value;
        if (pauseMainMenuButtonSelected) {
            pauseMainMenuButton.setScaleX(pauseMainMenuButton.getScaleX() + 0.1);
            pauseMainMenuButton.setScaleY(pauseMainMenuButton.getScaleY() + 0.1);
            pauseMainMenuButton.setTextFill(Color.color(200f/255f, 200f/255f, 200f/255f));
        } else {
            pauseMainMenuButton.setScaleX(pauseMainMenuButton.getScaleX() - 0.1);
            pauseMainMenuButton.setScaleY(pauseMainMenuButton.getScaleY() - 0.1);
            pauseMainMenuButton.setTextFill(Color.color(88f/255f, 88f/255f, 88f/255f));
        }
    }

    /**
     * Change whether the exit button is selected on the pause menu
     * @param value new true/false value for whether the button is selected
     */
    private void changePauseExitButtonSelected(boolean value) {
        if (pauseExitButtonSelected == value)
            return;
        pauseExitButtonSelected = value;
        if (pauseExitButtonSelected) {
            pauseExitButton.setScaleX(pauseExitButton.getScaleX() + 0.1);
            pauseExitButton.setScaleY(pauseExitButton.getScaleY() + 0.1);
            pauseExitButton.setTextFill(Color.color(200f/255f, 200f/255f, 200f/255f));
        } else {
            pauseExitButton.setScaleX(pauseExitButton.getScaleX() - 0.1);
            pauseExitButton.setScaleY(pauseExitButton.getScaleY() - 0.1);
            pauseExitButton.setTextFill(Color.color(88f/255f, 88f/255f, 88f/255f));
        }
    }

    /**
     * Change whether the restart button is selected on the results screen
     * @param value new true/false value for whether the button is selected
     */
    private void changeResultRestartButtonSelected(boolean value) {
        if (resultRestartButtonSelected == value)
            return;
        resultRestartButtonSelected = value;
        if (resultRestartButtonSelected) {
            resultRestartButton.setScaleX(resultRestartButton.getScaleX() + 0.1);
            resultRestartButton.setScaleY(resultRestartButton.getScaleY() + 0.1);
            resultRestartButton.setTextFill(Color.color(200f/255f, 200f/255f, 200f/255f));
        } else {
            resultRestartButton.setScaleX(resultRestartButton.getScaleX() - 0.1);
            resultRestartButton.setScaleY(resultRestartButton.getScaleY() - 0.1);
            resultRestartButton.setTextFill(Color.color(88f/255f, 88f/255f, 88f/255f));
        }
    }

    /**
     * Change whether the return to main menu button is selected on the results screen
     * @param value new true/false value for whether the button is selected
     */
    private void changeResultMainMenuButtonSelected(boolean value) {
        if (resultMainMenuButtonSelected == value)
            return;
        resultMainMenuButtonSelected = value;
        if (resultMainMenuButtonSelected) {
            resultMainMenuButton.setScaleX(resultMainMenuButton.getScaleX() + 0.1);
            resultMainMenuButton.setScaleY(resultMainMenuButton.getScaleY() + 0.1);
            resultMainMenuButton.setTextFill(Color.color(200f/255f, 200f/255f, 200f/255f));
        } else {
            resultMainMenuButton.setScaleX(resultMainMenuButton.getScaleX() - 0.1);
            resultMainMenuButton.setScaleY(resultMainMenuButton.getScaleY() - 0.1);
            resultMainMenuButton.setTextFill(Color.color(88f/255f, 88f/255f, 88f/255f));
        }
    }

    /**
     * Change whether the exit button is selected on the results screen
     * @param value new true/false value for whether the button is selected
     */
    private void changeResultExitButtonSelected(boolean value) {
        if (resultExitButtonSelected == value)
            return;
        resultExitButtonSelected = value;
        if (resultExitButtonSelected) {
            resultExitButton.setScaleX(resultExitButton.getScaleX() + 0.1);
            resultExitButton.setScaleY(resultExitButton.getScaleY() + 0.1);
            resultExitButton.setTextFill(Color.color(200f/255f, 200f/255f, 200f/255f));
        } else {
            resultExitButton.setScaleX(resultExitButton.getScaleX() - 0.1);
            resultExitButton.setScaleY(resultExitButton.getScaleY() - 0.1);
            resultExitButton.setTextFill(Color.color(88f/255f, 88f/255f, 88f/255f));
        }
    }

    /**
     * Change whether the continue button is selected on the victory screen
     * @param value new true/false value for whether the button is selected
     */
    private void changeContinueButtonSelected(boolean value) {
        if (continueButtonSelected == value)
            return;
        continueButtonSelected = value;
        if (continueButtonSelected) {
            continueButton.setScaleX(continueButton.getScaleX() + 0.1);
            continueButton.setScaleY(continueButton.getScaleY() + 0.1);
        } else {
            continueButton.setScaleX(continueButton.getScaleX() - 0.1);
            continueButton.setScaleY(continueButton.getScaleY() - 0.1);
        }
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
            SceneTools.switchScene(e, "gameplay.fxml");
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
