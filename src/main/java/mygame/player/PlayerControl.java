package mygame.player;

import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.capdevon.animation.Animation3;
import com.capdevon.anim.ActionAnimEventListener;
import com.capdevon.anim.Animator;
import com.capdevon.control.AdapterControl;
import com.capdevon.engine.FRotator;
import com.capdevon.physx.Physics;
import com.capdevon.physx.PhysxQuery;
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
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import mygame.camera.MainCamera;
import mygame.states.ParticleManager;
import mygame.util.AnimDefs;

/**
 *
 * @author capdevon
 */
public class PlayerControl extends AdapterControl implements ActionAnimEventListener {

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
    private float farClipPlane = 100f;
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
            this.aimNode     = addEmptyNode("aim-node", new Vector3f(0, 2, 0));
            this.chaseCamera = getComponent(ChaseCamera.class);
            this.bcc         = getComponent(BetterCharacterControl.class);
            this.animator    = getComponent(Animator.class);

            animator.setAnimCallback(AnimDefs.Idle);
            animator.setAnimCallback(AnimDefs.Running);
            animator.setAnimCallback(AnimDefs.Running_2);
            animator.setAnimCallback(AnimDefs.Aim_Idle);
            animator.setAnimCallback(AnimDefs.Aim_Overdraw);
            animator.setAnimCallback(AnimDefs.Aim_Recoil);
            animator.setAnimCallback(AnimDefs.Draw_Arrow);
            animator.addListener(this);

            _MainCamera = new MainCamera(camera, defaultFOV, nearClipPlane, farClipPlane);
            logger.log(Level.INFO, "Initialized");
        }
    }

    @Override
    protected void controlUpdate(float tpf) {

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
            FRotator.smoothDamp(spatial.getWorldRotation(), lookRotation, m_TurnSpeed * tpf, viewDirection);
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

                float smoothTime = 1 - (tpf * m_TurnSpeed);
                FRotator.smoothDamp(spatial.getWorldRotation(), dr, smoothTime, viewDirection);

                bcc.setViewDirection(viewDirection);
            }

            float xSpeed = isRunning ? m_RunSpeed : m_MoveSpeed;
            bcc.setWalkDirection(walkDirection.multLocal(xSpeed));

            boolean isMoving = walkDirection.lengthSquared() > 0;
            if (isMoving) {
                playAnim(isRunning ? AnimDefs.Running_2 : AnimDefs.Running);
                footstepsSFX.setVolume(isRunning ? 2f : .4f);
                footstepsSFX.setPitch(isRunning ? 1f : .85f);
                footstepsSFX.play();

            } else {
                playAnim(AnimDefs.Idle);
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
        //chaseCamera.setDefaultDistance(isAiming ? chaseCamera.getMinDistance() : chaseCamera.getMaxDistance());
        chaseCamera.setRotationSpeed(isAiming ? 0.5f : 1);
        weapon.crosshair.setEnabled(isAiming);
        playAnim(AnimDefs.Draw_Arrow);
    }

    public void changeAmmo() {
        weapon.nextAmmo();
    }

    public void shooting() {
        shooting(weapon);
    }

    private void shooting(Weapon weapon) {
        if (isAiming && canShooting && weapon.currAmmo > 0) {

            weapon.currAmmo--;
            shootSFX.playInstance();
            playAnim(AnimDefs.Aim_Recoil);

            // Aim the ray from character location in camera direction.
            if (Physics.doRaycast(aimNode.getWorldTranslation(), camera.getDirection(), shootHit, weapon.range)) {
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
        Function<PhysicsRigidBody, Boolean> dynamicObjects = (x) -> x.getMass() > 0;
        ColorRGBA color = ColorRGBA.randomColor();

        for (PhysicsRigidBody rb : PhysxQuery.overlapSphere(hit.point, ammoType.explosionRadius, shootLayer, dynamicObjects)) {

            Physics.addExplosionForce(rb, ammoType.baseStrength, hit.point, ammoType.explosionRadius);
            Spatial userObj = (Spatial) rb.getUserObject();
            applyDamage(userObj, color);
        }

        particleManager.playEffect(ammoType.effect, shootHit.point, 10f);
    }

    private void applyDamage(Spatial sp, ColorRGBA color) {
        Node root = (Node) sp;
        Geometry geom = (Geometry) root.getChild(0);
        geom.getMaterial().setColor("Color", color);
    }

    private void playAnim(Animation3 newAnim) {
        if (checkTransition(newAnim, AnimDefs.Running, AnimDefs.Running_2)) {
            animator.crossFade(newAnim);
        } else {
            animator.setAnimation(newAnim);
        }
    }

    private boolean checkTransition(Animation3 newAnim, Animation3 a, Animation3 b) {
        String curAnim = animator.getAnimation();
        return (newAnim.equals(a) && b.getName().equals(curAnim)) || (newAnim.equals(b) && a.getName().equals(curAnim));
    }

    @Override
    public void onAnimCycleDone(AnimComposer animComposer, String animName, boolean loop) {
        if (animName.equals(AnimDefs.Aim_Recoil.getName())) {
            playAnim(AnimDefs.Draw_Arrow);

        } else if (animName.equals(AnimDefs.Draw_Arrow.getName())) {
            playAnim(AnimDefs.Aim_Overdraw);

        } else if (!loop) {
            animComposer.removeCurrentAction();
        }
    }

    @Override
    public void onAnimChange(AnimComposer animComposer, String animName) {
        if (animName.equals(AnimDefs.Aim_Recoil.getName())
                || animName.equals(AnimDefs.Draw_Arrow.getName())) {
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
