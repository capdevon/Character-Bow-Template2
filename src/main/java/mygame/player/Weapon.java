/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.scene.Node;
import java.util.ArrayList;

/**
 * 
 * @author capdevon
 */
public class Weapon {

    private String name;
    private Node weaponHook;
    private Node model;

    CrosshairData crosshair;
    float range = 30f;
    float damage = 50f;
    int currAmmo = 20;
    int maxAmmo = 40;

    private int ammoTypeIndex = 0;
    private final ArrayList<AmmoType> ammoTypes;

    /**
     * Create new Weapon
     * 
     * @param name
     * @param weaponHook
     * @param model
     */
    public Weapon(String name, Node weaponHook, Node model) {
        this.weaponHook = weaponHook;
        this.model = model;
        this.name = name;
        this.ammoTypes = new ArrayList<>();
    }

    public void setActive(boolean active) {
        if (active) {
            weaponHook.attachChild(model);
        } else {
            weaponHook.detachChild(model);
        }
    }

    public void nextAmmo() {
        ammoTypeIndex = (ammoTypeIndex + 1) % ammoTypes.size();
        System.out.println("AmmoType: " + ammoTypeIndex);
    }

    public AmmoType getAmmoType() {
        return ammoTypes.get(ammoTypeIndex);
    }

    public void addAmmoType(AmmoType ammoType) {
        ammoTypes.add(ammoType);
    }

    public String getDescription() {
        return "Weapon[" +
            " name: " + name +
            " damage: " + damage +
            " ammo: " + currAmmo + " / " + getAmmoType().name +
            " range: " + range +
            " ]";
    }
}
