package mygame.states;

import com.capdevon.engine.SimpleAppState;
import com.jme3.app.Application;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.environment.LightProbeFactory;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.filters.TranslucentBucketFilter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.util.SkyFactory;

/**
 * 
 * @author capdevon
 */
public class TownAppState extends SimpleAppState {

    private DirectionalLight sun;
    private FilterPostProcessor fpp;

    boolean lightProbeEnabled = false;

    @Override
    public void initialize(Application app) {
        super.initialize(app);
        
        setupSkyBox();
        setupScene();
        setupLights();
        setupFilters();
    }

    private void setupSkyBox() {
        Spatial sky = SkyFactory.createSky(assetManager, "Scenes/Beach/FullskiesSunset0068.dds", SkyFactory.EnvMapType.CubeMap);
        sky.setShadowMode(RenderQueue.ShadowMode.Off);
        rootNode.attachChild(sky);
    }

    private void setupScene() {
        assetManager.registerLocator("town.zip", ZipLocator.class);
        Spatial scene = assetManager.loadModel("main.scene");
        //Spatial scene = am.loadModel("Scenes/town/main.scene");
        scene.setName("MainScene");
        scene.setLocalTranslation(0, -5.2f, 0);
        rootNode.attachChild(scene);
        rootNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        rootNode.setQueueBucket(RenderQueue.Bucket.Opaque);

        CollisionShape shape = CollisionShapeFactory.createMeshShape(scene);
        RigidBodyControl rgb = new RigidBodyControl(shape, 0f);
        scene.addControl(rgb);
        getPhysicsSpace().add(rgb);

        /* nature sound - keeps playing in a loop. */
        AudioNode audio = new AudioNode(assetManager, "Sound/Environment/Nature.ogg", AudioData.DataType.Stream);
        audio.setLooping(true);
        audio.setPositional(false);
        audio.setVolume(2);
        rootNode.attachChild(audio);
        audio.play();
    }

    private void setupLights() {
        AmbientLight al = new AmbientLight();
        rootNode.addLight(al);
        
        Vector3f lightDir = new Vector3f(-4.9236743f, -1.27054665f, 5.896916f).normalizeLocal();
        sun = new DirectionalLight(lightDir);
        rootNode.addLight(sun);

        if (lightProbeEnabled) {
            EnvironmentCamera envCam = new EnvironmentCamera(); // Make an env camera
            stateManager.attach(envCam);
            envCam.initialize(stateManager, getApplication()); // Manually initialize so we can add a probe before the next update happens
            LightProbe probe = LightProbeFactory.makeProbe(envCam, rootNode);
            probe.getArea().setRadius(100); // Set the probe's radius in world units
            rootNode.addLight(probe);
        }
    }

    private void setupFilters() {
        // Shadows
        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, 2048, 3);
        dlsf.setLight(sun);
        dlsf.setShadowIntensity(0.65f);

        FXAAFilter fxaa = new FXAAFilter();
        TranslucentBucketFilter tbf = new TranslucentBucketFilter(true);

        fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(dlsf);
        fpp.addFilter(fxaa);
        fpp.addFilter(tbf);
        viewPort.addProcessor(fpp);
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

}
