package com.cubejello.spacegame2.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.cubejello.spacegame2.utils.ImageButton;

/**
 * Created by Jake on 5/6/2017.
 */

public class MenuScreen extends Stage implements Screen {

    private Game game;
    private OrthographicCamera camera;
    private SpriteBatch batch;

    private Texture background;
    private Sprite bgSprite;

    private Vector3 touchPoint;
    private ImageButton playButton;
    private ImageButton spButton;
    private ImageButton coopButton;
    private ImageButton dmButton;

    private boolean currentScreen;
    private boolean isDesktop = false;

    public MenuScreen(Game game) {
        this.game = game;
        currentScreen = true;
        batch = new SpriteBatch();
        camera = new OrthographicCamera(800, 480);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f);
        camera.update();

        if(Gdx.app.getType() == Application.ApplicationType.Desktop)
            isDesktop = true;

        background = new Texture(Gdx.files.internal("textures\\main_menu.png"));
        bgSprite = new Sprite(background);
        bgSprite.setSize(camera.viewportWidth, camera.viewportHeight);

        setupTouchControlAreas();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        bgSprite.draw(batch);
        updateButtons();
        batch.end();
    }

    private void updateButtons() {
        if(isDesktop) {
            spButton.update(batch);
            coopButton.update(batch);
            dmButton.update(batch);
        } else
            playButton.update(batch);
    }

    private void setupTouchControlAreas() {
        touchPoint = new Vector3();
        if(isDesktop) {
            Texture spButtonTexture = new Texture(Gdx.files.internal("textures\\singleplayer.png"));
            Texture coopButtonTexture = new Texture(Gdx.files.internal("textures\\coop.png"));
            Texture dmButtonTexture = new Texture(Gdx.files.internal("textures\\deathmatch.png"));
            spButton = new ImageButton(400 - spButtonTexture.getWidth() / 2, 142 + spButtonTexture.getHeight() - 10, 201, 50, spButtonTexture, false);
            coopButton = new ImageButton(400 - coopButtonTexture.getWidth() / 2, 142 - coopButtonTexture.getHeight() / 2, 201, 50, coopButtonTexture, false);
            dmButton = new ImageButton(400 - dmButtonTexture.getWidth() / 2, 142 - dmButtonTexture.getHeight() * 2 + 10, 201, 50, dmButtonTexture, false);
        } else {
            Texture playButtonTexture = new Texture(Gdx.files.internal("textures\\android_play.png"));
            playButton = new ImageButton(400 - playButtonTexture.getWidth() / 2, 142  - playButtonTexture.getHeight() / 2, 188, 125, playButtonTexture, false);
        }
        Gdx.input.setInputProcessor(this);
    }

    public boolean touchDown(int x, int y, int pointer, int button) {
        if(currentScreen) {
            translateScreenToWorldCoordinates(x, y);

            if(isDesktop) {
                if(spButton.checkIfClicked(touchPoint)) {
                    game.setScreen(new SpMenuScreen(game, batch));
                    currentScreen = false;
                    dispose();
                } else if(coopButton.checkIfClicked(touchPoint)) {
                    game.setScreen(new GameScreen(game, batch, true));
                    currentScreen = false;
                    dispose();
                } else if(dmButton.checkIfClicked(touchPoint)) {
                    game.setScreen(new DmScreen(game));
                    currentScreen = false;
                    dispose();
                }
            } else
                if(playButton.checkIfClicked(touchPoint)) {
                    game.setScreen(new GameScreen(game, batch, false));
                    currentScreen = false;
                    dispose();
                }
        }

        return super.touchDown(x, y, pointer, button);
    }

    private void translateScreenToWorldCoordinates(int x, int y) {
        camera.unproject(touchPoint.set(x, y, 0));
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
        //batch.dispose();
        background.dispose();
        game.dispose();
    }
}
