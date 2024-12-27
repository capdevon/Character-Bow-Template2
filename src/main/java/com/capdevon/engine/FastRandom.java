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
     * Return a random float number between min [inclusive] and max [inclusive].
     * 
     * @param min
     * @param max
     * @return 
     */
    public static float range(float min, float max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        return FastMath.nextRandomFloat() * (max - min) + min;
    }
    
    /**
     * Returns a random point inside or on a circle with radius 1.0 (Read Only).
     * @return
     */
    public static Vector2f insideUnitCircle() {
        return null; //TODO:
    }
    
    /**
     * Returns a random point inside or on a sphere with radius 1.0 (Read Only).
     * @return
     */
    public static Vector3f insideUnitSphere() {
        return null; //TODO:
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
