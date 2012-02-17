/*
 The MIT License

 Copyright (c) 2012 Zloteanu Nichita (ZNickq) and Andre Mohren (IceReaper)

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 */

package net.morematerials.morematerials.materials;

import java.util.logging.Level;

import net.morematerials.morematerials.SmpPackage;
import org.bukkit.configuration.ConfigurationSection;
import org.getspout.spoutapi.block.design.GenericCuboidBlockDesign;
import org.getspout.spoutapi.material.block.GenericCuboidCustomBlock;
import org.getspout.spoutapi.sound.SoundEffect;

public class SMCustomBlock extends GenericCuboidCustomBlock {

	private MaterialAction actionL = null;
	private MaterialAction actionR = null;
	private MaterialAction actionWalk = null;
	private Float speedMultiplier = (float) 1;
	private Float jumpMultiplier = (float) 1;
	private Float fallMultiplier = (float) 1;
	private SmpPackage smpPackage;
	private String redstoneTransform = null;
	

	public SMCustomBlock(SmpPackage smpPackage, String name, Boolean opaque, GenericCuboidBlockDesign design) {
		super(smpPackage.getSmpManager().getPlugin(), name, opaque, design);
		this.smpPackage = smpPackage;
	}

	public void setConfig(ConfigurationSection config) {
		double hardness = config.getDouble("Hardness", 0);
		double friction = config.getDouble("Friction", 0);
		int lightLevel = config.getInt("LightLevel", 0);
		Float lspeedMultiplier = (float) config.getDouble("WalkSpeed", 1);
		Float ljumpMultiplier = (float) config.getDouble("JumpHeight", 1);
		Float lfallMultiplier = (float) config.getDouble("FallDamage", 1);
		String stepSound = config.getString("StepSound", null);
		String lredstoneTransform = config.getString("RedstoneTransform", null);

		if (hardness != 0) {
			this.setHardness((float) hardness);
		}

		if (friction != 0) {
			this.setFriction((float) friction);
		}

		if (lightLevel > 0) {
			this.setLightLevel(lightLevel);
		}
		
		if (stepSound != null) {
			try {
				this.setStepSound(SoundEffect.getSoundEffectFromName(stepSound.toUpperCase()));
			} catch(Exception exception) {
				this.smpPackage.getSmpManager().getPlugin().log("Tried to set invalid sound effect!", Level.WARNING);
			}
		}
		
		if (lredstoneTransform != null) {
			this.redstoneTransform = lredstoneTransform;
		}

		if (config.isConfigurationSection("Lclick")) {
			this.actionL = new MaterialAction(config.getConfigurationSection("Lclick"), this.smpPackage);
		}
		
		if (config.isConfigurationSection("Rclick")) {
			this.actionR = new MaterialAction(config.getConfigurationSection("Rclick"), this.smpPackage);
		}
		
		if (config.isConfigurationSection("WalkAction")) {
			this.actionWalk = new MaterialAction(config.getConfigurationSection("WalkAction"), this.smpPackage);
		}

		this.speedMultiplier = lspeedMultiplier;
		this.jumpMultiplier = ljumpMultiplier;
		this.fallMultiplier = lfallMultiplier;
	}

	public Float getSpeedMultiplier() {
		return this.speedMultiplier;
	}

	public Float getJumpMultiplier() {
		return this.jumpMultiplier;
	}

	public Float getFallMultiplier() {
		return this.fallMultiplier;
	}
	
	public MaterialAction getActionL() {
		return this.actionL;
	}
	
	public MaterialAction getActionR() {
		return this.actionR;
	}
	
	public MaterialAction getActionWalk() {
		return this.actionWalk;
	}
	
	public String getRedstoneTransform() {
		return this.redstoneTransform;
	}
}
