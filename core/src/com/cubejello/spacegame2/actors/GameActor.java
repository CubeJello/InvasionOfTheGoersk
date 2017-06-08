package com.cubejello.spacegame2.actors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.cubejello.spacegame2.box2d.UserData;

/**
 * Created by Jake on 4/11/2017.
 */

public abstract class GameActor extends Actor{

    public Body body;
    protected UserData userData;

    public GameActor(Body body) {
        this.body = body;
        this.userData = (UserData) body.getUserData();
    }

    public UserData getUserData() { return userData;}

    public Vector2 getPosition() {
        return body.getPosition();
    }

    //public float getX() { return body.getPosition().x; }

    public float getY() { return body.getPosition().y; }
}
