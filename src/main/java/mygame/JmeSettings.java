package mygame;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.jme3.system.AppSettings;

/**
 * 
 * @author capdevon
 */
public class JmeSettings {
    
    private static final Logger logger = Logger.getLogger(JmeSettings.class.getName());

    public static AppSettings getDefault() {
        AppSettings settings = new AppSettings(true);
        settings.setRenderer(AppSettings.LWJGL_OPENGL32);
        settings.setAudioRenderer(AppSettings.LWJGL_OPENAL);
        settings.setResolution(800, 600);
        settings.setFrameRate(60);
        settings.setSamples(4);
        settings.setDepthBits(24);
        settings.setBitsPerPixel(24);
        settings.setVSync(true);
        settings.setGammaCorrection(true);
        settings.setUseJoysticks(false);

        try {
            printPreferences(settings.getTitle());
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
        return settings;
    }
    
    /**
     * Prints all key-value pairs stored under a given preferences key
     * in the Java Preferences API to standard output.
     *
     * @param preferencesKey The preferences key (node path) to inspect.
     * @throws BackingStoreException If an exception occurs while accessing the preferences.
     */
    public static void printPreferences(String preferencesKey) throws BackingStoreException {
        Preferences prefs = Preferences.userRoot().node(preferencesKey);
        String[] keys = prefs.keys();

        if (keys == null || keys.length == 0) {
            logger.log(Level.WARNING, "No Preferences found under key: {0}", preferencesKey);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Preferences for key: ").append(preferencesKey);
            for (String key : keys) {
                // Retrieve the value as a String (default fallback for Preferences API)
                String value = prefs.get(key, "[Unknown]");
                sb.append("\n * ").append(key).append(" = ").append(value);
            }
            logger.log(Level.INFO, sb.toString());
        }
    }
    
}