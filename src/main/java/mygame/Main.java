/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import com.capdevon.input.GInputAppState;
import com.capdevon.physx.PhysxDebugAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.system.AppSettings;

import mygame.states.CubeAppState;
import mygame.states.SceneAppState;

/**
 * 
 * @author capdevon
 */
public class Main extends SimpleApplication {

    /**
     * Start the jMonkeyEngine application
     * @param args
     */
    public static void main(String[] args) {
        //Get the Resolution of the main/default display
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        DisplayMode display = device.getDisplayMode();

        Main app = new Main();

        AppSettings settings = new AppSettings(true);
        settings.setResolution(1280, 720);
        settings.setFrameRate(60);
        settings.setBitsPerPixel(display.getBitDepth());
        settings.setFrequency(display.getRefreshRate());
        settings.setSamples(4);
        settings.setVSync(true);
        settings.setGammaCorrection(true);
        // settings.setUseJoysticks(true);

        app.setSettings(settings);
        app.setShowSettings(true);
        app.setPauseOnLostFocus(false);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // disable the default 1st-person flyCam!
        stateManager.detach(stateManager.getState(FlyCamAppState.class));
        flyCam.setEnabled(false);

        /** Initialize the physics simulation */
        BulletAppState physics = new BulletAppState();
        stateManager.attach(physics);
        physics.setDebugEnabled(false);

        stateManager.attach(new SceneAppState());
        stateManager.attach(new CubeAppState());
        stateManager.attach(new PhysxDebugAppState());
        stateManager.attach(new GInputAppState());
        stateManager.attach(new ParticleManager());
        stateManager.attach(new PlayerManager());
    }

}
