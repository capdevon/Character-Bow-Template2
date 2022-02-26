package com.capdevon.anim;

import java.util.Objects;

import com.jme3.anim.AnimComposer;
import com.jme3.anim.SkinningControl;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

/**
 *
 * @author capdevon
 */
public class AnimUtils {

    /**
     * A private constructor to inhibit instantiation of this class.
     */
    private AnimUtils() {}

    public static AnimComposer getAnimCompser(Spatial sp) {
        AnimComposer control = findControl(sp, AnimComposer.class);
        return Objects.requireNonNull(control, "AnimComposer not found: " + sp);
    }

    public static SkinningControl getSkinningControl(Spatial sp) {
        SkinningControl control = findControl(sp, SkinningControl.class);
        return Objects.requireNonNull(control, "SkinningControl not found: " + sp);
    }

    public static Node getAttachmentsNode(Spatial sp, String boneName) {
        SkinningControl skControl = getSkinningControl(sp);
        Node attachNode = skControl.getAttachmentsNode(boneName);
        return Objects.requireNonNull(attachNode, "AttachmentsNode not found: " + boneName);
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
