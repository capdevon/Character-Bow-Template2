package mygame.states;

import com.capdevon.control.TimerControl;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.effect.ParticleEmitter;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * 
 * @author capdevon
 */
public class ParticleManager extends BaseAppState {

    private AssetManager assetManager;
    private final Node rootLocal = new Node("Particles");

    @Override
    protected void initialize(Application app) {
        this.assetManager = app.getAssetManager();
    }

    @Override
    protected void cleanup(Application app) {
        rootLocal.detachAllChildren();
    }

    @Override
    protected void onEnable() {
        getRootNode().attachChild(rootLocal);
    }

    @Override
    protected void onDisable() {
        getRootNode().detachChild(rootLocal);
    }

    private Node getRootNode() {
        return ((SimpleApplication) getApplication()).getRootNode();
    }

    public void playEffect(String name, Vector3f location, float lifeTime) {

        Node emitter = (Node) assetManager.loadModel(name);
        emitter.setLocalTranslation(location);
        rootLocal.attachChild(emitter);

        EmitterData data = new EmitterData(emitter);
        emitter.addControl(new TimerControl(lifeTime) {
            @Override
            public void onTrigger() {
                data.stop();
                rootLocal.detachChild(emitter);
            }
        });

        // play effect
        data.play();
    }

    /**
     * ------------------------------------------------------------------
     * @EmitterData 
     * ------------------------------------------------------------------
     */
    private class EmitterData {

        public Node emitter;

        public EmitterData(Node emitter) {
            this.emitter = emitter;
        }

        protected void stop() {
            for (Spatial sp: emitter.getChildren()) {
                if (sp instanceof AudioNode) {
                    ((AudioNode) sp).stop();

                } else if (sp instanceof ParticleEmitter) {
                    ((ParticleEmitter) sp).killAllParticles();
                }
            }
        }

        protected void play() {
            for (Spatial sp: emitter.getChildren()) {
                if (sp instanceof AudioNode) {
                    ((AudioNode) sp).play();

                } else if (sp instanceof ParticleEmitter) {
                    ((ParticleEmitter) sp).emitAllParticles();
                }
            }
        }

    }

}
