package mygame.player;

import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;

/**
 * 
 * @author capdevon
 */
public class CrosshairData {

    // The BitmapText that will be used for this weapon's crosshair
    private final BitmapText bmp;
    // parent node
    private final Node guiNode;

    /**
     * 
     * @param guiNode
     * @param bmp
     */
    public CrosshairData(Node guiNode, BitmapText bmp) {
        this.guiNode = guiNode;
        this.bmp = bmp;
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            guiNode.attachChild(bmp);
        } else {
            guiNode.detachChild(bmp);
        }
    }

    public float getSize() {
        return bmp.getSize();
    }

    public void setSize(float size) {
        bmp.setSize(size);
    }

    public ColorRGBA getColor() {
        return bmp.getColor();
    }

    public void setColor(ColorRGBA color) {
        bmp.setColor(color);
    }

}
