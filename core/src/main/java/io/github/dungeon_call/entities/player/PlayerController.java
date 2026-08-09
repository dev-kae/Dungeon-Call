package io.github.dungeon_call.entities.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import io.github.dungeon_call.GameClient;

public class PlayerController {

    private final GameClient client;

    public PlayerController(GameClient client) {
        this.client = client;
    }

    public void update(float delta) {

        if (client.getMyPlayer() == null) {
            return;
        }

        float dx = 0;
        float dy = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            dy += 1;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            dy -= 1;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            dx -= 1;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            dx += 1;
        }

        if (dx != 0 || dy != 0) {
            client.move(dx, dy);
        }
    }
}
