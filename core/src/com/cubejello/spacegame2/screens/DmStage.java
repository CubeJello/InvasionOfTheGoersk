package com.cubejello.spacegame2.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.cubejello.spacegame2.actors.Asteroid;
import com.cubejello.spacegame2.actors.Bullet;
import com.cubejello.spacegame2.actors.Ground;
import com.cubejello.spacegame2.actors.SpaceShip;
import com.cubejello.spacegame2.utils.BodyUtils;
import com.cubejello.spacegame2.utils.Constants;

/**
 * Created by Jake on 5/10/2017.
 */

public class DmStage extends Stage implements ContactListener {
    public final float TIME_STEP = 1 / 300f;
    private float accumulator = 0f;

    private OrthographicCamera camera;
    private Box2DDebugRenderer renderer;

    private World world;
    private Texture background;
    private Sprite bgSprite;
    private SpriteBatch batch;
    private Hud hud;

    private boolean gameOver = false;
    private boolean paused = false;
    private boolean sound = true;
    private PauseScreen pauseScreen;

    private Ground ground;
    private Array<Asteroid> asteroids;
    private SpaceShip spaceShip1;
    private SpaceShip spaceShip2;
    private Array<Bullet> bullets;
    private float spaceShip1BulletTimer;
    private float spaceShip2BulletTimer;

    public DmStage() {
        super();
        renderer = new Box2DDebugRenderer();
        batch = new SpriteBatch();
        asteroids = new Array<Asteroid>();
        spaceShip1 = new SpaceShip();
        spaceShip2 = new SpaceShip();
        spaceShip2.setDmShip(true);
        ground = new Ground();
        bullets = new Array<Bullet>();
        setupHud();
        setupCamera();
        pauseScreen = new PauseScreen(batch);
        setupTextures();
        setupWorld();
    }

    private void setupHud() {
        hud = new Hud(batch, camera);
        hud.addMpReloadBar();
    }

    private void setupTextures() {
        background = new Texture(Gdx.files.internal("textures\\background.png"));
        bgSprite = new Sprite(background);
        bgSprite.setSize(100, 60);
    }

    private void setupCamera() {
        camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f);
        camera.update();
    }

    private void setupWorld() {
        setupWalls();
        setupSpaceShip();
        initAsteroids();
    }

    private void initAsteroids() {
        for(int i = 0; i < 3; i++) {
            Asteroid asteroid = new Asteroid();
            asteroid.createDm(world);
            asteroids.add(asteroid);
        }
    }

    private void setupWalls() {
        world = ground.getWorld();
        world.setContactListener(this);
        ground.createWalls(world);
    }

    private void setupSpaceShip() {
        if(!spaceShip1.isMarkedForDestroy()) {
            spaceShip1.createSpaceShip(world);
            spaceShip1.setTexture();
        }
        if(!spaceShip2.isMarkedForDestroy()) {
            spaceShip2.createSpaceShip(world);
            spaceShip2.setTexture();
        }
    }

    private void setupShip1Bullet() {
        if(!gameOver && !paused && spaceShip1.getCanShoot()) {
            resetSpaceShip1BulletTimer();
            spaceShip1.shoot();
            hud.setSpReloadBar(0);
            Bullet bullet = new Bullet();
            bullets.add(bullet);
            bullet.createBullet(world, new Vector2(spaceShip1.getPosition().x + spaceShip1.getWidth() + Constants.BULLET_RADIUS + 1,
                    spaceShip1.getPosition().y - 0.3f), spaceShip1.body.getLinearVelocity(), Constants.BULLET_IMPULSE);
            spaceShip1.body.applyLinearImpulse(Constants.BULLET_EFFECT, spaceShip1.body.getWorldCenter(), true);
            updateTextures();
            if(sound)
                bullet.playShoot();
        }
    }

    private void setupShip2Bullet() {
        if(!gameOver && !paused && spaceShip2.getCanShoot()) {
            resetSpaceShip2BulletTimer();
            spaceShip2.shoot();
            hud.setMpReloadBar(0);
            Bullet bullet = new Bullet();
            bullets.add(bullet);
            bullet.createBullet(world, new Vector2(spaceShip2.getPosition().x - spaceShip2.getWidth() - Constants.BULLET_RADIUS - 1,
                    spaceShip2.getPosition().y - 0.3f), spaceShip2.body.getLinearVelocity(), Constants.BULLET_EFFECT);
            spaceShip2.body.applyLinearImpulse(Constants.BULLET_IMPULSE, spaceShip2.body.getWorldCenter(), true);
            updateTextures();
            if(sound)
                bullet.playShoot();
        }
    }

    private void gameOver() {
        gameOver = true;
        spaceShip1.playExplosion();
        if(spaceShip1.getWinner()) {
            spaceShip2.setExplosionTexture();
        } else if(spaceShip2.getWinner()) {
            spaceShip1.setExplosionTexture();
        }
    }

    private void reset() {
        gameOver = false;

        for(Bullet bullet : bullets)
            bullet.reset();

        for(Asteroid asteroid : asteroids)
            asteroid.reset();
        initAsteroids();

        spaceShip1.setWinner(false);
        spaceShip2.setWinner(false);

        spaceShip1BulletTimer = 0.5f;
        spaceShip2BulletTimer = 0.5f;

        spaceShip1.reset();
        spaceShip2.reset();
        setupSpaceShip();
    }

    public void draw() {
        super.draw();
        //renderer.render(world, camera.combined);
    }

    @Override
    public void act(float delta) {
        if(!gameOver && !paused) {
            super.act(delta);

            accumulator += delta;

            while (accumulator >= delta) {
                world.step(TIME_STEP, 6, 2);
                accumulator -= TIME_STEP;
            }
        }
        update(delta);
    }

    private void resetSpaceShip1BulletTimer() {
        spaceShip1BulletTimer = 0;
    }

    private void resetSpaceShip2BulletTimer() {
        spaceShip2BulletTimer = 0;
    }


    private void updateBulletTimers(float delta) {
        if(spaceShip1BulletTimer < 0.5f)
            spaceShip1BulletTimer += delta;
        if(spaceShip2BulletTimer < 0.5f)
            spaceShip2BulletTimer += delta;
    }


    private void removeBodies() {
        for(Bullet bullet : bullets) {
            if(bullet.isDestroyed()) {
                bullet.remove();
            }
        }

        if(spaceShip1.isMarkedForDestroy())
            spaceShip1.remove();
        if(spaceShip2.isMarkedForDestroy()) {
            spaceShip2.remove();
            setupSpaceShip();
        }
    }

    private void update(float delta) {
        handleInput();

        updateTextures();

        removeBodies();

        updateHud(delta);

        for(Bullet bullet : bullets)
            bullet.update();
        for(Bullet bullet : bullets)
            bullet.update();

        updateSpaceShip(delta);

        if(paused)
            pauseScreen.update();
    }

    private void updateHud(float delta) {
        hud.updateDm();

        updateBulletTimers(delta);
        hud.setSpReloadBar(spaceShip1BulletTimer);
        hud.setMpReloadBar(spaceShip2BulletTimer);
    }

    private void updateSpaceShip(float delta) {
        if(spaceShip1.body != null) {
            spaceShip1.update(delta);
            if (!BodyUtils.bodyInBounds(spaceShip1.body)) {
                if (!spaceShip2.getWinner())
                    hud.increaseDmScore(2);
                spaceShip2.setWinner(true);
                gameOver();
            }
        }

        if(spaceShip2.body != null) {
            spaceShip2.update(delta);
            if (!BodyUtils.bodyInBounds(spaceShip2.body)) {
                if (!spaceShip1.getWinner())
                    hud.increaseDmScore(1);
                spaceShip1.setWinner(true);
                gameOver();
            }
        }
    }

    private void updateTextures() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        bgSprite.draw(batch);

        for(Bullet bullet : bullets) {
            if(bullet.body != null)
                bullet.getSprite().draw(batch);
        }

        spaceShip1.getSprite().draw(batch);
        spaceShip2.getSprite().draw(batch);

        for(Asteroid asteroid : asteroids) {
            asteroid.update(batch);
        }

        hud.update(batch);

        batch.end();
    }

    private void handleInput() {
        if(!gameOver && !paused) {
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
                spaceShip2.body.applyLinearImpulse(new Vector2(112.5f, 0), spaceShip2.body.getWorldCenter(), true);

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
                spaceShip2.body.applyLinearImpulse(new Vector2(-112.5f, 0), spaceShip2.body.getWorldCenter(), true);

            if (Gdx.input.isKeyPressed(Input.Keys.UP))
                spaceShip2.body.applyLinearImpulse(new Vector2(0, 112.5f), spaceShip2.body.getWorldCenter(), true);

            if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
                spaceShip2.body.applyLinearImpulse(new Vector2(0, -112.5f), spaceShip1.body.getWorldCenter(), true);

            if (Gdx.input.isKeyPressed(Input.Keys.D))
                spaceShip1.body.applyLinearImpulse(new Vector2(112.5f, 0), spaceShip1.body.getWorldCenter(), true);

            if (Gdx.input.isKeyPressed(Input.Keys.A))
                spaceShip1.body.applyLinearImpulse(new Vector2(-112.5f, 0), spaceShip1.body.getWorldCenter(), true);

            if (Gdx.input.isKeyPressed(Input.Keys.W))
                spaceShip1.body.applyLinearImpulse(new Vector2(0, 112.5f), spaceShip1.body.getWorldCenter(), true);

            if (Gdx.input.isKeyPressed(Input.Keys.S))
                spaceShip1.body.applyLinearImpulse(new Vector2(0, -112.5f), spaceShip1.body.getWorldCenter(), true);

            if (Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_LEFT))
                setupShip1Bullet();

            if (Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_RIGHT))
                setupShip2Bullet();
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
            reset();

        if(Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            if(!paused)
                paused = true;
            else if(paused)
                paused = false;
        }
    }

    @Override
    public void beginContact(Contact contact) {
        final Body a = contact.getFixtureA().getBody();
        final Body b = contact.getFixtureB().getBody();


        Gdx.app.postRunnable(new Runnable() {

            @Override
            public void run() {
                if (BodyUtils.bodyIsBullet(a) && BodyUtils.bodyIsSpaceShip(b) || BodyUtils.bodyIsSpaceShip(a) && BodyUtils.bodyIsBullet(b)) {

                    if(b == spaceShip1.body || a == spaceShip1.body) {
                        spaceShip2.setWinner(true);
                        hud.increaseDmScore(2);
                        gameOver();
                    } else if(b == spaceShip2.body || a == spaceShip2.body) {
                        hud.increaseDmScore(1);
                        spaceShip1.setWinner(true);
                        gameOver();
                    }
                }
            }
        });
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    public void dispose() {
        spaceShip1.dispose();
        spaceShip2.dispose();
        background.dispose();
        hud.dispose();
        batch.dispose();
        for(Bullet bullet : bullets)
            bullet.dispose();
        for(Asteroid asteroid : asteroids)
            asteroid.dispose();
    }
}
