package mygame.player;

import com.capdevon.anim.AnimUtils;
import com.capdevon.anim.Animator;
import com.capdevon.audio.AudioClip;
import com.capdevon.engine.SimpleAppState;
import com.capdevon.input.GInputAppState;
import com.jme3.anim.SkinningControl;
import com.jme3.anim.util.AnimMigrationUtils;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;

import mygame.camera.CameraCollisionControl;
import mygame.camera.TPSChaseCamera;
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
    private PlayerInput playerInput;

    @Override
    protected void simpleInit() {
        setupPlayer();
        registerInput();
    }

    private void registerInput() {
        GInputAppState ginput = stateManager.getState(GInputAppState.class);
        ginput.addActionListener(playerInput);
    }

    private void setupPlayer() {
        // Create a node for the character model
        player = (Node) assetManager.loadModel(AnimDefs.MODEL);
        AnimMigrationUtils.migrate(player);
        player.setLocalTranslation(0, -4f, 0);
        player.addControl(new Animator());
        player.addControl(new BetterCharacterControl(.5f, 1.8f, 80f));

        initCamera();
        
        Node node = new Node("aim-node");
        player.attachChild(node);
        node.setLocalTranslation(new Vector3f(0, 2, 0));

        PlayerControl playerControl = new PlayerControl();
        playerControl.camera           = camera;
        playerControl.weaponUI         = getBitmapText(20, settings.getHeight() - 20);
        playerControl.particleManager  = stateManager.getState(ParticleManager.class);
        playerControl.weapon           = initWeapon();
        playerControl.footstepsSFX     = getAudioClip(AudioLib.GRASS_FOOTSTEPS);
        playerControl.shootSFX         = getAudioClip(AudioLib.ARROW_HIT);
        playerControl.reloadSFX        = getAudioClip(AudioLib.BOW_PULL);
        player.addControl(playerControl);

        playerInput = new PlayerInput();
        player.addControl(playerInput);

        getPhysicsSpace().add(player);
        rootNode.attachChild(player);
    }

    private void initCamera() {
        TPSChaseCamera chaseCam = new TPSChaseCamera(camera, player);
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
        CameraCollisionControl cameraCollision = new CameraCollisionControl(camera, player, scene);
    }

    private Weapon initWeapon() {
    	SkinningControl skControl = AnimUtils.getSkinningControl(player);
    	//AnimUtils.addArmatureDebugger(assetManager, skControl);
        Node rh = skControl.getAttachmentsNode("Armature_mixamorig:" + MixamoBodyBones.RightHand);

        // replace this with the bow's model
        Node model = new Node("weapon-node");
        Geometry geo = createWeapon("weapon-geomesh", ColorRGBA.Green);
        model.setCullHint(Spatial.CullHint.Never);
        model.setLocalScale(100);
        model.attachChild(geo);
        rh.attachChild(model);

        Weapon weapon = new Weapon("Bow", rh, model);
        weapon.crosshair = new CrosshairData(guiNode, getCrossHair("- + -"));

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
        return weapon;
    }

    private Geometry createWeapon(String name, ColorRGBA color) {
        Sphere mesh = new Sphere(8, 8, .05f);
        Geometry geo = new Geometry(name, mesh);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", color);
        geo.setMaterial(mat);
        return geo;
    }

    private BitmapText getBitmapText(float xPos, float yPos) {
        BitmapText hud = new BitmapText(guiFont);
        hud.setSize(guiFont.getCharSet().getRenderedSize());
        hud.setLocalTranslation(xPos, yPos, 0);
        hud.setColor(ColorRGBA.Red);
        guiNode.attachChild(hud);
        return hud;
    }

    /* A centered plus sign to help the player aim. */
    private BitmapText getCrossHair(String text) {
        BitmapText ch = new BitmapText(guiFont);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 1.6f);
        ch.setText(text);
        float width = settings.getWidth() / 2 - ch.getLineWidth() / 2;
        float height = settings.getHeight() / 2 + ch.getLineHeight() / 2;
        ch.setLocalTranslation(width, height, 0);
        return ch;
    }

    /**
     * @param sound
     * @return
     */
    private AudioNode getAudioClip(AudioClip sound) {
        AudioNode audio = new AudioNode(assetManager, sound.file, AudioData.DataType.Buffer);
        audio.setVolume(sound.volume);
        audio.setLooping(sound.looping);
        audio.setPositional(sound.positional);
        return audio;
    }
}
