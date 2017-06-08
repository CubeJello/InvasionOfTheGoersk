package com.cubejello.spacegame2.utils;

import com.badlogic.gdx.physics.box2d.Body;
import com.cubejello.spacegame2.box2d.UserData;
import com.cubejello.spacegame2.enums.UserDataType;

/**
 * Created by Jake on 4/11/2017.
 */

public class BodyUtils {

    public static boolean bodyInBounds(Body body) {

        if(body.getPosition().x > 105 || body.getPosition().x < -1)
            return false;

        return true;
    }

    public static boolean bodyIsEnemy(Body body) {
        UserData userData = (UserData) body.getUserData();

        return userData != null && userData.getUserDataType() == UserDataType.ENEMY;
    }

    public static boolean bodyIsSpaceShip(Body body) {
        UserData userData = (UserData) body.getUserData();

        return userData != null && userData.getUserDataType() == UserDataType.SPACESHIP;
    }

    public static boolean bodyIsBullet(Body body) {
        UserData userData = (UserData) body.getUserData();

        return userData != null && userData.getUserDataType() == UserDataType.BULLET;
    }
}