package com.cubejello.spacegame2.actors;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.cubejello.spacegame2.utils.Constants;

/**
 * Created by Jake on 4/11/2017.
 */

public class Ground {

    public Ground() {

    }

    public void createWalls(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(new Vector2(Constants.GROUND_X, Constants.GROUND_Y));
        Body body = world.createBody(bodyDef);
        PolygonShape ground = new PolygonShape();
        PolygonShape roof = new PolygonShape();
        ground.setAsBox(Constants.GROUND_WIDTH, Constants.GROUND_HEIGHT, new Vector2(0, -30f), 0);
        roof.setAsBox(Constants.GROUND_WIDTH, Constants.GROUND_HEIGHT, new Vector2(0, 30f), 0);
        body.createFixture(ground, Constants.GROUND_DENSITY);
        body.createFixture(roof, Constants.GROUND_DENSITY);
        ground.dispose();
    }

    public World getWorld() { return new World(new Vector2(0f, 0f), true); }
}
