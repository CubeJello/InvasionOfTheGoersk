package com.cubejello.spacegame2.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.cubejello.spacegame2.box2d.BulletUserData;
import com.cubejello.spacegame2.utils.BodyUtils;
import com.cubejello.spacegame2.utils.Constants;

/**
 * Created by Jake on 4/12/2017.
 */

public class Bullet {

    public boolean destroyed;
    public boolean created = false;
    private boolean sound = true;

    private Sound shoot;
    private Sound explosion;

    private Texture texture;
    private Sprite sprite;

    public Body body;

    public Bullet() {
        shoot = Gdx.audio.newSound(Gdx.files.internal("sfx\\sfx_shoot.ogg"));
        explosion = Gdx.audio.newSound(Gdx.files.internal("sfx\\enemy_explosion.ogg"));
        texture = new Texture(Gdx.files.internal("textures\\bullet.png"));
        sprite = new Sprite(texture);
        sprite.setSize(Constants.BULLET_RADIUS * 2.31f, Constants.BULLET_RADIUS * 2.31f);
    }

    public void hit() {
        destroyed = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public boolean isCreated() {
        return  created;
    }

    public void createBullet(World world, Vector2 position, Vector2 shipSpeed, Vector2 impulse) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position);
        bodyDef.linearVelocity.set(shipSpeed);
        body = world.createBody(bodyDef);
        body.isBullet();
        CircleShape shape = new CircleShape();
        shape.setRadius(Constants.BULLET_RADIUS);
        body.createFixture(shape, Constants.BULLET_DENSITY);
        body.resetMassData();
        body.setUserData(new BulletUserData(Constants.BULLET_RADIUS, Constants.BULLET_RADIUS));
        body.applyLinearImpulse(impulse, body.getPosition(), true);
        shape.dispose();
        created = true;
        sprite.setPosition(getPosition().x - sprite.getWidth() / 2, getPosition().y - sprite.getHeight() / 2);
    }

    public void playShoot() {
        shoot.play(0.7f);
    }

    public void playExplosion() {
        explosion.play(0.7f);
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void update() {
        if(body != null) {
            if (isCreated() && !BodyUtils.bodyInBounds(body))
                destroy();



            sprite.setPosition(getPosition().x - sprite.getWidth() / 2, getPosition().y - sprite.getHeight() / 2);
        }
    }

    public void checkIfBodyIsTooSlow() {
        if(body.getLinearVelocity().x + body.getLinearVelocity().y < 2)
            destroy();
    }

    public void setSound(boolean sound) {
        this.sound = sound;
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public void destroy() {
        if(body != null) {
            destroyed = true;
        }
    }

    public void reset() {
        if(body != null) {
            destroyed = true;
        }
    }

    public void remove() {
        body.getWorld().destroyBody(body);
        update();
        body = null;
        created = false;
        destroyed = false;
    }

    public void dispose() {
        texture.dispose();
        explosion.dispose();
        shoot.dispose();
    }
}
