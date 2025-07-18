package mygame.util;

import com.capdevon.audio.AudioClip;
import com.jme3.audio.AudioData;

public interface AudioLib {

    AudioClip ENV_NATURE        = new AudioClip("Sound/Environment/Nature.ogg", 2f, true, false, AudioData.DataType.Stream);
    AudioClip BOW_PULL          = new AudioClip("Sounds/Archer/bow-pull.wav", 1f);
    AudioClip ARROW_HIT         = new AudioClip("Sounds/Archer/arrow-impact-2.wav", 1f);
    AudioClip GRASS_FOOTSTEPS   = new AudioClip("Sounds/Footsteps/Grass-Running-3.wav", .4f, true);

}
