package com.capdevon.audio;

import java.util.HashMap;
import java.util.Map;

import com.jme3.app.Application;
import com.jme3.audio.AudioData.DataType;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource.Status;

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

    public static AudioNode makeAudioBuffer(String name) {
        AudioNode audio = new AudioNode(app.getAssetManager(), name, DataType.Buffer);
        audio.setPositional(false);
        return audio;
    }
    
    public static AudioNode makeAudioStream(String name) {
        AudioNode audio = new AudioNode(app.getAssetManager(), name, DataType.Stream);
        audio.setPositional(false);
        return audio;
    }
    
    public static AudioNode makeAudio(AudioClip ac) {
        AudioNode audio = new AudioNode(app.getAssetManager(), ac.file, ac.dataType);
        audio.setVolume(ac.volume);
        audio.setLooping(ac.looping);
        audio.setPositional(ac.positional);
        return audio;
    }

    public static void registerAudio(String name, AudioClip clip) {
        if (soundsMap.get(name) == null) {
            soundsMap.put(name, makeAudio(clip));
        }
    }
    
    public static void clear() {
        soundsMap.clear();
    }
    
    public static AudioNode getAudioNode(String name) {
        return soundsMap.get(name);
    }

    public static void stopAll() {
        for (Map.Entry<String, AudioNode> entry : soundsMap.entrySet()) {
            AudioNode audio = entry.getValue();
            if (audio.getStatus() == Status.Playing) {
                audio.stop();
            }
        }
    }

    public static void pauseAll() {
        for (Map.Entry<String, AudioNode> entry : soundsMap.entrySet()) {
            AudioNode audio = entry.getValue();
            if (audio.getStatus() == Status.Playing) {
                audio.pause();
            }
        }
    }

    public static void resumeAll() {
        for (Map.Entry<String, AudioNode> entry : soundsMap.entrySet()) {
            AudioNode audio = entry.getValue();
            if (audio.getStatus() == Status.Paused) {
                audio.play();
            }
        }
    }

}
