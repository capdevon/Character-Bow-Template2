package com.capdevon.control;

import com.jme3.material.MatParamOverride;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import com.jme3.shader.VarType;

/**
 * @author capdevon
 */
public class Damageable extends AbstractControl {
    
    private MatParamOverride mpo;

    @Override
    protected void controlUpdate(float tpf) {
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
    
    public void takeDamage(ColorRGBA color) {
        if (mpo == null) {
            mpo = new MatParamOverride(VarType.Vector4, "BaseColor", color);
            spatial.addMatParamOverride(mpo);
        } else {
            mpo.setValue(color);
        }
    }

}
