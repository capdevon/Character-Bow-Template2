package mygame.states;

import com.capdevon.audio.SoundManager;
import com.capdevon.engine.SimpleAppState;
import com.jme3.app.Application;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.environment.EnvironmentCamera;
import com.jme3.environment.LightProbeFactory;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.filters.TranslucentBucketFilter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.util.SkyFactory;

import mygame.util.AudioLib;

/**
 * 
 * @author capdevon
 */
public class TownAppState extends SimpleAppState {

    private DirectionalLight sun;
    private FilterPostProcessor fpp;

    private boolean generateLightProbe = false;

    @Override
    public void initialize(Application app) {
        super.initialize(app);
        
        setupSkyBox();
        setupLights();
        setupScene();
        setupFilters();
    }

    private void setupSkyBox() {
        String texture = "Scenes/Beach/FullskiesSunset0068.dds";
        Spatial sky = SkyFactory.createSky(assetManager, texture, SkyFactory.EnvMapType.CubeMap);
        sky.setShadowMode(RenderQueue.ShadowMode.Off);
        rootNode.attachChild(sky);
    }

    private void setupScene() {
        assetManager.registerLocator("town.zip", ZipLocator.class);
        Spatial scene = assetManager.loadModel("main.scene"); //"Scenes/town/main.scene"
        scene.setName("MainScene");
        scene.setLocalTranslation(0, -5.2f, 0);
        
        CollisionShape shape = CollisionShapeFactory.createMeshShape(scene);
        RigidBodyControl rb = new RigidBodyControl(shape, 0f);
        scene.addControl(rb);
        getPhysicsSpace().add(rb);

        /* nature sound - keeps playing in a loop. */
        AudioNode audio = SoundManager.makeAudio(AudioLib.ENV_NATURE);
        rootNode.attachChild(audio);
        audio.play();
        
        rootNode.attachChild(scene);
        rootNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
    }

    private void setupLights() {
        AmbientLight al = new AmbientLight();
        al.setColor(new ColorRGBA(.5f, .5f, .5f, 1));
        rootNode.addLight(al);
        
        Vector3f lightDir = new Vector3f(-4.9236743f, -1.27054665f, 5.896916f).normalizeLocal();
        //lightDir = new Vector3f(.5f, -1, .3f).normalizeLocal();
        sun = new DirectionalLight(lightDir);
        rootNode.addLight(sun);

        if (generateLightProbe) {
            // Make an env camera
            EnvironmentCamera envCam = new EnvironmentCamera();
            stateManager.attach(envCam);
            // Manually initialize so we can add a probe before the next update happens
            envCam.initialize(stateManager, getApplication());
            LightProbe probe = LightProbeFactory.makeProbe(envCam, rootNode);
            probe.getArea().setRadius(100); // Set the probe's radius in world units
            rootNode.addLight(probe);

        } else {
            // add a PBR probe.
            Spatial probeModel = assetManager.loadModel("Scenes/defaultProbe.j3o");
            LightProbe lightProbe = (LightProbe) probeModel.getLocalLightList().get(0);
            lightProbe.getArea().setRadius(100);
            rootNode.addLight(lightProbe);
        }
    }

    private void setupFilters() {
        // Shadows
        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, 2048, 3);
        dlsf.setLight(sun);
        dlsf.setEdgesThickness(5);
        dlsf.setShadowIntensity(0.65f);
        dlsf.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);

        FXAAFilter fxaa = new FXAAFilter();
        TranslucentBucketFilter tbf = new TranslucentBucketFilter(true);

        fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(dlsf);
        fpp.addFilter(fxaa);
        fpp.addFilter(tbf);
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
        viewPort.addProcessor(fpp);
    }

    @Override
    protected void onDisable() {
        viewPort.removeProcessor(fpp);
    }

}
