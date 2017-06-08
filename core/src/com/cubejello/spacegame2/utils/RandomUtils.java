package com.cubejello.spacegame2.utils;

import java.util.Random;

/**
 * Created by Jake on 5/26/2017.
 */

public class RandomUtils {

    public static float pseudoRandomYCoord() {
        Random rand = new Random();

        return rand.nextFloat() * (52 - 5) + 5;
    }

    public static float pseudoRandomXCoord() {
        Random rand = new Random();

        return rand.nextFloat() * (75 - 30) + 30;
    }

    public static float pseudoRandomSpeed(float min, float max) {
        Random rand = new Random();

        return rand.nextFloat() * (max - min) + min;
    }

    public static float pseudoRandomAngle() {
        Random rand = new Random();

        return rand.nextFloat() * (359 - 1) + 1;
    }

    public static float pseudoRandom(float min, float max) {
        Random rand = new Random();

        return rand.nextFloat() * (max - min) + min;
    }
}
