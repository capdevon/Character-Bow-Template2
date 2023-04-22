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
    public final BitmapText bitmapText;
    // parent node
    public final Node guiNode;

    /**
     * 
     * @param guiNode
     * @param bmp
     */
    public CrosshairData(Node guiNode, BitmapText bmp) {
        this.guiNode = guiNode;
        this.bitmapText = bmp;
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            guiNode.attachChild(bitmapText);
        } else {
            guiNode.detachChild(bitmapText);
        }
    }

    public float getSize() {
        return bitmapText.getSize();
    }

    public void setSize(float size) {
        bitmapText.setSize(size);
    }

    public ColorRGBA getColor() {
        return bitmapText.getColor();
    }

    public void setColor(ColorRGBA color) {
        bitmapText.setColor(color);
    }

}
