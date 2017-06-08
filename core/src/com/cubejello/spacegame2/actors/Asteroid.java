package com.cubejello.spacegame2.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.cubejello.spacegame2.utils.BodyUtils;
import com.cubejello.spacegame2.utils.RandomUtils;

/**
 * Created by Jake on 5/26/2017.
 */

public class Asteroid {

    private Array<Body> bodies;
    public Body body;

    private Texture texture;
    private Texture texture1;
    private Texture texture2;
    private Sprite sprite;
    private Array<Texture> textures;

    private float spin;

    public Asteroid() {
        bodies = new Array<Body>();

        spin = RandomUtils.pseudoRandom(-1, 1);

        initTextures();
        initSprite();
    }

    private void initTextures() {
        texture = new Texture(Gdx.files.internal("textures\\asteroid.png"));
        texture1 = new Texture(Gdx.files.internal("textures\\asteroid1.png"));
        texture2 = new Texture(Gdx.files.internal("textures\\asteroid2.png"));
        textures = new Array<Texture>();
        textures.add(texture);
        textures.add(texture1);
        textures.add(texture2);
    }

    private void initSprite() {
        sprite = new Sprite(textures.random());
        sprite.setSize(4 * 2, 4 * 2);
        sprite.setOrigin(sprite.getWidth() / 2, sprite.getWidth() / 2);
        sprite.rotate(RandomUtils.pseudoRandom(0, 1000));
    }

    public void create(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(new Vector2(104, RandomUtils.pseudoRandomYCoord()));
        bodyDef.linearVelocity.set(new Vector2(-RandomUtils.pseudoRandomSpeed(12, 5), RandomUtils.pseudoRandomSpeed(-4, 4)));
        bodyDef.angularVelocity = RandomUtils.pseudoRandom(1, 15);
        CircleShape shape = new CircleShape();
        shape.setRadius(4);
        body = world.createBody(bodyDef);
        bodies.add(body);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 50;
        fixtureDef.friction = 0;
        fixtureDef.restitution = 0.2f;
        body.createFixture(fixtureDef);
        body.setFixedRotation(true);
        body.resetMassData();
        shape.dispose();
    }

    public void createDm(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(new Vector2(RandomUtils.pseudoRandomXCoord(), RandomUtils.pseudoRandomYCoord()));
        CircleShape shape = new CircleShape();
        shape.setRadius(4);
        body = world.createBody(bodyDef);
        bodies.add(body);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 50;
        fixtureDef.friction = 0;
        fixtureDef.restitution = 0.2f;
        body.createFixture(fixtureDef);
        body.setFixedRotation(true);
        body.resetMassData();
        shape.dispose();
    }

    public void update(SpriteBatch batch) {
        if(body != null) {
            sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getWidth() / 2);
            sprite.draw(batch);
            sprite.rotate(spin);
        }
    }

    public void remove() {
        if(body != null) {
            if (!BodyUtils.bodyInBounds(body) && body.getPosition().x < -4) {
                body.getWorld().destroyBody(body);
                body = null;
            }
        }
    }

    public void reset() {
        if(body != null) {
            body.getWorld().destroyBody(body);
            body = null;
        }
    }

    public void dispose() {
        for(Texture texture : textures) {
            texture.dispose();
        }
    }
}
