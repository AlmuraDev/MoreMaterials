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

import java.util.ArrayList;
import java.util.List;

import net.morematerials.MoreMaterials;

import org.bukkit.configuration.file.YamlConfiguration;
import org.getspout.spoutapi.block.design.GenericBlockDesign;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.Material;
import org.getspout.spoutapi.material.block.GenericCustomBlock;

public class MMCustomBlock extends GenericCustomBlock {

	private String materialName;
	private String smpName;
	private YamlConfiguration config;
	private MoreMaterials plugin;
	private ArrayList<String> requiredTools = new ArrayList<String>();

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
		MMCustomBlock block = new MMCustomBlock(plugin, yaml.getString("Title", matName), texture, smpName, matName, customDesign, yaml.getBoolean("Rotation", false), yaml.getInt("BaseId", 1), yaml);
		block.configureBase();
		return block;
	}

	public MMCustomBlock(MoreMaterials plugin, String name, String texture, String smpName, String matName, GenericBlockDesign design, Boolean rotate, Integer baseId, YamlConfiguration config) {
		super(plugin, name, baseId, design, rotate);
		this.plugin = plugin;
		this.smpName = smpName;
		this.materialName = matName;
		this.config = config;
	}

	private void configureBase() {
		// Set the blocks base hardness
		if (this.config.contains("Hardness")) {
			this.setHardness((float) this.config.getDouble("Hardness"));
		}
		
		// Set the blocks friction
		if (this.config.contains("Friction")) {
			this.setFriction((float) this.config.getDouble("Friction"));
		}
		
		// Set the blocks lightlevel
		if (this.config.contains("LightLevel")) {
			this.setLightLevel(this.config.getInt("LightLevel"));
		}
		
		// Set the required tools to make this block drop
		if (this.config.contains("RequiredTools")) {
			String[] tools = this.config.getString("RequiredTools").split("[\\s]+");
			for (Integer i = 0; i < tools.length; i++) {
				this.requiredTools.add(tools[i]);
			}
		}
	}

	public void configureDrops() {
		// Configure itemdrop
		String drop = this.config.contains("ItemDrop") ? this.config.getString("ItemDrop") : this.materialName;
		Integer dropCount = this.config.contains("ItemDropAmount") ? this.config.getInt("ItemDropAmount") : 1;
		ArrayList<Material> materials = this.plugin.getSmpManager().getMaterial(this.smpName, drop);
		this.setItemDrop(new SpoutItemStack(materials.get(0), dropCount));
	}

	public String getSmpName() {
		return this.smpName;
	}

	public String getMaterialName() {
		return this.materialName;
	}

	public ArrayList<String> getRequiredTools() {
		return this.requiredTools;
	}

}
