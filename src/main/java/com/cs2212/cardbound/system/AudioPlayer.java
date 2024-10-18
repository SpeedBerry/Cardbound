package com.cs2212.cardbound.system;

import com.cs2212.cardbound.MainStage;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.Objects;

public class AudioPlayer {

    /**
     * Current master volume value
     */
    private static final IntegerProperty masterVolume = new SimpleIntegerProperty(75);
    /**
     * Current music volume value
     */
    private static final IntegerProperty musicVolume = new SimpleIntegerProperty(75);
    /**
     * Current SFX volume
     */
    private static final IntegerProperty sfxVolume = new SimpleIntegerProperty(75);

    /*
    The rest of the variables beyond this point are all audio assets.
     */
    private static final AudioClip cardClick = new AudioClip(Objects.requireNonNull(MainStage.class.getResource("audio/sfx/interface/card_click.wav")).toExternalForm());

    private static final AudioClip clockTick = new AudioClip(Objects.requireNonNull(MainStage.class.getResource("audio/sfx/interface/clock_tick.wav")).toExternalForm());

    private static final AudioClip playerDeath = new AudioClip(Objects.requireNonNull(MainStage.class.getResource("audio/sfx/player/death/player_death.wav")).toExternalForm());

    private static final AudioClip enemyAttack = new AudioClip(Objects.requireNonNull(MainStage.class.getResource("audio/sfx/enemy/attack/enemy_attack.wav")).toExternalForm());

    private static final AudioClip enemyDeath = new AudioClip(Objects.requireNonNull(MainStage.class.getResource("audio/sfx/enemy/death/enemy_death.wav")).toExternalForm());

    private static final AudioClip bossAttack = new AudioClip(Objects.requireNonNull(MainStage.class.getResource("audio/sfx/boss/attack/boss_attack.wav")).toExternalForm());

    private static final AudioClip bossDeath = new AudioClip(Objects.requireNonNull(MainStage.class.getResource("audio/sfx/boss/death/boss_death.wav")).toExternalForm());

    private static final Media clockTickAccelerate = new Media(Objects.requireNonNull(MainStage.class.getResource("audio/sfx/interface/clock_tick_accelerate.wav")).toExternalForm());

    private static final MediaPlayer clockTickAcceleratePlayer = new MediaPlayer(clockTickAccelerate);

    private static final Media gameplayMusic = new Media(Objects.requireNonNull(MainStage.class.getResource("audio/music/gameplay_music.wav")).toExternalForm());

    private static final MediaPlayer gameplayMusicPlayer = new MediaPlayer(gameplayMusic);

    private static final Media menuMusic = new Media(Objects.requireNonNull(MainStage.class.getResource("audio/music/menu_music.wav")).toExternalForm());

    private static final MediaPlayer menuMusicPlayer = new MediaPlayer(menuMusic);

    private static final Media gameOverMusic = new Media(Objects.requireNonNull(MainStage.class.getResource("audio/music/game_over_music.wav")).toExternalForm());

    private static final MediaPlayer gameOverMusicPlayer = new MediaPlayer(gameOverMusic);

    /**
     * Gets the current master volume
     * @return the current master volume
     */
    public static int getMasterVolume() {
        return masterVolume.get();
    }

    /**
     * Gets the master volume property
     * @return the master volume property
     */
    public static IntegerProperty masterVolumeProperty() {
        return masterVolume;
    }

    /**
     * Sets the master volume
     * @param masterVolume the new master volume
     */
    public static void setMasterVolume(int masterVolume) {
        AudioPlayer.masterVolume.set(masterVolume);
    }

    /**
     * Gets the current music volume
     * @return the current music volume
     */
    public static int getMusicVolume() {
        return musicVolume.get();
    }

    /**
     * Gets the music volume property
     * @return the music volume property
     */
    public static IntegerProperty musicVolumeProperty() {
        return musicVolume;
    }

    /**
     * Sets the music volume
     * @param musicVolume the new music volume
     */
    public static void setMusicVolume(int musicVolume) {
        AudioPlayer.musicVolume.set(musicVolume);
    }

    /**
     * Gets the sfx volume property
     * @return the sfx volume property
     */
    public static int getSfxVolume() {
        return sfxVolume.get();
    }

    /**
     * Gets the sfx volume property
     * @return the sfx volume property
     */
    public static IntegerProperty sfxVolumeProperty() {
        return sfxVolume;
    }

    /**
     * Sets the sfx volume
     * @param sfxVolume the new sfx volume
     */
    public static void setSfxVolume(int sfxVolume) {
        AudioPlayer.sfxVolume.set(sfxVolume);
    }

    public static void fadeOutMusic(MediaPlayer music, int millis) {
        Timeline fadeOut = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(music.volumeProperty(), music.getVolume())),
                new KeyFrame(Duration.millis(millis), new KeyValue(music.volumeProperty(), 0))
        );
        fadeOut.setOnFinished(e -> music.stop());
        fadeOut.play();
    }

    /**
     * Fades in the audio for a given media player
     * @param music the given media player
     * @param millis the amount of time the fade will last
     */
    public static void fadeInMusic(MediaPlayer music, int millis) {
        music.setVolume(0);
        music.setCycleCount(MediaPlayer.INDEFINITE);
        music.play();
        Timeline fadeIn = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(music.volumeProperty(), 0)),
                new KeyFrame(Duration.millis(millis), new KeyValue(music.volumeProperty(), (musicVolume.get() / 100f) * (masterVolume.get() / 100f)))
        );
        fadeIn.play();
    }

    /**
     * Fades in the audio for a given media player
     * @param music the given media player
     * @param millis the amount of time the fade will last
     */
    public static void fadeMusicTo(MediaPlayer music, double newVolume, int millis) {
        Timeline fadeIn = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(music.volumeProperty(), music.getVolume())),
                new KeyFrame(Duration.millis(millis), new KeyValue(music.volumeProperty(), newVolume))
        );
        fadeIn.play();
    }

    /**
     * Plays the card click sound
     */
    public static void playCardClick() {
        cardClick.play((sfxVolume.get() / 100f) * (masterVolume.get() / 100f));
    }

    /**
     * Plays the clock tick sound
     */
    public static void playClockTick() {
        clockTick.setCycleCount(AudioClip.INDEFINITE);
        clockTick.play((sfxVolume.get() / 100f) * (masterVolume.get() / 100f));
    }

    /**
     * Stops the clock tick sound
     */
    public static void stopClockTick() {
        clockTick.stop();
    }

    /**
     * Plays the clock tick accelerate sound
     */
    public static void playClockTickAccelerate() {
        clockTickAcceleratePlayer.setVolume((sfxVolume.get() / 100f) * (masterVolume.get() / 100f));
        clockTickAcceleratePlayer.setOnEndOfMedia(clockTickAcceleratePlayer::stop);
        clockTickAcceleratePlayer.play();
    }

    /**
     * Plays a player attack sound effect at random
     */
    public static void playPlayerAttack() {
        new AudioClip(Objects.requireNonNull(MainStage.class.getResource("audio/sfx/player/attack/hit" + (int) (Math.random() * 30 + 1) + ".wav")).toExternalForm()).play((sfxVolume.get() / 100f) * (masterVolume.get() / 100f));
    }

    /**
     * Plays a player hurt sound effect at random
     */
    public static void playPlayerHurt() {
        new AudioClip(Objects.requireNonNull(MainStage.class.getResource("audio/sfx/player/hurt/player_hurt" + (int) (Math.random() * 4 + 1) + ".wav")).toExternalForm()).play((sfxVolume.get() / 100f) * (masterVolume.get() / 100f));
    }

    /**
     * Plays the player death sound effect
     */
    public static void playPlayerDeath() {
        playerDeath.play((sfxVolume.get() / 100f) * (masterVolume.get() / 100f));
    }

    /**
     * Plays the enemy attack sound effect
     */
    public static void playEnemyAttack() {
        enemyAttack.play((sfxVolume.get() / 100f) * (masterVolume.get() / 100f));
    }

    /**
     * Plays an enemy attack sound effect at random
     */
    public static void playEnemyHurt() {
        new AudioClip(Objects.requireNonNull(MainStage.class.getResource("audio/sfx/enemy/hurt/enemy_hurt" + (int) (Math.random() * 8 + 1) + ".wav")).toExternalForm()).play((sfxVolume.get() / 100f) * (masterVolume.get() / 100f));
    }

    /**
     * Plays the enemy death sound effect
     */
    public static void playEnemyDeath() {
        enemyDeath.play((sfxVolume.get() / 100f) * (masterVolume.get() / 100f));
    }

    /**
     * Plays the boss attack sound effect
     */
    public static void playBossAttack() {
        bossAttack.play((sfxVolume.get() / 100f) * (masterVolume.get() / 100f));
    }

    /**
     * Plays a boss hurt sound effect at random
     */
    public static void playBossHurt() {
        new AudioClip(Objects.requireNonNull(MainStage.class.getResource("audio/sfx/boss/hurt/boss_hurt" + (int) (Math.random() * 9 + 1) + ".wav")).toExternalForm()).play((sfxVolume.get() / 100f) * (masterVolume.get() / 100f));
    }

    /**
     * Plays the boss death sound effect
     */
    public static void playBossDeath() {
        bossDeath.play((sfxVolume.get() / 100f) * (masterVolume.get() / 100f));
    }

    /**
     * Plays the gameplay music
     */
    public static void playGameplayMusic() {
        gameplayMusicPlayer.setVolume((musicVolume.get() / 100f) * (masterVolume.get() / 100f));
        gameplayMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        gameplayMusicPlayer.play();
    }

    /**
     * Pauses the gameplay music
     */
    public static void pauseGameplayMusic() {
        gameplayMusicPlayer.pause();
    }

    /**
     * Stops the gameplay music
     */
    public static void stopGameplayMusic() {
        gameplayMusicPlayer.stop();
    }

    /**
     * Gets the gameplay music player
     * @return the gameplay music player
     */
    public static MediaPlayer getGameplayMusicPlayer() {
        return gameplayMusicPlayer;
    }

    /**
     * Gets the menu music player
     * @return the menu music player
     */
    public static MediaPlayer getMenuMusicPlayer() {
        return menuMusicPlayer;
    }

    /**
     * Plays the game over music
     */
    public static void playGameOverMusic() {
        gameOverMusicPlayer.setVolume((musicVolume.get() / 100f) * (masterVolume.get() / 100f));
        gameOverMusicPlayer.play();
    }

    /**
     * Stops the game over music
     */
    public static void stopGameOverMusic() {
        gameOverMusicPlayer.stop();
    }

    /**
     * Gets the game over music player
     * @return the game over music player
     */
    public static MediaPlayer getGameOverMusicPlayer() {
        return gameOverMusicPlayer;
    }

}
