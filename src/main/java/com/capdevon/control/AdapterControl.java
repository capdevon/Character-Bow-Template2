/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.control;

import java.util.Objects;

import com.capdevon.engine.GameObject;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 *
 * @author capdevon
 */
public class AdapterControl extends AbstractControl {

    public Spatial getChild(String name) {
        return getChild(name, Spatial.class);
    }

    public <T extends Spatial> T getChild(String name, Class<T> spatialType) {
        Node root = (Node) spatial;
        T child = (T) root.getChild(name);

        if (child == null) {
            String error = "The child %s could not be found";
            throw new NullPointerException(String.format(error, name));
        }
        return child;
    }

    public <T> T getUserData(String key, boolean failOnMiss) {
        T objValue = spatial.getUserData(key);
        if (failOnMiss) {
            String error = "The component data %s could not be found";
            return Objects.requireNonNull(objValue, String.format(error, key));
        }
        return objValue;
    }

    /**
     * Returns the component of Type type if the game object has one attached,
     * null if it doesn't.
     */
    public <T extends Control> T getComponent(Class<T> clazz) {
        T control = spatial.getControl(clazz);
        return control;
    }

    /**
     * Returns the component of Type type in the GameObject or any of its
     * children using depth first search.
     */
    public <T extends Control> T getComponentInChildren(final Class<T> clazz) {
        return GameObject.getComponentInChildren(spatial, clazz);
    }

    /**
     * Retrieves the component of Type type in the GameObject or any of its
     * parents.
     */
    public <T extends Control> T getComponentInParent(Class<T> clazz) {
        return GameObject.getComponentInParent(spatial, clazz);
    }

    @Override
    protected void controlUpdate(float tpf) {
        //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //To change body of generated methods, choose Tools | Templates.
    }

}
