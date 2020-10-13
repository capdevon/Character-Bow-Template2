/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;

/**
 *
 */
public class CrosshairData {
    
    // The BitmapText that will be used for this weapon's crosshair
    public BitmapText bmp;
    // The image that will be used for this weapon's crosshair
    public Picture picture;
    // The size of the crosshair image
    public int size;
    // The color of the crosshair image
    public ColorRGBA color;
    // parent node
    public Node guiNode;
    
    public CrosshairData(Node guiNode, Picture picture) {
        this.guiNode = guiNode;
        this.picture = picture;
        this.size = 1;
        this.color = ColorRGBA.White.clone();
    }
    
    public CrosshairData(Node guiNode, BitmapText bmp) {
        this.guiNode = guiNode;
        this.bmp = bmp;
        this.size = 1;
        this.color = ColorRGBA.White.clone();
    }
    
    public void setEnabled(boolean enabled) {
        int i = -1;
        
        if (picture != null)
            i = enabled ? guiNode.attachChild(picture) : guiNode.detachChild(picture);
        else if (bmp != null)
            i = enabled ? guiNode.attachChild(bmp) : guiNode.detachChild(bmp);
        
        System.out.println("showCrosshair: " + enabled + " gui.numberOfChildren: " + i);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public ColorRGBA getColor() {
        return color;
    }

    public void setColor(ColorRGBA color) {
        this.color.set(color);
        bmp.setColor(color);
    }
    
}

