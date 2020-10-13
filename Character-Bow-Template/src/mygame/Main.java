/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.FlyCamAppState;
import com.jme3.audio.AudioNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import jme.ymod.util.PhysicsTestHelper;
import jme.ymod.util.BaseGameApplication;
import jme.ymod.engine.SoundManager;
import jme.ymod.engine.input.GInputAppState;
import jme.ymod.engine.ParticleManager;

/**
 *
 */
public class Main extends BaseGameApplication {
    
    /**
     * Start the jMonkeyEngine application
     * @param args
     */
    public static void main(String[] args) {
        Main app = new Main();
        AppSettings settings = new AppSettings(true);
        settings.setUseJoysticks(true);
        settings.setResolution(800, 600);
        settings.setFrequency(60);
        settings.setFrameRate(30);
        settings.setSamples(4);
        settings.setBitsPerPixel(32);
        app.setSettings(settings);
        app.setShowSettings(false);
        app.setPauseOnLostFocus(false);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // disable the default 1st-person flyCam!
        stateManager.detach(stateManager.getState(FlyCamAppState.class));
        flyCam.setEnabled(false);
        
        SoundManager.init(assetManager);
        
        initPhysics();
        setupScene();
        setupSkyBox();
        setupLights();
        setupFilters();
        
        stateManager.attach(new GInputAppState());
        stateManager.attach(new ParticleManager());
        stateManager.attach(new PlayerManager());
    }
    
    @Override
    public void setupScene() {
        Spatial scene = PhysicsTestHelper.attachMainScene(rootNode, assetManager);
        Node targets  = PhysicsTestHelper.createUnshadedBox(assetManager, 20);
        rootNode.attachChild(targets);

        /* nature sound - keeps playing in a loop. */
        AudioNode audio_nature = getAudioEnv("Sound/Environment/Nature.ogg", true, false, 4);
    }
    
}

