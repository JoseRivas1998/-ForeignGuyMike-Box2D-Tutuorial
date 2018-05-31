package com.tcg.blockbunny.gamestates;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.tcg.blockbunny.Game;
import com.tcg.blockbunny.managers.B2DVars;
import com.tcg.blockbunny.managers.GameStateManager;
import com.tcg.blockbunny.managers.MyContactListener;
import com.tcg.blockbunny.managers.MyInput;

import static com.tcg.blockbunny.managers.B2DVars.PPM;

public class Play extends GameState {

    private World world;
    private Box2DDebugRenderer b2dr;

    private OrthographicCamera b2dCam;

    private Body playerBody;
    private MyContactListener cl;

    public Play(GameStateManager gsm) {
        super(gsm);

        world = new World(new Vector2(0, -9.81f), true);
        cl = new MyContactListener();
        world.setContactListener(cl);
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
        fdef.filter.maskBits = B2DVars.BIT_PLAYER;
        body.createFixture(fdef).setUserData("ground");

        // create player
        bdef.position.set(160 / PPM, 200 / PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        playerBody = world.createBody(bdef);

        shape.setAsBox(5 / PPM, 5 / PPM);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fdef.filter.maskBits = B2DVars.BIT_GROUND;
        playerBody.createFixture(fdef).setUserData("player");

        // create foot sensor
        shape.setAsBox(2 / PPM, 2 / PPM, new Vector2(0, -5 / PPM), 0);
        fdef.shape = shape;
        fdef.isSensor = true;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fdef.filter.maskBits = B2DVars.BIT_GROUND;
        playerBody.createFixture(fdef).setUserData("foot");

        //set up box2d cam
        b2dCam = new OrthographicCamera();
        b2dCam.setToOrtho(false, Game.V_WIDTH / PPM, Game.V_HEIGHT / PPM);

    }

    @Override
    public void handleInput() {
        // player jump
        if(MyInput.isPressed(MyInput.BUTTON1)) {
            if(cl.isPlayerOnGround()) {
                playerBody.applyForce(0, 200, playerBody.getWorldCenter().x, playerBody.getWorldCenter().y, true);
            }
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
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
