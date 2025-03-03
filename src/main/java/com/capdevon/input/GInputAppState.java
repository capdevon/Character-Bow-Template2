package com.capdevon.input;

import com.jme3.input.Joystick;
import com.jme3.input.JoystickAxis;
import com.jme3.input.JoystickButton;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;

/**
 *
 * @author capdevon
 */
public class GInputAppState extends AbstractInputAppState {

    @Override
    public void registerInput() {

        addMapping(KeyMapping.MOVE_FORWARD,     new KeyTrigger(KeyInput.KEY_W));
        addMapping(KeyMapping.MOVE_BACKWARD,    new KeyTrigger(KeyInput.KEY_S));
        addMapping(KeyMapping.MOVE_LEFT,        new KeyTrigger(KeyInput.KEY_A));
        addMapping(KeyMapping.MOVE_RIGHT,       new KeyTrigger(KeyInput.KEY_D));
        addMapping(KeyMapping.AIMING,           new KeyTrigger(KeyInput.KEY_E));
        addMapping(KeyMapping.SWITCH_WEAPON,    new KeyTrigger(KeyInput.KEY_R));
        addMapping(KeyMapping.RUNNING,          new KeyTrigger(KeyInput.KEY_SPACE));
        addMapping(KeyMapping.RELOAD_WEAPON,    new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        addMapping(KeyMapping.FIRE,             new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
    }

    @Override
    public void mapJoystick(Joystick joypad) {

        // Map it differently if there are Z axis
        if (joypad.getAxis(JoystickAxis.Z_ROTATION) != null && joypad.getAxis(JoystickAxis.Z_AXIS) != null) {
            // Make the left stick move
            assignAxis(joypad.getXAxis(), KeyMapping.MOVE_RIGHT, KeyMapping.MOVE_LEFT);
            assignAxis(joypad.getYAxis(), KeyMapping.MOVE_BACKWARD, KeyMapping.MOVE_FORWARD);

            // And let the dpad be up and down
            assignAxis(joypad.getPovYAxis(), KeyMapping.SWITCH_WEAPON, KeyMapping.SWITCH_WEAPON);
        }
        
        //assignButton(joypad, JoystickButton.BUTTON_0, KeyMapping.TRIGGER_ACTION);
        //assignButton(joypad, JoystickButton.BUTTON_1, KeyMapping.TOGGLE_FLASHLIGHT);
        //assignButton(joypad, JoystickButton.BUTTON_2, KeyMapping.TOGGLE_CROUCH);
        assignButton(joypad, JoystickButton.BUTTON_4, KeyMapping.RUNNING);
        //assignButton(joypad, JoystickButton.BUTTON_5, KeyMapping.AUTO_AIM);
        assignButton(joypad, JoystickButton.BUTTON_6, KeyMapping.AIMING);
        assignButton(joypad, JoystickButton.BUTTON_7, KeyMapping.FIRE);
        //assignButton(joypad, JoystickButton.BUTTON_8, KeyMapping.TOGGLE_INVENTORY);
        //assignButton(joypad, JoystickButton.BUTTON_9, KeyMapping.TOGGLE_PAUSE);
    }

}
