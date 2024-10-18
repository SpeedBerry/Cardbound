package com.cs2212.cardbound.gameplay;

/**
 * Container class to store game save information for a specific user.
 * <br><br>
 * The GameSave class is instantiated/constructed with a corresponding User ID,
 * which is the primary key used to differentiate each user.
 * @See {@link User}
 *
 * @author Pranav Arora
 */
public class GameSave {

    /**
     * The unique identifier of the user. GameSave is mapped to the specific user,
     * similar to a relational database.
     */
    int userId;
    /**
     * The number of lives the user has.
     */
    int numLives;
    /**
     * The level the user is currently on.
     */
    int level;
    /**
     * The score of the user.
     */
    int score;
    /**
     * The run timer of the game.
     */
    String runTimer;
    /**
     * The hit points (health) of the enemy.
     */
    int enemyHp;
    /**
     * The list of cards in the current level.
     * <br>
     * @see Card
     */
    String[][] cardsList;


    /**
     * Constructs a GameSave object with default values for fields.
     * @param ID The unique identifier of the user.
     */
    public GameSave(int ID) {
        this.userId = ID;
        this.numLives = 3;
        this.level = 0;
        this.score = 0;
        this.runTimer = "";
    }

    /**
     * Constructs a GameSave object with specified values from the User's previously saved game.
     * @param ID The unique identifier of the user.
     * @param numLives The number of lives the user has.
     * @param level The level the user is currently on.
     * @param score The score of the user.
     * @param runTimer The run timer of the game.
     * @param enemyHp The hit points of the enemy.
     * @param cardsList The list of cards in the game.
     */
    public GameSave(int ID, int numLives, int level, int score, String runTimer, int enemyHp, String[][] cardsList) {
        //FIXME: SAFE DELETE??
        this.userId = ID;
        this.numLives = numLives;
        this.level = level;
        this.score = score;
        this.runTimer = runTimer;
        this.enemyHp = enemyHp;
        this.cardsList = cardsList;
    }

    /**
     * Updates the values of the user's game save data.
     * @param numLives The number of lives the user has.
     * @param level The level the user is currently on.
     * @param score The score of the user.
     * @param runTimer The run timer of the game.
     * @param enemyHp The hit points of the enemy.
     * @param cardsList The list of cards in the game.
     */
    public void setGameSave(int numLives, int level, int score, String runTimer, int enemyHp, String[][] cardsList) {
        this.numLives = numLives;
        this.level = level;
        this.score = score;
        this.runTimer = runTimer;
        this.enemyHp = enemyHp;
        this.cardsList = cardsList;
    }

    /**
     * Returns the user's unique identifier.
     * @return user's id
     */
    public int getUserId() {
        return this.userId;
    }

    /**
     * Returns the number of lives the user has.
     * @return The number of lives.
     */
    public int getNumLives() {
        return numLives;
    }

    /**
     * Returns the level the user is currently on.
     * @return The level.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Returns the score of the user.
     * @return The score.
     */
    public int getScore() {
        return score;
    }

    /**
     * Returns the run timer of the game.
     * @return The run timer.
     */
    public String getRunTimer() {
        return runTimer;
    }

    /**
     * Returns the hit points of the enemy.
     * @return The enemy's hit points.
     */
    public int getEnemyHp() {
        return enemyHp;
    }

    /**
     * Returns the list of cards in the game.
     * @return The list of cards.
     */
    public String[][] getCardsList() {
        return cardsList;
    }

}
