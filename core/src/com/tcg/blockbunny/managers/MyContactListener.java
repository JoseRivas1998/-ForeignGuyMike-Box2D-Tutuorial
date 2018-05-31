package com.tcg.blockbunny.managers;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class MyContactListener implements ContactListener {

    private int numFootContacts;
    private Array<Body> bodiesToRemove;

    public MyContactListener() {
        super();
        bodiesToRemove = new Array<>();
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if(fa.getUserData() != null && fa.getUserData().equals("foot")) {
            numFootContacts++;
        }
        if(fb.getUserData() != null && fb.getUserData().equals("foot")) {
            numFootContacts++;
        }
        if(fa.getUserData() != null && fa.getUserData().equals("crystal")) {
            //remove crystal
            bodiesToRemove.add(fa.getBody());
        }
        if(fb.getUserData() != null && fb.getUserData().equals("crystal")) {
            //remove crystal
            bodiesToRemove.add(fb.getBody());
        }


    }

    @Override
    public void endContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if(fa.getUserData() != null && fa.getUserData().equals("foot")) {
            numFootContacts--;
        }
        if(fb.getUserData() != null && fb.getUserData().equals("foot")) {
            numFootContacts--;
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    public boolean isPlayerOnGround() {
        return numFootContacts > 0;
    }

    public Array<Body> getBodiesToRemove() {
        return bodiesToRemove;
    }
}
