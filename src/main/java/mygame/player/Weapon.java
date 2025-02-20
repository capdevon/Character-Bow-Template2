package mygame.player;

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

    private CrosshairData crosshair;
    private float range = 30f;
    private float damage = 50f;
    private int currAmmo = 20;
    private int maxAmmo = 40;

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
    
    public boolean canShooting() {
        return currAmmo > 0;
    }
    
    public void shoot() {
        currAmmo--;
    }
    
    public void reload() {
        currAmmo = maxAmmo;
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
    
    // Getters/Setters

    public CrosshairData getCrosshair() {
        return crosshair;
    }

    public void setCrosshair(CrosshairData crosshair) {
        this.crosshair = crosshair;
    }

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public int getMaxAmmo() {
        return maxAmmo;
    }

    public void setMaxAmmo(int maxAmmo) {
        this.maxAmmo = maxAmmo;
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
