package com.cubejello.spacegame2.box2d;

import com.cubejello.spacegame2.enums.UserDataType;

/**
 * Created by Jake on 4/14/2017.
 */

public class BulletUserData extends UserData {

    public BulletUserData(float width, float height) {
        super(width, height);
        userDataType = UserDataType.BULLET;
    }
}