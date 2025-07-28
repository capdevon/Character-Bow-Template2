package mygame;

import com.capdevon.audio.SoundManager;
import com.capdevon.engine.AsyncOperation;
import com.capdevon.engine.Scene;
import com.capdevon.engine.SceneManager;
import com.capdevon.physx.Physics;
import com.capdevon.physx.TogglePhysicsDebugState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.material.TechniqueDef;
import com.jme3.renderer.Limits;
import com.jme3.system.AppSettings;

/**
 *
 * @author capdevon
 */
public class Main extends SimpleApplication {

    /**
     * Start the jMonkeyEngine application
     *
     * @param args
     */
    public static void main(String[] args) {
        Main app = new Main();
        AppSettings settings = JmeSettings.getDefault();
        app.setSettings(settings);
        app.setShowSettings(false);
        app.setPauseOnLostFocus(false);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // disable the default 1st-person flyCam!
        stateManager.detach(stateManager.getState(FlyCamAppState.class));
        flyCam.setEnabled(false);
        
        // Rendered configurations
        renderManager.setPreferredLightMode(TechniqueDef.LightMode.SinglePassAndImageBased);
        renderManager.setSinglePassLightBatchSize(2);

        /**
         * JME supports anisotropic filtering only on desktop platforms. Determine the
         * maximum possible degree of anisotropy on the graphics hardware:
         */
        int maxDegree = renderer.getLimits().get(Limits.TextureAnisotropy);
        renderer.setDefaultAnisotropicFilter(Math.min(8, maxDegree));

        /**
         * Initialize the physics simulation
         */
        Physics.initEngine(this);
        SoundManager.init(this);
        //stateManager.attach(new BulletAppState());
        stateManager.attach(new TogglePhysicsDebugState());
        //stateManager.attach(new DetailedProfilerState());
        //stateManager.attach(new BasicProfilerState(false));

        currScene = Boot.Scene1.get();
        sceneManager = new SceneManager();
        stateManager.attach(sceneManager);
    }

    private boolean sceneLoaded = false;
    private Scene currScene;
    private SceneManager sceneManager;

    @Override
    public void simpleUpdate(float tpf) {
//        if (!sceneLoaded) {
//            stateManager.getState(SceneManager.class).loadScene(currScene);
//            sceneLoaded = true;
//        }

        if (!sceneLoaded && sceneManager.isInitialized()) {
            loadSceneAsync();
            sceneLoaded = true;
            //Capture.captureVideo(this, 0.5f);
        }
    }

    private void loadSceneAsync() {
        AsyncOperation operation = sceneManager.loadSceneAsync(currScene);
        operation.onCompleted(b -> onLoadSceneComplete((boolean) b));
        System.out.println("loading scene: " + currScene.getName());
    }
    
    private void onLoadSceneComplete(boolean sceneLoaded) {
        if (!sceneLoaded) {
            System.out.println("An error occurred while loading scene: " + currScene.getName());
        }
        System.out.println("loadLevel completed");
    }

}
