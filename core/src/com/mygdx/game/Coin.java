package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.ArrayList;
import java.util.Iterator;

public class Coin extends Actor {
    private Texture coinTexture;
    Rectangle bounds;
    AssetManager manager;

    public Coin() {
        //coinTexture = new Texture(Gdx.files.internal("coins.png"));
        setSize(48, 48);
        bounds = new Rectangle();
        setVisible(false);
    }
    @Override
    public void act(float delta) {
        moveBy(-20, 0);
        bounds.set(getX(), getY(), getWidth(), getHeight());
        if(!isVisible())
            setVisible(true);
        if (getX() < -64)
            remove();
    }
    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw( manager.get("coins.png", Texture.class), getX(), getY() );
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setManager(AssetManager manager) {
        this.manager = manager;
    }

}