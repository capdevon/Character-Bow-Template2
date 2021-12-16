package mygame;

import java.util.Arrays;

import com.capdevon.engine.Scene;
import com.capdevon.input.GInputAppState;
import com.capdevon.physx.PhysxDebugAppState;
import com.jme3.app.DetailedProfilerState;
import com.jme3.bullet.BulletAppState;

import mygame.states.CubeAppState;
import mygame.states.SceneAppState;

/**
 *
 * @author capdevon
 */
public class Boot {

    public static final Scene scene_01 = new Scene();

    static {
        scene_01.name = "Scene 1";
        scene_01.systemPrefabs = Arrays.asList(
                BulletAppState.class,
                PhysxDebugAppState.class,
                SceneAppState.class,
                CubeAppState.class,
                GInputAppState.class,
                ParticleManager.class,
                PlayerManager.class,
                DetailedProfilerState.class
                //BasicProfilerState.class
        );
    }

}
