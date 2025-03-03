package mygame.player;

import java.util.ArrayList;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;

/**
 * 
 * @author capdevon
 */
public class Weapon extends AbstractControl {

    // The name of the weapon.
    private String name;
    // The node where the weapon's spatial is attached.
    private Node weaponHook;
    // Data related to the weapon's crosshair.
    private CrosshairData crosshair;
    // The effective range of the weapon.
    private float range = 30f;
    // The damage the weapon inflicts.
    private float damage = 50f;
    // The current amount of ammunition available.
    private int currAmmo = 20;
    // The maximum amount of ammunition the weapon can hold.
    private int maxAmmo = 40;
    // The index of the currently selected ammo type.
    private int ammoTypeIndex = 0;
    // A list of ammunition types the weapon can use.
    private final ArrayList<AmmoType> ammoTypes = new ArrayList<>();

    /**
     * Constructs a new Weapon.
     *
     * @param name       The name of the weapon.
     * @param weaponHook The node where the weapon's spatial will be attached.
     */
    public Weapon(String name, Node weaponHook) {
        this.weaponHook = weaponHook;
        this.name = name;
    }

    /**
     * Sets the active state of the weapon, attaching or detaching its spatial from
     * the weapon hook.
     *
     * @param active True to activate the weapon, false to deactivate.
     */
    public void setActive(boolean active) {
        if (active) {
            weaponHook.attachChild(spatial);
        } else {
            weaponHook.detachChild(spatial);
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
    }

    public AmmoType getAmmoType() {
        return ammoTypes.get(ammoTypeIndex);
    }

    public void addAmmoType(AmmoType ammoType) {
        ammoTypes.add(ammoType);
    }

    // ------------------------------------------
    // Getters/Setters
    // ------------------------------------------

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
        return "Weapon " +
            "name: " + name +
            ", type: " + getAmmoType().name +
            ", ammo: " + currAmmo + " / " + maxAmmo + 
            ", damage: " + damage +
            ", range: " + range;
    }

    @Override
    protected void controlUpdate(float tpf) {
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
