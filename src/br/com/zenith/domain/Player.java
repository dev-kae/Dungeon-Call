package br.com.zenith.domain;

import java.io.Serializable;
import java.util.UUID;

public class Player implements Serializable {

    private final UUID id;
    private final String name;

    private float x;
    private float y;

    public Player(String name) {
        this.id = UUID.randomUUID();
        this.name = name;

        this.x = 400;
        this.y = 300;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
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
