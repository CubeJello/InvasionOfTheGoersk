package com.cubejello.spacegame2.box2d;

import com.cubejello.spacegame2.enums.UserDataType;

/**
 * Created by Jake on 4/11/2017.
 */

public abstract class UserData {
    protected UserDataType userDataType;
    protected float width;
    protected float height;

    public UserData() {

    }

    public UserData(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public UserDataType getUserDataType() {
        return userDataType;
    }
}
