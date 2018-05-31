package com.tcg.blockbunny.gamestates;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.tcg.blockbunny.Game;
import com.tcg.blockbunny.managers.B2DVars;
import com.tcg.blockbunny.managers.GameStateManager;
import com.tcg.blockbunny.managers.MyContactListener;

import static com.tcg.blockbunny.managers.B2DVars.PPM;

public class Play extends GameState {

    private World world;
    private Box2DDebugRenderer b2dr;

    private OrthographicCamera b2dCam;

    public Play(GameStateManager gsm) {
        super(gsm);

        world = new World(new Vector2(0, -0.81f), true);
        world.setContactListener(new MyContactListener());
        b2dr = new Box2DDebugRenderer();

        // create platform
        BodyDef bdef = new BodyDef();
        bdef.position.set(160 / PPM, 120 / PPM);
        bdef.type = BodyDef.BodyType.StaticBody;
        // static body - don't move, unaffected by forces
        // kinematic body - don't get affected forces, can have velocity
        // dynamic body - always get affected by forces

        Body body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(50 / PPM, 5 / PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_GROUND;
        fdef.filter.maskBits = B2DVars.BIT_BOX | B2DVars.BIT_BALL;
        body.createFixture(fdef).setUserData("ground");

        // create falling box
        bdef.position.set(160 / PPM, 200 / PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        shape.setAsBox(5 / PPM, 5 / PPM);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_BOX;
        fdef.filter.maskBits = B2DVars.BIT_GROUND;
        body.createFixture(fdef).setUserData("box");

        //create ball
        bdef.position.set(153 / PPM, 220 / PPM);
        body = world.createBody(bdef);

        CircleShape cShape = new CircleShape();
        cShape.setRadius(5 / PPM);
        fdef.shape = cShape;
        fdef.filter.categoryBits = B2DVars.BIT_BALL;
        fdef.filter.maskBits = B2DVars.BIT_GROUND;
        body.createFixture(fdef).setUserData("ball");

        //set up box2d cam
        b2dCam = new OrthographicCamera();
        b2dCam.setToOrtho(false, Game.V_WIDTH / PPM, Game.V_HEIGHT / PPM);

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
        b2dr.render(world, b2dCam.combined);
    }

    @Override
    public void dispose() {

    }
}
