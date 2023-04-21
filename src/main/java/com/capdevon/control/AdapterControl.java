package com.capdevon.control;

import java.util.Objects;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author capdevon
 */
public class AdapterControl extends AbstractControl {

    /**
     * Returns the first child found with exactly the given name
     * (case-sensitive).
     *
     * @param name - the name of the child to retrieve
     * @return
     */
    public Spatial getChild(String name) {
        Spatial child = ((Node) spatial).getChild(name);
        if (child == null) {
            String error = "The child %s could not be found";
            throw new NullPointerException(String.format(error, name));
        }
        return child;
    }

    public <T> T getUserData(String key, boolean failOnMiss) {
        T objValue = spatial.getUserData(key);
        if (failOnMiss) {
            String error = "The UserData %s could not be found";
            return Objects.requireNonNull(objValue, String.format(error, key));
        }
        return objValue;
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
