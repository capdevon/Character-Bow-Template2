package com.capdevon.engine;

import java.awt.Color;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

/**
 * @author capdevon
 */
public class FastRandom {
    
    private FastRandom() {}

    /**
     * It returns a uniformly distributed value within the range [0.0, 1.0)
     *
     * @param min The lower bound (inclusive).
     * @param max The upper bound (exclusive).
     * @return A random value within the specified range.
     */
    public static double range(double min, double max) {
        if (min >= max) {
            throw new IllegalArgumentException("Invalid range [" + min + ", " + max + "]");
        }
        return min + Math.random() * (max - min);
    }

    /**
     * It returns a uniformly distributed value within the range [0.0, 1.0)
     *
     * @param min The lower bound (inclusive).
     * @param max The upper bound (exclusive).
     * @return A random value within the specified range.
     */
    public static float range(float min, float max) {
        if (min >= max) {
            throw new IllegalArgumentException("Invalid range [" + min + ", " + max + "]");
        }
        return min + FastMath.nextRandomFloat() * (max - min);
    }

    /**
     * Returns a random point on the surface of a sphere with radius 1.0
     *
     * @return A new {@link Vector3f} representing a random point on the surface of
     *         the unit sphere.
     */
    public static Vector3f onUnitSphere() {

        float u = FastMath.nextRandomFloat();
        float v = FastMath.nextRandomFloat();

        // azimuthal angle: The angle between x-axis in radians [0, 2PI]
        float theta = FastMath.TWO_PI * u;
        // polar angle: The angle between z-axis in radians [0, PI]
        float phi = (float) Math.acos(2f * v - 1f);

        float cosPolar = FastMath.cos(phi);
        float sinPolar = FastMath.sin(phi);
        float cosAzim = FastMath.cos(theta);
        float sinAzim = FastMath.sin(theta);

        return new Vector3f(cosAzim * sinPolar, sinAzim * sinPolar, cosPolar);
    }

    /**
     * Returns a random point inside or on a sphere with radius 1.0 This method uses
     * spherical coordinates combined with a cubed-root radius.
     *
     * @return A new {@link Vector3f} representing a random point within the unit
     *         sphere.
     */
    public static Vector3f insideUnitSphere() {
        float u = FastMath.nextRandomFloat();
        // Azimuthal angle [0, 2PI]
        float theta = FastMath.TWO_PI * FastMath.nextRandomFloat();
        // Polar angle [0, PI] for uniform surface distribution
        float phi = FastMath.acos(2f * FastMath.nextRandomFloat() - 1f);

        // For uniform distribution within the volume, R = cbrt(random_uniform_0_to_1)
        float radius = (float) Math.cbrt(u);

        float sinPhi = FastMath.sin(phi);
        float x = radius * sinPhi * FastMath.cos(theta);
        float y = radius * sinPhi * FastMath.sin(theta);
        float z = radius * FastMath.cos(phi);

        return new Vector3f(x, y, z);
    }

    /**
     * Returns a random point inside or on a circle with radius 1.0. This method
     * uses polar coordinates combined with a square-root radius.
     *
     * @return A new {@link Vector2f} representing a random point within the unit
     *         circle.
     */
    public static Vector2f insideUnitCircle() {
        // Angle [0, 2PI]
        float angle = FastMath.TWO_PI * FastMath.nextRandomFloat();
        // For uniform distribution, R^2 is uniform
        float radius = FastMath.sqrt(FastMath.nextRandomFloat());

        float x = radius * FastMath.cos(angle);
        float y = radius * FastMath.sin(angle);

        return new Vector2f(x, y);
    }
    
    /**
     * Generates a random color from HSV ranges.
     * @return
     */
    public static ColorRGBA colorHSV() {
        return colorHSV(0f, 1f, 0f, 1f, 0f, 1f);
    }
    
    /**
     * Generates a random color from HSV ranges.
     * @return
     */
    public static ColorRGBA colorHSV(float hueMin, float hueMax, 
            float saturationMin, float saturationMax, 
            float valueMin, float valueMax) {
        float h = range(hueMin, hueMax);
        float s = range(saturationMin, saturationMax);
        float b = range(valueMin, valueMax);
        Color c = Color.getHSBColor(h, s, b);
        return ColorRGBA.fromRGBA255(c.getRed(), c.getGreen(), c.getBlue(), 255);
    }

}
