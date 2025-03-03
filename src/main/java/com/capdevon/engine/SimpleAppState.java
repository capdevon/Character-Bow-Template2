package com.capdevon.engine;

import java.util.Objects;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.font.BitmapFont;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;

/**
 * A simplified AppState that provides convenient access to common jME resources.
 * This class caches frequently used application components for easy access.
 * 
 * @author capdevon
 */
public abstract class SimpleAppState extends BaseAppState {
    
    // Cached application resources
    public AppSettings      settings;
    public AppStateManager  stateManager;
    public AssetManager     assetManager;
    public InputManager     inputManager;
    public RenderManager    renderManager;
    public ViewPort         viewPort;
    public Camera           camera;
    public Node             rootNode;
    public Node             guiNode;
    public BitmapFont       guiFont;
    
    @Override
    public void initialize(Application app) {
        refreshCacheFields();
    }
    
    /**
     * Refreshes the cached application resource fields.
     */
    protected void refreshCacheFields() {
        SimpleApplication app = (SimpleApplication) getApplication();
        this.settings       = app.getContext().getSettings();
        this.stateManager   = app.getStateManager();
        this.assetManager   = app.getAssetManager();
        this.inputManager   = app.getInputManager();
        this.renderManager  = app.getRenderManager();
        this.viewPort       = app.getViewPort();
        this.camera         = app.getCamera();
        this.rootNode       = app.getRootNode();
        this.guiNode        = app.getGuiNode();
        this.guiFont        = assetManager.loadFont("Interface/Fonts/Default.fnt");
    }

    /**
     * Retrieves the physics space from the BulletAppState.
     *
     * @return The physics space.
     */
    public PhysicsSpace getPhysicsSpace() {
        return getState(BulletAppState.class, true).getPhysicsSpace();
    }

    /**
     * Finds a GameObject by name within the root node.
     *
     * @param childName The name of the spatial to find.
     * @return The found spatial.
     * @throws NullPointerException if the spatial with the given name is not found.
     */
    public Spatial find(String childName) {
        Spatial sp = rootNode.getChild(childName);
        return Objects.requireNonNull(sp, "The spatial could not be found: " + childName);
    }

}
