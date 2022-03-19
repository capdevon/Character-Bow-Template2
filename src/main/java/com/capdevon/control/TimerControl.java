package com.capdevon.control;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author capdevon
 */
public abstract class TimerControl extends AbstractControl {

    private float lifeTime = 0;
    private float maxLifeTime = 0;

    public TimerControl(float maxLifeTime) {
        this.maxLifeTime = maxLifeTime;
    }

    @Override
    protected void controlUpdate(float tpf) {
        lifeTime += tpf;
        if (lifeTime > maxLifeTime) {
            onTrigger();
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        // TODO Auto-generated method stub
    }

    public abstract void onTrigger();
}
