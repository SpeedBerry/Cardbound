package com.cs2212.cardbound.gameplay;

/**
 * Container class to store user based information
 *
 * <p>
 * This class stores information about a user, including their username, password, game statistics,
 * and settings.
 * </p>
 * <p>
 * A user can have various attributes such as their lifetime games, best score, best time, etc.
 * </p>
 *
 * <p>
 * Users are uniquely identified by their userID, which serves as the primary key.
 * </p>
 *
 * <p>
 * This class also provides methods to access and modify user attributes.
 * </p>
 *
 * <p>
 * @author Pranav Arora
 * </p>
 */
public class User {

    /**
     * userID - primary key to differentiate each user
     * username - label chosen by user; in-game name (variable character)
     * lifetimeGames - tracks historical number of games (Number)
     * bestScore - best historical game score recorded (Number)
     * bestTime - best historical time taken by player (DateTime/Number)
     * totalLevelsCleared - historical number of levels cleared by player (Number)
     * totalPlaytime - tracks the amount of time player has played in total (DateTime/Number)
     * ranking - tracks relative ranking against other players based on game stats (NUMBER)
     */

    /**
     * Unique identifier (primary key) for each user (NUMBER)
     */
    int userID;

    /**
     * The username chosen by the user. (VARCHAR)
     */
    String username;

    /**
     * The password chosen by the user. (VARCHAR)
     */
    String password;   //FIXME: ensure all char can be passed including "\", special and esc chars

    /**
     * tracks historical number of games (NUMBER)
     */
    int lifetimeGames;

    /**
     * best historical game score recorded (NUMBER)
     */
    int bestScore;

    /**
     * best historical time taken by player (NUMBER)
     */
    int bestTime;

    /**
     * historical most number of levels cleared by player (NUMBER)
     */
    int mostLevelsCleared;

    /**
     * historical total number of levels cleared by player (NUMBER)
     */
    int totalPlaytime;

    /**
     * best historical time taken by player (VARCHAR)
     */
    String bestTimeString;

    /**
     * tracks the amount of time player has played in total (STRING)
     */
    String totalPlaytimeString;

    /**
     * Integer property bidirectionally bound to master volume level (NUMBER)
     */
    int masterVolume;

    /**
     * Integer property bidirectionally bound to music volume level (NUMBER)
     */
    int musicVolume;

    /**
     * Integer property bidirectionally bound to sfx volume level (NUMBER)
     */
    int sfxVolume;

    /**
     * Boolean property bidirectionally bound to level timer toggle (BOOL)
     */
    boolean disableLevelTimer;

    /**
     * Boolean property bidirectionally bound to run timer toggle (BOOL)
     */
    boolean disableRunTimer;

    /**
     * Boolean property bidirectionally bound to unlimited health toggle (BOOL)
     */
    boolean unlHealth;

    /**
     * Flag to indicate whether the user is the current active user.
     */
    private boolean currentUser;

    /**
     * Constructs a new User object with the given username, password, and ID.
     *
     * @param username The username chosen by the user.
     * @param password The password chosen by the user.
     * @param ID       The unique identifier for the user.
     */
    public User(String username, String password, int ID) {
        this.userID = ID;
        this.username = username;
        this.password = password;

        // Assigning default values
        this.lifetimeGames = 0;
        this.bestScore = 0;
        this.bestTime = 0;
        this.mostLevelsCleared = 0;
        this.totalPlaytime = 0;
        this.masterVolume = 75;
        this.musicVolume = 75;
        this.sfxVolume = 75;
        this.disableRunTimer = false;
        this.disableLevelTimer = false;
        this.unlHealth = false;
        this.currentUser = false;

        // Formatting default time strings
        bestTimeString = formatTime(0);
        totalPlaytimeString = formatTime(0);
    }

    /**
     * Instantiates a pre-existing User object with the given parameters.
     *
     * @param userID            The unique identifier for the user.
     * @param username          The username chosen by the user.
     * @param password          The password chosen by the user.
     * @param lifetimeGames     The total number of games played by the user.
     * @param bestScore         The best score achieved by the user.
     * @param bestTime          The best time taken by the user to complete a game.
     * @param totalLevelsCleared The total number of levels cleared by the user.
     * @param totalPlaytime     The total playtime of the user.
     * @param masterVolume      The master volume setting of the user.
     * @param musicVolume       The music volume setting of the user.
     * @param sfxVolume         The sound effects volume setting of the user.
     * @param levelTimer        Flag indicating whether the level timer is disabled for the user.
     * @param runTimer          Flag indicating whether the run timer is disabled for the user.
     * @param unlHealth         Flag indicating whether the user has unlimited health.
     */
    public User(int userID, String username, String password, int lifetimeGames, int bestScore, int bestTime, int totalLevelsCleared, int totalPlaytime, int masterVolume, int musicVolume, int sfxVolume, boolean levelTimer, boolean runTimer, boolean unlHealth) {
        // Assigning provided values
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.lifetimeGames = lifetimeGames;
        this.bestScore = bestScore;
        this.bestTime = bestTime;
        this.mostLevelsCleared = totalLevelsCleared;
        this.totalPlaytime = totalPlaytime;
        this.masterVolume = masterVolume;
        this.musicVolume = musicVolume;
        this.sfxVolume = sfxVolume;
        this.disableLevelTimer = levelTimer;
        this.disableRunTimer = runTimer;
        this.unlHealth = unlHealth;
        this.currentUser = false;

        // Formatting time strings
        bestTimeString = formatTime(bestTime);
        totalPlaytimeString = formatTime(totalPlaytime);
    }

    /**
     * Retrieves the total number of games played by the user.
     *
     * @return The total number of games played.
     */
    public int getLifetimeGames() {
        return this.lifetimeGames;
    }

    /**
     * Retrieves the username of the user.
     *
     * @return The username.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Retrieves the password of the user.
     *
     * @return The password.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Retrieves the best score achieved by the user.
     *
     * @return The best score.
     */
    public int getBestScore() {
        return this.bestScore;
    }

    /**
     * Retrieves the best time taken by the user to complete a game.
     *
     * @return The best time in milliseconds.
     */
    public int getBestTime() {
        return this.bestTime;
    }

    /**
     * Retrieves the total number of levels cleared by the user.
     *
     * @return The total number of levels cleared.
     */
    public int getMostLevelsCleared() {
        return this.mostLevelsCleared;
    }

    /**
     * Retrieves the unique identifier of the user.
     *
     * @return The unique identifier.
     */
    public int getUserID() {
        return this.userID;
    }

    /**
     * Retrieves the total playtime of the user.
     *
     * @return The total playtime in milliseconds.
     */
    public int getTotalPlaytime() {
        return this.totalPlaytime;
    }

    /**
     * Sets the total number of games played by the user.
     *
     * @param lifetimeGames The total number of games played.
     */
    public void setLifetimeGames(int lifetimeGames) {
        this.lifetimeGames = lifetimeGames;
    }

    /**
     * Sets the best score achieved by the user.
     *
     * @param bestScore The best score.
     */
    public void setBestScore(int bestScore) {
        this.bestScore = bestScore;
    }

    /**
     * Sets the best time taken by the user to complete a game and updates the corresponding time string.
     *
     * @param bestTime The best time in milliseconds.
     */
    public void setBestTime(int bestTime) {
        this.bestTime = bestTime;
        setBestTimeString(bestTime);
    }

    /**
     * Sets the total number of levels cleared by the user.
     *
     * @param mostLevelsCleared The total number of levels cleared.
     */
    public void setMostLevelsCleared(int mostLevelsCleared) {
        this.mostLevelsCleared = mostLevelsCleared;
    }

    /**
     * Sets the total playtime of the user and updates the corresponding time string.
     *
     * @param totalPlaytime The total playtime in milliseconds.
     */
    public void setTotalPlaytime(int totalPlaytime) {
        this.totalPlaytime = totalPlaytime;
        setTotalPlaytimeString(totalPlaytime);
    }

    /**
     * Sets the user as a player or not.
     *
     * @param state The state indicating whether the user is a player.
     */
    public void setAsPlayer(boolean state) {
        this.currentUser = state;
    }

    /**
     * Retrieves the formatted string representation of the best time taken by the user.
     *
     * @return The formatted best time string.
     */
    public String getBestTimeString() {
        return formatTime(bestTime);
    }

    /**
     * Retrieves the formatted string representation of the total playtime of the user.
     *
     * @return The formatted total playtime string.
     */
    public String getTotalPlaytimeString() {
        return formatTime(totalPlaytime);
    }

    /**
     * Sets the formatted string representation of the best time taken by the user.
     *
     * @param bestTime The best time in milliseconds.
     */
    public void setBestTimeString(int bestTime) {
        bestTimeString = formatTime(bestTime);
    }

    /**
     * Sets the formatted string representation of the total playtime of the user.
     *
     * @param totalPlaytime The total playtime in milliseconds.
     */
    public void setTotalPlaytimeString(int totalPlaytime) {
        totalPlaytimeString = formatTime(totalPlaytime);
    }

    /**
     * Sets the flag indicating whether the level timer is disabled for the user.
     *
     * @param disableLevelTimer The flag indicating whether the level timer is disabled.
     */
    public void setDisableLevelTimer(boolean disableLevelTimer) {
        this.disableLevelTimer = disableLevelTimer;
    }

    /**
     * Sets the flag indicating whether the run timer is disabled for the user.
     *
     * @param disableRunTimer The flag indicating whether the run timer is disabled.
     */
    public void setDisableRunTimer(boolean disableRunTimer) {
        this.disableRunTimer = disableRunTimer;
    }

    /**
     * Sets the flag indicating whether the user has unlimited health.
     *
     * @param unlHealth The flag indicating whether the user has unlimited health.
     */
    public void setUnlHealth(boolean unlHealth) {
        this.unlHealth = unlHealth;
    }

    /**
     * Formats the given time in milliseconds into a string representation of hours, minutes, and seconds.
     *
     * @param time The time in milliseconds to format.
     * @return A formatted string representation of the time in the format "HH:MM:SS".
     */
    private String formatTime(int time) {
        // Extracting hours, minutes, and seconds from the given time
        int hours = time / 10000;
        int minutes = ((time - hours * 10000) / 100);
        int seconds = (time - hours * 10000 - minutes * 100);

        // Adjusting minutes and seconds if they exceed their respective limits
        if (seconds > 59) {
            minutes++;
            seconds -= 60;
        }
        if (minutes > 59) {
            hours++;
            minutes -= 60;
        }

        // Formatting hours, minutes, and seconds as two-digit strings
        String hoursString = String.format("%02d", hours);
        String minutesString = String.format("%02d", minutes);
        String secondsString = String.format("%02d", seconds);

        // Combining formatted strings to form the time representation
        return hoursString + ":" + minutesString + ":" + secondsString;
    }

    /**
     * Retrieves the flag indicating whether the level timer is disabled for the user.
     *
     * @return True if the level timer is disabled, false otherwise.
     */
    public boolean getDisableLevelTimer() {
        return this.disableLevelTimer;
    }

    /**
     * Retrieves the flag indicating whether the run timer is disabled for the user.
     *
     * @return True if the run timer is disabled, false otherwise.
     */
    public boolean getDisableRunTimer() {
        return this.disableRunTimer;
    }

    /**
     * Retrieves the flag indicating whether the user has unlimited health.
     *
     * @return True if the user has unlimited health, false otherwise.
     */
    public boolean getUnlHealth() {
        return this.unlHealth;
    }

    /**
     * Retrieves the master volume setting of the user.
     *
     * @return The master volume setting.
     */
    public int getMasterVolume() {
        return this.masterVolume;
    }

    /**
     * Retrieves the music volume setting of the user.
     *
     * @return The music volume setting.
     */
    public int getMusicVolume() {
        return this.musicVolume;
    }

    /**
     * Retrieves the sound effects volume setting of the user.
     *
     * @return The sound effects volume setting.
     */
    public int getSfxVolume() {
        return this.sfxVolume;
    }

    /**
     * Sets the master volume setting of the user.
     *
     * @param vol The master volume setting to set.
     */
    public void setMasterVolume(int vol) {
        this.masterVolume = vol;
    }

    /**
     * Sets the music volume setting of the user.
     *
     * @param vol The music volume setting to set.
     */
    public void setMusicVolume(int vol) {
        this.musicVolume = vol;
    }

    /**
     * Sets the sound effects volume setting of the user.
     *
     * @param vol The sound effects volume setting to set.
     */
    public void setSfxVolume(int vol) {
        this.sfxVolume = vol;
    }
}
