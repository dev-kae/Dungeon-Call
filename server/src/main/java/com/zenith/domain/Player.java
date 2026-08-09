package com.zenith.domain;

import com.zenith.network.packet.PlayerData;

import java.util.UUID;

public class Player {
    private final UUID id;
    private final String name;
    private Long lives;
    private Long score;
    private float x;
    private float y;

    public Player(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.lives = 3L;
        this.score = 0L;
    }

    public PlayerData toData() {
        return new PlayerData(
            id,
            name,
            x,
            y
        );
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }

    public Long getLives() {
        return lives;
    }

    public Long getScore() {
        return score;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
