package com.cubejello.spacegame2.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cubejello.spacegame2.actors.Asteroid;
import com.cubejello.spacegame2.actors.Bullet;
import com.cubejello.spacegame2.actors.Enemy;
import com.cubejello.spacegame2.actors.Ground;
import com.cubejello.spacegame2.actors.SpaceShip;
import com.cubejello.spacegame2.utils.BodyUtils;
import com.cubejello.spacegame2.utils.Constants;

/**
 * Created by Jake on 4/11/2017.
 */

public class GameStage extends Stage implements ContactListener {

    private Texture background;
    private Sprite bgSprite;

    private World world;
    private Ground ground;
    private Array<Bullet> bullets;
    private Enemy enemy;
    private float enemySpeedChangeMarker = 10;
    private SpaceShip spaceShip;
    private SpaceShip spaceShip2;
    private Array<Asteroid> asteroids;

    private SpriteBatch batch;

    private Hud hud;

    private boolean gameOver = false;
    public final float TIME_STEP = 1 / 300f;
    private float accumulator = 0f;
    private float asteroidTimer = 0;

    private Viewport viewport;
    private OrthographicCamera camera;
    private Box2DDebugRenderer renderer;
    private boolean debug = false;

    private PauseScreen pauseScreen;
    private boolean paused = false;
    private boolean coop;
    private boolean sound = true;

    private float spaceShipBulletTimer;
    private float spaceShip2BulletTimer;

    private Vector3 touchPoint;

    private Controller controller;

    public GameStage(SpriteBatch batch, Game game) {
        super();
        this.batch = batch;
        asteroids = new Array<Asteroid>();
        bullets = new Array<Bullet>();
        spaceShip = new SpaceShip();
        spaceShip2 = new SpaceShip();
        enemy = new Enemy();
        ground = new Ground();
        renderer = new Box2DDebugRenderer();
        pauseScreen = new PauseScreen(batch);
        setupCamera();
        hud = new Hud(batch, camera);
        setupWorld();
        setupControl();
    }

    public void setCoop(boolean i) {
        coop = i;
        setupSpaceShip();
        hud.setCoop(coop);
    }

    private void setupWorld() {
        setupWalls();
        background = new Texture(Gdx.files.internal("textures\\background.png"));
        bgSprite = new Sprite(background);
        bgSprite.setSize(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        setupEnemy();
        //asteroid = new Asteroid();
    }

    private void setupControl() {
        if(Gdx.app.getType() == Application.ApplicationType.Desktop)
            controller = Controllers.getControllers().first();

        touchPoint = new Vector3();
        Gdx.input.setInputProcessor(this);
    }

    private void setupSpaceShip() {
        spaceShip.createSpaceShip(world);
        spaceShip.setTexture();
        if(coop) {
            spaceShip2.setCoopShip(true);
            spaceShip2.createSpaceShip(world);
            spaceShip2.setTexture();
        }
    }

    private void setupBullet() {
        if(!gameOver && !paused && spaceShip.getCanShoot()) {
            resetSpaceShipBulletTimer();
            spaceShip.shoot();
            hud.setSpReloadBar(0);
            Bullet bullet = new Bullet();
            bullets.add(bullet);
            bullet.createBullet(world, new Vector2(spaceShip.getPosition().x + spaceShip.getWidth() + Constants.BULLET_RADIUS,
                    spaceShip.getPosition().y - 0.3f), spaceShip.body.getLinearVelocity(), Constants.BULLET_IMPULSE);
            spaceShip.body.applyLinearImpulse(Constants.BULLET_EFFECT, spaceShip.body.getWorldCenter(), true);
            updateTextures();
            if (sound)
                bullet.playShoot();
        } else if(!gameOver && !paused && !spaceShip.getCanShoot() && sound && spaceShip.getTimer() > 0.2)
            spaceShip.playRecharge(0.4f);
    }

    private void setupAftBullet() {
        if(!gameOver && !paused && spaceShip.getCanShoot()) {
            resetSpaceShipBulletTimer();
            spaceShip.shoot();
            hud.setSpReloadBar(0);
            Bullet bullet = new Bullet();
            bullets.add(bullet);
            bullet.createBullet(world, new Vector2(spaceShip.getPosition().x - spaceShip.getWidth() - Constants.BULLET_RADIUS,
                    spaceShip.getPosition().y), spaceShip.body.getLinearVelocity(), Constants.BULLET_EFFECT);
            spaceShip.body.applyLinearImpulse(Constants.BULLET_IMPULSE, spaceShip.body.getWorldCenter(), true);
            updateTextures();
            if(sound)
                bullet.playShoot();
        }  else if(!gameOver && !paused && !spaceShip.getCanShoot() && sound && spaceShip.getTimer() > 0.2)
            spaceShip.playRecharge(0.4f);
    }

    private void setupAftBullet2() {
        if(!gameOver && !paused && spaceShip2.getCanShoot()) {
            resetSpaceShip2BulletTimer();
            spaceShip2.shoot();
            Bullet bullet = new Bullet();
            bullets.add(bullet);
            bullet.createBullet(world, new Vector2(spaceShip2.getPosition().x - spaceShip.getWidth() - Constants.BULLET_RADIUS,
                    spaceShip2.getPosition().y - 0.3f), spaceShip2.body.getLinearVelocity(), Constants.BULLET_EFFECT);
            spaceShip2.body.applyLinearImpulse(Constants.BULLET_IMPULSE, spaceShip2.body.getWorldCenter(), true);
            updateTextures();
            if(sound)
                bullet.playShoot();
        }
    }

    private void setupBullet2() {
        if(!gameOver && !paused && spaceShip2.getCanShoot()) {
            resetSpaceShip2BulletTimer();
            spaceShip2.shoot();
            Bullet bullet = new Bullet();
            bullets.add(bullet);
            bullet.createBullet(world, new Vector2(spaceShip2.getPosition().x + spaceShip.getWidth() + Constants.BULLET_RADIUS,
                    spaceShip2.getPosition().y - 0.3f), spaceShip2.body.getLinearVelocity(), Constants.BULLET_IMPULSE);
            spaceShip2.body.applyLinearImpulse(Constants.BULLET_EFFECT, spaceShip2.body.getWorldCenter(), true);
            updateTextures();
            if(sound)
                bullet.playShoot();
        }
    }

    private void setupEnemy() {
        if(!gameOver) {
            enemy.createEnemy(world);
        }
    }

    private void setupWalls() {
        world = ground.getWorld();
        world.setContactListener(this);
        ground.createWalls(world);
    }

    private void setupCamera() {
        camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        viewport = new FitViewport(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT, camera);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0f);
        camera.update();
    }

    public void draw() {
        super.draw();

        if(debug)
            renderer.render(world, camera.combined);
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
        removeBodies();
        handleInput();
        update(delta);
    }

    private void resetSpaceShipBulletTimer() {
        spaceShipBulletTimer = 0;
    }

    private void resetSpaceShip2BulletTimer() {
        spaceShip2BulletTimer = 0;
    }

    private void updateBulletTimers(float delta) {
        if(!paused && !gameOver) {
            if (spaceShipBulletTimer < 0.5f)
                spaceShipBulletTimer += delta;
            if (spaceShip2BulletTimer < 0.5f)
                spaceShip2BulletTimer += delta;
        }
    }

    private void removeBodies() {
        for (Bullet bullet : bullets) {
            if (bullet.isDestroyed()) {
                bullet.remove();
            }
        }
        for(Asteroid asteroid : asteroids) {
            asteroid.remove();
        }
    }

    private void updateHud(float delta) {
        hud.updateHighScore();
        hud.updateInvasion(delta);

        hud.setSpReloadBar(spaceShipBulletTimer);
        hud.setMpReloadBar(spaceShip2BulletTimer);

        updateBulletTimers(delta);
    }

    private void update(float delta) {
        updateTextures();
        updateHud(delta);
        updateAsteroid();

        if(!gameOver) {
            updateEnemy();
            updateSpaceShip();

            for(Bullet bullet : bullets) {
                if(bullet.body != null)
                    bullet.update();
            }

            if(!paused) {
                spaceShip.update(delta);
                if (spaceShip2.body != null)
                    spaceShip2.update(delta);
            }

            enemy.update();


        }
    }

    private void updateAsteroid() {
        if(!paused)
            asteroidTimer += Gdx.graphics.getDeltaTime();

        if(asteroidTimer >= 10) {
            Asteroid asteroid = new Asteroid();
            asteroid.create(world);
            asteroids.add(asteroid);
            asteroidTimer = 0;
        }
    }

    private void updateTextures() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        bgSprite.draw(batch);

        for(Asteroid asteroid : asteroids) {
            asteroid.update(batch);
        }

        for (Bullet bullet : bullets) {
            if(bullet.body != null)
                bullet.getSprite().draw(batch);
        }

        enemy.getEnemySprite().draw(batch);

        spaceShip.getSprite().draw(batch);
        if(coop)
            spaceShip2.getSprite().draw(batch);

        hud.update(batch);

        if(paused)
            pauseScreen.update();

        batch.end();
    }

    private void gameOver() {
        touchPoint = new Vector3();
        gameOver = true;
        updateScore();
        hud.gameover();
        spaceShip.setExplosionTexture();
        if(spaceShip2.body != null)
        spaceShip2.setExplosionTexture();
        spaceShip.playExplosion();
        enemy.setGameOver(true);
    }

    private void updateSpaceShip() {
        if(!BodyUtils.bodyInBounds(spaceShip.body))
            gameOver();

        if(spaceShip2.body != null && !BodyUtils.bodyInBounds(spaceShip2.body))
            gameOver();

        if(Gdx.app.getType() == Application.ApplicationType.Desktop)
            spaceShip.body.applyLinearImpulse(new Vector2(controller.getAxis(1) * 112.5f, (controller.getAxis(0) - controller.getAxis(0) * 2) * 112.5f), spaceShip.getPosition(), true);
    }

    private void updateEnemy() {
        if(!enemy.isDestroyed() && enemy.isOutOfBounds() && enemy.body.getPosition().x < 50)
            gameOver();

        if(enemy.isOutOfBounds() && enemy.body.getLinearVelocity().x > 0) {
            enemy.destroyEnemy();
            setupEnemy();
        }

        if(enemy.isDestroyed()) {
            setupEnemy();
            updateScore();
        }

        if(hud.getScore() == enemySpeedChangeMarker-1) {
            enemySpeedChangeMarker += 10;
            enemy.increaseSpeedMod();
        }

        if(hud.getScore() == 20)
            enemy.setTexture("blue");
        else if(hud.getScore() == 40)
            enemy.setTexture("red");
    }

     private void updateScore() {
        if(BodyUtils.bodyInBounds(spaceShip.body) && !gameOver) {
            hud.increaseScore();
        }
    }

    private void reset() {
        gameOver = false;

        spaceShipBulletTimer = 0.5f;

        hud.resetScore();

        for(Bullet bullet : bullets)
            bullet.reset();

        enemy.reset();
        enemySpeedChangeMarker = 10;
        setupEnemy();

        for(Asteroid asteroid : asteroids) {
            asteroid.reset();
        }

        spaceShip.reset();
        spaceShip2.reset();
        setupSpaceShip();
    }

    private void handleInput() {
        if(!coop && !gameOver && !paused) {
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D) || hud.isRightButtonClicked(touchPoint) && Gdx.input.isTouched())
                spaceShip.body.applyLinearImpulse(new Vector2(112.5f, 0), spaceShip.body.getWorldCenter(), true);

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A) || hud.isLeftButtonClicked(touchPoint) && Gdx.input.isTouched())
                spaceShip.body.applyLinearImpulse(new Vector2(-112.5f, 0), spaceShip.body.getWorldCenter(), true);

            if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W) || hud.isUpButtonClicked(touchPoint) && Gdx.input.isTouched())
                spaceShip.body.applyLinearImpulse(new Vector2(0, 112.5f), spaceShip.body.getWorldCenter(), true);

            if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S) || hud.isDownButtonClicked(touchPoint) && Gdx.input.isTouched())
                spaceShip.body.applyLinearImpulse(new Vector2(0, -112.5f), spaceShip.body.getWorldCenter(), true);

            if(Gdx.app.getType() == Application.ApplicationType.Desktop) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_RIGHT) || controller.getButton(7))
                    setupBullet();
            } else if(Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_RIGHT))
                setupBullet();

            if(Gdx.app.getType() == Application.ApplicationType.Desktop) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT) || controller.getButton(6))
                    setupAftBullet();
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT))
                setupAftBullet();

        } else if(coop && !gameOver && !paused) {
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
                spaceShip.body.applyLinearImpulse(new Vector2(112.5f, 0), spaceShip.body.getWorldCenter(), true);

            if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
                spaceShip.body.applyLinearImpulse(new Vector2(-112.5f, 0), spaceShip.body.getWorldCenter(), true);

            if (Gdx.input.isKeyPressed(Input.Keys.UP))
                spaceShip.body.applyLinearImpulse(new Vector2(0, 112.5f), spaceShip.body.getWorldCenter(), true);

            if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
                spaceShip.body.applyLinearImpulse(new Vector2(0, -112.5f), spaceShip.body.getWorldCenter(), true);

            if (Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_RIGHT))
                setupBullet();

            if(Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_RIGHT))
                setupAftBullet();

            if (Gdx.input.isKeyPressed(Input.Keys.D))
                spaceShip2.body.applyLinearImpulse(new Vector2(112.5f, 0), spaceShip.body.getWorldCenter(), true);

            if (Gdx.input.isKeyPressed(Input.Keys.A))
                spaceShip2.body.applyLinearImpulse(new Vector2(-112.5f, 0), spaceShip.body.getWorldCenter(), true);

            if (Gdx.input.isKeyPressed(Input.Keys.W))
                spaceShip2.body.applyLinearImpulse(new Vector2(0, 112.5f), spaceShip.body.getWorldCenter(), true);

            if (Gdx.input.isKeyPressed(Input.Keys.S))
                spaceShip2.body.applyLinearImpulse(new Vector2(0, -112.5f), spaceShip.body.getWorldCenter(), true);

            if (Gdx.input.isKeyJustPressed(Input.Keys.CONTROL_LEFT))
                setupBullet2();

            if(Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT))
                setupAftBullet2();
        }

        if(Gdx.app.getType() == Application.ApplicationType.Desktop) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.R) || hud.isResetButtonClicked(touchPoint) || controller.getButton(2))
                reset();
        } else if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.R) || hud.isResetButtonClicked(touchPoint))
                reset();

        if(Gdx.input.isKeyJustPressed(Input.Keys.F1) && Gdx.input.isKeyPressed(Input.Keys.ALT_RIGHT) && debug)
            hud.resetHighScore();

        if(Gdx.input.isKeyJustPressed(Input.Keys.F2) && Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
            if(!debug)
                debug = true;
            else if(debug)
                debug = false;
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.F3) && debug) {
            System.out.println("GameStage.handleInput().getViewport.getScreenWidth(): " + getViewport().getScreenWidth());
            System.out.println("GameStage.handleInput().getViewport.getWorldWidth(): " + getViewport().getWorldWidth());
            System.out.println("Gdx.graphics.getWidth() from GameStage.handleInput: " + Gdx.graphics.getWidth());
        }


        if(Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            if(!paused)
                paused = true;
            else if(paused)
                paused = false;
        }

        if(Gdx.app.getType() == Application.ApplicationType.Desktop) {
            if(controller.getButton(3)) {
                if (!paused)
                    paused = true;
                else if (paused)
                    paused = false;
            }
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            if(!sound) {
                spaceShip.setSound(true);
                sound = true;
            } else if(sound) {
                spaceShip.setSound(false);
                sound = false;
            }


        }

        if(paused && Gdx.input.isKeyJustPressed(Input.Keys.PERIOD))
            world.step(TIME_STEP, 6, 2);
    }

    public boolean touchDown(int x, int y, int pointer, int button) {
        translateScreenToWorldCoordinates(x,y);

        if(hud.isFireButtonClicked(touchPoint))
            setupBullet();
        else if(hud.isAftButtonClicked(touchPoint))
            setupAftBullet();
        else if(hud.isPauseButtonClicked(touchPoint)) {
            if(!paused)
                paused = true;
            else if(paused)
                paused = false;
        } else if(hud.isSoundButtonClicked(touchPoint)) {
            if(!sound) {
                spaceShip.setSound(true);
                sound = true;
            } else if(sound) {
                spaceShip.setSound(false);
                sound = false;
            }
        }

        return super.touchDown(x, y, pointer, button);
    }

    private void translateScreenToWorldCoordinates(int x, int y) {
        camera.unproject(touchPoint.set(x, y, 0));
    }

    @Override
    public void beginContact(Contact contact) {
        final Body a = contact.getFixtureA().getBody();
        final Body b = contact.getFixtureB().getBody();


        Gdx.app.postRunnable(new Runnable() {

            @Override
            public void run() {
                if (BodyUtils.bodyIsBullet(a) && BodyUtils.bodyIsEnemy(b) || BodyUtils.bodyIsEnemy(a) && BodyUtils.bodyIsBullet(b)) {
                    enemy.destroyEnemy();

                    for(Bullet bullet : bullets) {
                        if(bullet.body == a || bullet.body == b) {
                            bullet.destroy();
                            if(sound)
                                bullet.playExplosion();
                            break;
                        }
                    }
                } else if(BodyUtils.bodyIsBullet(a) || BodyUtils.bodyIsBullet(b)) {
                    for (Bullet bullet : bullets) {
                        if(bullet.body == a || bullet.body == b)
                            bullet.checkIfBodyIsTooSlow();
                    }
                } else if(BodyUtils.bodyIsSpaceShip(a) && BodyUtils.bodyIsEnemy(b) || BodyUtils.bodyIsEnemy(a) && BodyUtils.bodyIsSpaceShip(b)) {
                    gameOver();
                }

                for(Asteroid asteroid : asteroids) {
                    if (BodyUtils.bodyIsEnemy(a) && b == asteroid.body || a == asteroid.body && BodyUtils.bodyIsEnemy(b)) {
                        enemy.confuse();
                    }
                }
            }
        });
    }

    public void dispose() {
        batch.dispose();
        world.dispose();
        hud.dispose();
        background.dispose();
        spaceShip.dispose();

        for(Bullet bullet : bullets)
            bullet.dispose();

        enemy.dispose();

        for(Asteroid asteroid : asteroids) {
            asteroid.dispose();
        }
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
}