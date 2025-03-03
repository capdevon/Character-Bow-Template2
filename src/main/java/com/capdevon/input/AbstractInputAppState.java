package com.capdevon.input;

import java.util.ArrayList;
import java.util.List;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;
import com.jme3.input.Joystick;
import com.jme3.input.JoystickAxis;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.Trigger;
import com.jme3.util.SafeArrayList;

/**
 *
 * @author capdevon
 */
public abstract class AbstractInputAppState extends BaseAppState implements AnalogListener, ActionListener {

    private InputManager inputManager;
    private List<ActionListener> actionListeners = new SafeArrayList<>(ActionListener.class);
    private List<AnalogListener> analogListeners = new SafeArrayList<>(AnalogListener.class);
    private List<String> mappingNames = new ArrayList<>();

    @Override
    public void initialize(Application app) {
        inputManager = app.getInputManager();
        registerInput();
        
        Joystick[] joysticks = inputManager.getJoysticks();
        if (joysticks != null && joysticks.length > 0) {
            for (Joystick j : joysticks) {
                mapJoystick(j);
            }
        }
    }

    @Override
    public void cleanup(Application app) {
        for (String input : mappingNames) {
            if (inputManager.hasMapping(input)) {
                inputManager.deleteMapping(input);
            }
        }
        inputManager.removeListener(this);
    }

    @Override
    protected void onEnable() {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onDisable() {
        // TODO Auto-generated method stub
    }
    
    /**
     * Creates a new input mapping for the specified triggers.
     *
     * @param bindingName The name of the input mapping.
     * @param triggers    The triggers that activate this mapping.
     */
    public void addMapping(String bindingName, Trigger... triggers) {
        mappingNames.add(bindingName);
        inputManager.addMapping(bindingName, triggers);
        inputManager.addListener(this, bindingName);
    }

    /**
     * Assign the mapping name to receive events from the given button index on the
     * joystick.
     * 
     * @param joystick    The joystick containing the button.
     * @param logicalId   The logical ID of the button.
     * @param mappingName The name of the input mapping.
     */
    public void assignButton(Joystick joystick, String logicalId, String mappingName) {
        joystick.getButton(logicalId).assignButton(mappingName);
        inputManager.addListener(this, mappingName);
    }

    /**
     * Assign the mappings to receive events from the given joystick axis.
     * 
     * @param axis            The joystick axis.
     * @param positiveMapping The name of the input mapping for positive axis movement.
     * @param negativeMapping The name of the input mapping for negative axis movement.
     */
    public void assignAxis(JoystickAxis axis, String positiveMapping, String negativeMapping) {
        axis.assignAxis(positiveMapping, negativeMapping);
        inputManager.addListener(this, positiveMapping, negativeMapping);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (isEnabled()) {
            for (ActionListener listener : actionListeners) {
                listener.onAction(name, isPressed, tpf);
            }
        }
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        if (isEnabled()) {
            for (AnalogListener listener : analogListeners) {
                listener.onAnalog(name, value, tpf);
            }
        }
    }

    public void addActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }

    public void addAnalogListener(AnalogListener listener) {
        analogListeners.add(listener);
    }

    public void removeActionListener(ActionListener listener) {
        actionListeners.remove(listener);
    }

    public void removeAnalogListener(AnalogListener listener) {
        analogListeners.remove(listener);
    }

    /** 
     * Custom Keybinding: Mapping a named action to a key input.
     */
    protected abstract void registerInput();
    
    /**
     * Maps joystick inputs to game actions using the provided Joystick object.
     * 
     * @param joystick The Joystick object representing the connected joystick device.
     */
    protected abstract void mapJoystick(Joystick joystick);
    
}

