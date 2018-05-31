package com.tcg.blockbunny;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tcg.blockbunny.managers.Content;
import com.tcg.blockbunny.managers.GameStateManager;
import com.tcg.blockbunny.managers.MyInput;
import com.tcg.blockbunny.managers.MyInputProcessor;

public class Game extends ApplicationAdapter {

	public static final String TITLE = "Block Bunny";
	public static final int V_WIDTH = 320;
	public static final int V_HEIGHT = 240;
	public static final int SCALE = 2;

	public static final float STEP = 1 / 60f;
	private float accum;

	private SpriteBatch sb;
	private OrthographicCamera cam;
	private OrthographicCamera hudCam;

	private GameStateManager gsm;

	public static Content res;

	@Override
	public void create () {

		res = new Content();
		res.loadTexture("res/images/bunny.png", "bunny");
		res.loadTexture("res/images/crystal.png", "crystal");

		sb = new SpriteBatch();
		cam = new OrthographicCamera();
		cam.setToOrtho(false, V_WIDTH, V_HEIGHT);

		hudCam = new OrthographicCamera();
		hudCam.setToOrtho(false, V_WIDTH, V_HEIGHT);

		gsm = new GameStateManager(this);
		Gdx.input.setInputProcessor(new MyInputProcessor());
	}

	@Override
	public void render () {
		float dt = Gdx.graphics.getDeltaTime();
		accum += Gdx.graphics.getDeltaTime();
		while(accum >= STEP) {
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			accum -= STEP;
			gsm.update(STEP);
			gsm.render();
			MyInput.update();
		}
	}

	public SpriteBatch getSpriteBatch() {
		return sb;
	}

	public OrthographicCamera getCamera() {
		return cam;
	}

	public OrthographicCamera getHudCamera() {
		return hudCam;
	}

	@Override
	public void dispose () {

	}
}
