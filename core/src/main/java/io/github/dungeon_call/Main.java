package io.github.dungeon_call;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.zenith.ServerLogger;
import io.github.dungeon_call.entities.player.PlayerController;
import io.github.dungeon_call.entities.player.RemotePlayer;

public class Main extends ApplicationAdapter {

    private GameClient client;
    private PlayerController playerController;
    private ServerLogger logger;

    private SpriteBatch batch;
    private Texture image;
    private ShapeRenderer shapeRenderer;

    @Override
    public void create() {

        logger = new ServerLogger();

        client = new GameClient(logger);
        client.connect();

        playerController = new PlayerController(client);

        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    public void render() {

        float delta = com.badlogic.gdx.Gdx.graphics.getDeltaTime();

        playerController.update(delta);

        ScreenUtils.clear(0, 0, 0, 1);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (RemotePlayer player : client.getPlayers().values()) {

            shapeRenderer.rect(
                player.getX(),
                player.getY(),
                20,
                20
            );
        }

        shapeRenderer.end();
    }

    @Override
    public void dispose() {

        client.disconnect();
        shapeRenderer.dispose();
        batch.dispose();
        image.dispose();
    }
}
