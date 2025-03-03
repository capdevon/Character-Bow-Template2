package com.capdevon.engine;

import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.jme3.app.state.BaseAppState;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * 
 * @author capdevon
 */
public class SceneManager extends BaseAppState {
    
    private Scene currScene;
    private AsyncOperation asyncOperation;
    private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2);
    
    @Override
    protected void initialize(Application app) {
        System.out.println(".....................SceneManager initialized");
        System.out.println("Available Processors: " + Runtime.getRuntime().availableProcessors());
    }

    @Override
    protected void cleanup(Application app) {
        executor.shutdown();
        System.out.println(".....................SceneManager cleanup");
    }
    
    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }
    
    /**
     * Unloads the Scene asynchronously in the background
     * @param newScene
     * @return 
     */
    public AsyncOperation unloadSceneAsync(Scene newScene) {
        currScene = newScene;
        // Run a task specified by a Supplier object asynchronously
        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> unloadScene(true), executor);
        asyncOperation = new AsyncOperation(future);
        return asyncOperation;
    }
    
    public void unloadScene(Scene newScene) {
        currScene = newScene;
        asyncOperation = null;
        unloadScene(false);
    }
    
    /**
     * Loads the Scene asynchronously in the background
     * @param newScene
     * @return 
     */
    public AsyncOperation loadSceneAsync(Scene newScene) {
        currScene = newScene;
        // Run a task specified by a Supplier object asynchronously
        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> loadScene(true), executor);
        asyncOperation = new AsyncOperation(future);
        return asyncOperation;
    }
    
    public void loadScene(Scene newScene) {
        currScene = newScene;
        asyncOperation = null;
        loadScene(false);
    }
    
    private boolean loadScene(boolean asynch) {
        // attach all systemPrefabs
        int i = 1;
        for (Class<? extends AppState> clazz : currScene.systemPrefabs) {
            try {
                AppState appState = clazz.getDeclaredConstructor().newInstance();
                getStateManager().attach(appState);
                System.out.println("attaching ... AppState: " + clazz.getCanonicalName());
                
                if (asynch) {
                    while (!appState.isInitialized()) {
                        Thread.sleep(500);
                    }
                    System.out.println("AppState Attached: " + clazz.getCanonicalName());
                    updateProgress(i);
                    i++;
                }
            } catch (ReflectiveOperationException | InterruptedException ex) {
                System.err.println(ex);
                return false;
            }
        }
        
        return true;
    }

    private boolean unloadScene(boolean asynch) {
        // detach all systemPrefabs
        int i = 1;
        for (Class<? extends AppState> clazz : currScene.systemPrefabs) {
            try {
                AppState appState = getState(clazz);
                if (appState != null) {
                    getStateManager().detach(appState);
                    System.out.println("detaching ... AppState: " + clazz.getCanonicalName());

                    if (asynch) {
                        while (appState.isInitialized()) {
                            Thread.sleep(500);
                        }
                        System.out.println("AppState Detached: " + clazz.getCanonicalName());
                        updateProgress(i);
                        i++;
                    }
                }
            } catch (InterruptedException ex) {
                System.err.println(ex);
                return false;
            }
        }

        return true;
    }
    
    private void updateProgress(float i) throws InterruptedException {
        float progress = (i / currScene.systemPrefabs.size()) * 100;
        int value = Math.round(progress);

        System.out.println("progress: " + value + "%");
        asyncOperation.setProgress(value);

        Thread.sleep(500);
    }

}
