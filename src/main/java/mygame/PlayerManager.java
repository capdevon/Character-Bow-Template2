/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.capdevon.animation.AnimUtils;
import com.capdevon.animation.Animator;
import com.capdevon.engine.AudioClip;
import com.capdevon.engine.SimpleAppState;
import com.capdevon.input.GInputAppState;
import com.capdevon.util.AudioLib;
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

/**
 * 
 * @author capdevon
 */
public class PlayerManager extends SimpleAppState {

	private Node player;
	private PlayerControl playerCtrl;
	private PlayerInput playerInput;

	@Override
	protected void simpleInit() {
		setupPlayer();
	}

	@Override
	protected void registerInput() {
		GInputAppState ginput = stateManager.getState(GInputAppState.class);
		ginput.addActionListener(playerInput);
	}

	private void setupPlayer() {
		// Create a node for the character model
		player = (Node) assetManager.loadModel(AnimDefs.MODEL);

		player.addControl(new Animator());
		player.addControl(new BetterCharacterControl(.5f, 1.8f, 80f));

		initCamera();

		playerCtrl = new PlayerControl();
		playerCtrl.camera = camera;
		playerCtrl.particleManager = stateManager.getState(ParticleManager.class);
		playerCtrl.weapon = initWeapons();
		playerCtrl.footstepsSFX = getAudioClip(AudioLib.GRASS_FOOTSTEPS);
		playerCtrl.shootSFX = getAudioClip(AudioLib.ARROW_HIT);
		playerCtrl.reloadSFX = getAudioClip(AudioLib.BOW_PULL);
		player.addControl(playerCtrl);

		playerInput = new PlayerInput();
		player.addControl(playerInput);

		physics.getPhysicsSpace().add(player);
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

	private Weapon initWeapons() {
		Node rh = AnimUtils.getAttachments(player, "Armature_mixamorig:" + MixamoBodyBones.RightHand);

		Node model = new Node("weapon-node");
		Geometry geo = getRuntimeWeapon("weapon-geomesh", ColorRGBA.Green);
		model.setCullHint(Spatial.CullHint.Never);
		model.setLocalScale(100);
		model.attachChild(geo);
		rh.attachChild(model);

		Weapon weapon = new Weapon("Bow", rh, model);
		weapon.crosshair = new CrosshairData(guiNode, getCrossHair());
		weapon.addAmmoType("Scenes/jMonkey/Flame.j3o");
		weapon.addAmmoType("Scenes/jMonkey/Poison.j3o");
		return weapon;
	}

	private Geometry getRuntimeWeapon(String name, ColorRGBA color) {
		Sphere mesh = new Sphere(8, 8, .05f);
		Geometry geo = new Geometry(name, mesh);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", color);
		geo.setMaterial(mat);
		return geo;
	}

	/* A centered plus sign to help the player aim. */
	private BitmapText getCrossHair() {
		BitmapText ch = new BitmapText(guiFont, false);
		ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
		ch.setText("+");
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
