package com.tcg.blockbunny.managers;

import com.badlogic.gdx.physics.box2d.*;

public class MyContactListener implements ContactListener {

    private int numFootContacts;

    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if(fa.getUserData() != null && fa.getUserData().equals("foot")) {
            System.out.println("fa is foot");
            numFootContacts++;
        }
        if(fb.getUserData() != null && fb.getUserData().equals("foot")) {
            System.out.println("fb is foot");
            numFootContacts++;
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
}
