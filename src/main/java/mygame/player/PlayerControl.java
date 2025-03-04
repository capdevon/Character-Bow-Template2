package mygame.player;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.capdevon.anim.Animation3;
import com.capdevon.anim.AnimationListener;
import com.capdevon.anim.Animator;
import com.capdevon.anim.HumanBodyBones;
import com.capdevon.anim.IKRig;
import com.capdevon.control.AdapterControl;
import com.capdevon.control.Damageable;
import com.capdevon.engine.FRotator;
import com.capdevon.physx.Physics;
import com.capdevon.physx.RaycastHit;
import com.jme3.anim.AnimComposer;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import jme3utilities.debug.SkeletonVisualizer;
import mygame.camera.MainCamera;
import mygame.states.ParticleManager;
import mygame.util.AnimDefs;

/**
 *
 * @author capdevon
 */
public class PlayerControl extends AdapterControl implements AnimationListener {

    private static final Logger logger = Logger.getLogger(PlayerControl.class.getName());

    ParticleManager particleManager;
    Camera camera;
    BitmapText weaponUI;
    Weapon weapon;
    AudioNode footstepsSFX;
    AudioNode shootSFX;
    AudioNode reloadSFX;

    private MainCamera _MainCamera;
    private float nearClipPlane = 0.01f;
    private float farClipPlane = 200f;
    private float fov = 0;
    private float aimingSpeed = 5f;
    private float aimFOV = 45;
    private float defaultFOV = 60;

    private ChaseCamera chaseCamera;
    private Animator animator;
    private IKRig rig;
    private SkeletonVisualizer sv;
    private BetterCharacterControl bcc;
    private final Vector3f walkDirection = new Vector3f(0, 0, 0);
    private final Vector3f viewDirection = new Vector3f(0, 0, 1);

    private final Quaternion dr = new Quaternion();
    private final Vector3f camDir = new Vector3f();
    private final Vector3f camLeft = new Vector3f();
    
    private final String spine = "mixamorig:" + HumanBodyBones.Spine;
    private final Quaternion targetRotation = new Quaternion();
    private final float[] angles = new float[3];

    private float runSpeed = 5.5f;
    private float moveSpeed = 4.5f;
    private float rotateSpeed = 10f;

    boolean bMoveForward, bMoveBackward, bMoveLeft, bMoveRight;
    boolean isRunning, isAiming, canShooting;

    private Node aimNode;
    private final RaycastHit shootHit = new RaycastHit();

    @Override
    public void setSpatial(Spatial sp) {
        super.setSpatial(sp);
        if (spatial != null) {
            this.aimNode     = (Node) getChild("aim-node");
            this.chaseCamera = getComponent(ChaseCamera.class);
            this.bcc         = getComponent(BetterCharacterControl.class);
            this.animator    = getComponent(Animator.class);
            this.rig         = getComponentInChildren(IKRig.class);
            this.sv          = getComponentInChildren(SkeletonVisualizer.class);

            configureAnimClips();

            _MainCamera = new MainCamera(camera, defaultFOV, nearClipPlane, farClipPlane);
            logger.log(Level.INFO, "Initialized");
        }
    }
    
    private void configureAnimClips() {
        animator.actionCycleDone(AnimDefs.Idle);
        animator.actionCycleDone(AnimDefs.Running);
        animator.actionCycleDone(AnimDefs.Sprinting);
        animator.actionCycleDone(AnimDefs.StandingAimIdle);
        animator.actionCycleDone(AnimDefs.StandingAimOverdraw);
        animator.actionCycleDone(AnimDefs.StandingAimRecoil);
        animator.actionCycleDone(AnimDefs.StandingDrawArrow);
        animator.addListener(this);
    }

    @Override
    protected void controlUpdate(float tpf) {

        updateBoneIK(tpf);
        updateWeaponAiming(tpf);
        weaponUI.setText(weapon.getDescription());

        camera.getDirection(camDir).setY(0);
        camera.getLeft(camLeft).setY(0);

        walkDirection.set(0, 0, 0);

        if (isAiming) {
            Vector3f lookDir = camera.getDirection();
            lookDir.y = 0;
            lookDir.normalizeLocal();

            Quaternion lookRotation = FRotator.lookRotation(lookDir);
            FRotator.smoothDamp(spatial.getWorldRotation(), lookRotation, rotateSpeed * tpf, viewDirection);
            bcc.setViewDirection(viewDirection);

            //bcc.setViewDirection(camDir);
            bcc.setWalkDirection(walkDirection);
            footstepsSFX.stop();

        } else {
            if (bMoveForward) {
                walkDirection.addLocal(camDir);
            } else if (bMoveBackward) {
                walkDirection.addLocal(camDir.negateLocal());
            }

            if (bMoveLeft) {
                walkDirection.addLocal(camLeft);
            } else if (bMoveRight) {
                walkDirection.addLocal(camLeft.negateLocal());
            }

            walkDirection.normalizeLocal();
            boolean isMoving = walkDirection.lengthSquared() > 0;
            
            if (isMoving) {
                float angle = FastMath.atan2(walkDirection.x, walkDirection.z);
                dr.fromAngleNormalAxis(angle, Vector3f.UNIT_Y);

                float smoothTime = 1 - (tpf * rotateSpeed);
                FRotator.smoothDamp(spatial.getWorldRotation(), dr, smoothTime, viewDirection);

                bcc.setViewDirection(viewDirection);
            }

            float xSpeed = isRunning ? runSpeed : moveSpeed;
            bcc.setWalkDirection(walkDirection.multLocal(xSpeed));
            
            if (isMoving) {
                playAnimation(isRunning ? AnimDefs.Sprinting : AnimDefs.Running);
                footstepsSFX.setVolume(isRunning ? 2f : .4f);
                footstepsSFX.setPitch(isRunning ? 1f : .85f);
                footstepsSFX.play();

            } else {
                playAnimation(AnimDefs.Idle);
                footstepsSFX.stop();
            }
        }
    }
    
    private void updateBoneIK(float tpf) {
        if (isAiming) {
            camera.getRotation().toAngles(angles);
            float rx = angles[0];
            targetRotation.fromAngles(0, 0, -rx);
            rig.setAvatarIKRotation(spine, targetRotation);
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
        //chaseCamera.setDefaultDistance(isAiming ? chaseCamera.getMinDistance() : chaseCamera.getMaxDistance());
        chaseCamera.setRotationSpeed(isAiming ? 0.5f : 1);
        weapon.getCrosshair().setEnabled(isAiming);
        playAnimation(AnimDefs.StandingDrawArrow);

        rig.setAvatarIKActive(spine, isAiming);
        sv.setHeadColor(animator.getJoint(spine).getId(), isAiming ? ColorRGBA.Red : ColorRGBA.White);
    }

    public void changeAmmo() {
        weapon.nextAmmo();
    }
    
    public void reload() {
        weapon.reload();
    }

    public void shooting() {
        if (isAiming && canShooting && weapon.canShooting()) {

            weapon.shoot();
            shootSFX.playInstance();
            playAnimation(AnimDefs.StandingAimRecoil);

            // Aim the ray from character location in camera direction.
            if (Physics.doRaycast(aimNode.getWorldTranslation(), camera.getDirection(), shootHit, weapon.getRange())) {
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
        AmmoType ammoType = weapon.getAmmoType();
        int shootLayer = PhysicsCollisionObject.COLLISION_GROUP_03;
        ColorRGBA color = ColorRGBA.randomColor();

        for (PhysicsCollisionObject pco : Physics.overlapSphere(hit.point, ammoType.explosionRadius, shootLayer)) {
            if (pco instanceof PhysicsRigidBody) {
                PhysicsRigidBody rb = (PhysicsRigidBody) pco;
                if (rb.getMass() > 0) {
                    Physics.addExplosionForce(rb, ammoType.baseStrength, hit.point, ammoType.explosionRadius);
                    Spatial userObj = (Spatial) rb.getUserObject();
                    applyDamage(userObj, color);
                }
            }
        }

        particleManager.playEffect(ammoType.effect, shootHit.point, 10f);
    }

    private void applyDamage(Spatial sp, ColorRGBA color) {
        Damageable damageable = sp.getControl(Damageable.class);
        if (damageable != null) {
            damageable.takeDamage(color);
        }
    }

    private void playAnimation(Animation3 newAnim) {
        if (checkTransition(newAnim, AnimDefs.Running, AnimDefs.Sprinting)) {
            animator.crossFade(newAnim);
        } else {
            animator.setAnimation(newAnim);
        }
    }

    private boolean checkTransition(Animation3 newAnim, Animation3 a, Animation3 b) {
        String curAnim = animator.getCurrentAnimName();
        return (newAnim.equals(a) && b.getName().equals(curAnim))
                || (newAnim.equals(b) && a.getName().equals(curAnim));
    }

    @Override
    public void onAnimCycleDone(AnimComposer animComposer, String animName, boolean loop) {
        if (animName.equals(AnimDefs.StandingAimRecoil.getName())) {
            playAnimation(AnimDefs.StandingDrawArrow);

        } else if (animName.equals(AnimDefs.StandingDrawArrow.getName())) {
            playAnimation(AnimDefs.StandingAimOverdraw);
            
        } else if (!loop) {
            animComposer.removeCurrentAction();
        }
    }

    @Override
    public void onAnimChange(AnimComposer animComposer, String animName) {
        if (animName.equals(AnimDefs.StandingAimRecoil.getName())
                || animName.equals(AnimDefs.StandingDrawArrow.getName())) {
            setWeaponCharging();

        } else if (animName.equals(AnimDefs.StandingAimOverdraw.getName())) {
            setWeaponReady();
        }
    }

    private void setWeaponReady() {
        canShooting = true;
        weapon.getCrosshair().setColor(ColorRGBA.White);
        reloadSFX.play();
    }

    private void setWeaponCharging() {
        canShooting = false;
        weapon.getCrosshair().setColor(ColorRGBA.Red);
        reloadSFX.stop();
    }

}
