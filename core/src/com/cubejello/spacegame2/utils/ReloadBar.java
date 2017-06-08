package com.cubejello.spacegame2.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Created by Jake on 5/17/2017.
 */

public class ReloadBar {
    private ProgressBar progressBar;

    public ReloadBar(boolean isMultiplayer) {
        Skin skin = new Skin();
        Pixmap pixmap = new Pixmap(16, 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));

        TextureRegionDrawable textureBar1 = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("textures\\progress_bar1.png"))));
        TextureRegionDrawable textureBar2 = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("textures\\progress_bar2.png"))));
        ProgressBar.ProgressBarStyle spStyle = new ProgressBar.ProgressBarStyle(skin.newDrawable("white", Color.WHITE), textureBar1);
        ProgressBar.ProgressBarStyle mpStyle = new ProgressBar.ProgressBarStyle(skin.newDrawable("white", Color.WHITE), textureBar2);
        spStyle.knobBefore = spStyle.knob;
        mpStyle.knobBefore = mpStyle.knob;

        if(!isMultiplayer) {
            progressBar = new ProgressBar(0, 0.5f, 0.005f, true, spStyle);
            progressBar.setPosition(10, 10);
        } else if(isMultiplayer) {
            progressBar = new ProgressBar(0, 0.5f, 0.005f, true, mpStyle);
            progressBar.setPosition(790 - progressBar.getWidth(), 10);
        }
        progressBar.setSize(progressBar.getPrefWidth(), 100);
        progressBar.setValue(0.5f);
    }

    public void add(Stage stage) {
        stage.addActor(progressBar);
    }

    public void set(float value) {
        progressBar.setValue(value);
    }
}
