package mygame.player;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 * A control that manages player respawning when falling below a specified
 * height.
 * 
 * @author capdevon
 */
public class PlayerRespawn extends AbstractControl {

    // The location where the player respawns.
    private Vector3f spawnPoint = new Vector3f(0, 0, 0);
    // The height below which the player is considered to have fallen.
    private float fallHeight = -20f;
    // The character control component for player movement.
    private BetterCharacterControl bcc;

    @Override
    public void setSpatial(Spatial sp) {
        super.setSpatial(sp);
        if (spatial != null) {
            this.bcc = spatial.getControl(BetterCharacterControl.class);
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (spatial.getWorldTranslation().y < fallHeight) {
            bcc.warp(spawnPoint);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public Vector3f getSpawnPoint() {
        return spawnPoint;
    }

    public void setSpawnPoint(Vector3f spawnPoint) {
        this.spawnPoint.set(spawnPoint);
    }

    public float getFallHeight() {
        return fallHeight;
    }

    public void setFallHeight(float fallHeight) {
        this.fallHeight = fallHeight;
    }

}
