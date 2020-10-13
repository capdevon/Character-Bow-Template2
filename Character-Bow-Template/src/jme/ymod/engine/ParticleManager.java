/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jme.ymod.engine;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.effect.ParticleEmitter;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;


/**
 *
 */
public class ParticleManager extends SimpleAppState {

    private final Node rootLocal = new Node("RootLocal");
    private final List<EmitterData> liveEmitters;
    private GenericKeyedObjectPool<String, Node> pool;

    public ParticleManager() {
        liveEmitters = new LinkedList<>();
    }

    @Override
    protected void simpleInit() {
        pool = new GenericKeyedObjectPool<>(new ParticleFactory(assetManager));
        pool.setTestOnBorrow(true);
        pool.setMaxTotal(10);
        rootNode.attachChild(rootLocal);
    }

    @Override
    public void cleanup() {
        try {
            // Close this pool, and free any resources associated with it.
            pool.close();
            rootLocal.detachAllChildren();
        } catch (Exception e) {
        }
    }

    @Override
    public void update(float tpf) {
        Iterator<EmitterData> it = liveEmitters.iterator();
        while (it.hasNext()) {

            EmitterData data = it.next();
            data.update(tpf);

            if (!data.isEnabled) {
                it.remove();
                data.stop();
                rootLocal.detachChild(data.emitter);

                try {
                    pool.returnObject(data.name, data.emitter);
                } catch (Exception e) {
                }
            }
        }
    }

    public void playEffect(String name, Vector3f location, float time) {
        try {
            Node emitter = pool.borrowObject(name);
            emitter.setLocalTranslation(location);
            rootLocal.attachChild(emitter);

            EmitterData data = new EmitterData(emitter, name, time);
            liveEmitters.add(data);

            data.play();

        } catch (Exception e) {
        }
    }

    /**
     * ------------------------------------------------------------------
     * @EmitterData
     * ------------------------------------------------------------------
     */
    private class EmitterData {

        public Node emitter;
        public String name;
        public float time;
        public float curTime = 0;
        public boolean isEnabled = true;

        public EmitterData(Node emitter, String name, float time) {
            this.emitter = emitter;
            this.name = name;
            this.time = time;
        }

        public void update(float tpf) {
            curTime += tpf;
            if (curTime > time) {
                isEnabled = false;
            }
        }

        protected void stop() {
            for (Spatial sp : emitter.getChildren()) {
                if (sp instanceof AudioNode) {
                    ((AudioNode) sp).stop();

                } else if (sp instanceof ParticleEmitter) {
                    ((ParticleEmitter) sp).killAllParticles();
                }
            }
        }

        protected void play() {
            for (Spatial sp : emitter.getChildren()) {
                if (sp instanceof AudioNode) {
                    ((AudioNode) sp).play();

                } else if (sp instanceof ParticleEmitter) {
                    ((ParticleEmitter) sp).emitAllParticles();
                }
            }
        }

    }

    /**
     * ------------------------------------------------------------------
     * @ParticleFactory
     * ------------------------------------------------------------------
     */
    private class ParticleFactory extends BaseKeyedPoolableObjectFactory<String, Node> {

        private final AssetManager assetManager;

        /**
         */
        public ParticleFactory(AssetManager assetManager) {
            this.assetManager = assetManager;
        }

        @Override
        public Node makeObject(String name) throws Exception {
            return (Node) assetManager.loadModel(name);
        }

        @Override
        public boolean validateObject(String key, Node obj) {
            System.out.println(obj.getName() + '@' + Integer.toHexString(obj.hashCode()));
            return true;
        }
    }
}
