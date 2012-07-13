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

package net.morematerials.materials;

import java.util.List;

import net.morematerials.MoreMaterials;

import org.bukkit.configuration.file.YamlConfiguration;
import org.getspout.spoutapi.block.design.GenericBlockDesign;
import org.getspout.spoutapi.material.block.GenericCustomBlock;

public class MMCustomBlock extends GenericCustomBlock {

	private String materialName;
	private String smpName;

	@SuppressWarnings("unchecked")
	public static MMCustomBlock create(MoreMaterials plugin, YamlConfiguration yaml, String smpName, String matName) {
		String texture = yaml.getString("Texture");
		texture = plugin.getWebManager().getAssetsUrl(smpName + "_" + texture);

		// Getting the correct model for this block.
		String shapeFile = yaml.getString("Shape");
		shapeFile = shapeFile.substring(0, shapeFile.lastIndexOf("."));
		CustomShape customDesign;
		if (shapeFile != null && plugin.getSmpManager().getShape(smpName, shapeFile) != null) {
			customDesign = plugin.getSmpManager().getShape(smpName, shapeFile);
		} else {
			customDesign = new CustomShape(plugin);
		}
		customDesign.build(texture, (List<String>) yaml.getList("Coords"));

		// Build the block.
		MMCustomBlock block = new MMCustomBlock(plugin, yaml.getString("Title", matName), texture, smpName, matName, customDesign, yaml.getBoolean("Rotation", false), yaml.getInt("BaseId", 1));
		block.configureBase(yaml);
		return block;
	}

	public MMCustomBlock(MoreMaterials plugin, String name, String texture, String smpName, String matName, GenericBlockDesign design, Boolean rotate, Integer baseId) {
		super(plugin, name, baseId, design, rotate);
		this.smpName = smpName;
		this.materialName = matName;
	}

	private void configureBase(YamlConfiguration config) {
		// Set the blocks base hardness
		if (config.contains("Hardness")) {
			this.setHardness((float) config.getDouble("Hardness"));
		}
		
		// Set the blocks friction
		if (config.contains("Friction")) {
			this.setFriction((float) config.getDouble("Friction"));
		}
		
		// Set the blocks lightlevel
		if (config.contains("LightLevel")) {
			this.setLightLevel(config.getInt("LightLevel"));
		}
	}

	public String getSmpName() {
		return this.smpName;
	}

	public String getMaterialName() {
		return this.materialName;
	}

}
