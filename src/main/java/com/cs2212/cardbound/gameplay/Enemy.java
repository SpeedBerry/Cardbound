package com.cs2212.cardbound.gameplay;

import com.cs2212.cardbound.MainStage;
import javafx.scene.image.Image;

import java.util.Objects;

public class Enemy extends Entity {

    private String type;

    private final Image idle = new Image(Objects.requireNonNull(MainStage.class.getResourceAsStream("sprites/enemy/enemy_idle.png")));
    private final Image dead = new Image(Objects.requireNonNull(MainStage.class.getResourceAsStream("sprites/enemy/enemy_dead.png")));
    private final Image attackWindUp = new Image(Objects.requireNonNull(MainStage.class.getResourceAsStream("sprites/enemy/enemy_attack_wind_up.png")));
    private final Image attack = new Image(Objects.requireNonNull(MainStage.class.getResourceAsStream("sprites/enemy/enemy_attack.png")));

    private final Image bossIdle = new Image(Objects.requireNonNull(MainStage.class.getResourceAsStream("sprites/boss/boss_idle.png")));

    private final Image bossDead = new Image(Objects.requireNonNull(MainStage.class.getResourceAsStream("sprites/boss/boss_dead.png")));
    private final Image bossAttackWindUp = new Image(Objects.requireNonNull(MainStage.class.getResourceAsStream("sprites/boss/boss_attack_wind_up.png")));
    private final Image bossAttack = new Image(Objects.requireNonNull(MainStage.class.getResourceAsStream("sprites/boss/boss_attack.png")));

    public Enemy(int health, String type) {
        super(health);
        this.type = type;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Image getIdle() {
        if (type.equals("boss"))
            return bossIdle;
        return idle;
    }

    public Image getDead() {
        if (type.equals("boss"))
            return bossDead;
        return dead;
    }

    public Image getAttackWindUp() {
        if (type.equals("boss"))
            return bossAttackWindUp;
        return attackWindUp;
    }

    public Image getAttack() {
        if (type.equals("boss"))
            return bossAttack;
        return attack;
    }

}
