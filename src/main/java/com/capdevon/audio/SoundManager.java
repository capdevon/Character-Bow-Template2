package com.capdevon.audio;

import java.util.HashMap;
import java.util.Map;

import com.jme3.app.Application;
import com.jme3.audio.AudioNode;

/**
 *
 * @author capdevon
 */
public class SoundManager {

    private static boolean initialized;
    private static Application app;
    private static Map<String, AudioNode> soundsMap = new HashMap<>();

    private SoundManager() {
        // singleton constructor
    }

    public static void init(Application app) {
        if (!initialized) {
            initialized = true;
            SoundManager.app = app;
        }
    }

    /**
     * @param clip
     * @return
     */
    public static AudioNode makeAudio(AudioClip clip) {
        AudioNode audio = new AudioNode(app.getAssetManager(), clip.file, clip.dataType);
        audio.setVolume(clip.volume);
        audio.setLooping(clip.looping);
        audio.setPositional(clip.positional);
        return audio;
    }

    public static void registerAudio(String name, AudioClip clip) {
        if (soundsMap.get(name) == null) {
            soundsMap.put(name, makeAudio(clip));
        }
    }

    /**
     * Called when all sounds must be stopped.
     */
    public static void stopAll() {
        for (Map.Entry<String, AudioNode> entry : soundsMap.entrySet()) {
            AudioNode audio = entry.getValue();
            audio.stop();
        }
    }

    public static AudioNode getAudioNode(String name) {
        return soundsMap.get(name);
    }

}
