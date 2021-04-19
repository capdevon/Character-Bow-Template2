/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.capdevon.util;

import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 * 
 * @author capdevon
 */
public class SceneHelper {

    public static Spatial getMainScene(AssetManager am) {
        Spatial scene = am.loadModel("Scenes/ManyLights/Main.scene");
        scene.setName("MainScene");
        scene.scale(1f, .5f, 1f);
        scene.setLocalTranslation(0f, -10f, 0f);

        return scene;
    }
    
    public static Spatial getTownScene(AssetManager am) {
    	am.registerLocator("town.zip", ZipLocator.class);
        Spatial scene = am.loadModel("main.scene");
//        Spatial scene = am.loadModel("Scenes/town/main.scene");
        scene.setName("MainScene");
        scene.setLocalTranslation(0, -5.2f, 0);

        return scene;
    }

    public static Geometry getQuadFloor(AssetManager am, ColorRGBA color) {
        Box mesh = new Box(20, 0.1f, 20);
        Geometry floor = new Geometry("Floor.GeoMesh", mesh);
        Material mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        floor.setMaterial(mat);
        return floor;
    }

}
