package com.cs2212.cardbound.gameplay;

import com.cs2212.cardbound.MainStage;
import com.cs2212.cardbound.system.AudioPlayer;
import com.cs2212.cardbound.system.Gameplay;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

/**
 * A class that represents a card object in the Gameplay scene.
 * @version 1.0
 * @author Jacob Couture, Abhinav Bhati
 * @since 3/13/2024
 */
public class Card extends Pane {

    /**
     * Whether this card is selected
     */
    private boolean isSelected = false;
    /**
     * Whether this is a permanent card
     */
    private boolean isPermanent = false;

    private int attackDamage;
    private double attackModifier;

    /**
     * The initial Y position of this card
     */
    private final double BASE_Y = this.getLayoutY();
    /**
     * The initial stroke size of the card border
     */
    private final double BASE_STROKE = 2;
    /**
     * The amount of time all the card animations take
     */
    private final int ANIMATION_TIME = 75;
    /**
     * Animation for moving the card up
     */
    TranslateTransition cardSlideUp = new TranslateTransition(Duration.millis(ANIMATION_TIME), this);
    /**
     * Animation for moving the card down
     */
    TranslateTransition cardSlideDown = new TranslateTransition(Duration.millis(ANIMATION_TIME), this);
    /**
     * Animation for making the card size bigger
     */
    ScaleTransition cardSizeUp = new ScaleTransition(Duration.millis(ANIMATION_TIME), this);
    /**
     * Animation for making the card size smaller
     */
    ScaleTransition cardSizeDown = new ScaleTransition(Duration.millis(ANIMATION_TIME), this);
    /**
     * The drop shadow effect when hovering over the card
     */
    DropShadow cardShadow = new DropShadow();
    /**
     * The color of the card border when it is selected
     */
    private final Color selectedColor = new Color(1.0f, 228f / 255f, 120f / 255f, 1);

    @FXML
    private Rectangle cardBorder;

    @FXML
    private ImageView cardImage, typeIcon, cardSelectionIcon;

    @FXML
    private Label cardTitle, cardDesc, cardType;

    @FXML
    private Line cardDiv;

    @FXML
    private Pane cardSelection;

    private int attackDmg;

    private int numAttacks;

    private String cardEffect;

    private int numAttacksAffected;

    /**
     * Constructor for creating a new Card object. Because it is a JavaFX object, it MUST have a constructor with no arguments.
     */
    public Card() {
        Platform.runLater(this::postInit);

        FXMLLoader loader = new FXMLLoader(MainStage.class.getResource("card.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            Parent root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        // Setup mouse hover interaction
        this.setOnMouseEntered(event -> hoverSelect());
        this.setOnMouseExited(event -> hoverDeselect());

        /*
        Runs the code for selecting a card on a separate thread to improve performance.
        JavaFX isn't multithreaded and having lots of moving parts and different checks and conditions
        all on the main JavaFX application thread can cause UI elements to appear laggy or just not update.
         */
        this.setOnMouseClicked(event -> new Thread(() -> {
            toggleSelect();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }).start());
    }

    /**
     * Code to run immediately after this card has been initialized
     */
    private void postInit() {
        cardSelection.setVisible(false);
        cardSelectionIcon.setImage(new Image(Objects.requireNonNull(MainStage.class.getResourceAsStream("sprites/selection_checkmark.png"))));

        // Sets up mouse entrance interaction for if the card is selected
        cardSelection.setOnMouseEntered(event -> {
            if (isSelected) {
                cardSelectionIcon.setOpacity(0.55);
                cardDiv.setOpacity(0.55);
                cardBorder.setStroke(selectedColor.deriveColor(1, 1, 1, 0.55));
            }
        });

        // Sets up mouse exit interaction for if the card is selected
        cardSelection.setOnMouseExited(event -> {
            if (isSelected) {
                cardSelectionIcon.setOpacity(0.85);
                cardDiv.setOpacity(1);
                cardBorder.setStroke(selectedColor.deriveColor(1, 1, 1, 1));
            }
        });

        cardShadow.setColor(Color.TRANSPARENT);
        cardBorder.setEffect(cardShadow);

        // Clips the card image to make the top corners of the image fit inside the card
        cardImage.setClip(getClip(0.1, 0.1, 0, 0));
    }

    /**
     * Gets a clip with rounded edges by drawing a path.
     * @param topLeft the top left clip radius
     * @param topRight the top right clip radius
     * @param bottomLeft the bottom left clip radius
     * @param bottomRight the bottom right clip radius
     * @return a node to be used as a clip
     * @see <a href="https://stackoverflow.com/questions/72949214/javafx-image-with-different-corners">...</a>
     */
    private Node getClip(double topLeft, double topRight, double bottomLeft, double bottomRight) {
        Path clip;

        double height = cardImage.getFitHeight();
        double width = cardImage.getFitWidth();
        double radius1 = height * topLeft;
        double radius2 = height * topRight;
        double radius3 = height * bottomLeft;
        double radius4 = height * bottomRight;

        clip = new Path(new MoveTo(0, radius1),
                new ArcTo(radius1, radius1, 0, radius1, 0, false, true),
                new HLineTo(width - radius2),
                new ArcTo(radius2, radius2, 0, width, radius2, false, true),
                new VLineTo(height - radius4),
                new ArcTo(radius4, radius4, 0, width - radius4, height, false, true),
                new HLineTo(radius3),
                new ArcTo(radius3, radius3, 0, 0, height - radius3, false, true));
        clip.setFill(Color.RED);

        return clip;
    }

    /**
     * Gets the title of the card
     * @return the card title
     */
    public Label getTitle() {
        return cardTitle;
    }

    /**
     * Sets the card title
     * @param title the given title
     */
    public void setTitle(String title) {
        cardTitle.setText(title);
    }

    /**
     * Gets the description of the card
     * @return the card description
     */
    public Label getDescription() {
        return cardDesc;
    }

    /**
     * Sets the description of the card
     * @param description the given description
     */
    public void setDescription(String description) {
        cardDesc.setText(description);
    }

    /**
     * Gets the type of the card
     * @return the card type
     */
    public Label getType() {
        return cardType;
    }

    /**
     * Sets the type of the card
     * @param type the given type. Must be either "attack" or "modifier"
     */
    public void setType(String type) {
        cardType.setText(type);
        setTypeIcon(type);
    }

    /**
     * Gets the image for the card art
     * @return the card image
     */
    public ImageView getImageView() {
        return cardImage;
    }

    /**
     * Sets the image for the card art
     * @param image the given image. Image ratio must be 16:9
     */
    public void setImage(Image image) {
        cardImage.setImage(image);
    }

    /**
     * Sets the icon for the card type
     * @param type the given card type. Must be either "attack" or "modifier"
     */
    private void setTypeIcon(String type) {
        if (type.equals("attack"))
            typeIcon.setImage(new Image(Objects.requireNonNull(MainStage.class.getResourceAsStream("sprites/attack_type_icon.png"))));
        else if (type.equals("modifier"))
            typeIcon.setImage(new Image(Objects.requireNonNull(MainStage.class.getResourceAsStream("sprites/modifier_type_icon.png"))));
        else
            typeIcon.setImage(new Image(Objects.requireNonNull(MainStage.class.getResourceAsStream("sprites/error.png"))));
    }

    public int getAttackDamage(){ return attackDmg; }
    public void setAttackDmg(int attackDmg){ this.attackDmg = attackDmg; }
    public int getNumAttacks(){ return numAttacks; }
    public void setNumAttacks(int numAttacks){ this.numAttacks = numAttacks; }
    public String getCardEffect(){ return cardEffect;}
    public void setCardEffect(String equation){ this.cardEffect = equation; }
    public int getNumAttacksAffected(){ return numAttacksAffected; }
    public void setNumAttacksAffected(int numAttacksAffected){ this.numAttacksAffected = numAttacksAffected; }

    /**
     * Whether this card is selected
     * @return true if it is selected, false otherwise
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * Whether this card is a permanent card
     * @return true if it is permanent, false otherwise
     */
    public boolean isPermanent() {
        return isPermanent;
    }

    /**
     * Sets this card to be a permanent card
     */
    public void setPermanent() {
        isPermanent = true;
        cardBorder.setStrokeWidth(BASE_STROKE + 4);
        cardBorder.setStroke(Color.GOLD);
        cardDiv.setStrokeWidth(BASE_STROKE + 10);
        cardDiv.setStroke(Color.GOLD);
    }

    /**
     * Handles effects for when this card is hovered
     */
    public void hoverSelect() {
        if (!isSelected) {
            // Grow card
            cardSizeUp.setToX(0.9);
            cardSizeUp.setToY(0.9);
            cardSizeUp.play();
            // Animate card up
            cardSlideUp.setToY(BASE_Y - 20);
            cardSlideUp.play();
            // Show card shadow
            cardShadow.setOffsetY(6);
            cardShadow.setSpread(0.2);
            cardShadow.setRadius(10.0);
            cardShadow.setColor(new Color(0, 0, 0, 0.6));
            // Play card click sfx
            AudioPlayer.playCardClick();
        }
    }

    /**
     * Handles effects for when this card stops being hovered
     */
    public void hoverDeselect() {
        if (!isSelected) {
            // Shrink card
            cardSizeDown.setToX(0.85);
            cardSizeDown.setToY(0.85);
            cardSizeDown.play();
            // Animate card down
            cardSlideDown.setToY(BASE_Y);
            cardSlideDown.play();
            // Hide card shadow
            cardShadow.setColor(Color.TRANSPARENT);
            cardShadow.setOffsetY(0);
            cardShadow.setSpread(0);
            cardShadow.setRadius(0);
        }
    }

    /**
     * Toggles whether this card is selected
     */
    public void toggleSelect() {
        if (!isSelected && !Gameplay.isAttacking) {
            if (Gameplay.cardCount.get() < 2) {
                // Select card if less than 2 have already been selected
                cardBorder.setStrokeWidth(BASE_STROKE + 4);
                cardBorder.setStroke(selectedColor);
                cardDiv.setStrokeWidth(BASE_STROKE + 4);
                cardDiv.setStroke(selectedColor);
                cardSelection.setVisible(true);
                hoverDeselect();
                isSelected = true;

                Gameplay.cardCount.set(Gameplay.cardCount.getValue() + 1);
                Gameplay.selectedCards.add(this);
            } else {
                // Shake card to indicate that no more cards can be selected
                Timeline shake = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(this.translateXProperty(), 0)),
                        new KeyFrame(Duration.millis(50), new KeyValue(this.translateXProperty(), 10)),
                        new KeyFrame(Duration.millis(100), new KeyValue(this.translateXProperty(), 0)),
                        new KeyFrame(Duration.millis(150), new KeyValue(this.translateXProperty(), -10)),
                        new KeyFrame(Duration.millis(200), new KeyValue(this.translateXProperty(), 0))
                        );
                shake.setCycleCount(3);
                shake.play();
            }
        } else {
            // Deselect card
            isSelected = false;
            cardBorder.setStrokeWidth(BASE_STROKE);
            cardBorder.setStroke(Color.BLACK);
            cardDiv.setStrokeWidth(BASE_STROKE);
            cardDiv.setStroke(Color.BLACK);
            cardSelection.setVisible(false);

            Timeline mouseCheck = new Timeline(new KeyFrame(Duration.millis(10), event -> {
                if (this.contains(this.screenToLocal(Gameplay.mouseRobot.getMousePosition())))
                    hoverSelect();
            }));
            mouseCheck.play();

            Gameplay.cardCount.set(Gameplay.cardCount.getValue() - 1);
            Gameplay.selectedCards.remove(this);
        }
    }

}
