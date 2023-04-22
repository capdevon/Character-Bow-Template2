package com.capdevon.physx;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author capdevon
 */
public class RaycastHit {

    public PhysicsCollisionObject rigidBody;
    public CollisionShape collider;
    public Spatial userObject;
    public float distance;
    public Vector3f normal = new Vector3f();
    public Vector3f point = new Vector3f();

    protected void set(Vector3f beginVec, Vector3f finalVec, PhysicsRayTestResult ray) {
        PhysicsCollisionObject pco = ray.getCollisionObject();
        float hf = ray.getHitFraction();

        rigidBody = pco;
        collider = pco.getCollisionShape();
        userObject = (Spatial) pco.getUserObject();
        distance = finalVec.subtract(beginVec).length() * hf;
        point.interpolateLocal(beginVec, finalVec, hf);
        ray.getHitNormalLocal(normal);
    }

    public void clear() {
        rigidBody = null;
        collider = null;
        userObject = null;
        distance = Float.NaN;
        point.set(Vector3f.NAN);
        normal.set(Vector3f.NAN);
    }

    @Override
    public String toString() {
        return "RaycastHit [rigidBody=" + toHexString(rigidBody)
                + ", collider=" + toHexString(collider)
                + ", userObject=" + toHexString(userObject)
                + ", distance=" + distance
                + ", normal=" + normal
                + ", point=" + point
                + "]";
    }

    private String toHexString(Object obj) {
        if (obj != null) {
            return obj.getClass().getSimpleName() + '@' + Integer.toHexString(obj.hashCode());
        }
        return null;
    }

}
