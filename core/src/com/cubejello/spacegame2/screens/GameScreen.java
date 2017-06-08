package com.cubejello.spacegame2.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Jake on 4/11/2017.
 */

public class GameScreen implements Screen {

    private GameStage stage;

    private Game game;

    public GameScreen(Game game, SpriteBatch batch, boolean coop) {
        this.game = game;
        stage = new GameStage(batch, game);
        stage.setCoop(coop);
    }

    @Override
    public void show() {
        System.out.println("GameScreen.show()");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            setScreen(new MenuScreen(game));
        }
    }

    public void setScreen(Screen screen) {
        game.setScreen(screen);
        stage.dispose();
    }


    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
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
        stage.dispose();
    }
}
