/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.animation;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.capdevon.control.AdapterControl;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.Animation;
import com.jme3.animation.LoopMode;
import com.jme3.animation.SkeletonControl;
import com.jme3.animation.Track;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.SkeletonDebugger;

/**
 *
 * @author capdevon
 */
public class Animator extends AdapterControl {

    private static final Logger logger = Logger.getLogger(Animator.class.getName());

    private SkeletonControl skControl;
    private AnimControl animControl;
    private AnimChannel animChannel;
    private SkeletonDebugger debugger;

    @Override
    public void setSpatial(Spatial sp) {
        super.setSpatial(sp);
        if (spatial != null) {
            this.skControl = getComponentInChildren(SkeletonControl.class);
            this.animControl = getComponentInChildren(AnimControl.class);
            this.animChannel = animControl.createChannel();

            printInfo();
        }
    }

    protected void printInfo() {
        StringBuilder sb = new StringBuilder();
        String r = String.format("Owner: %s, AnimRoot: %s", spatial, animControl.getSpatial());
        sb.append(r);

        for (String name : animControl.getAnimationNames()) {
            Animation anim = animControl.getAnim(name);
            Track[] tracks = anim.getTracks();
            String s = String.format("%n * %s (%d), Length: %f", anim.getName(), tracks.length, anim.getLength());
            sb.append(s);
        }
        logger.log(Level.INFO, sb.toString());
    }

    public void setAnimation(String animName, LoopMode loopMode) {
        if (hasAnimation(animName)) {
            if (!animName.equals(animChannel.getAnimationName())) {
                animChannel.setAnim(animName, .15f);
                animChannel.setSpeed(1);
                animChannel.setLoopMode(loopMode);
            }
        }
    }

    public void setAnimation(Animation3 animation) {
        if (hasAnimation(animation.getName())) {
            if (!animation.getName().equals(animChannel.getAnimationName())) {
                animChannel.setAnim(animation.getName(), animation.getBlendTime());
                animChannel.setSpeed(animation.getSpeed());
                animChannel.setLoopMode(animation.getLoopMode());
            }
        }
    }

    public void crossFade(Animation3 newAnim) {
        float dt = animChannel.getTime();
        animChannel.setAnim(newAnim.getName(), newAnim.getBlendTime());
        animChannel.setSpeed(newAnim.getSpeed());
        animChannel.setLoopMode(newAnim.getLoopMode());
        animChannel.setTime(dt);
    }

    private boolean hasAnimation(String animName) {
        boolean result = animControl.getAnimationNames().contains(animName);
        if (!result) {
            logger.log(Level.WARNING, "Cannot find animation named: {0}", animName);
        }
        return result;
    }

    public Spatial getAnimRoot() {
        return animControl.getSpatial();
    }

    public String getCurrentAnimation() {
        return animChannel.getAnimationName();
    }

    public float getDeltaTime() {
        return animChannel.getTime() / animChannel.getAnimMaxTime();
    }

    public void addAnimListener(AnimEventListener listener) {
        animControl.addListener(listener);
    }

    public void removeAnimListener(AnimEventListener listener) {
        animControl.removeListener(listener);
    }
    
    public void disableSkeletonDebug() {
        debugger.removeFromParent();
        debugger = null;
    }

    public void enableSkeletonDebug(AssetManager asm) {
        if (debugger == null) {
            Node animRoot = (Node) skControl.getSpatial();
            String name = animRoot.getName() + "_Skeleton";
            debugger = new SkeletonDebugger(name, skControl.getSkeleton());
            debugger.setMaterial(createWireMaterial(asm));
            animRoot.attachChild(debugger);
        }
    }

    private Material createWireMaterial(AssetManager asm) {
        Material mat = new Material(asm, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        mat.getAdditionalRenderState().setWireframe(true);
        mat.getAdditionalRenderState().setDepthTest(false);
        return mat;
    }

}
