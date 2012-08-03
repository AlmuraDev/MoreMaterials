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
import java.util.Map;
import java.util.Set;

import net.morematerials.MoreMaterials;

import org.bukkit.configuration.file.YamlConfiguration;
import org.getspout.spoutapi.block.design.GenericBlockDesign;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.Material;
import org.getspout.spoutapi.material.MaterialData;
import org.getspout.spoutapi.material.block.GenericCustomBlock;

public class MMCustomBlock extends GenericCustomBlock {

	private String materialName;
	private String smpName;
	private YamlConfiguration config;
	private MoreMaterials plugin;
	private Boolean itemDropRequired;

	@SuppressWarnings("unchecked")
	public static MMCustomBlock create(MoreMaterials plugin, YamlConfiguration yaml, String smpName, String matName) {
		String texture = yaml.getString("Texture");
		
		// Allow to reference textures from other .smp files.
		String[] fileNameParts = texture.split("/");
		if (fileNameParts.length == 2) {
			texture = plugin.getWebManager().getAssetsUrl(fileNameParts[0] + "_" + fileNameParts[1]);
		} else {
			texture = plugin.getWebManager().getAssetsUrl(smpName + "_" + texture);
		}

		// Getting the correct model for this block.
		String shapeFile = yaml.getString("Shape");
		shapeFile = shapeFile.substring(0, shapeFile.lastIndexOf("."));
		CustomShape customDesign;
		if (shapeFile != null && plugin.getSmpManager().getShape(smpName, shapeFile) != null) {
			customDesign = plugin.getSmpManager().getShape(smpName, shapeFile).createInstance();
		} else {
			customDesign = new CustomShape(plugin);
		}
		customDesign.build(texture, (List<String>) yaml.getList("Coords"));

		// Build the block.
		return new MMCustomBlock(plugin, yaml.getString("Title", matName), texture, smpName, matName, customDesign, yaml.getBoolean("Rotation", false), yaml.getInt("BaseId", 1), yaml);
	}

	public MMCustomBlock(MoreMaterials plugin, String name, String texture, String smpName, String matName, GenericBlockDesign design, Boolean rotate, Integer baseId, YamlConfiguration config) {
		super(plugin, smpName + "." + matName, baseId, design, rotate);
		this.plugin = plugin;
		this.smpName = smpName;
		this.materialName = matName;
		this.config = config;
		this.setName(name);
		
		// Set the blocks base hardness
		this.setHardness((float) this.config.getDouble("Hardness", this.getHardness()));
		
		// Set the blocks friction
		this.setFriction((float) this.config.getDouble("Friction", this.getFriction()));
		
		// Set the blocks lightlevel
		this.setLightLevel(this.config.getInt("LightLevel", 0));
		
		// Does this object require an tool to be dropped.
		this.itemDropRequired = this.config.getBoolean("ItemDropRequired", false);
		
		// Register handlers.
		if (this.config.contains("Handlers")) {
			this.registerHandlers();
		}
	}
	
	private void registerHandlers() {
		Set<String> eventTypes = this.config.getConfigurationSection("Handlers").getKeys(false);
		for (String eventType : eventTypes) {
			if (this.config.contains("Handlers." + eventType)) {
				List<?> handlerList = this.config.getList("Handlers." + eventType);
				for (Object handlerEntry : handlerList) {
					@SuppressWarnings("unchecked")
					Map<String, Object> handlerConfig = (Map<String, Object>) handlerEntry;
					if (this.plugin.getHandlerManager().getHandler((String) handlerConfig.get("Name")) != null) {
						this.plugin.getHandlerManager().registerHandler(eventType, this.getCustomId(), handlerConfig);
					}
				}
			}
		}
	}

	public void configureDrops() {
		// Configure itemdrop
		String drop = this.config.getString("ItemDrop", this.materialName);
		Integer dropCount = this.config.getInt("ItemDropAmount", 1);
		Material material;
		if (drop.matches("^[0-9@]+$")) {
			String[] matInfo = drop.split("@");
			if (matInfo.length == 1) {
				material = MaterialData.getMaterial(Integer.parseInt(matInfo[0]));
			} else {
				material = MaterialData.getMaterial(Integer.parseInt(matInfo[0]), (short) Integer.parseInt(matInfo[1]));
			}
		} else {
			material = this.plugin.getSmpManager().getMaterial(this.smpName, drop);
		}
		this.setItemDrop(new SpoutItemStack(material, dropCount));
	}

	public String getSmpName() {
		return this.smpName;
	}

	public String getMaterialName() {
		return this.materialName;
	}

	public Boolean getItemDropRequired() {
		return this.itemDropRequired;
	}

}
