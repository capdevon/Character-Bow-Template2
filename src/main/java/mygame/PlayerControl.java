/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.capdevon.animation.Animation3;
import com.capdevon.animation.Animator;
import com.capdevon.control.AdapterControl;
import com.capdevon.engine.FRotator;
import com.capdevon.physx.Physics;
import com.capdevon.physx.PhysxQuery;
import com.capdevon.physx.RaycastHit;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.input.ChaseCamera;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import mygame.camera.MainCamera;

/**
 * 
 * @author capdevon
 */
public class PlayerControl extends AdapterControl implements AnimEventListener {
	
	private static final Logger logger = Logger.getLogger(PlayerControl.class.getName());

    ParticleManager particleManager;
    Camera camera;
    Weapon weapon;
    AudioNode footstepsSFX;
    AudioNode shootSFX;
    AudioNode reloadSFX;

    private MainCamera _MainCamera;
    public float nearClipPlane = 0.01f;
    public float farClipPlane = 100f;
    private float fov = 0;
    private float aimingSpeed = 5f;
    private float aimFOV = 45;
    private float defaultFOV = 60;

    private ChaseCamera chaseCamera;
    private Animator animator;
    private BetterCharacterControl bcc;
    private final Vector3f walkDirection = new Vector3f(0, 0, 0);
    private final Vector3f viewDirection = new Vector3f(0, 0, 1);

    private final Quaternion dr = new Quaternion();
    private final Vector3f camDir = new Vector3f();
    private final Vector3f camLeft = new Vector3f();
    private final Vector2f velocity = new Vector2f();

    float m_RunSpeed = 5.5f;
    float m_MoveSpeed = 4.5f;
    float m_TurnSpeed = 10f;

    boolean _MoveForward, _MoveBackward, _MoveLeft, _MoveRight;
    boolean isRunning, isAiming, canShooting;

    private Node aimNode;
    private final RaycastHit shootHit = new RaycastHit();

    @Override
    public void setSpatial(Spatial sp) {
        super.setSpatial(sp);
        if (spatial != null) {
            this.aimNode    = addEmptyNode("aim-node", new Vector3f(0, 2, 0));
            this.chaseCamera = getComponent(ChaseCamera.class);
            this.bcc        = getComponent(BetterCharacterControl.class);
            this.animator   = getComponent(Animator.class);
            animator.addAnimListener(this);
            
            _MainCamera = new MainCamera(camera, defaultFOV, nearClipPlane, farClipPlane);
            logger.log(Level.INFO, "Initialized");
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        // TODO Auto-generated method stub

        updateWeaponAiming(tpf);

        camera.getDirection(camDir).setY(0);
        camera.getLeft(camLeft).setY(0);

        walkDirection.set(0, 0, 0);

        if (isAiming) {
        	Vector3f lookDir = camera.getDirection();
            lookDir.y = 0;
            lookDir.normalizeLocal();
            Quaternion lookRotation = FRotator.lookRotation(lookDir);
            spatial.getLocalRotation().slerp(lookRotation, m_TurnSpeed * tpf);
            spatial.getLocalRotation().mult(Vector3f.UNIT_Z, viewDirection);
            bcc.setViewDirection(viewDirection);
            
            //bcc.setViewDirection(camDir);
            bcc.setWalkDirection(walkDirection);
            footstepsSFX.stop();

        } else {
            if (_MoveForward) {
                walkDirection.addLocal(camDir);
            } else if (_MoveBackward) {
                walkDirection.addLocal(camDir.negateLocal());
            }

            if (_MoveLeft) {
                walkDirection.addLocal(camLeft);
            } else if (_MoveRight) {
                walkDirection.addLocal(camLeft.negateLocal());
            }

            walkDirection.normalizeLocal();

            if (walkDirection.lengthSquared() > 0) {
                float angle = FastMath.atan2(walkDirection.x, walkDirection.z);
                dr.fromAngleNormalAxis(angle, Vector3f.UNIT_Y);
                spatial.getWorldRotation().slerp(dr, 1 - (tpf * m_TurnSpeed));
                spatial.getWorldRotation().mult(Vector3f.UNIT_Z, viewDirection);
                bcc.setViewDirection(viewDirection);
            }

            float xSpeed = isRunning ? m_RunSpeed : m_MoveSpeed;
            bcc.setWalkDirection(walkDirection.multLocal(xSpeed));

            Vector3f v = bcc.getVelocity(null);
            velocity.set(v.x, v.z);
            boolean isMoving = (velocity.length() / xSpeed) > .2f;

            if (isMoving) {
                setAnimTrigger(isRunning ? AnimDefs.Running_2 : AnimDefs.Running);
                footstepsSFX.setVolume(isRunning ? 2f : .4f);
                footstepsSFX.setPitch(isRunning ? 1f : .85f);
                footstepsSFX.play();

            } else {
                setAnimTrigger(AnimDefs.Idle);
                footstepsSFX.stop();
            }
        }
    }
    
	private void updateWeaponAiming(float tpf) {
		if (isAiming) {
			fov += tpf * aimingSpeed;
		} else {
			fov -= tpf * aimingSpeed;
		}
		
		fov = FastMath.clamp(fov, 0, 1);
		_MainCamera.setFieldOfView(FastMath.interpolateLinear(fov, defaultFOV, aimFOV));
	}

    public void setAiming(boolean isAiming) {
        this.isAiming = isAiming;
//        chaseCamera.setDefaultDistance(isAiming ? chaseCamera.getMinDistance() : chaseCamera.getMaxDistance());
        chaseCamera.setRotationSpeed(isAiming ? 0.5f : 1);
        weapon.crosshair.setEnabled(isAiming);
        setAnimTrigger(AnimDefs.Draw_Arrow);
    }

    public void changeAmmo() {
        weapon.onChangeAmmo();
    }

    public void shooting() {
        shooting(weapon);
    }

    private void shooting(Weapon weapon) {
        if (isAiming && canShooting) {

            shootSFX.playInstance();
            setAnimTrigger(AnimDefs.Aim_Recoil);

            // Aim the ray from character location in camera direction.
            if (Physics.doRaycast(aimNode.getWorldTranslation(), camera.getDirection(), shootHit, weapon.distance)) {
                System.out.println(" * You shot: " + shootHit);
                applyExplosion(shootHit, weapon);

            } else {
                System.out.println("Target not in range...");
            }
        }
    }

    /**
     * @param hit
     * @param weapon
     */
    private void applyExplosion(RaycastHit hit, Weapon weapon) {
        float explosionRadius = 5;
        float baseStrength = 10f;
        ColorRGBA color = ColorRGBA.randomColor();
        int shootLayer = PhysicsCollisionObject.COLLISION_GROUP_03;

        for (PhysicsRigidBody rb : PhysxQuery.overlapSphere(hit.point, explosionRadius, shootLayer)) {
        	
        	Physics.addExplosionForce(rb, baseStrength, hit.point, explosionRadius);
            Spatial userObj = (Spatial) rb.getUserObject();
            applyDamage(userObj, color);
        }
        
        particleManager.playEffect(weapon.getAmmoType(), shootHit.point, 10f);
    }

    private void applyDamage(Spatial sp, ColorRGBA color) {
        if (sp instanceof Geometry) {
            Geometry geom = (Geometry) sp;
            geom.getMaterial().setColor("Color", color);
        }
    }

    private void setAnimTrigger(Animation3 newAnim) {
        if (checkTransition(newAnim, AnimDefs.Running, AnimDefs.Running_2)) {
            animator.crossFade(newAnim);
        } else {
            animator.setAnimation(newAnim);
        }
    }

    private boolean checkTransition(Animation3 newAnim, Animation3 a, Animation3 b) {
        String curAnim = animator.getAnimationName();
        return (newAnim.equals(a) && b.getName().equals(curAnim)) || (newAnim.equals(b) && a.getName().equals(curAnim));
    }

    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        //To change body of generated methods, choose Tools | Templates.
        if (animName.equals(AnimDefs.Aim_Recoil.getName())) {
            setAnimTrigger(AnimDefs.Draw_Arrow);

        } else if (animName.equals(AnimDefs.Draw_Arrow.getName())) {
            setAnimTrigger(AnimDefs.Aim_Overdraw);
        }
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
        // To change body of generated methods, choose Tools | Templates.
        if (animName.equals(AnimDefs.Aim_Recoil.getName()) || animName.equals(AnimDefs.Draw_Arrow.getName())) {
            setWeaponCharging();

        } else if (animName.equals(AnimDefs.Aim_Overdraw.getName())) {
            setWeaponReady();
        }
    }

    private void setWeaponReady() {
        canShooting = true;
        weapon.crosshair.setColor(ColorRGBA.White);
        reloadSFX.play();
    }

    private void setWeaponCharging() {
        canShooting = false;
        weapon.crosshair.setColor(ColorRGBA.Red);
        reloadSFX.stop();
    }
}
