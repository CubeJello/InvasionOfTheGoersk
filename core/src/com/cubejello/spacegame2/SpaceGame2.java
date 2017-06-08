package com.cubejello.spacegame2;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.cubejello.spacegame2.screens.MenuScreen;

public class SpaceGame2 extends Game {

	private Music music;

	private boolean sound = true;

	@Override
	public void create () {
		music = Gdx.audio.newMusic(Gdx.files.internal("sfx\\music.ogg"));
		music.setLooping(true);
		music.play();
		this.setScreen(new MenuScreen(this));
	}

	@Override
	public void render() {
		super.render();
		if(Gdx.input.isKeyJustPressed(Input.Keys.M)) {
			if(sound) {
				sound = false;
				music.pause();
			} else if (!sound) {
				sound = true;
				music.play();
			}
		}
	}
}
