package com.cubejello.spacegame2.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cubejello.spacegame2.SpaceGame2;
import com.cubejello.spacegame2.utils.Constants;

public class DesktopLauncher {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Constants.APP_WIDTH;
		config.height = Constants.APP_HEIGHT;
		config.addIcon("textures\\icon.png", Files.FileType.Internal);
		config.title = "Invasion of The Goersk v0.4";
		new LwjglApplication(new SpaceGame2(), config);
	}
}
