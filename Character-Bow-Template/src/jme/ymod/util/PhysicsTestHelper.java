/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jme.ymod.util;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.ConeCollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.util.TangentBinormalGenerator;


/**
 *
 */
public class PhysicsTestHelper {

    public static ColorRGBA color = ColorRGBA.Cyan;
    public static String floorTexture = "Textures/Floor/Grid/chess_grid.jpg";

    public static Node createLightingBox(AssetManager am, int nClone) {
        Node root = new Node("Group.L");
        Geometry cube = getBoxLighting(am);
        build(root, cube, nClone);

        return root;
    }

    public static Node createUnshadedBox(AssetManager am, int nClone) {
        Node root = new Node("Group.U");
        Geometry cube = getBoxUnshaded(am);
        build(root, cube, nClone);

        return root;
    }

    private static void build(Node root, Geometry cube, int nClone) {
        for (int i = 0; i < nClone; i++) {

            Geometry g = cube.clone(true);
            g.setName("Box.GeoMesh." + i);
            root.attachChild(g);

            Vector3f loc = getRandomPoint(-20, 20).setY(20);
            g.setLocalTranslation(loc);

            CollisionShape collShape = CollisionShapeFactory.createBoxShape(g);
            RigidBodyControl rgb = new RigidBodyControl(collShape, 1f);
            g.addControl(rgb);
            PhysicsSpace.getPhysicsSpace().add(rgb);
        }
    }

    private static Geometry getBoxUnshaded(AssetManager am) {
        Box box = new Box(.5f, .5f, .5f);
        Geometry g = new Geometry("GeoMesh", box);
        Material mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", am.loadTexture("Common/Textures/MissingTexture.png"));
        mat.setColor("Color", ColorRGBA.randomColor());
        g.setMaterial(mat);
        return g;
    }

    private static Geometry getBoxLighting(AssetManager am) {
        Box box = new Box(.5f, .5f, .5f);
        TangentBinormalGenerator.generate(box);
        Geometry g = new Geometry("Box.GeoMesh", box);
        Material mat = new Material(am, "Common/MatDefs/Light/Lighting.j3md");
        mat.setTexture("DiffuseMap", am.loadTexture("Interface/Logo/Monkey.png"));
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", ColorRGBA.randomColor());
        mat.setColor("Specular", ColorRGBA.White);
        mat.setFloat("Shininess", 12);
        g.setMaterial(mat);
        return g;
    }

    public static Spatial attachMainScene(Node rootNode, AssetManager am) {
        Spatial scene = am.loadModel("Scenes/ManyLights/Main.scene");
        scene.setName("MainScene");
        scene.scale(1f, .5f, 1f);
        scene.setLocalTranslation(0f, -10f, 0f);
        rootNode.attachChild(scene);
        addStaticMeshCollider(scene);

        return scene;
    }
    
    public static Spatial attachTownScene(Node rootNode, AssetManager am) {
        Spatial scene = am.loadModel("Scenes/town/main.scene");
//        Spatial scene = am.loadModel("Scenes/NuclearPlant/nuclear-plant.j3o");
        scene.setName("MainScene");
        scene.setLocalTranslation(0, -5.2f, 0);
        rootNode.attachChild(scene);
        addStaticMeshCollider(scene);

        return scene;
    }

    public static void attachFloor(Node rootNode, AssetManager am) {
        Box mesh = new Box(20, 0.1f, 20);
        Geometry g = new Geometry("Floor.GeoMesh", mesh);
        Material mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        g.setMaterial(mat);
        rootNode.attachChild(g);

        addStaticMeshCollider(g);
    }

    public static void attachFloorTex(Node rootNode, AssetManager am) {
        Box box = new Box(20, .1f, 20);
        box.scaleTextureCoordinates(new Vector2f(10, 10));
        Geometry g = new Geometry("Floor.GeoMesh", box);
        Material mat = new Material(am, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = am.loadTexture(floorTexture);
        tex.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("ColorMap", tex);
        g.setMaterial(mat);
        rootNode.attachChild(g);

        addStaticMeshCollider(g);
    }

    private static Vector3f getRandomPoint(int a, int b) {
        int dx = nextInt(a, b);
        int dz = nextInt(a, b);
        return new Vector3f(dx, 0, dz);
    }

    private static int nextInt(int a, int b) {
        return FastMath.nextRandomInt(a, b);
    }

    public static void addStaticMeshCollider(Spatial sp) {
        addMeshCollider(sp, 0f);
    }

    public static void addMeshCollider(Spatial sp, float mass) {
        CollisionShape shape = CollisionShapeFactory.createMeshShape(sp);
        RigidBodyControl rgb = new RigidBodyControl(shape, mass);
        sp.addControl(rgb);
        PhysicsSpace.getPhysicsSpace().add(rgb);
    }

    public static void setUserDataRecursive(Spatial sp, final String key, final Object data) {
        sp.depthFirstTraversal(new SceneGraphVisitorAdapter() {
            @Override
            public void visit(Geometry g) {
                g.setUserData(key, data);
            }
        });
    }

    public enum ShapeType {
        NONE, CAPSULE, BOX, SPHERE, CYLINDER, CONE, PLANE, COMPLEX, HULL, MESH
    }

    /**
     *
     * @param type
     * @param sp
     * @param widthScale
     * @param heightScale
     * @return
     */
    public static CollisionShape createPhysicShape(ShapeType type, Spatial sp, float widthScale, float heightScale) {

        BoundingBox vol = (BoundingBox) sp.getWorldBound();
        CollisionShape collShape = null;

        if (type.equals(ShapeType.CAPSULE)) {
            float radius = Math.max(vol.getXExtent(), vol.getZExtent()) * widthScale;
            float height = vol.getYExtent() * heightScale;
            collShape = new CapsuleCollisionShape(radius, height);
        }
        if (type.equals(ShapeType.BOX)) {
            float x = vol.getXExtent() * widthScale;
            float z = vol.getZExtent() * widthScale;
            float y = vol.getYExtent() * heightScale;
            collShape = new BoxCollisionShape(new Vector3f(x, y, z));
        }
        if (type.equals(ShapeType.SPHERE)) {
            float radius = Math.max(Math.max(vol.getXExtent(), vol.getZExtent()), vol.getYExtent());
            collShape = new SphereCollisionShape(radius);
        }
        if (type.equals(ShapeType.CYLINDER)) {
            float x = Math.max(vol.getXExtent(), vol.getZExtent()) * widthScale;
            float z = Math.max(vol.getXExtent(), vol.getZExtent()) * widthScale;
            float y = vol.getYExtent() * heightScale;
            collShape = new CylinderCollisionShape(new Vector3f(x, y, z));
        }
        if (type.equals(ShapeType.CONE)) {
            float radius = Math.max(vol.getXExtent(), vol.getZExtent()) * widthScale;
            float height = vol.getYExtent();
            collShape = new ConeCollisionShape(radius, height);
        }
        if (type.equals(ShapeType.PLANE)) {
            Vector3f normal = sp.getWorldRotation().mult(Vector3f.UNIT_XYZ);
            float constant = Math.max(vol.getXExtent(), vol.getZExtent());
            collShape = new PlaneCollisionShape(new Plane(normal, constant));
        }
        if (type.equals(ShapeType.COMPLEX)) {
            collShape = CollisionShapeFactory.createMeshShape(sp);
        }
        if (type.equals(ShapeType.HULL)) {
            Geometry geo = (Geometry) sp;
            collShape = new HullCollisionShape(geo.getMesh());
        }
        if (type.equals(ShapeType.MESH)) {
            Geometry geo = (Geometry) sp;
            collShape = new MeshCollisionShape(geo.getMesh());
        }

        return collShape;
    }

}
