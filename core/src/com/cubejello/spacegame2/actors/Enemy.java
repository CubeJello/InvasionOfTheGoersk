package com.cubejello.spacegame2.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.cubejello.spacegame2.box2d.EnemyUserData;
import com.cubejello.spacegame2.utils.BodyUtils;
import com.cubejello.spacegame2.utils.Constants;
import com.cubejello.spacegame2.utils.RandomUtils;

/**
 * Created by Jake on 4/14/2017.
 */

public class Enemy {

    private Texture greenEnemy;
    private Texture blueEnemy;
    private Texture redEnemy;
    private Sprite enemySprite;

    public Body body;

    private float speedMod = 1;
    private int enemyType;

    private boolean gameOver = false;
    private boolean destroyed;

    public float xCoord;

    private float timer;
    private boolean confused;

    public Enemy() {
        greenEnemy = new Texture(Gdx.files.internal("textures\\enemy.png"));
        blueEnemy = new Texture(Gdx.files.internal("textures\\enemy_blue.png"));
        redEnemy = new Texture(Gdx.files.internal("textures\\enemy_red.png"));
        enemySprite = new Sprite(greenEnemy);
        enemySprite.setPosition(Constants.ENEMY_X, 0);
        enemySprite.setSize(6.4f, 6.4f);
    }

    public void createEnemy(World world) {
        if(!gameOver) {
            enemyType = 0;
            EnemyUserData enemyUserData = new EnemyUserData(Constants.ENEMY_WIDTH, Constants.ENEMY_HEIGHT);
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(new Vector2(Constants.ENEMY_X, RandomUtils.pseudoRandomYCoord()));
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(Constants.ENEMY_WIDTH, Constants.ENEMY_HEIGHT);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = Constants.ENEMY_DENSITY;

            body = world.createBody(bodyDef);
            body.createFixture(fixtureDef);

            float pseudoRandom = RandomUtils.pseudoRandom(0, 20);
            if(pseudoRandom <= 10 && pseudoRandom > 5 && speedMod >= 2) {
                body.setLinearVelocity(Constants.ENEMY_SPEED.x * speedMod, -10 * speedMod);
                enemyType = 1;
            } else if(pseudoRandom <= 5 && speedMod >= 2) {
                body.setLinearVelocity(Constants.ENEMY_SPEED.x * speedMod, 10 * speedMod);
                enemyType = 2;
            } else if(pseudoRandom > 10 || speedMod < 2) {
                body.setLinearVelocity(Constants.ENEMY_SPEED.x * speedMod, 0);
                enemyType = 3;
            }

            body.resetMassData();
            body.setUserData(enemyUserData);
            shape.dispose();

            enemySprite.setOrigin(enemySprite.getWidth() / 2, enemySprite.getWidth() / 2);
            enemySprite.setRotation(0);

            //body.setAngularDamping(0.5f);

            destroyed = false;
        }
    }

    public void reset() {
        setTexture("green");
        destroyEnemy();
        resetSpeedMod();
        setGameOver(false);
    }

    public void confuse() {
        timer = 0;
        confused = true;
        float rand = RandomUtils.pseudoRandom(1, 20);
        if(rand > 10)
            body.setAngularVelocity(10);
        else if(rand < 11)
            body.setAngularVelocity(-10);
    }

    public boolean isOutOfBounds() {
        if(body != null && !BodyUtils.bodyInBounds(body))
            return true;
        else
            return false;
    }

    public void update() {
        updateSprite();

        if(enemyType == 1 && !confused || enemyType == 2 && !confused) {
            if (body.getPosition().y <= 4) {
                body.setLinearVelocity(Constants.ENEMY_SPEED.x * speedMod, 10 * speedMod);
            } else if (body.getPosition().y >= 56) {
                body.setLinearVelocity(Constants.ENEMY_SPEED.x * speedMod, -10 * speedMod);
            }
        }

        timer += Gdx.graphics.getDeltaTime();

        if(timer > 1 && confused) {
            body.setLinearVelocity(Constants.ENEMY_SPEED);
            timer = 0;
            confused = false;
        }

        if(!confused) {
            if(body.getAngle() > 0.1 || body.getAngle() < -0.1)
                body.setAngularVelocity(-body.getAngle() * 2);
            else
                body.setAngularVelocity(0);
        }
    }

    public void updateSprite() {
        enemySprite.rotate(body.getAngularVelocity());
        if(body.getAngularVelocity() == 0)
            enemySprite.setRotation(0);
        enemySprite.setPosition(body.getPosition().x - enemySprite.getWidth() / 2, body.getPosition().y - enemySprite.getHeight() / 2);
    }

    public Sprite getEnemySprite() {
        return enemySprite;
    }

    public void setTexture(String type) {
        if(type == "green") {
            enemySprite = new Sprite(greenEnemy);
            enemySprite.setSize(6.39f, 6.39f);
        } else if(type == "blue") {
            enemySprite = new Sprite(blueEnemy);
            enemySprite.setSize(6.39f, 6.39f);
        } else if(type == "red") {
            enemySprite = new Sprite(redEnemy);
            enemySprite.setSize(6.39f, 6.39f);
        }

        if(body != null)
            enemySprite.setPosition(body.getPosition().x - enemySprite.getWidth() / 2, body.getPosition().y - enemySprite.getHeight() / 2);
    }

    public void destroyEnemy() {
        xCoord = body.getPosition().x;
        if(body != null)
            body.getWorld().destroyBody(body);
        enemySprite.setPosition(Constants.ENEMY_X, 0);
        body = null;
        destroyed = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void increaseSpeedMod() {
        speedMod++;
    }

    public void resetSpeedMod() {
        speedMod = 1;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public void dispose() {
        greenEnemy.dispose();
        blueEnemy.dispose();
        redEnemy.dispose();
    }
}
