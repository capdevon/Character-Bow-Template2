package mygame;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import com.capdevon.engine.AsyncOperation;
import com.capdevon.engine.Scene;
import com.capdevon.engine.SceneManager;
import com.capdevon.physx.Physics;
import com.capdevon.physx.PhysxDebugAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
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
        // Get the Resolution of the main/default display
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        DisplayMode display = device.getDisplayMode();

        Main app = new Main();

        AppSettings settings = new AppSettings(true);
        settings.setResolution(800, 600);
        settings.setBitsPerPixel(display.getBitDepth());
        settings.setFrequency(display.getRefreshRate());
        //settings.setFrameRate(60);
        //settings.setUseJoysticks(true);

        app.setSettings(settings);
        app.setShowSettings(true);
        app.setPauseOnLostFocus(false);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        // disable the default 1st-person flyCam!
        stateManager.detach(stateManager.getState(FlyCamAppState.class));
        flyCam.setEnabled(false);

        /**
         * Initialize the physics simulation
         */
        Physics.initEngine(this);
        //stateManager.attach(new BulletAppState());
        stateManager.attach(new PhysxDebugAppState());
        //stateManager.attach(new DetailedProfilerState());
        //stateManager.attach(new BasicProfilerState(false));
        //stateManager.attach(new SceneAppState());
        //stateManager.attach(new CubeAppState());
        //stateManager.attach(new GInputAppState());
        //stateManager.attach(new ParticleManager());
        //stateManager.attach(new PlayerManager());

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
        }
    }

    private void onLoadSceneComplete(boolean sceneLoaded) {
        if (!sceneLoaded) {
            System.out.println("An error occurred while loading scene: " + currScene.getName());
        }
        System.out.println("loadLevel completed");
    }

    private void loadSceneAsync() {
        AsyncOperation operation = sceneManager.loadSceneAsync(currScene);
        operation.onCompleted(b -> onLoadSceneComplete((boolean) b));
        System.out.println("loading scene: " + currScene.getName());
    }

}
