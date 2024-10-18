package com.cs2212.cardbound.gameplay;

public class Entity {

    private int health;
    private int maxHealth;

    public Entity(int health) {
        this.health = health;
        this.maxHealth = health;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

}
