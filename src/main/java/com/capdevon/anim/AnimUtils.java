package com.capdevon.anim;

import java.util.Objects;

import com.capdevon.engine.GameObject;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.Armature;
import com.jme3.anim.SkinningControl;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.custom.ArmatureDebugger;

/**
 *
 * @author capdevon
 */
public class AnimUtils {

    /**
     * A private constructor to inhibit instantiation of this class.
     */
    private AnimUtils() {
    }

    /**
     * Retrieves the AnimComposer component from the given spatial.
     *
     * @param sp the spatial from which to retrieve the AnimComposer
     * @return the AnimComposer component
     * @throws NullPointerException if the AnimComposer is not found
     */
    public static AnimComposer getAnimCompser(Spatial sp) {
        AnimComposer control = GameObject.getComponentInChildren(sp, AnimComposer.class);
        return Objects.requireNonNull(control, "AnimComposer not found: " + sp);
    }

    /**
     * Retrieves the SkinningControl component from the given spatial.
     *
     * @param sp the spatial from which to retrieve the SkinningControl
     * @return the SkinningControl component
     * @throws NullPointerException if the SkinningControl is not found
     */
    public static SkinningControl getSkinningControl(Spatial sp) {
        SkinningControl control = GameObject.getComponentInChildren(sp, SkinningControl.class);
        return Objects.requireNonNull(control, "SkinningControl not found: " + sp);
    }

    /**
     * Adds an ArmatureDebugger to the spatial associated with the given
     * SkinningControl.
     *
     * @param asm the asset manager used to create the material for the debugger
     * @param sc  the SkinningControl whose spatial will receive the
     *            ArmatureDebugger
     */
    public static void addArmatureDebugger(AssetManager asm, SkinningControl sc) {
        Node animRoot = (Node) sc.getSpatial();
        String name = animRoot.getName() + "_Armature";
        Armature armature = sc.getArmature();
        ArmatureDebugger debugger = new ArmatureDebugger(name, armature, armature.getJointList());
        Material mat = createWireMaterial(asm, ColorRGBA.Blue);
        debugger.setMaterial(mat);
        animRoot.attachChild(debugger);
    }

    private static Material createWireMaterial(AssetManager asm, ColorRGBA color) {
        Material mat = new Material(asm, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        mat.getAdditionalRenderState().setWireframe(true);
        return mat;
    }

}
