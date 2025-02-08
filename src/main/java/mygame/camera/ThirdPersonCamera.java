package mygame.camera;

import java.util.logging.Logger;

import com.jme3.input.CameraInput;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.Joystick;
import com.jme3.input.JoystickAxis;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;

/**
 * A third-person shooter (TPS) chase camera that extends the base `ChaseCamera`
 * class. This camera provides a behind-the-shoulder view of the target object.
 *
 * @author capdevon
 */
public class ThirdPersonCamera extends ChaseCamera {

    private static final Logger logger = Logger.getLogger(ChaseCamera.class.getName());

    /**
     * Constructs a ThirdPersonCamera instance. This constructor does not register
     * any inputs, you need to call the `registerWithInput` method for that.
     *
     * @param cam    The camera object to control.
     * @param target The target object to follow.
     */
    public ThirdPersonCamera(Camera cam, Spatial target) {
        super(cam, target);
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        setDragToRotate(false);
    }

    /**
     * Registers the camera with the provided input manager and optionally
     * configures joystick controls.
     *
     * @param inputManager The input manager to register with.
     * @param useJoysticks Whether to configure joystick controls.
     */
    public void registerWithInput(InputManager inputManager, boolean useJoysticks) {
        registerWithInput(inputManager);
        setDragToRotate(false);

        if (useJoysticks) {
            Joystick[] joysticks = inputManager.getJoysticks();
            if (joysticks != null) {
                for (Joystick j : joysticks) {
                    mapJoysticks(j);
                }
            }
        }
    }

    /**
     * Maps joystick axes for camera control. This method retrieves specific
     * joystick axes (Z rotation and Z axis) and assigns them to predefined camera
     * input mappings.
     *
     * @param joystick The joystick to map axes from.
     */
    private void mapJoysticks(Joystick joystick) {
        JoystickAxis zRotation = joystick.getAxis(JoystickAxis.Z_ROTATION);
        JoystickAxis zAxis = joystick.getAxis(JoystickAxis.Z_AXIS);

        if (zRotation != null && zAxis != null) {
            assignAxis(zRotation, CameraInput.CHASECAM_UP, CameraInput.CHASECAM_DOWN);
            assignAxis(zAxis, CameraInput.CHASECAM_MOVERIGHT, CameraInput.CHASECAM_MOVELEFT);
        } else {
            logger.warning("Unable to map joystick axes for joystick: " + joystick.getJoyId());
        }
    }

    /**
     * Assigns a joystick axis to positive and negative camera input mappings.
     *
     * @param axis     The joystick axis to assign.
     * @param pMapping The positive input mapping for the axis.
     * @param nMapping The negative input mapping for the axis.
     */
    private void assignAxis(JoystickAxis axis, String pMapping, String nMapping) {
        logger.info("addMapping: " + axis);
        axis.assignAxis(pMapping, nMapping);
        inputManager.addListener(this, pMapping, nMapping);
    }

}
