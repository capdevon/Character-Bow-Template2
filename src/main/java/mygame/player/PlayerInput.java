/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.player;

import com.capdevon.control.AdapterControl;
import com.capdevon.input.KeyMapping;
import com.jme3.input.controls.ActionListener;
import com.jme3.scene.Spatial;

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
            playerControl._MoveLeft = keyPressed;
        } else if (action.equals(KeyMapping.MOVE_RIGHT)) {
            playerControl._MoveRight = keyPressed;
        } else if (action.equals(KeyMapping.MOVE_FORWARD)) {
            playerControl._MoveForward = keyPressed;
        } else if (action.equals(KeyMapping.MOVE_BACKWARD)) {
            playerControl._MoveBackward = keyPressed;
        } else if (action.equals(KeyMapping.RUNNING)) {
            playerControl.isRunning = keyPressed;
        } else if (action.equals(KeyMapping.AIMING)) {
            playerControl.setAiming(keyPressed);
        } else if (action.equals(KeyMapping.FIRE) && keyPressed) {
            playerControl.shooting();
        } else if (action.equals(KeyMapping.SWITCH_WEAPON) && keyPressed) {
            playerControl.changeAmmo();
        }
    }
}
