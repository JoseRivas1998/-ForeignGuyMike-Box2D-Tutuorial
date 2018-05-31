package com.tcg.blockbunny.gamestates;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.tcg.blockbunny.managers.GameStateManager;

public class Play extends GameState {

    private World world;
    private Box2DDebugRenderer b2dr;

    public Play(GameStateManager gsm) {
        super(gsm);

        world = new World(new Vector2(0, -9.81f), true);
        b2dr = new Box2DDebugRenderer();

        // create platform
        BodyDef bdef = new BodyDef();
        bdef.position.set(160, 120);
        bdef.type = BodyDef.BodyType.StaticBody;


        // 

    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {
        world.step(dt, 6, 2);
    }

    @Override
    public void render() {
        b2dr.render(world, cam.combined);
    }

    @Override
    public void dispose() {

    }
}
