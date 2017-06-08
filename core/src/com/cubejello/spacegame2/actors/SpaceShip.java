package com.cubejello.spacegame2.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.cubejello.spacegame2.box2d.SpaceShipUserData;
import com.cubejello.spacegame2.utils.Constants;

/**
 * Created by Jake on 4/11/2017.
 */

public class SpaceShip {

    private Texture texture;
    private Texture explosionTexture;
    public Body body;
    private Sprite sprite;

    private Sound recharging;
    private Sound explosion;
    private boolean isExplosionPlayed = false;

    private boolean coopShip;
    private boolean dmShip;

    private boolean winner;
    private boolean sound = true;
    private boolean canShoot = true;

    private boolean markedForDestroy = false;

    private float timer;

    public SpaceShip() {
        texture = new Texture(Gdx.files.internal("textures\\space_ship.png"));
        explosionTexture = new Texture(Gdx.files.internal("textures\\ship_explosion.png"));
        sprite = new Sprite(texture);
        sprite.setSize(12, 7);
        explosion = Gdx.audio.newSound(Gdx.files.internal("sfx\\spaceship_explosion.ogg"));
        recharging = Gdx.audio.newSound(Gdx.files.internal("sfx\\recharging.ogg"));
    }

    public void createSpaceShip(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        if(coopShip)
            bodyDef.position.set(new Vector2(Constants.SPACESHIP2_X, Constants.SPACESHIP2_Y));
        else if(dmShip)
            bodyDef.position.set(new Vector2(Constants.DM_SHIP_X, Constants.SPACESHIP_Y));
        else
            bodyDef.position.set(new Vector2(Constants.SPACESHIP_X, Constants.SPACESHIP_Y));

        PolygonShape nacelleShape = new PolygonShape();
        PolygonShape bridgeShape = new PolygonShape();
        if(!dmShip) {
            nacelleShape.setAsBox(Constants.SPACESHIP_NACELLE_WIDTH, Constants.SPACESHIP_NACELLE_HEIGHT, new Vector2(-2.5f, -0.3f), 0);
            bridgeShape.setAsBox(Constants.SPACESHIP_BRIDGE_WIDTH, Constants.SPACESHIP_BRIDGE_HEIGHT, new Vector2(3.2f, -0.3f), 0);
        } else if(dmShip) {
            nacelleShape.setAsBox(Constants.SPACESHIP_NACELLE_WIDTH, Constants.SPACESHIP_NACELLE_HEIGHT, new Vector2(2.5f, -0.3f), 0);
            bridgeShape.setAsBox(Constants.SPACESHIP_BRIDGE_WIDTH, Constants.SPACESHIP_BRIDGE_HEIGHT, new Vector2(-3.2f, -0.3f), 0);
        }
        body = world.createBody(bodyDef);
        body.createFixture(bridgeShape, Constants.SPACESHIP_DENSITY);
        body.createFixture(nacelleShape, Constants.SPACESHIP_DENSITY);
        body.setFixedRotation(true);
        body.resetMassData();
        body.setUserData(new SpaceShipUserData(getWidth(), Constants.SPACESHIP_NACELLE_HEIGHT));
        nacelleShape.dispose();
        bridgeShape.dispose();
        isExplosionPlayed = false;
    }

    public boolean getCanShoot() {
        return canShoot;
    }

    public void setSound(boolean sound) {
        this.sound = sound;
    }

    public void update(float delta) {
        sprite.setPosition(getPosition().x - sprite.getWidth() / 2, getPosition().y - sprite.getHeight() / 2);
        if(timer < 0.5)
            timer += delta;
        if(timer >= 0.5)
            canShoot = true;
    }

    public void playRecharge(float volume) {
        if(!canShoot)
            recharging.play(volume);
    }

    public void shoot() {
        timer = 0;
        canShoot = false;
    }

    public void reset() {
        if(body != null) {
            markedForDestroy = true;
            remove();
            timer = 0.5f;
            canShoot = true;
        }
    }

    public void remove() {
        body.getWorld().destroyBody(body);
        markedForDestroy = false;
    }

    public boolean isMarkedForDestroy() {
        return markedForDestroy;
    }

    public void playExplosion() {
        if(!isExplosionPlayed && sound) {
            explosion.play(0.6f);
            isExplosionPlayed = true;
        }
    }

    public void setExplosionTexture() {
        sprite = new Sprite(explosionTexture);
        sprite.setSize(12, 7);
        sprite.setPosition(getPosition().x - sprite.getWidth() / 2, getPosition().y - sprite.getHeight() / 2);
        if(dmShip)
            sprite.flip(true, false);
    }

    public void setTexture() {
        sprite = new Sprite(texture);
        sprite.setSize(12, 7);
        if(dmShip)
            sprite.flip(true, false);
        sprite.setPosition(getPosition().x - sprite.getWidth() / 2, getPosition().y - sprite.getHeight() / 2);
    }

    public Sprite getSprite() {
        return sprite;
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public float getWidth() {
        return Constants.SPACESHIP_NACELLE_WIDTH + Constants.SPACESHIP_BRIDGE_WIDTH;
    }

    public void setCoopShip(boolean i) {
        coopShip = i;
        if(i) {
            texture = new Texture(Gdx.files.internal("textures\\space_ship2.png"));
            explosionTexture = new Texture(Gdx.files.internal("textures\\blue_ship_explosion.png"));
        }
    }

    public void setDmShip(boolean dm) {
        dmShip = dm;
        if(dm) {
            texture = new Texture(Gdx.files.internal("textures\\space_ship2.png"));
            explosionTexture = new Texture(Gdx.files.internal("textures\\blue_ship_explosion.png"));
        }
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    public boolean getWinner() {
        return winner;
    }

    public float getTimer() {
        return timer;
    }

    public void dispose() {
        texture.dispose();
        explosionTexture.dispose();
        explosion.dispose();
    }

}
