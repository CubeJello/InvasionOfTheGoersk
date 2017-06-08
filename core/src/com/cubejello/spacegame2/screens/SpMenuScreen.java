package com.cubejello.spacegame2.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Created by Jake on 5/11/2017.
 */

public class SpMenuScreen extends Stage implements Screen {

    Game game;
    private OrthographicCamera camera;
    private SpriteBatch batch;

    private Texture menuTexture;
    private Sprite menuSprite;

    private Vector3 touchPoint;
    private Rectangle playButton;
    private Rectangle backButton;

    private boolean currentScreen;

    public SpMenuScreen(Game game, SpriteBatch batch) {
        this.game = game;
        this.batch = batch;
        currentScreen = true;
        camera = new OrthographicCamera(800, 480);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f);
        camera.update();

        menuTexture = new Texture(Gdx.files.internal("textures\\sp_menu.png"));
        menuSprite = new Sprite(menuTexture);
        menuSprite.setSize(camera.viewportWidth, camera.viewportHeight);

        setupTouchControlAreas();
    }

    private void setupTouchControlAreas() {
        touchPoint = new Vector3();
        playButton = new Rectangle(300, 122, 202, 51);
        backButton = new Rectangle(300, 62, 202, 51);
        Gdx.input.setInputProcessor(this);
    }

    private boolean playButtonTouched(float x, float y) {
        return playButton.contains(x, y);
    }

    private boolean backButtonTouched(float x, float y) {
        return backButton.contains(x, y);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(currentScreen) {
            translateScreenToWorldCoordinates(screenX, screenY);

            if (playButtonTouched(touchPoint.x, touchPoint.y)) {
                game.setScreen(new GameScreen(game, batch, false));
                currentScreen = false;
                dispose();
            } else if (backButtonTouched(touchPoint.x, touchPoint.y)) {
                game.setScreen(new MenuScreen(game));
                currentScreen = false;
                dispose();
            }
        }
        return super.touchDown(screenX, screenY, pointer, button);
    }

    private void translateScreenToWorldCoordinates(int x, int y) {
        camera.unproject(touchPoint.set(x, y, 0));
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        menuSprite.draw(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        menuTexture.dispose();
        game.dispose();
    }
}
