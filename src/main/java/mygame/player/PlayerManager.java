package mygame.player;

import com.capdevon.anim.AnimUtils;
import com.capdevon.anim.Animator;
import com.capdevon.anim.AvatarMask;
import com.capdevon.anim.IKRig;
import com.capdevon.audio.AudioClip;
import com.capdevon.engine.GameObject;
import com.capdevon.engine.SimpleAppState;
import com.capdevon.input.GInputAppState;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.SkinningControl;
import com.jme3.app.Application;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;

import jme3utilities.debug.SkeletonVisualizer;
import mygame.camera.CameraCollisionControl;
import mygame.camera.ThirdPersonCamera;
import mygame.states.ParticleManager;
import mygame.util.AnimDefs;
import mygame.util.AudioLib;
import mygame.util.MixamoBodyBones;

/**
 * 
 * @author capdevon
 */
public class PlayerManager extends SimpleAppState {

    private Node player;

    @Override
    public void initialize(Application app) {
        super.initialize(app);
        
        setupPlayer();
    }

    private void setupPlayer() {
        // Create a node for the character model
        player = (Node) assetManager.loadModel(AnimDefs.ARCHER_ASSET_MODEL);
        player.setName("Player");
        player.setUserData(GameObject.TAG_NAME, "TagPlayer");
        player.setLocalTranslation(0, -4f, 0);
        player.addControl(new BetterCharacterControl(.5f, 1.8f, 10f));
        
        AnimComposer animComposer = AnimUtils.getAnimCompser(player);
        SkinningControl skinningControl = AnimUtils.getSkinningControl(player);
        Spatial animRoot = animComposer.getSpatial();
        
        SkeletonVisualizer sv = new SkeletonVisualizer(assetManager, skinningControl);
        animRoot.addControl(sv);
        sv.setEnabled(true);

        // Override the default layer mask
        AvatarMask avatarMask = new AvatarMask(skinningControl.getArmature()).addAllJoints();
        animComposer.makeLayer(AnimComposer.DEFAULT_LAYER, avatarMask);

        IKRig rig = new IKRig(avatarMask);
        animRoot.addControl(rig);

        player.addControl(new Animator());
        player.addControl(new PlayerRespawn());

        setupChaseCamera(player);
        
        Node node = new Node("aim-node");
        player.attachChild(node);
        node.setLocalTranslation(new Vector3f(0, 2, 0));

        PlayerControl playerControl = new PlayerControl();
        playerControl.camera           = camera;
        playerControl.weaponUI         = createLabel(20, settings.getHeight() - 20);
        playerControl.particleManager  = getState(ParticleManager.class);
        playerControl.weapon           = initWeapon(skinningControl);
        playerControl.footstepsSFX     = createAudioNode(AudioLib.GRASS_FOOTSTEPS);
        playerControl.shootSFX         = createAudioNode(AudioLib.ARROW_HIT);
        playerControl.reloadSFX        = createAudioNode(AudioLib.BOW_PULL);
        player.addControl(playerControl);

        PlayerInput playerInput = new PlayerInput();
        player.addControl(playerInput);
        getState(GInputAppState.class).addActionListener(playerInput);

        getPhysicsSpace().add(player);
        rootNode.attachChild(player);
    }

    private void setupChaseCamera(Spatial target) {
        ThirdPersonCamera chaseCam = new ThirdPersonCamera(camera, target);
        chaseCam.registerWithInput(inputManager, settings.useJoysticks());
        chaseCam.setLookAtOffset(new Vector3f(0f, 2f, 0f));
        chaseCam.setMaxDistance(3f);
        chaseCam.setMinDistance(1f);
        chaseCam.setDefaultDistance(chaseCam.getMaxDistance());
        chaseCam.setMaxVerticalRotation(FastMath.QUARTER_PI);
        chaseCam.setMinVerticalRotation(-FastMath.QUARTER_PI);
        chaseCam.setRotationSensitivity(1.5f);
        chaseCam.setZoomSensitivity(3f);
        chaseCam.setDownRotateOnCloseViewOnly(false);

        Spatial scene = find("MainScene");

        CameraCollisionControl cameraCollision = new CameraCollisionControl(camera);
        cameraCollision.setScene(scene);
        target.addControl(cameraCollision);
    }

    private Weapon initWeapon(SkinningControl skinningControl) {
        
        Node leftHand = createBoneHook(skinningControl, "mixamorig:" + MixamoBodyBones.LeftHand);
        
        // Spatial model = assetManager.loadModel("Arrow.j3o");
        Spatial model = makeGeometry("Arrow", new Sphere(8, 8, .05f), ColorRGBA.Green);
        model.setCullHint(Spatial.CullHint.Never);
        leftHand.attachChild(model);
        
        Weapon weapon = new Weapon("Bow", leftHand);
        weapon.setCrosshair(new CrosshairData(guiNode, createCrossHair("- . -")));

        AmmoType flameArrow = new AmmoType();
        flameArrow.name             = "Flame";
        flameArrow.effect           = "Scenes/jMonkey/Flame.j3o";
        flameArrow.explosionRadius  = 5f;
        flameArrow.baseStrength     = 10f;

        AmmoType poisonArrow = new AmmoType();
        poisonArrow.name            = "Poison";
        poisonArrow.effect          = "Scenes/jMonkey/Poison.j3o";
        poisonArrow.explosionRadius = 4f;
        poisonArrow.baseStrength    = 6f;

        weapon.addAmmoType(flameArrow);
        weapon.addAmmoType(poisonArrow);
        
        model.addControl(weapon);
        return weapon;
    }

    private Node createBoneHook(SkinningControl skinningControl, String jointName) {
        Node aNode = skinningControl.getAttachmentsNode(jointName);
        Node ref = new Node("Joint-" + jointName);
        aNode.attachChild(ref);
        return ref;
    }

    private Geometry makeGeometry(String name, Mesh mesh, ColorRGBA color) {
        Geometry geo = new Geometry(name, mesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        geo.setMaterial(mat);
        return geo;
    }

    private BitmapText createLabel(float xPos, float yPos) {
        BitmapText hud = new BitmapText(guiFont);
        hud.setSize(guiFont.getCharSet().getRenderedSize());
        hud.setLocalTranslation(xPos, yPos, 0);
        hud.setColor(ColorRGBA.Red);
        guiNode.attachChild(hud);
        return hud;
    }

    /* A centered plus sign to help the player aim. */
    private BitmapText createCrossHair(String text) {
        BitmapText bmp = new BitmapText(guiFont);
        bmp.setSize(guiFont.getCharSet().getRenderedSize() * 1.6f);
        bmp.setText(text);
        float width = settings.getWidth() / 2f - bmp.getLineWidth() / 2f;
        float height = settings.getHeight() / 2f + bmp.getLineHeight() / 2f;
        bmp.setLocalTranslation(width, height, 0);
        return bmp;
    }

    /**
     * @param sound
     * @return
     */
    private AudioNode createAudioNode(AudioClip sound) {
        AudioNode audio = new AudioNode(assetManager, sound.file, AudioData.DataType.Buffer);
        audio.setVolume(sound.volume);
        audio.setLooping(sound.looping);
        audio.setPositional(sound.positional);
        return audio;
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }
}
