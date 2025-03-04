package com.capdevon.anim;

import java.util.ArrayList;

import com.capdevon.control.AdapterControl;
import com.jme3.anim.AnimClip;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.Joint;
import com.jme3.anim.SkinningControl;
import com.jme3.anim.tween.Tween;
import com.jme3.anim.tween.Tweens;
import com.jme3.anim.tween.action.Action;
import com.jme3.scene.Spatial;

/**
 *
 * @author capdevon
 */
public class Animator extends AdapterControl {

    private SkinningControl skinningControl;
    private AnimComposer animComposer;
    private String currentAnim;
    private ArrayList<AnimationListener> listeners = new ArrayList<>();

    @Override
    public void setSpatial(Spatial sp) {
        super.setSpatial(sp);

        if (spatial != null) {
            skinningControl = getComponentInChildren(SkinningControl.class);
            animComposer = getComponentInChildren(AnimComposer.class);
        }
    }

    public void createDefaultActions() {
        for (AnimClip clip : animComposer.getAnimClips()) {
            actionCycleDone(clip.getName(), true);
        }
    }

    /**
     * @param anim (not null)
     */
    public void actionCycleDone(Animation3 anim) {
        String animName = anim.getName();
        boolean isLooping = anim.isLooping();
        actionCycleDone(animName, isLooping).setSpeed(anim.getSpeed());
    }

    public Action actionCycleDone(String animName, boolean loop) {
        // Get action registered with specified name. It will make a new action if there isn't any.
        Action action = animComposer.action(animName);
        Tween doneTween = Tweens.callMethod(this, "notifyAnimCycleDone", animName, loop);
        // Register custom action with specified name.
        return animComposer.actionSequence(animName, action, doneTween);
    }

    /**
     * Run an action with specified anim params.
     */
    public void setAnimation(Animation3 anim) {
        setAnimation(anim.getName(), anim.getLayer());
    }

    /**
     * Run an action on specified layer.
     */
    public void setAnimation(String animName, String layerName) {
        if (!animName.equals(currentAnim)) {
            currentAnim = animName;
            animComposer.setCurrentAction(currentAnim, layerName);
            notifyAnimChange(currentAnim);
        }
    }

    public void crossFade(Animation3 anim) {
        crossFade(anim.getName(), anim.getLayer());
    }

    public void crossFade(String animName, String layerName) {
        currentAnim = animName;
        double dt = animComposer.getTime(layerName);
        animComposer.setCurrentAction(currentAnim, layerName);
        animComposer.setTime(layerName, dt);
        notifyAnimChange(currentAnim);
    }

    public String getCurrentAnimName() {
        return currentAnim;
    }

    public Spatial getAnimRoot() {
        return animComposer.getSpatial();
    }
    
    public Joint getJoint(String name) {
        return skinningControl.getArmature().getJoint(name);
    }

    /**
     * Adds a new listener to receive animation related events.
     */
    public void addListener(AnimationListener listener) {
        if (listeners.contains(listener)) {
            throw new IllegalArgumentException(
                    "The given listener is already registered at this Animator");
        }

        listeners.add(listener);
    }

    /**
     * Removes the given listener from listening to events.
     */
    public void removeListener(AnimationListener listener) {
        if (!listeners.remove(listener)) {
            throw new IllegalArgumentException(
                    "The given listener is not registered at this Animator");
        }
    }

    /**
     * Clears all the listeners added to this <code>Animator</code>
     */
    public void clearListeners() {
        listeners.clear();
    }

    void notifyAnimChange(String name) {
        for (AnimationListener listener : listeners) {
            listener.onAnimChange(animComposer, name);
        }
    }

    void notifyAnimCycleDone(String name, boolean loop) {
        for (AnimationListener listener : listeners) {
            listener.onAnimCycleDone(animComposer, name, loop);
        }
    }

}
