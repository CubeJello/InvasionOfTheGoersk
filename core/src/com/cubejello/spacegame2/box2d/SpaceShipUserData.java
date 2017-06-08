package com.cubejello.spacegame2.box2d;

import com.badlogic.gdx.math.Vector2;
import com.cubejello.spacegame2.enums.UserDataType;

/**
 * Created by Jake on 4/11/2017.
 */

public class SpaceShipUserData extends UserData {

    private Vector2 linearVelocity;

    public SpaceShipUserData(float width, float height) {
        super(width, height);
        userDataType = UserDataType.SPACESHIP;
    }

    public Vector2 getLinearVelocity() {
        return linearVelocity;
    }
}
