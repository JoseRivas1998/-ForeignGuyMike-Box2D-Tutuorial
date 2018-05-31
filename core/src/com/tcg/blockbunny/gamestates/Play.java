package com.tcg.blockbunny.gamestates;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
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

    private TiledMap tileMap;
    private OrthogonalTiledMapRenderer tmr;

    private int tileSize;

    public Play(GameStateManager gsm) {
        super(gsm);

        // set up box2d stuff`
        world = new World(new Vector2(0, -9.81f), true);
        cl = new MyContactListener();
        world.setContactListener(cl);
        b2dr = new Box2DDebugRenderer();

        //set up box2d cam
        b2dCam = new OrthographicCamera();
        b2dCam.setToOrtho(false, Game.V_WIDTH / PPM, Game.V_HEIGHT / PPM);
        createPlayer();
        createTiles();

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
        tmr.setView(cam);
        tmr.render();
        b2dr.render(world, b2dCam.combined);
    }

    @Override
    public void dispose() {

    }

    private void createPlayer() {
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();

        bdef.position.set(160 / PPM, 200 / PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        playerBody = world.createBody(bdef);

        shape.setAsBox(5 / PPM, 5 / PPM);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fdef.filter.maskBits = B2DVars.BIT_RED;
        playerBody.createFixture(fdef).setUserData("player");

        // create foot sensor
        shape.setAsBox(2 / PPM, 2 / PPM, new Vector2(0, -5 / PPM), 0);
        fdef.shape = shape;
        fdef.isSensor = true;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fdef.filter.maskBits = B2DVars.BIT_RED;
        playerBody.createFixture(fdef).setUserData("foot");
    }

    private void createTiles() {
        // load tile map
        tileMap = new TmxMapLoader().load("res/maps/test.tmx");
        tmr = new OrthogonalTiledMapRenderer(tileMap);
        tileSize = (int) tileMap.getProperties().get("tilewidth");
        TiledMapTileLayer layer;
        layer = (TiledMapTileLayer) tileMap.getLayers().get("red");
        createLayer(layer, B2DVars.BIT_RED);
        layer = (TiledMapTileLayer) tileMap.getLayers().get("green");
        createLayer(layer, B2DVars.BIT_GREEN);
        layer = (TiledMapTileLayer) tileMap.getLayers().get("blue");
        createLayer(layer, B2DVars.BIT_BLUE);
    }

    private void createLayer(TiledMapTileLayer layer, short bits) {
        // go through all cells in layer
        for (int row = 0; row < layer.getHeight(); row++) {
            for (int col = 0; col < layer.getWidth(); col++) {
                // get cell
                TiledMapTileLayer.Cell cell = layer.getCell(col, row);
                // check if cell exists
                if(cell == null) continue;
                if(cell.getTile() == null) continue;
                //create a body and a fixture from cell
                BodyDef cellBodyDef = new BodyDef();
                cellBodyDef.type = BodyDef.BodyType.StaticBody;
                cellBodyDef.position.set(((col + .5f) * tileSize) / PPM, ((row + .5f) * tileSize) / PPM);


                ChainShape cs = new ChainShape();
                Vector2[] v = new Vector2[] {
                        new Vector2(-tileSize / 2 / PPM, -tileSize / 2 / PPM), // bottom left
                        new Vector2(-tileSize / 2 / PPM, tileSize / 2 / PPM), // top left
                        new Vector2(tileSize / 2 / PPM, tileSize / 2 / PPM), // top right
                        new Vector2(tileSize / 2 / PPM, -tileSize / 2 / PPM), // bottom right
                        new Vector2(-tileSize / 2 / PPM, -tileSize / 2 / PPM) // bottom left
                };

                cs.createChain(v);
                FixtureDef cellFixtureDef = new FixtureDef();
                cellFixtureDef.friction = 0;
                cellFixtureDef.shape = cs;
                cellFixtureDef.filter.categoryBits = B2DVars.BIT_RED;
                cellFixtureDef.filter.maskBits = B2DVars.BIT_PLAYER;
                cellFixtureDef.isSensor = false;
                world.createBody(cellBodyDef).createFixture(cellFixtureDef);
            }
        }
    }

}
