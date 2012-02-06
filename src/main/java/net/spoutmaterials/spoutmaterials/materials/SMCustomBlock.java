/*
 The MIT License

 Copyright (c) 2011 Zloteanu Nichita (ZNickq), Sean Porter (Glitchfinder),
 Jan Tojnar (jtojnar, Lisured) and Andre Mohren (IceReaper)

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
package net.spoutmaterials.spoutmaterials.materials;

import net.spoutmaterials.spoutmaterials.SmpPackage;
import org.bukkit.configuration.ConfigurationSection;
import org.getspout.spoutapi.block.design.GenericCuboidBlockDesign;
import org.getspout.spoutapi.material.block.GenericCuboidCustomBlock;

public class SMCustomBlock extends GenericCuboidCustomBlock {

	private Float speedMultiplier = (float) 1;
	private Float jumpMultiplier = (float) 1;
	private Float fallMultiplier = (float) 1;

	public SMCustomBlock(SmpPackage smpPackage, String name, Boolean opaque, GenericCuboidBlockDesign design) {
		super(smpPackage.getSmpManager().getPlugin(), name, opaque, design);
	}

	public void setConfig(ConfigurationSection config) {
		double hardness = config.getDouble("Hardness", 0);
		double friction = config.getDouble("Friction", 0);
		int lightLevel = config.getInt("LightLevel", 0);
		Float lspeedMultiplier = (float) config.getDouble("WalkSpeed", 1);
		Float ljumpMultiplier = (float) config.getDouble("JumpHeight", 1);
		Float lfallMultiplier = (float) config.getDouble("FallDamage", 1);

		if (hardness != 0) {
			this.setHardness((float) hardness);
		}

		if (friction != 0) {
			this.setFriction((float) friction);
		}

		if (lightLevel > 0) {
			this.setLightLevel(lightLevel);
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
}
