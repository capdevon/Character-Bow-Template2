package com.capdevon.anim;

import com.jme3.anim.Joint;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;

/**
 *
 * @author capdevon
 */
public class IKControl {

    // The avatar mask associated with this IK control.
    private final AvatarMask mask;
    // The joint that this IK control manipulates.
    private final Joint joint;
    // The target position for the IK effect.
    private final Vector3f ikPosition = new Vector3f();
    // The target rotation for the IK effect.
    private final Quaternion ikRotation = new Quaternion();
    // The target scale for the IK effect.
    private final Vector3f ikScale = new Vector3f();
    // The weight of the IK effect, ranging from 0.0f (no effect) to 1.0f (full effect).
    private float weight = 1f;
    // Indicates whether the IK target is under user control.
    private boolean userControl;

    /**
     * Instantiate an IKControl.
     *
     * @param mask  The avatar mask associated with this IK control.
     * @param joint The joint to be manipulated by this IK control.
     */
    protected IKControl(AvatarMask mask, Joint joint) {
        this.mask = mask;
        this.joint = joint;
        Transform tr = joint.getInitialTransform();
        tr.getTranslation(ikPosition);
        tr.getRotation(ikRotation);
        tr.getScale(ikScale);
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    /**
     * Returns true if this joint can be directly manipulated by the user.
     *
     * @return true if it can be manipulated
     */
    public boolean hasUserControl() {
        return userControl;
    }

    /**
     * If enabled, user can control joint transform. Animation transforms are
     * not applied to this bone when enabled.
     *
     * @param enable true for direct control, false for canned animations
     */
    public void setUserControl(boolean enable) {
        this.userControl = enable;
        if (enable) {
            mask.removeJoints(joint.getName());
        } else {
            mask.addJoints(joint.getName());
        }
    }

    public Vector3f getIKPosition() {
        return ikPosition;
    }

    public void setIKPosition(Vector3f ikPosition) {
        this.ikPosition.set(ikPosition);
    }

    public Quaternion getIKRotation() {
        return ikRotation;
    }

    public void setIKRotation(Quaternion ikRotation) {
        this.ikRotation.set(ikRotation);
    }

    public Vector3f getIKScale() {
        return ikScale;
    }

    public void setIKScale(Vector3f ikScale) {
        this.ikScale.set(ikScale);
    }

    protected void update() {
        if (userControl) {
            Transform tr = joint.getInitialTransform();
            joint.getLocalRotation().slerp(tr.getRotation(), ikRotation, weight);
            joint.getLocalTranslation().interpolateLocal(tr.getTranslation(), ikPosition, weight);
            joint.getLocalScale().interpolateLocal(tr.getScale(), ikScale, weight);
        }
    }

}
