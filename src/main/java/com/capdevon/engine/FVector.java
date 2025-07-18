package com.capdevon.engine;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author capdevon
 */
public class FVector {
	
    public static final Vector3f Zero    = new Vector3f(0f, 0f, 0f);
    public static final Vector3f One     = new Vector3f(1f, 1f, 1f);
    public static final Vector3f Up      = new Vector3f(0f, 1f, 0f);
    public static final Vector3f Down    = new Vector3f(0f, -1f, 0f);
    public static final Vector3f Left    = new Vector3f(-1f, 0f, 0f);
    public static final Vector3f Right   = new Vector3f(1f, 0f, 0f);
    public static final Vector3f Forward = new Vector3f(0f, 0f, 1f);
    public static final Vector3f Back    = new Vector3f(0f, 0f, -1f);
    
    /**
     * A private constructor to inhibit instantiation of this class.
     */
    private FVector() {}
    
    /**
     * Spherically interpolates between start vector and the end vector by
     * alpha which is in the range [0,1].
     *
     * @param start The start vector
     * @param end The end vector
     * @param alpha The interpolation coefficient
     * @return The result vector
     */
    public static Vector3f slerp(final Vector3f start, final Vector3f end, float alpha) {
        final float dot = start.dot(end);
        // If the inputs are too close for comfort, simply linearly interpolate.
        if (dot > 0.9995 || dot < -0.9995) {
            return new Vector3f().interpolateLocal(start, end, alpha);
        }
        // theta0 = angle between input vectors
        final float theta0 = (float) Math.acos(dot);
        // theta = angle between this vector and result
        final float theta = theta0 * alpha;
        final float st = (float) Math.sin(theta);
        final float tx = end.x - start.x * dot;
        final float ty = end.y - start.y * dot;
        final float tz = end.z - start.z * dot;
        final float l2 = tx * tx + ty * ty + tz * tz;
        final float dl = st * ((l2 < 0.0001f) ? 1f : 1f / (float) Math.sqrt(l2));
        return start.scaleAdd((float) Math.cos(theta), new Vector3f(tx * dl, ty * dl, tz * dl)).normalizeLocal();
    }
        
    /**
     * Rotates this vector by the given angle in degrees around Y axis.
     */
    public static Vector3f rotate(final Vector3f v, float degrees) {
        return rotateRad(v, Vector3f.UNIT_Y, degrees * FastMath.DEG_TO_RAD);
    }

    /**
     * Rotates this vector by the given angle in degrees around the given axis.
     */
    public static Vector3f rotate(Vector3f v, Vector3f axis, float degrees) {
        return rotateRad(v, axis, degrees * FastMath.DEG_TO_RAD);
    }

    /**
     * Rotates this vector by the given angle in radians around Y axis.
     */
    public static Vector3f rotateRad(Vector3f v, float radians) {
        return rotateRad(v, Vector3f.UNIT_Y, radians);
    }

    /**
     * Rotates this vector by the given angle in radians around the given axis.
     */
    public static Vector3f rotateRad(Vector3f v, Vector3f axis, float radians) {
        Quaternion q = new Quaternion().fromAngleNormalAxis(radians, axis);
        return q.mult(v);
    }

    /**
     * truncate the length of the vector to the given limit
     */
    public static Vector3f truncate(Vector3f v, float limit) {
        float lengthSq = v.lengthSquared();
        if (lengthSq < limit * limit) {
            return v;
        }
        return v.mult(limit / FastMath.sqrt(lengthSq));
    }

    /**
     * The smallest squared distance between the world position of b and the bounding volume of a.
     */
    public static float sqrDistanceTo(Spatial a, Spatial b) {
        return a.getWorldBound().distanceSquaredTo(b.getWorldTranslation());
    }

    /**
     * The smallest distance between the world position of b and the bounding volume of a.
     */
    public static float distanceTo(Spatial a, Spatial b) {
        return a.getWorldBound().distanceTo(b.getWorldTranslation());
    }

    /**
     * Subtracts the world position of spatial b from those of spatial a creating a new vector object.
     */
    public static Vector3f subtract(Spatial a, Spatial b) {
        return b.getWorldTranslation().subtract(a.getWorldTranslation());
    }

    /**
     * Returns the distance between a and b.
     */
    public static float distance(Spatial a, Spatial b) {
        return distance(a.getWorldTranslation(), b.getWorldTranslation());
    }

    /**
     * Returns the squared distance between a and b.
     */
    public static float sqrDistance(Spatial a, Spatial b) {
        return sqrDistance(a.getWorldTranslation(), b.getWorldTranslation());
    }

    /**
     * Returns the distance between a and b.
     */
    public static float distance(Vector3f a, Vector3f b) {
        return a.distance(b);
    }

    /**
     * Returns the squared distance between a and b.
     */
    public static float sqrDistance(Vector3f a, Vector3f b) {
        return a.distanceSquared(b);
    }

    /**
     * Returns the angle in degrees between from and to.
     */
    public static float angle(Vector3f a, Vector3f b) {
        return a.angleBetween(b);
    }

    public static boolean hasSameDirection(Vector3f a, Vector3f b) {
        return a.dot(b) > 0;
    }

    public static boolean hasOppositeDirection(Vector3f a, Vector3f b) {
        return a.dot(b) < 0;
    }

    public static Vector3f forward(Spatial sp) {
        return sp.getWorldRotation().mult(Forward);
    }

    public static Vector3f up(Spatial sp) {
        return sp.getWorldRotation().mult(Up);
    }

    public static Vector3f right(Spatial sp) {
        return sp.getWorldRotation().mult(Right);
    }

    public static Vector3f left(Spatial sp) {
        return sp.getWorldRotation().mult(Left);
    }

}
