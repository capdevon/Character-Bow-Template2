package com.capdevon.engine;

import com.jme3.app.Application;
import com.jme3.app.state.VideoRecorderAppState;
import com.jme3.system.AppSettings;
import java.io.File;

/**
 *
 * @author capdevon
 */
public class Capture {

    /**
     * A private constructor to inhibit instantiation of this class.
     */
    private Capture() {
    }

    /**
     * @param app
     * @param quality [0.5f]
     * @param dirName
     */
    public static void captureVideo(Application app, float quality, String dirName) {
        AppSettings settings = app.getContext().getSettings();
        long fileId = (System.currentTimeMillis() / 1000);
        String fileName = settings.getTitle() + "-" + fileId + ".avi";
        File file = new File(dirName, fileName);

        int frameRate = settings.getFrameRate();
        if (settings.getFrameRate() < 0) {
            throw new IllegalArgumentException("FrameRate must not be negative: " + frameRate);
        }

        VideoRecorderAppState recorder = new VideoRecorderAppState(file, quality, frameRate);
        app.getStateManager().attach(recorder);

        System.out.println("Start VideoRecorder=" + file.getAbsolutePath());
    }

}
