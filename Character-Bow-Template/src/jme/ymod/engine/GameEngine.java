/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jme.ymod.engine;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public enum GameEngine {

    /* Instance of this singleton. */
    INSTANCE;

    private SimpleApplication app;

    public void init(SimpleApplication appl) {
        this.app = appl;
    }

    public Node getRootNode() {
        return app.getRootNode();
    }

    public Node getGUINode() {
        return app.getGuiNode();
    }

    public <T extends AppState> T getState(Class<T> clazz) {
        return app.getStateManager().getState(clazz);
    }

    public AssetManager getAssetManager() {
        return app.getAssetManager();
    }

    public InputManager getInputManager() {
        return app.getInputManager();
    }

    public AppSettings getSettings() {
        return app.getContext().getSettings();
    }

    /**
     * @param childName
     * @return 
     */
    public Node find(final String childName) {
        final List<Node> lst = new ArrayList<>();
        app.getRootNode().breadthFirstTraversal(new SceneGraphVisitorAdapter() {
            @Override
            public void visit(Node node) {
                if (childName.equals(node.getName())) {
                    lst.add(node);
                }
            }
        });
        if (lst.isEmpty()) {
            String err = "The component %s could not be found";
            throw new RuntimeException(String.format(err, childName));
        }
        return lst.get(0);
    }

    /**
     * @param tagName
     * @return 
     */
    public List<Node> findGameObjectsWithTag(final String tagName) {
        final List<Node> lst = new ArrayList<>();
        app.getRootNode().breadthFirstTraversal(new SceneGraphVisitorAdapter() {
            @Override
            public void visit(Node node) {
                if (tagName.equals(node.getUserData("TagName"))) {
                    lst.add(node);
                }
            }
        });
        return lst;
    }

    /**
     * @param tagName
     * @return 
     */
    public Node findWithTag(final String tagName) {
        List<Node> lst = findGameObjectsWithTag(tagName);
        if (lst.isEmpty()) {
            String err = "The object %s could not be found";
            throw new RuntimeException(String.format(err, tagName));
        }
        return lst.get(0);
    }
    
    /**
     * @param <T>
     * @param spatial
     * @param clazz
     * @return 
     */
    public <T extends Control> T getComponent(Spatial spatial, Class<T> clazz) {
        T control = spatial.getControl(clazz);
        if (control != null) {
            return control;
        }

        if (spatial instanceof Node) {
            for (Spatial child : ((Node) spatial).getChildren()) {
                control = getComponent(child, clazz);
                if (control != null) {
                    return control;
                }
            }
        }

        return null;
    }

}
