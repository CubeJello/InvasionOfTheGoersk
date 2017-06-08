package com.cubejello.spacegame2.utils;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Jake on 4/11/2017.
 */

public class Constants {

    public static final int APP_WIDTH = 800;
    public static final int APP_HEIGHT = 480;
    public static final int VIEWPORT_WIDTH = 100;
    public static final int VIEWPORT_HEIGHT = 60;

    public static final float GROUND_X = 50;
    public static final float GROUND_Y = 30;
    public static final float GROUND_WIDTH = 102;
    public static final float GROUND_HEIGHT = 0.5f;
    public static final float GROUND_DENSITY = 0;

    public static final float SPACESHIP_NACELLE_HEIGHT = 3.25f;
    public static final float SPACESHIP_NACELLE_WIDTH = 3;
    public static final float SPACESHIP_BRIDGE_WIDTH = 2.8f;
    public static final float SPACESHIP_BRIDGE_HEIGHT = 1.5f;
    public static final float SPACESHIP_X = 25;
    public static final float SPACESHIP_Y = 30;
    public static final float SPACESHIP2_X = 25;
    public static final float DM_SHIP_X = 80;
    public static final float SPACESHIP2_Y = 20;
    public static final float SPACESHIP_DENSITY = 2f;

    public static final float BULLET_RADIUS = 0.5625f;
    public static final float BULLET_DENSITY = 2f;
    public static final Vector2 BULLET_IMPULSE = new Vector2(187.5f, 0f);
    public static final Vector2 BULLET_EFFECT = new Vector2(-187.5f, 0);

    public static final float ENEMY_DENSITY = 4;
    public static final float ENEMY_WIDTH = 3f;
    public static final float ENEMY_HEIGHT = 3f;
    public static final float ENEMY_X = 103 + ENEMY_WIDTH / 2;
    public static final Vector2 ENEMY_SPEED = new Vector2(-5f, 0f);

    public static final Vector2 ASTEROID_SPEED = new Vector2(-5f, 0);
}
