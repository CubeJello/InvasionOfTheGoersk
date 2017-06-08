package com.cubejello.spacegame2.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cubejello.spacegame2.utils.ImageButton;
import com.cubejello.spacegame2.utils.ReloadBar;
import com.cubejello.spacegame2.utils.TouchPad;

/**
 * Created by Jake on 5/5/2017.
 */

public class Hud {
    private Stage stage;

    private Label ship1ScoreLabel;
    private Label ship2ScoreLabel;
    private Label scoreLabel;
    private Label highScoreLabel;
    private BitmapFont font;

    private ReloadBar spReloadBar;
    private ReloadBar mpReloadBar;

    private Table table;

    private SpriteBatch batch;
    private Texture terminalTexture;
    private Sprite terminalSprite;

    private int ship1Score = 0;
    private int ship2Score = 0;
    private int score;
    private int highScore;
    private int coopHighScore;

    private Preferences prefs;

    private boolean coop = false;
    private boolean isDesktop = true;

    private ImageButton soundButton;
    private ImageButton pauseButton;
    private ImageButton foreButton;
    private ImageButton aftButton;
    private ImageButton resetButton;
    private ImageButton upButton;
    private ImageButton rightButton;
    private ImageButton leftButton;
    private ImageButton downButton;
    private TouchPad touchpad;

    Viewport viewport;

    public Hud(SpriteBatch batch, OrthographicCamera camera) {
        viewport = new FitViewport(800, 480);
        stage = new Stage(viewport, batch);
        prefs = Gdx.app.getPreferences("Score Preferences");

        this.batch = batch;
        //this.camera = camera;

        //if(Gdx.app.getType() == Application.ApplicationType.Android)
            initButtons();

        highScore = prefs.getInteger("savedHighScore");
        coopHighScore = prefs.getInteger("savedCoopHighScore");

        terminalTexture = new Texture(Gdx.files.internal("textures\\terminal.png"));
        terminalSprite = new Sprite(terminalTexture);
        terminalSprite.setSize(75f, 6.5f);
        terminalSprite.setPosition(12, 0);

        initFont();
        Label.LabelStyle p2LabelStyle = new Label.LabelStyle(font, Color.WHITE);

        spReloadBar = new ReloadBar(false);
        mpReloadBar = new ReloadBar(true);

        table = new Table();
        table.bottom();
        table.setFillParent(true);

        ship1ScoreLabel = new Label("PLAYER 1 SCORE: " + String.format("%03d", ship1Score), p2LabelStyle);
        ship2ScoreLabel = new Label("PLAYER 2 SCORE: " + String.format("%03d", ship2Score), p2LabelStyle);
        scoreLabel = new Label("SCORE: " + String.format("%03d", score), p2LabelStyle);
        highScoreLabel = new Label(("HIGHSCORE: " + String.format("%03d", highScore)), p2LabelStyle);

        table.add(scoreLabel).expandX().padBottom(12);
        table.add(highScoreLabel).expandX().padBottom(12);

        spReloadBar.add(stage);
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
        touchpad = new TouchPad(stage, camera);
        touchpad.create();
    }

    private void initButtons() {
        //soundButton = new ImageButton(86, 53, 5, 5, new Texture(Gdx.files.internal("textures\\soundon.png")), true);
        //soundButton.setOffTexture(new Texture(Gdx.files.internal("textures\\soundoff.png")));
        pauseButton = new ImageButton(93, 53, 5, 5, new Texture(Gdx.files.internal("textures\\pause.png")), true);
        pauseButton.setOffTexture(new Texture(Gdx.files.internal("textures\\pausebuttonon.png")));

        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            foreButton = new ImageButton(80, 10, 10, 10, new Texture(Gdx.files.internal("textures\\fore_cannon.png")), false);
            aftButton = new ImageButton(80, 40, 10, 10, new Texture(Gdx.files.internal("textures\\aft_cannon.png")), false);
            resetButton = new ImageButton(25, 24, 50, 12, new Texture(Gdx.files.internal("textures\\taptoreset.png")), false);
            upButton = new ImageButton(20, 35, 10, 10, new Texture(Gdx.files.internal("textures\\arrow.png")), false);
            rightButton = new ImageButton(35, 25, 10, 10, new Texture(Gdx.files.internal("textures\\arrow.png")), false);
            leftButton = new ImageButton(5, 25, 10, 10, new Texture(Gdx.files.internal("textures\\arrow.png")), false);
            downButton = new ImageButton(20, 15, 10, 10, new Texture(Gdx.files.internal("textures\\arrow.png")), false);
            downButton.rotate90(true);
            downButton.rotate90(true);
            leftButton.rotate90(false);
            rightButton.rotate90(true);
            isDesktop = false;
            resetButton.setDisabled(true);
        }
    }

    private void initFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts\\data-latin.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();

        params.size = 24;

        font = generator.generateFont(params);
    }

    public void setSpReloadBar(float value) {
       spReloadBar.set(value);
    }

    public void setMpReloadBar(float value) {
        mpReloadBar.set(value);
    }

    public void setCoop(boolean coop) {
        this.coop = coop;
        if(coop)
            addMpReloadBar();
    }

    public void addMpReloadBar() {
        mpReloadBar.add(stage);
    }

    public void updateInvasion(float delta) {
        table.clear();

        if(coop) {
            scoreLabel.setText(String.format("%03d", score));
            highScoreLabel.setText("COOP HIGHSCORE: " + String.format("%03d", coopHighScore));
        } else if(!coop) {
            scoreLabel.setText("SCORE: " + String.format("%03d", score));
            highScoreLabel.setText("HIGHSCORE: " + String.format("%03d", highScore));
        }

        table.add(scoreLabel).expandX().padBottom(12);
        table.add(highScoreLabel).expandX().padBottom(12);

        touchpad.render();

        stage.draw();
        stage.act(delta);
    }


    public boolean isFireButtonClicked(Vector3 touchPoint) {
        return !isDesktop && foreButton.checkIfClicked(touchPoint);
    }

    public boolean isAftButtonClicked(Vector3 touchPoint) {
        return !isDesktop && aftButton.checkIfClicked(touchPoint);
    }

    public boolean isUpButtonClicked(Vector3 touchPoint) {
        return !isDesktop && upButton.checkIfClicked(touchPoint);
    }

    public boolean isLeftButtonClicked(Vector3 touchPoint) {
        return !isDesktop && leftButton.checkIfClicked(touchPoint);
    }

    public boolean isRightButtonClicked(Vector3 touchPoint) {
        return !isDesktop && rightButton.checkIfClicked(touchPoint);
    }

    public boolean isDownButtonClicked(Vector3 touchPoint) {
        return !isDesktop && downButton.checkIfClicked(touchPoint);
    }

    public boolean isResetButtonClicked(Vector3 touchPoint) {
        return !isDesktop && resetButton.checkIfClicked(touchPoint);
    }

    public boolean isSoundButtonClicked(Vector3 touchPoint) {
        //return soundButton.checkIfClicked(touchPoint);
        return false;
    }

    public boolean isPauseButtonClicked(Vector3 touchPoint) {
        return pauseButton.checkIfClicked(touchPoint);
    }

    public void gameover() {
        if(!isDesktop)
            resetButton.setDisabled(false);
    }

    public void update(SpriteBatch batch) {
        terminalSprite.draw(batch);
        updateButtons(batch);
    }

    private void updateButtons(SpriteBatch batch) {
        if(!isDesktop) {
            foreButton.update(batch);
            aftButton.update(batch);
            upButton.update(batch);
            leftButton.update(batch);
            rightButton.update(batch);
            downButton.update(batch);
            resetButton.update(batch);
        }
        pauseButton.update(batch);
        //soundButton.update(batch);
    }

    public void updateDm() {
        table.clear();

        ship1ScoreLabel.setText("P1 SCORE: " + String.format("%03d", ship1Score));
        ship2ScoreLabel.setText("P2 SCORE: " + String.format("%03d", ship2Score));

        table.add(ship1ScoreLabel).expandX().padBottom(12);
        table.add(ship2ScoreLabel).expandX().padBottom(12);

        stage.draw();
    }

    public void increaseScore() {
        score++;
    }

    public void  increaseDmScore(int ship) {
        if(ship == 1) {
            ship1Score++;
        } else if(ship == 2) {
            ship2Score++;
        }
    }

    public void updateHighScore() {
        if(!coop) {
            if (score > highScore) {
                highScore = score;
                prefs.putInteger("savedHighScore", highScore);
            }
        } else if(coop) {
            if (score > coopHighScore) {
                coopHighScore = score;
                prefs.putInteger("savedCoopHighScore", coopHighScore);
            }
        }
        prefs.flush();
    }

    public void resetScore() {
        score = 0;
        if(!isDesktop)
            resetButton.setDisabled(true);
    }

    public void resetHighScore() {
        prefs.putInteger("savedHighScore", 0);
        prefs.flush();
        highScore = 0;
    }

    public float getScore() {
        return score;
    }

    public void disposeButtons() {
        if(!isDesktop) {
            resetButton.dispose();
            soundButton.dispose();
            pauseButton.dispose();
            foreButton.dispose();
            leftButton.dispose();
            upButton.dispose();
            rightButton.dispose();
            downButton.dispose();
        }
    }

    public void dispose() {
        disposeButtons();
        stage.dispose();
        font.dispose();
        terminalTexture.dispose();
    }
}