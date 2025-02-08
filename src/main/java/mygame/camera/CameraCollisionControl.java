package mygame.camera;

import java.util.Objects;

import com.capdevon.physx.RaycastHit;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.CameraInput;
import com.jme3.input.ChaseCamera;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 * 
 * @author capdevon
 */
public class CameraCollisionControl extends AbstractControl {

    private Spatial scene;
    private Camera camera;
    private ChaseCamera chaseCam;

    private final Vector3f targetLocation = new Vector3f();
    private final Vector3f targetToCamDirection = new Vector3f();
    private final RaycastHit hitInfo = new RaycastHit();
    
    private boolean isZooming;

    public CameraCollisionControl(Camera camera) {
        this.camera = camera;
    }

    public Spatial getScene() {
        return scene;
    }

    public void setScene(Spatial scene) {
        this.scene = scene;
    }

    public void setZooming(boolean isZooming) {
        this.isZooming = isZooming;
        chaseCam.setRotationSpeed( isZooming ? .5f : 1 );
//        chaseCam.setDefaultDistance( isZooming ? chaseCam.getMinDistance() : chaseCam.getMaxDistance() );
    }

    @Override
    public void setSpatial(Spatial sp) {
        super.setSpatial(sp);
        if (spatial != null) {
            this.chaseCam = spatial.getControl(ChaseCamera.class);
            Objects.requireNonNull(chaseCam, "ChaseCamera not found: " + spatial);
        }
    }

    @Override
    protected void controlUpdate(float tpf) {

        // Retrieve camera settings
        float minDistance = chaseCam.getMinDistance();
        float maxDistance = chaseCam.getMaxDistance();
        float zSensitivity = chaseCam.getZoomSensitivity();

        // Handle zooming in
        if (isZooming) {
            if (chaseCam.getDistanceToTarget() > minDistance) {
                chaseCam.onAnalog(CameraInput.CHASECAM_ZOOMIN, tpf * zSensitivity, tpf);
            }
            return;
        }

        // Update target location and direction to camera
        targetLocation.set( spatial.getWorldTranslation() ).addLocal(chaseCam.getLookAtOffset());
        targetToCamDirection.set( camera.getLocation() ).subtractLocal(targetLocation).normalizeLocal();

        // Perform raycast to check for obstacles between the camera and the target
        if (doRaycast(targetLocation, targetToCamDirection, maxDistance, hitInfo)) {

            // Zoom in if an obstacle is detected within the camera's current distance
            if (chaseCam.getDistanceToTarget() + hitInfo.normal.length() > hitInfo.distance) {
                chaseCam.onAnalog(CameraInput.CHASECAM_ZOOMIN, tpf * zSensitivity, tpf);
            }
        } else if (chaseCam.getDistanceToTarget() < maxDistance) {
            // Zoom out if no obstacles are detected and the camera is closer than the max
            // distance
            chaseCam.onAnalog(CameraInput.CHASECAM_ZOOMOUT, tpf * zSensitivity, tpf);
        }
    }
    
    /**
     * Performs a raycast from the given origin in the specified direction up to the
     * maximum distance.
     *
     * @param origin      The starting point of the ray.
     * @param direction   The direction of the ray.
     * @param maxDistance The maximum distance the ray should travel.
     * @param hitResult   The object to store the results of the raycast.
     * @return true if the ray hits an object within the maximum distance, false otherwise.
     */
    private boolean doRaycast(Vector3f origin, Vector3f dir, float maxDistance, RaycastHit out) {

        // Clear previous hit results
        out.clear();

        // Create a new ray from the origin in the specified direction
        Ray ray = new Ray(origin, dir);
        ray.setLimit(maxDistance); // FIXME: Bug! Ensure this is correctly limiting the ray's distance

        // Collect collision results
        CollisionResults results = new CollisionResults();
        scene.collideWith(ray, results);

        boolean hit = false;
        if (results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();
            out.userObject  = closest.getGeometry();
            out.normal      = closest.getContactNormal();
            out.point       = closest.getContactPoint();
            out.distance    = closest.getDistance();

            if (out.distance < maxDistance) {
                hit = true;
            }
        }

        return hit;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

}
