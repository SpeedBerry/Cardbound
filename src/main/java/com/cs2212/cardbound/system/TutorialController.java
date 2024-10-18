package com.cs2212.cardbound.system;
import com.cs2212.cardbound.MainStage;
import com.cs2212.cardbound.SceneTools;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.util.Objects;

/**
 * @author Tina Tang
 * @author Jacob Couture
 *   This class represents the controller for the tutorial screen. Each image will display a "how to" for our game.
 */
public class TutorialController {

    @FXML
    private StackPane rootPane;

    @FXML
    private ImageView tutorialImage, leftArrow, rightArrow;

    @FXML
    private Label slideDescription, slideNumbers, backButton;

    private boolean backButtonSelected = false;

    private int currentSlide;

    private final int MAX_SLIDES = 16;

    private final ObservableList<Image> images = FXCollections.observableArrayList();

    private final String[] imageDescriptions = {
            "Welcome to the Cardbound tutorial!\nYou, the player, are a weak adventurer that has stumbled across some magical cards which have bestowed you with incredible abilities!",
            "In Cardbound, your goal is to defeat as many evil goblins as you can before you fall in battle.",
            "This is you, the player.\nUsing your newfound abilities from your cards, you can perform a wide variety of attacks, and even increase your strength!",
            "This is the enemy. Above them you can see their health. Your goal is to deal EXACTLY enough damage to defeat them in a single hit.",
            "In the top left of the screen you can see your remaining health. If it reaches 0, it's game over! Unlimited health can be toggled on or off in the settings if you would like to practice without pressure.",
            "At the top of the screen you will see the level timer. If you fail to make a selection before it reaches 0, then you will take damage and be prompted to retry.\nThis timer can be toggled on or off in the settings.",
            "In the top right of the screen there are various trackers, such as your current score, your current level, and the total amount of time you have spent playing this run.\nThe run timer can be toggled on or off in the settings.",
            "Also in the top right, there is a pause button. From here you can pause the game to take a short break, restart your run, exit to the main menu, or close the game.\nThe game will automatically save upon exiting to the menu or exiting the application.",
            "In each combat you will draw 4 cards. To attack the enemy, you must select 2 of your 4 cards. You cannot select more or less than 2.",
            "There are two types of cards: ATTACK and MODIFIER. ATTACK cards deal damage directly to the enemy, while MODIFIER cards change the effects of ATTACK cards.",
            "When you press the attack button, the power of your cards will combine and you will damage the enemy! It is up to you to figure out what the result of your card interactions are, but know that the final result will always round down to the nearest whole number.",
            "If you successfully defeat the enemy, you will be awarded points and may progress to the next level. Be warned though, as every 5 levels there is a scary boss! Defeating them will reward you with double points, but the game will increase in difficulty!",
            "Failing to deal enough damage to the enemy will give them a chance to retaliate, causing you to take damage and get staggered. Using the powers of the cards, you are able to turn back time to retry this encounter.",
            "If you deal too much damage to the enemy, you will anger the cards for wasting their power. They will damage you for your blunder, but you may continue fighting as long as you are still alive.",
            "If your health reaches 0, you will be presented with the results screen. This will show you all the information about this run, and it will update your global statistics.",
            "That's all for the tutorial! Have fun playing, and try to get the best high score!"
    };

    public void initialize() {
        Platform.runLater(this::postInit);

        // Set current slide to the first slide
        currentSlide = 1;

        rootPane.setOpacity(0);
        SceneTools.fadeInScene(rootPane, 1500);
    }

    private void postInit() {
        SceneTools.setScreenScale(MainMenu.screenWidthProperty.get(), MainMenu.screenHeightProperty.get(), rootPane);

        keyInputListener(rootPane.getScene());

        // Fill images list
        for (int i = 1; i <= MAX_SLIDES; i++)
            images.add(new Image(Objects.requireNonNull(MainStage.class.getResourceAsStream("sprites/tutorial/tutorial_slide" + i + ".png"))));

        slideNumbers.setText(currentSlide + "/" + MAX_SLIDES);
        slideDescription.setText(imageDescriptions[currentSlide - 1]);

        // Initialize back button
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
    }

    /**
     * Enlarges arrow when hovered
     * @param e the mouse event created by the arrow being hovered
     */
    public void hoverEnterArrow(MouseEvent e) {
        ImageView arrow = (ImageView) e.getSource();
        arrow.setScaleX(arrow.getScaleX() + 0.1);
        arrow.setScaleY(arrow.getScaleY() + 0.1);
    }

    /**
     * Shrinks arrow when hover ends
     * @param e the mouse event created by the arrow being unhovered
     */
    public void hoverExitArrow(MouseEvent e) {
        ImageView arrow = (ImageView) e.getSource();
        arrow.setScaleX(arrow.getScaleX() - 0.1);
        arrow.setScaleY(arrow.getScaleY() - 0.1);
    }

    public void clickLeftArrow() {
        if (currentSlide > 1) {
            currentSlide--;
            tutorialImage.setImage(images.get(currentSlide - 1));
            slideNumbers.setText(currentSlide + "/" + MAX_SLIDES);
            slideDescription.setText(imageDescriptions[currentSlide - 1]);
        }
        if (currentSlide == 1) {
            leftArrow.setOpacity(0.35);
            leftArrow.setDisable(true);
        }
        if (currentSlide < MAX_SLIDES) {
            rightArrow.setOpacity(1);
            rightArrow.setDisable(false);
        }
    }

    public void clickRightArrow() {
        if (currentSlide < MAX_SLIDES) {
            currentSlide++;
            tutorialImage.setImage(images.get(currentSlide - 1));
            slideNumbers.setText(currentSlide + "/" + MAX_SLIDES);
            slideDescription.setText(imageDescriptions[currentSlide - 1]);
        }
        if (currentSlide == MAX_SLIDES) {
            rightArrow.setOpacity(0.35);
            rightArrow.setDisable(true);
        }
        if (currentSlide > 1) {
            leftArrow.setOpacity(1);
            leftArrow.setDisable(false);
        }
    }

    private void keyInputListener(Scene scene) {
        // Listener for key presses
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case LEFT:
                    clickLeftArrow();
                    break;
                case RIGHT:
                    clickRightArrow();
                    break;
                case UP, DOWN:
                    if (!backButtonSelected) {
                        backButtonSelected = true;
                        backButton.setScaleX(backButton.getScaleX() + 0.1);
                        backButton.setScaleY(backButton.getScaleY() + 0.1);
                    }
                    break;
                case SPACE:
                    SceneTools.fadeOutToScene(event, "main-menu.fxml", 500, rootPane);
                default:
                    break;
            }
        });
    }

}
