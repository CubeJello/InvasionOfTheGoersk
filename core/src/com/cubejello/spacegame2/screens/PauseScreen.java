package com.cubejello.spacegame2.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by Jake on 5/11/2017.
 */

public class PauseScreen extends Stage {

    private Texture pauseTexture;
    private Sprite pauseSprite;
    private SpriteBatch batch;

    //private Camera camera;

    public PauseScreen(SpriteBatch batch) {
        this.batch = batch;
        //this.camera = camera;
        Viewport viewport = new FitViewport(100, 60);
        pauseTexture = new Texture(Gdx.files.internal("textures\\paused.png"));
        pauseSprite = new Sprite(pauseTexture);
        pauseSprite.setSize(pauseTexture.getWidth() / 6, pauseTexture.getHeight() / 6.5f);
        pauseSprite.setPosition(viewport.getWorldWidth() / 2 - pauseSprite.getWidth() / 2, viewport.getWorldHeight() / 2 - pauseSprite.getHeight() / 2);
    }

    public void update() {
        pauseSprite.draw(batch);
    }
}
