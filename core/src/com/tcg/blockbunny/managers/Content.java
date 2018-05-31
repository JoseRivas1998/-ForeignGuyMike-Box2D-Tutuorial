package com.tcg.blockbunny.managers;

import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;
import java.util.Map;

public class Content {

    private Map<String, Texture> textures;

    public Content() {
        textures = new HashMap<String, Texture>();
    }

    public void loadTexture(String path, String key) {
        textures.put(key, new Texture(path));
    }

    public Texture getTexture(String key) {
        return textures.get(key);
    }

    public void disposeTexture(String key) {
        if(textures.get(key) != null) textures.get(key).dispose();
    }

}
