package com.cs2212.cardbound.system;

import com.cs2212.cardbound.gameplay.Card;
import com.cs2212.cardbound.gameplay.Player;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.robot.Robot;
import javafx.util.Duration;

import java.util.ArrayList;

public class Gameplay {

    /**
     * The current in-game player that holds health and all player audio
     */
    public static Player player = new Player(3);
    /**
     * A list of the currently selected cards
     */
    public static ArrayList<Card> selectedCards = new ArrayList<>();
    /**
     * A property representing the number of selected cards
     */
    public static IntegerProperty cardCount = new SimpleIntegerProperty(0);
    /**
     * Whether the game is currently paused
     */
    public static boolean isPaused = false;
    /**
     * Whether the game is currently in the attack phase
     */
    public static boolean isAttacking = false;
    /**
     * Whether the level timer is active
     */
    public static boolean levelTimerDisabled = false;
    /**
     * Whether the run timer is active
     */
    public static boolean runTimerDisabled = false;
    /**
     * Whether the unlimited health mode is active
     */
    public static boolean unlimitedHealthToggled = false;
    /**
     * The base level timer value
     */
    private static final int baseLevelTimer = 60;
    /**
     * The timer for the run timer
     */
    private static final PauseTransition runTimer = new PauseTransition(Duration.millis(1000));
    /**
     * The amount of time passed since the run started as an integer
     */
    private static int timePassed = 0;
    /**
     * The amount of time passed as a string
     */
    private static final StringProperty timerValue = new SimpleStringProperty();
    /**
     * The current score
     */
    private static int score = 0;
    /**
     * The base score modifier that is added to the player's score value
     */
    public static final int BASE_SCORE_MODIFIER = 100;
    /**
     * The score multiplier that increases as the player progresses
     */
    private static double scoreMultiplier = 1;
    /**
     * The multiplier for the attack damage numbers on the cards
     */
    private static int cardMultiplier = 1;
    /**
     * The current level the player is on
     */
    private static int levelsCleared = 1;
    /**
     * A robot that gives the current location of the mouse on the screen
     */
    public static Robot mouseRobot = new Robot();

    /**
     * Starts the run timer
     */
    public static void startRunTimer() {
        runTimer.setOnFinished(e -> {
            updateRunTimer();
            runTimer.play();
        });
        runTimer.play();
    }

    /**
     * Pauses the run timer
     */
    public static void pauseRunTimer() {
        runTimer.setOnFinished(null);
        runTimer.pause();
    }

    /**
     * Stops the run timer
     */
    public static void stopRunTimer() {
        if (runTimer.getStatus() != Animation.Status.STOPPED) {
            runTimer.stop();
            timePassed = 0;
        }
    }

    /**
     * Updates the run timer every second
     */
    private static void updateRunTimer() {
        timePassed++;
        int hours = timePassed / 10000;
        int minutes = ((timePassed - hours * 10000) / 100);
        int seconds = (timePassed - hours * 10000 - minutes * 100);

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

        timerValue.set(hoursString + ":" + minutesString + ":" + secondsString);
    }

    /**
     * Gets the current run timer value as a string
     * @return the current run timer value as a string
     */
    public static String getTimerValue() {
        return timerValue.get();
    }

    /**
     * The current run timer value property
     * @return the run timer value property
     */
    public static StringProperty timerValueProperty() {
        return timerValue;
    }

    /**
     * Sets the run timer value
     * @param timerValue the new run timer value as a string
     */
    public static void setTimerValue(String timerValue) {
        Gameplay.timerValue.set(timerValue);
    }

    /**
     * Gets the current run's score
     * @return the current run's score
     */
    public static int getScore() {
        return score;
    }

    /**
     * Sets the current run's score
     * @param score the new given score
     */
    public static void setScore(int score) {
        Gameplay.score = score;
    }

    /**
     * Gets the number of levels cleared during the current run
     * @return the number of levels cleared during the current run
     */
    public static int getLevelsCleared() {
        return levelsCleared;
    }

    /**
     * Sets the number of levels cleared during the current run
     * @param levelsCleared the new given number of levels cleared
     */
    public static void setLevelsCleared(int levelsCleared) {
        Gameplay.levelsCleared = levelsCleared;
    }

    /**
     * Gets the base level timer
     * @return the base level timer
     */
    public static int getBaseLevelTimer() {
        return baseLevelTimer;
    }

    /**
     * Gets the amount of time passed since the run started
     * @return the amount of time passed since the run started
     */
    public static int getTimePassed() {
        return timePassed;
    }

    /**
     * Sets the amount of time passed since the run started
     * @param runTimer the new amount of time passed as a string
     */
    public static void setTimePassed(String runTimer) {
        String[] values = runTimer.split(":");
        int hours = Integer.parseInt(values[0]) * 10000;
        int minutes = Integer.parseInt(values[1]) * 100;
        int seconds = Integer.parseInt(values[2]);

        timePassed = hours + minutes + seconds;
    }

    /**
     * Gets the current run's score multiplier
     * @return the current run's score multiplier
     */
    public static double getScoreMultiplier() {
        return scoreMultiplier;
    }

    /**
     * Sets the current run's score multiplier
     * @param scoreMultiplier the new given score multiplier
     */
    public static void setScoreMultiplier(double scoreMultiplier) {
        Gameplay.scoreMultiplier = scoreMultiplier;
    }

    /**
     * Gets the current run's card multiplier
     * @return Gets the current run's card multiplier
     */
    public static int getCardMultiplier() {
        return cardMultiplier;
    }

    /**
     * Sets the current run's card multiplier
     * @param cardMultiplier the new given card multiplier
     */
    public static void setCardMultiplier(int cardMultiplier) {
        Gameplay.cardMultiplier = cardMultiplier;
    }
}
