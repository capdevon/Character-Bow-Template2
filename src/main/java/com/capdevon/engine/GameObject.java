package com.capdevon.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

/**
 * https://docs.unity3d.com/ScriptReference/GameObject.html
 *
 * @author capdevon
 */
public class GameObject {

    /**
     * A private constructor to inhibit instantiation of this class.
     */
    private GameObject() {
    }

    public static final String TAG_NAME = "TagName";

    /**
     * Is Spatial tagged with tag ?
     */
    public static boolean compareTag(Spatial sp, String tag) {
        return Objects.equals(sp.getUserData(TAG_NAME), tag);
    }

    /**
     * Returns one active GameObject tagged tag. Returns null if no GameObject
     * was found.
     */
    public static Spatial findWithTag(Spatial subtree, String tag) {
        List<Spatial> lst = findGameObjectsWithTag(subtree, tag);
        return lst.isEmpty() ? null : lst.get(0);
    }

    /**
     * Returns an array of active GameObjects tagged tag. Returns empty array if
     * no GameObject was found.
     */
    public static List<Spatial> findGameObjectsWithTag(Spatial subtree, String tag) {
        List<Spatial> lst = new ArrayList<>();
        subtree.depthFirstTraversal(new SceneGraphVisitor() {
            @Override
            public void visit(Spatial sp) {
                if (tag.equals(sp.getUserData(TAG_NAME))) {
                    lst.add(sp);
                }
            }
        });
        return lst;
    }

    /**
     * Returns all components of Type type in the GameObject or any of its
     * children using depth first search. Works recursively.
     */
    public static <T extends Control> List<T> getComponentsInChildren(Spatial subtree, Class<T> clazz) {
        List<T> lst = new ArrayList<>(5);
        subtree.depthFirstTraversal(new SceneGraphVisitor() {
            @Override
            public void visit(Spatial sp) {
                T control = sp.getControl(clazz);
                if (control != null) {
                    lst.add(control);
                }
            }
        });
        return lst;
    }

    /**
     * Returns the component of Type type in the GameObject or any of its
     * children using depth first search.
     */
    public static <T extends Control> T getComponentInChildren(Spatial subtree, final Class<T> type) {
        T control = subtree.getControl(type);
        if (control != null) {
            return control;
        }

        if (subtree instanceof Node) {
            for (Spatial child : ((Node) subtree).getChildren()) {
                control = getComponentInChildren(child, type);
                if (control != null) {
                    return control;
                }
            }
        }

        return null;
    }

    /**
     * Retrieves the component of Type type in the GameObject or any of its
     * parents.
     */
    public static <T extends Control> T getComponentInParent(Spatial subtree, Class<T> type) {
        Node parent = subtree.getParent();
        while (parent != null) {
            T control = parent.getControl(type);
            if (control != null) {
                return control;
            }
            parent = parent.getParent();
        }
        return null;
    }

}
