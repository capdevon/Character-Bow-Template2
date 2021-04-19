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
import com.jme3.animation.Bone;
import com.jme3.animation.LoopMode;
import com.jme3.animation.SkeletonControl;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * 
 * @author capdevon
 */
public class Animator extends AdapterControl {

	private static final Logger logger = Logger.getLogger(Animator.class.getName());

	private SkeletonControl skControl;
	private AnimControl animControl;
	private AnimChannel animChannel;

	@Override
	public void setSpatial(Spatial sp) {
		super.setSpatial(sp);
		if (spatial != null) {
			this.skControl 	 = getComponentInChild(SkeletonControl.class);
			this.animControl = getComponentInChild(AnimControl.class);
			this.animChannel = animControl.createChannel();

			System.out.println(spatial.getName() + " --Animations: " + animControl.getAnimationNames());
		}
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

	public Bone getBone(String boneName) {
		return skControl.getSkeleton().getBone(boneName);
	}

	public Node getAttachments(String boneName) {
		return skControl.getAttachmentsNode(boneName);
	}

	public Spatial getRootMotion() {
		return animControl.getSpatial();
	}

	public String getAnimationName() {
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

}
