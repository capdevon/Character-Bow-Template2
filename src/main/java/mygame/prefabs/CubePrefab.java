package mygame.prefabs;

import com.capdevon.control.Damageable;
import com.capdevon.engine.GameObject;
import com.jme3.app.Application;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 *
 * @author capdevon
 */
public class CubePrefab extends PrefabComponent {

    float halfExtent = .3f;
    float mass = 5f;
    
    private Box mesh;
    private CollisionShape collShape;
    private ColorRGBA color;

    public CubePrefab(Application app) {
        super(app);
        mesh = new Box(halfExtent, halfExtent, halfExtent);
        collShape = new BoxCollisionShape(halfExtent);
        color = ColorRGBA.Red.clone();
    }

    private Spatial loadModel() {
        Material mat = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
        mat.setTexture("BaseColorMap", assetManager.loadTexture("Textures/white_grid.jpg"));
        mat.setColor("BaseColor", color);
        mat.setFloat("Metallic", 0);
        mat.setFloat("Roughness", 0.8f);
        
        int id = nextSeqId();
        Node node = new Node("Cube." + id);
        Geometry body = new Geometry("Cube.GeoMesh." + id, mesh);
        body.setMaterial(mat);
        node.attachChild(body);

        return node;
    }

    @Override
    public Spatial instantiate(Vector3f position, Quaternion rotation, Node parent) {
        Spatial model = loadModel();
        model.setUserData(GameObject.TAG_NAME, "TagCube");
        model.setLocalTranslation(position);
        model.setLocalRotation(rotation);
        parent.attachChild(model);

        RigidBodyControl rb = new RigidBodyControl(collShape, mass);
        model.addControl(rb);
        getPhysicsSpace().add(rb);
        rb.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_03);
        
        model.addControl(new Damageable());

        return model;
    }

}
