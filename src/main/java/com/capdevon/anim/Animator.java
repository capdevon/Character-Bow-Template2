package com.capdevon.anim;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.capdevon.animation.Animation3;
import com.capdevon.control.AdapterControl;
import com.jme3.anim.AnimClip;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.SkinningControl;
import com.jme3.anim.tween.Tween;
import com.jme3.anim.tween.Tweens;
import com.jme3.anim.tween.action.Action;
import com.jme3.anim.tween.action.BaseAction;
import com.jme3.animation.LoopMode;
import com.jme3.scene.Spatial;

/**
 *
 * @author capdevon
 */
public class Animator extends AdapterControl {

    private static final Logger logger = Logger.getLogger(Animator.class.getName());

    private AnimComposer animComposer;
    private SkinningControl skinningControl;
    private String currentAnim;
    private ArrayList<ActionAnimEventListener> listeners = new ArrayList<>();

    @Override
    public void setSpatial(Spatial sp) {
        super.setSpatial(sp);

        if (spatial != null) {
            logger.log(Level.INFO, "Setup: {0}", spatial);
            animComposer = getComponentInChild(AnimComposer.class);
            skinningControl = getComponentInChild(SkinningControl.class);

            for (AnimClip animClip : animComposer.getAnimClips()) {
                setAnimCallback(animClip.getName(), true);
            }
        }
    }

    public void setAnimCallback(String animName, boolean loop) {
        logger.log(Level.INFO, "setAnimCallback: {0}", animName);
        Action action = animComposer.action(animName);
        Tween callback = Tweens.callMethod(this, "notifyAnimCycleDone", animName, loop);
        action = new BaseAction(Tweens.sequence(action, callback));
        animComposer.addAction(animName, action);
    }

    /**
     * @param anim (not null)
     */
    public void setAnimCallback(Animation3 anim) {
        String animName = anim.getName();
        float speed = anim.getSpeed();
        boolean isLooping = (anim.getLoopMode() == LoopMode.Loop);
        setAnimCallback(animName, isLooping);

        /*
        // Get action registered with specified name. It will make a new action if there isn't any.
        Tween delegate = animComposer.action(animName);
        // Configure custom action with specified name, layer, loop, speed and listener.
        CustomAction action = new CustomAction(delegate, animComposer, animName, AnimComposer.DEFAULT_LAYER);
        action.setLooping(isLooping);
        action.setSpeed(speed);
        // Register custom action with specified name.
        animComposer.addAction(animName, action);
         */
    }

    /**
     * Run an action on the default layer.
     * @param name The name of the action to run.
     */
    public void setAnimation(Animation3 anim) {
        setAnimation(anim.getName(), false);
    }

    /**
     * Run an action on the default layer.
     * @param name The name of the action to run.
     */
    public void setAnimation(String animName, boolean override) {
        if (override || !animName.equals(currentAnim)) {
            animComposer.setCurrentAction(animName);
            notifyAnimChange(animName);
        }
    }

    public void crossFade(Animation3 newAnim) {
        crossFade(newAnim.getName());
    }

    public void crossFade(String animName) {
        double dt = animComposer.getTime();
        animComposer.setCurrentAction(animName);
        animComposer.setTime(dt);
        notifyAnimChange(animName);
    }

    public String getAnimation() {
        return currentAnim;
    }

    public Spatial getAnimRoot() {
        return animComposer.getSpatial();
    }

    public AnimComposer getAnimComposer() {
        return animComposer;
    }

    public SkinningControl getSkinningControl() {
        return skinningControl;
    }

    /**
     * Adds a new listener to receive animation related events.
     */
    public void addListener(ActionAnimEventListener listener) {
        if (listeners.contains(listener)) {
            throw new IllegalArgumentException("The given listener is already "
                    + "registered at this Animator");
        }

        listeners.add(listener);
    }

    /**
     * Removes the given listener from listening to events.
     */
    public void removeListener(ActionAnimEventListener listener) {
        if (!listeners.remove(listener)) {
            throw new IllegalArgumentException("The given listener is not "
                    + "registered at this Animator");
        }
    }

    /**
     * Clears all the listeners added to this <code>Animator</code>
     */
    public void clearListeners() {
        listeners.clear();
    }

    void notifyAnimChange(String name) {
    	currentAnim = name;
        for (ActionAnimEventListener listener : listeners) {
            listener.onAnimChange(animComposer, name);
        }
    }

    void notifyAnimCycleDone(String name, boolean loop) {
        for (ActionAnimEventListener listener : listeners) {
            listener.onAnimCycleDone(animComposer, name, loop);
        }
    }

}
