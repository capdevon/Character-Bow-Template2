package mygame.states;

import com.capdevon.control.TimerControl;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
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

    private SimpleApplication simpleApp;
    private Node rootLocal;

    @Override
    protected void initialize(Application app) {
        this.simpleApp = (SimpleApplication) app;
        rootLocal = new Node("Particles");
    }

    @Override
    protected void cleanup(Application app) {
        rootLocal.detachAllChildren();
    }

    @Override
    protected void onEnable() {
        simpleApp.getRootNode().attachChild(rootLocal);
    }

    @Override
    protected void onDisable() {
        simpleApp.getRootNode().attachChild(rootLocal);
    }

    public void playEffect(String name, Vector3f location, float lifeTime) {

        Node emitter = (Node) simpleApp.getAssetManager().loadModel(name);
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
