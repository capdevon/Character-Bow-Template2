package com.capdevon.animation;

import java.util.Objects;

import com.jme3.animation.AnimControl;
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
    private AnimUtils() {}

    public static AnimControl getAnimControl(Spatial sp) {
        AnimControl control = findControl(sp, AnimControl.class);
        return Objects.requireNonNull(control, "AnimControl not found: " + sp);
    }

    public static SkeletonControl getSkeletonControl(Spatial sp) {
        SkeletonControl control = findControl(sp, SkeletonControl.class);
        return Objects.requireNonNull(control, "SkeletonControl not found: " + sp);
    }

    public static Node getAttachmentsNode(Spatial sp, String boneName) {
        SkeletonControl skControl = getSkeletonControl(sp);
        Node attachNode = skControl.getAttachmentsNode(boneName);
        return Objects.requireNonNull(attachNode, "AttachmentsNode not found: " + boneName);
    }

    public static void addSkeletonDebugger(AssetManager asm, Spatial sp) {
        SkeletonControl skControl = getSkeletonControl(sp);
        Node animRoot = (Node) skControl.getSpatial();
        SkeletonDebugger debugger = new SkeletonDebugger(animRoot.getName() + "_Skeleton", skControl.getSkeleton());
        Material mat = new Material(asm, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        mat.getAdditionalRenderState().setWireframe(true);
        mat.getAdditionalRenderState().setDepthTest(false);
        debugger.setMaterial(mat);
        animRoot.attachChild(debugger);
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
