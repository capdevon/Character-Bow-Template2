package mygame.states;

import com.capdevon.control.Spawner;
import com.capdevon.engine.SimpleAppState;
import com.jme3.scene.Node;

import mygame.prefabs.CubePrefab;

/**
 * 
 * @author capdevon
 */
public class CubeAppState extends SimpleAppState {

    @Override
    protected void simpleInit() {
        Node cubes = new Node("CubeSpawner");
        rootNode.attachChild(cubes);

        Spawner spawner = new Spawner();
        spawner.maxObjects = 20;
        spawner.radius = 6;
        spawner.height = 1f;
        spawner.spawnTime = 5f;
        spawner.prefab = new CubePrefab(sapp);
        cubes.addControl(spawner);
    }

    @Override
    public void update(float tpf) {}

}
