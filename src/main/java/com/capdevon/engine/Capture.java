package com.capdevon.engine;

import java.io.File;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.app.Application;
import com.jme3.app.state.VideoRecorderAppState;
import com.jme3.system.AppSettings;

/**
 *
 * @author capdevon
 */
public class Capture {
    
    private static final Logger logger = Logger.getLogger(Capture.class.getName());

    /**
     * A private constructor to inhibit instantiation of this class.
     */
    private Capture() {
    }

    /**
     * Captures a video recording of the application's output.
     *
     * @param app     The Application instance for which to capture video.
     * @param quality The quality of the video recording, ranging from 0.0f to 1.0f (default: 0.5f).
     */
    public static void captureVideo(Application app, float quality) {
        AppSettings settings = app.getContext().getSettings();
        int frameRate = settings.getFrameRate();
        if (settings.getFrameRate() < 0) {
            throw new IllegalArgumentException("FrameRate must not be negative: " + frameRate);
        }
        
        String dirName = System.getProperty("user.dir");
        String fileName = UUID.randomUUID() + ".avi";
        File file = new File(dirName, fileName);
        
        VideoRecorderAppState recorder = new VideoRecorderAppState(file, quality, frameRate);
        app.getStateManager().attach(recorder);

        logger.log(Level.WARNING, "Start VideoRecorder=" + file.getAbsolutePath());
    }

}
