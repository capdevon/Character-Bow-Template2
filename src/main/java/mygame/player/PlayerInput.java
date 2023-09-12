package mygame.player;

import com.capdevon.control.AdapterControl;
import com.capdevon.input.KeyMapping;
import com.jme3.input.controls.ActionListener;
import com.jme3.scene.Spatial;

/**
 * 
 * @author capdevon
 */
public class PlayerInput extends AdapterControl implements ActionListener {

    private PlayerControl playerControl;

    @Override
    public void setSpatial(Spatial sp) {
        super.setSpatial(sp);
        if (spatial != null) {
            this.playerControl = getComponent(PlayerControl.class);
        }
    }

    @Override
    public void onAction(String action, boolean keyPressed, float tpf) {
        if (action.equals(KeyMapping.MOVE_LEFT)) {
            playerControl.bMoveLeft = keyPressed;
        } else if (action.equals(KeyMapping.MOVE_RIGHT)) {
            playerControl.bMoveRight = keyPressed;
        } else if (action.equals(KeyMapping.MOVE_FORWARD)) {
            playerControl.bMoveForward = keyPressed;
        } else if (action.equals(KeyMapping.MOVE_BACKWARD)) {
            playerControl.bMoveBackward = keyPressed;
        } else if (action.equals(KeyMapping.RUNNING)) {
            playerControl.isRunning = keyPressed;
        } else if (action.equals(KeyMapping.AIMING)) {
            playerControl.setAiming(keyPressed);
        } else if (action.equals(KeyMapping.FIRE) && keyPressed) {
            playerControl.shooting();
        } else if (action.equals(KeyMapping.SWITCH_WEAPON) && keyPressed) {
            playerControl.changeAmmo();
        } else if (action.equals(KeyMapping.RELOAD_WEAPON) && keyPressed) {
            playerControl.reload();
        }
    }
}
