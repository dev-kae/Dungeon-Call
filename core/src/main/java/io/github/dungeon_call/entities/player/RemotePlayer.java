package io.github.dungeon_call.entities.player;

import com.zenith.network.packet.PlayerData;

import java.util.UUID;

public class RemotePlayer {

    private final UUID id;
    private final String name;

    private float x;
    private float y;

    public RemotePlayer(PlayerData data) {
        this.id = data.id();
        this.name = data.name();
        this.x = data.x();
        this.y = data.y();
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
