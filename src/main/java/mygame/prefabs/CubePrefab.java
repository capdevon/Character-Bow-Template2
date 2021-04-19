/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.prefabs;

import com.capdevon.control.PrefabComponent;
import com.jme3.app.Application;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.environment.util.BoundingSphereDebug;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 *
 * @author capdevon
 */
public class CubePrefab extends PrefabComponent {

    float radius = 3f;
    float halfExtent = .2f;
    float mass = 5f;
    
    private Box mesh;
    private Material mat;
    private CollisionShape collShape;

    public CubePrefab(Application app) {
        super(app);
        mesh = new Box(halfExtent, halfExtent, halfExtent);
        collShape = new BoxCollisionShape(halfExtent);
        mat = new Material(getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.randomColor());
    }

    private Spatial loadModel() {
        int sequenceId = nextSeqId();
        Node node = new Node("Cube." + sequenceId);

        Geometry body = new Geometry("Cube.GeoMesh." + sequenceId, mesh);
        body.setMaterial(mat);
        node.attachChild(body);

        Geometry area = BoundingSphereDebug.createDebugSphere(getAssetManager());
        area.setShadowMode(RenderQueue.ShadowMode.Off);
//        area.setLocalScale(radius);
//        node.attachChild(area);

        return node;
    }

    @Override
    public Spatial instantiate(Vector3f position, Quaternion rotation, Node parent) {
        Spatial model = loadModel();
        model.setLocalTranslation(position);
        parent.attachChild(model);

        RigidBodyControl rb = new RigidBodyControl(collShape, mass);
        model.addControl(rb);
        getPhysicsSpace().add(rb);
        rb.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_03);

        return model;
    }

}
