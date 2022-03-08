package mygame;

import com.capdevon.engine.Scene;
import com.capdevon.input.GInputAppState;
import com.capdevon.physx.PhysxDebugAppState;
import com.jme3.bullet.BulletAppState;

import mygame.player.PlayerManager;
import mygame.states.CubeAppState;
import mygame.states.ParticleManager;
import mygame.states.TownAppState;

/**
 *
 * @author capdevon
 */
public enum Boot {

    Scene1 {
        @Override
        public Scene get() {
            Scene scene = new Scene("Scene 1");
            scene.addSystemPrefab(BulletAppState.class);
            scene.addSystemPrefab(PhysxDebugAppState.class);
            scene.addSystemPrefab(TownAppState.class);
            scene.addSystemPrefab(CubeAppState.class);
            scene.addSystemPrefab(GInputAppState.class);
            scene.addSystemPrefab(ParticleManager.class);
            scene.addSystemPrefab(PlayerManager.class);
            //scene.addSystemPrefab(DetailedProfilerState.class);
            //scene.addSystemPrefab(BasicProfilerState.class);
            return scene;
        }
    };

    public abstract Scene get();

}
