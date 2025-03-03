package mygame.prefabs;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author capdevon
 */
public abstract class PrefabComponent {

    public final Application app;
    public final AssetManager assetManager;
    public final AppStateManager stateManager;
    private int objectId = 0;

    public PrefabComponent(Application app) {
        this.app = app;
        this.assetManager = app.getAssetManager();
        this.stateManager = app.getStateManager();
    }

    public final <T extends AppState> T getState(Class<T> type) {
        return stateManager.getState(type);
    }

    public final PhysicsSpace getPhysicsSpace() {
        return stateManager.getState(BulletAppState.class, true).getPhysicsSpace();
    }

    public final Node getRootNode() {
        return ((SimpleApplication) app).getRootNode();
    }

    public final Node getGuiNode() {
        return ((SimpleApplication) app).getGuiNode();
    }

    public final int nextSeqId() {
        objectId++;
        return objectId;
    }

    public Spatial instantiate(Vector3f position, Quaternion rotation) {
        return instantiate(position, rotation, getRootNode());
    }

    public abstract Spatial instantiate(Vector3f position, Quaternion rotation, Node parent);

}
