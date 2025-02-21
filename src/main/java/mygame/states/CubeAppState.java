package mygame.states;

import com.capdevon.control.Spawner;
import com.capdevon.engine.SimpleAppState;
import com.jme3.app.Application;
import com.jme3.scene.Node;

import mygame.prefabs.CubePrefab;

/**
 * 
 * @author capdevon
 */
public class CubeAppState extends SimpleAppState {

    @Override
    public void initialize(Application app) {
        super.initialize(app);
        
        Node cubes = new Node("CubeSpawner");
        rootNode.attachChild(cubes);

        Spawner spawner = new Spawner();
        spawner.maxObjects = 20;
        spawner.radius = 6;
        spawner.height = 1f;
        spawner.spawnTime = 5f;
        spawner.prefab = new CubePrefab(app);
        cubes.addControl(spawner);
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

}
