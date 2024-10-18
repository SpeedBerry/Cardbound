package com.cs2212.cardbound.gameplay;

import com.cs2212.cardbound.MainStage;
import javafx.scene.image.Image;
import java.util.Objects;

public class Player extends Entity {

    private final Image idle = new Image(Objects.requireNonNull(MainStage.class.getResourceAsStream("sprites/player/player_idle.png")));
    private final Image downed = new Image(Objects.requireNonNull(MainStage.class.getResourceAsStream("sprites/player/player_downed.png")));
    private final Image dead = new Image(Objects.requireNonNull(MainStage.class.getResourceAsStream("sprites/player/player_dead.png")));
    private final Image attackWindUp = new Image(Objects.requireNonNull(MainStage.class.getResourceAsStream("sprites/player/player_attack_wind_up.png")));
    private final Image attack = new Image(Objects.requireNonNull(MainStage.class.getResourceAsStream("sprites/player/player_attack.png")));

    public Player(int health) {
        super(health);
    }

    public Image getIdle() {
        return idle;
    }

    public Image getDowned() {
        return downed;
    }

    public Image getDead() {
        return dead;
    }

    public Image getAttackWindUp() {
        return attackWindUp;
    }

    public Image getAttack() {
        return attack;
    }

}
