package com.capdevon.anim;

import java.util.Objects;

import com.jme3.anim.AnimComposer;
import com.jme3.anim.SkinningControl;
import com.jme3.animation.SkeletonControl;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.scene.debug.SkeletonDebugger;

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

    public static AnimComposer getAnimCompser(Spatial sp) {
        AnimComposer control = findControl(sp, AnimComposer.class);
        return Objects.requireNonNull(control, "AnimComposer not found: " + sp);
    }

    public static SkinningControl getSkinningControl(Spatial sp) {
        SkinningControl control = findControl(sp, SkinningControl.class);
        return Objects.requireNonNull(control, "SkinningControl not found: " + sp);
    }

    public static void addSkeletonDebugger(AssetManager asm, SkeletonControl sc) {
        Node animRoot = (Node) sc.getSpatial();
        String name = animRoot.getName() + "_Skeleton";
        SkeletonDebugger debugger = new SkeletonDebugger(name, sc.getSkeleton());
        debugger.setMaterial(createWireMaterial(asm));
        animRoot.attachChild(debugger);
    }

    private static Material createWireMaterial(AssetManager asm) {
        Material mat = new Material(asm, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        mat.getAdditionalRenderState().setWireframe(true);
        mat.getAdditionalRenderState().setDepthTest(false);
        return mat;
    }

    /**
     * @param <T>
     * @param sp
     * @param clazz
     * @return
     */
    private static <T extends Control> T findControl(Spatial sp, Class<T> clazz) {
        T control = sp.getControl(clazz);
        if (control != null) {
            return control;
        }
        if (sp instanceof Node) {
            for (Spatial child : ((Node) sp).getChildren()) {
                control = findControl(child, clazz);
                if (control != null) {
                    return control;
                }
            }
        }
        return null;
    }

}
