package com.capdevon.engine;

/**
 * @author capdevon
 */
public class MathUtils {
	
    private MathUtils() {}

    /**
     * Clamps value between 0 and 1 and returns value. If the value is negative then
     * zero is returned. If value is greater than one then one is returned.
     * 
     * @param value
     * @return
     */
    public static int clamp01(int value) {
        return clamp(value, 0, 1);
    }

    public static int clamp(int value, int min, int max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    /**
     * Clamps value between 0 and 1 and returns value. If the value is negative then
     * zero is returned. If value is greater than one then one is returned.
     * 
     * @param value
     * @return
     */
    public static float clamp01(float value) {
        return clamp(value, 0, 1);
    }

    public static float clamp(float value, float min, float max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    /**
     * Clamps value between 0 and 1 and returns value. If the value is negative then
     * zero is returned. If value is greater than one then one is returned.
     * 
     * @param value
     * @return
     */
    public static double clamp01(double value) {
        return clamp(value, 0, 1);
    }

    public static double clamp(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    /**
     * Linearly interpolates between fromValue to toValue on progress position.
     * 
     * @param fromValue
     * @param toValue
     * @param progress
     * @return 
     */
    public static float lerp(float fromValue, float toValue, float progress) {
        return fromValue + (toValue - fromValue) * progress;
    }

}
