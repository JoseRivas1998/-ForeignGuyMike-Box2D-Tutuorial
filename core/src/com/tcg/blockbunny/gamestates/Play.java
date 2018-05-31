package com.tcg.blockbunny.gamestates;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.tcg.blockbunny.Game;
import com.tcg.blockbunny.entities.Crystal;
import com.tcg.blockbunny.entities.Player;
import com.tcg.blockbunny.managers.B2DVars;
import com.tcg.blockbunny.managers.GameStateManager;
import com.tcg.blockbunny.managers.MyContactListener;
import com.tcg.blockbunny.managers.MyInput;

import static com.tcg.blockbunny.managers.B2DVars.PPM;

public class Play extends GameState {

    private boolean debug = false;

    private World world;
    private Box2DDebugRenderer b2dr;

    private OrthographicCamera b2dCam;

    private MyContactListener cl;

    private TiledMap tileMap;
    private OrthogonalTiledMapRenderer tmr;

    private int tileSize;

    private Player player;
    private Array<Crystal> crystals;

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
        createCrystals();

    }

    @Override
    public void handleInput() {
        // player jump
        if(MyInput.isPressed(MyInput.BUTTON1)) {
            if(cl.isPlayerOnGround()) {
                player.getBody().applyForceToCenter(0, 250, true);
            }
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
        world.step(dt, 6, 2);
        player.update(dt);

        Array<Body> bodies = cl.getBodiesToRemove();
        for(Body body : bodies) {
            crystals.removeValue((Crystal) body.getUserData(), true);
            world.destroyBody(body);
            player.collectCrystal();
        }
        bodies.clear();

        for(Crystal crystal : crystals) {
            crystal.update(dt);
        }
    }

    @Override
    public void render() {
        tmr.setView(cam);
        tmr.render();
        sb.setProjectionMatrix(cam.combined);
        player.render(sb);
        for(Crystal crystal : crystals) {
            crystal.render(sb);
        }
        if(debug) b2dr.render(world, b2dCam.combined);
    }

    @Override
    public void dispose() {

    }

    private void createPlayer() {
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();

        bdef.position.set(100 / PPM, 200 / PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.linearVelocity.set(.1f, 0);
        Body body = world.createBody(bdef);

        shape.setAsBox(13 / PPM, 13 / PPM);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fdef.filter.maskBits = B2DVars.BIT_RED;
        body.createFixture(fdef).setUserData("player");

        // create foot sensor
        shape.setAsBox(13 / PPM, 2 / PPM, new Vector2(0, -13 / PPM), 0);
        fdef.shape = shape;
        fdef.isSensor = true;
        fdef.filter.categoryBits = B2DVars.BIT_PLAYER;
        fdef.filter.maskBits = B2DVars.BIT_RED | B2DVars.BIT_CRYSTAL;
        body.createFixture(fdef).setUserData("foot");

        player = new Player(body);
        body.setUserData(player);

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
                cellFixtureDef.filter.categoryBits = bits;
                cellFixtureDef.filter.maskBits = B2DVars.BIT_PLAYER;
                cellFixtureDef.isSensor = false;
                world.createBody(cellBodyDef).createFixture(cellFixtureDef);
            }
        }
    }

    private void createCrystals() {
        crystals = new Array<>();
        MapLayer layer = tileMap.getLayers().get("crystals");
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();
        for(MapObject mapObject : layer.getObjects()) {
            bodyDef.type = BodyDef.BodyType.StaticBody;
            Ellipse ellipse = ((EllipseMapObject) mapObject).getEllipse();
            float x = ellipse.x / PPM;
            float y = ellipse.y / PPM;
            bodyDef.position.set(x, y);
            CircleShape cshape = new CircleShape();
            cshape.setRadius(8 / PPM);
            fixtureDef.shape = cshape;
            fixtureDef.isSensor = true;
            fixtureDef.filter.categoryBits = B2DVars.BIT_CRYSTAL;
            fixtureDef.filter.maskBits = B2DVars.BIT_PLAYER;
            Body body = world.createBody(bodyDef);
            body.createFixture(fixtureDef).setUserData("crystal");
            Crystal c = new Crystal(body);
            crystals.add(c);
            body.setUserData(c);


        }
    }

}
