package com.capdevon.physx;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.objects.PhysicsGhostObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.util.TempVars;

/**
 *
 * @author capdevon
 */
public class Physics {

    public static final Vector3f DEFAULT_GRAVITY = new Vector3f(0, -9.81f, 0).multLocal(2);

    private static final int DefaulRaycastLayers = ~0; // All Layers

    /**
     * A private constructor to inhibit instantiation of this class.
     */
    private Physics() {}

    /**
     * Applies a force to a rigidbody that simulates explosion effects.
     *
     * @param rb                - The rigidbody object.
     * @param explosionForce    - The force of the explosion (which may be modified by distance).
     * @param explosionPosition - The centre of the sphere within which the explosion has its effect.
     * @param explosionRadius   - The radius of the sphere within which the explosion has its effect.
     */
    public static void addExplosionForce(PhysicsRigidBody rb, float explosionForce, Vector3f explosionPosition, float explosionRadius) {
        Vector3f expCenter2Body = rb.getPhysicsLocation().subtract(explosionPosition);
        float distance = expCenter2Body.length();
        if (distance < explosionRadius) {
            // apply proportional explosion force
            float strength = (1.f - FastMath.clamp(distance / explosionRadius, 0, 1)) * explosionForce;
            rb.setLinearVelocity(expCenter2Body.normalize().mult(strength));
        }
    }

    /**
     * Casts a ray through the scene and returns all hits.
     */
    public static List<RaycastHit> raycastAll(Ray ray, float maxDistance) {
        return raycastAll(ray, maxDistance, DefaulRaycastLayers);
    }

    /**
     * Casts a ray through the scene and returns all hits.
     * 
     * @param ray         The starting point and direction of the ray.
     * @param maxDistance The max distance the rayhit is allowed to be from the start of the ray.
     * @param layerMask   A Layer mask that is used to selectively ignore colliders when casting a ray.
     * @return A list of RaycastHit objects.
     */
    public static List<RaycastHit> raycastAll(Ray ray, float maxDistance, int layerMask) {

        List<RaycastHit> lstResults = new ArrayList<>();

        TempVars t = TempVars.get();
        Vector3f beginVec = t.vect1.set(ray.origin);
        Vector3f finalVec = t.vect2.set(ray.direction).scaleAdd(maxDistance, ray.origin);

        List<PhysicsRayTestResult> results = PhysicsSpace.getPhysicsSpace().rayTest(beginVec, finalVec);
        for (PhysicsRayTestResult phRay : results) {
            PhysicsCollisionObject pco = phRay.getCollisionObject();
            if (applyMask(layerMask, pco.getCollisionGroup())) {
                RaycastHit hitInfo = new RaycastHit();
                hitInfo.set(beginVec, finalVec, phRay);
                lstResults.add(hitInfo);
            }
        }

        t.release();
        return lstResults;
    }

    /**
     * Casts a ray, from point origin, in direction direction, of length
     * maxDistance, against all colliders in the Scene.
     */
    public static boolean doRaycast(Vector3f origin, Vector3f direction, RaycastHit hitInfo, float maxDistance) {
        return doRaycast(origin, direction, hitInfo, maxDistance, DefaulRaycastLayers);
    }

    /**
     * Casts a ray, from point origin, in direction direction, of length
     * maxDistance, against all colliders in the Scene.
     * 
     * @param origin      The starting point of the ray in world coordinates. (not null, unaffected)
     * @param direction   The direction of the ray. (not null, unaffected)
     * @param hitInfo     If true is returned, hitInfo will contain more information
     *                    about where the closest collider was hit. (See Also: RaycastHit).
     * @param maxDistance The max distance the ray should check for collisions.
     * @param layerMask   A Layer mask that is used to selectively ignore Colliders when casting a ray.
     * @return Returns true if the ray intersects with a Collider, otherwise false.
     */
    public static boolean doRaycast(Vector3f origin, Vector3f direction, RaycastHit hitInfo, float maxDistance, int layerMask) {

        hitInfo.clear();
        boolean collision = false;
        float hf = maxDistance;

        TempVars t = TempVars.get();
        Vector3f beginVec = t.vect1.set(origin);
        Vector3f finalVec = t.vect2.set(direction).scaleAdd(maxDistance, origin);

        List<PhysicsRayTestResult> results = PhysicsSpace.getPhysicsSpace().rayTestRaw(beginVec, finalVec);
        for (PhysicsRayTestResult ray : results) {
            PhysicsCollisionObject pco = ray.getCollisionObject();
            if (ray.getHitFraction() < hf && applyMask(layerMask, pco.getCollisionGroup())) {
                collision = true;
                hf = ray.getHitFraction();
                hitInfo.set(beginVec, finalVec, ray);
            }
        }

        t.release();
        return collision;
    }

    /**
     * Returns true if there is any collider intersecting the line between
     * beginVec and finalVec.
     */
    public static boolean doLinecast(Vector3f beginVec, Vector3f finalVec, RaycastHit hitInfo) {
        return doLinecast(beginVec, finalVec, hitInfo, DefaulRaycastLayers);
    }

    /**
     * Returns true if there is any collider intersecting the line between beginVec and finalVec.
     * 
     * @param beginVec  (not null, unaffected)
     * @param finalVec  (not null, unaffected)
     * @param hitInfo   If true is returned, hitInfo will contain more information
     *                  about where the closest collider was hit. (See Also: RaycastHit).
     * @param layerMask A Layer mask that is used to selectively ignore Colliders when casting a ray.
     * @return Returns true if the ray intersects with a Collider, otherwise false.
     */
    public static boolean doLinecast(Vector3f beginVec, Vector3f finalVec, RaycastHit hitInfo, int layerMask) {

        hitInfo.clear();
        boolean collision = false;
        float hf = Float.MAX_VALUE;

        List<PhysicsRayTestResult> results = PhysicsSpace.getPhysicsSpace().rayTestRaw(beginVec, finalVec);
        for (PhysicsRayTestResult ray : results) {
            PhysicsCollisionObject pco = ray.getCollisionObject();
            if (ray.getHitFraction() < hf && applyMask(layerMask, pco.getCollisionGroup())) {
                collision = true;
                hf = ray.getHitFraction();
                hitInfo.set(beginVec, finalVec, ray);
            }
        }

        return collision;
    }

    /**
     * Computes and stores colliders inside the sphere.
     * https://docs.unity3d.com/ScriptReference/Physics.OverlapSphere.html
     *
     * @param position  Center of the sphere.
     * @param radius    Radius of the sphere.
     * @param layerMask A Layer mask defines which layers of colliders to include in the query.
     * @return Returns all colliders that overlap with the given sphere.
     */
    public static Set<PhysicsCollisionObject> overlapSphere(Vector3f position, float radius, int layerMask) {

        Set<PhysicsCollisionObject> overlappingObjects = new HashSet<>(5);
        PhysicsGhostObject ghost = new PhysicsGhostObject(new SphereCollisionShape(radius));
        ghost.setPhysicsLocation(position);

        contactTest(ghost, overlappingObjects, layerMask);
        return overlappingObjects;
    }

    public static Set<PhysicsCollisionObject> overlapSphere(Vector3f position, float radius) {
        return overlapSphere(position, radius, DefaulRaycastLayers);
    }
    
    /**
     * Find all colliders touching or inside of the given box.
     * https://docs.unity3d.com/ScriptReference/Physics.OverlapBox.html
     * 
     * @param center      Center of the box.
     * @param halfExtents Half of the size of the box in each dimension.
     * @param rotation    Rotation of the box.
     * @param layerMask   A Layer mask that is used to selectively ignore colliders when casting a ray.
     * @return Returns all colliders that overlap with the given box.
     */
    public static Set<PhysicsCollisionObject> overlapBox(Vector3f center, Vector3f halfExtents, Quaternion rotation, int layerMask) {

        Set<PhysicsCollisionObject> overlappingObjects = new HashSet<>(5);
        PhysicsGhostObject ghost = new PhysicsGhostObject(new BoxCollisionShape(halfExtents));
        ghost.setPhysicsLocation(center);
        ghost.setPhysicsRotation(rotation);

        contactTest(ghost, overlappingObjects, layerMask);
        return overlappingObjects;
    }

    public static Set<PhysicsCollisionObject> overlapBox(Vector3f center, Vector3f halfExtents, Quaternion rotation) {
        return overlapBox(center, halfExtents, rotation, DefaulRaycastLayers);
    }

    /**
     * Perform a contact test. This will not detect contacts with soft bodies.
     */
    private static int contactTest(PhysicsGhostObject ghost, final Set<PhysicsCollisionObject> overlappingObjects, int layerMask) {

        overlappingObjects.clear();

        PhysicsSpace physicsSpace = PhysicsSpace.getPhysicsSpace();
        int numContacts = physicsSpace.contactTest(ghost, new PhysicsCollisionListener() {
            @Override
            public void collision(PhysicsCollisionEvent event) {
                if (event.getDistance1() > 0f) {
                    // bug: Discard contacts with positive distance between the colliding objects.
                } else {
                    PhysicsCollisionObject pco = event.getNodeA() != null ? event.getObjectA() : event.getObjectB();
                    if (applyMask(layerMask, pco.getCollisionGroup())) {
                        overlappingObjects.add(pco);
                    }
                }
            }
        });
        return numContacts;
    }
    
    /**
     * Check if a collisionGroup is in a layerMask
     *
     * @param layerMask
     * @param collisionGroup
     * @return
     */
    private static boolean applyMask(int layerMask, int collisionGroup) {
        return layerMask == (layerMask | collisionGroup);
    }
}
