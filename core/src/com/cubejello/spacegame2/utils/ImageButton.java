package com.cubejello.spacegame2.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Jake on 5/23/2017.
 */

public class ImageButton {

    private Sprite sprite;
    private float width;
    private float height;
    private Vector2 position;

    private Texture offTexture;
    private Texture onTexture;

    private boolean disabled = false;
    private boolean toggle;
    private boolean buttonOn = false;

    private int clickCounter = 0;

    public ImageButton(float x, float y, float spriteWidth, float spriteHeight, Texture onTexture, boolean toggle) {
        sprite = new Sprite(onTexture);
        sprite.setPosition(x, y);
        sprite.setSize(spriteWidth, spriteHeight);
        position = new Vector2(x, y);
        this.onTexture = onTexture;
        this.width = spriteWidth;
        this.height = spriteHeight;
        this.toggle = toggle;
    }

    public void setOffTexture(Texture texture) {
        offTexture = texture;
    }

    public void setSprite(Texture texture) {
        sprite = new Sprite(texture);
        sprite.setPosition(position.x, position.y);
        sprite.setSize(width, height);
    }

    public void rotate90(boolean clockwise) {
        sprite.rotate90(clockwise);
    }

    public void update(SpriteBatch batch) {
        if(!disabled)
            sprite.draw(batch);
    }

    public boolean checkIfClicked(Vector3 touchPoint) {
        if(!disabled) {
            if (touchPoint.x > sprite.getX() && touchPoint.x < sprite.getX() + width) {
                if (touchPoint.y > sprite.getY() && touchPoint.y < sprite.getY() + height) {
                    if(toggle) {
                        if(buttonOn || clickCounter == 0) {
                            buttonOn = false;
                            setSprite(offTexture);
                        } else {
                            buttonOn = true;
                            setSprite(onTexture);
                        }
                    }
                    clickCounter++;
                    return true;
                }
            }

            return false;

        } else
            return false;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public void dispose() {
        onTexture.dispose();
        offTexture.dispose();
    }
}
