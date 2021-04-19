/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.control;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.ModelKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;

/**
 *
 * @author capdevon
 */
public abstract class PrefabComponent {
    
    private final Application app;
    private final AssetManager assetManager;
    private final AppStateManager stateManager;
    private final AppSettings settings;
    private int objectId = 0;
    
    public PrefabComponent(Application app) {
        this.app = app;
        this.assetManager = app.getAssetManager();
        this.stateManager = app.getStateManager();
        this.settings = app.getContext().getSettings();
    }
    
    public Application getApplication() {
        return app;
    }
    
    public final AssetManager getAssetManager() {
        return assetManager;
    }

    public final <T extends AppState> T getState( Class<T> type ) {
        return stateManager.getState(type);
    }
    
    public final PhysicsSpace getPhysicsSpace() {
        return stateManager.getState(BulletAppState.class).getPhysicsSpace();
    }
    
    public final AppSettings getSettings() {
        return settings;
    }
    
    public Node getRootNode() {
        return ((SimpleApplication) app).getRootNode();
    }
    
    public Node getGuiNode() {
        return ((SimpleApplication) app).getGuiNode();
    }
    
    public int nextSeqId() {
        objectId++;
        return objectId;
    }
    
    /**
     * 
     * @param name
     * @param key
     * @return 
     */
    public Spatial loadModel(String name, ModelKey key) {
        Spatial model = assetManager.loadModel(key);
        model.setName(name + "." + nextSeqId());
        return model;
    }
    
    /**
     * 
     * @param position
     * @param rotation
     * @return 
     */
    public Spatial instantiate(Vector3f position, Quaternion rotation) {
        return instantiate(position, rotation, getRootNode());
    }
    
    /**
     * 
     * @param position
     * @param rotation
     * @param parent
     * @return 
     */
    public abstract Spatial instantiate(Vector3f position, Quaternion rotation, Node parent);
    
}
