package com.capdevon.audio;

import com.jme3.audio.AudioData;

/**
 *
 * @author capdevon
 */
public class AudioClip {

    public String file;
    public float volume = 1;
    public boolean looping = false;
    public boolean positional = false;
    public AudioData.DataType dataType = AudioData.DataType.Buffer;

    public AudioClip(String file) {
        this.file = file;
    }

    public AudioClip(String file, float volume) {
        this.file = file;
        this.volume = volume;
    }

    public AudioClip(String file, float volume, boolean looping) {
        this.file = file;
        this.volume = volume;
        this.looping = looping;
    }

    public AudioClip(String file, float volume, boolean looping, boolean positional, AudioData.DataType dataType) {
        this.file = file;
        this.volume = volume;
        this.looping = looping;
        this.positional = positional;
        this.dataType = dataType;
    }

    @Override
    public String toString() {
        return "AudioClip [file=" + file
                + ", volume=" + volume
                + ", looping=" + looping
                + ", positional=" + positional
                + ", dataType=" + dataType
                + "]";
    }

}
