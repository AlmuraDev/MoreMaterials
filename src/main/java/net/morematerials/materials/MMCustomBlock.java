/*
 * This file is part of MoreMaterials, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2013 AlmuraDev <http://www.almuradev.com/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.morematerials.materials;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.morematerials.MoreMaterials;

import org.bukkit.configuration.file.YamlConfiguration;
import org.getspout.spoutapi.block.design.GenericBlockDesign;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.Material;
import org.getspout.spoutapi.material.MaterialData;
import org.getspout.spoutapi.material.block.GenericCustomBlock;

public class MMCustomBlock extends GenericCustomBlock implements CustomFuel, CustomMaterial {

	private String materialName;
	private String smpName;
	private YamlConfiguration config;
	private MoreMaterials plugin;
	private Integer burnTime, speedMultiplier, jumpMultiplier;
	private List<?> requiredTools;

	@SuppressWarnings("unchecked")
	public static MMCustomBlock create(MoreMaterials plugin, YamlConfiguration yaml, String smpName, String matName) {
		String texture = yaml.getString("Texture", "");
		
		// Allow to reference textures from other .smp files.
		String[] fileNameParts = texture.split("/");
		if (fileNameParts.length == 2) {
			texture = fileNameParts[0] + "_" + fileNameParts[1];
		} else {
			texture = smpName + "_" + texture;
		}

		// Getting the correct model for this block.
		String shapeFile = yaml.getString("Shape");
		CustomShapeTemplate designTemplate;
		if (shapeFile == null) {
			designTemplate = new CustomShapeTemplate(plugin);
		} else {
			shapeFile = shapeFile.substring(0, shapeFile.lastIndexOf("."));
			if (plugin.getSmpManager().getShape(smpName, shapeFile) != null) {
				designTemplate = plugin.getSmpManager().getShape(smpName, shapeFile);
			} else {
				designTemplate = new CustomShapeTemplate(plugin);
			}
		}
		GenericBlockDesign design = designTemplate.createInstance(texture, (List<String>) yaml.getList("Coords", new ArrayList<String>()));

		// Build the block.
		return new MMCustomBlock(plugin, texture, smpName, matName, design, yaml);
	}

	public MMCustomBlock(MoreMaterials plugin, String texture, String smpName, String matName, GenericBlockDesign design, YamlConfiguration config) {
		super(plugin, smpName + "." + matName, config.getInt("BaseId", 1), design, config.getBoolean("Rotation", false), config.getBoolean("MirroredRotation", false), config.getBoolean("FullRotation", false));
		this.plugin = plugin;
		this.smpName = smpName;
		this.materialName = matName;
		this.config = config;
		this.setName(this.config.getString("Title", matName));
		
		// Fuel support
		this.burnTime = this.config.getInt("BurnTime", 0);
		
		// Set the blocks base hardness
		this.setHardness((float) this.config.getDouble("Hardness", this.getHardness()));
		
		// Set the blocks friction
		this.setFriction((float) this.config.getDouble("Friction", this.getFriction()));
		
		// Set the blocks lightlevel
		this.setLightLevel(this.config.getInt("LightLevel", 0));

		// Walk Speed Multiplier
		this.speedMultiplier = this.config.getInt("Walkspeed", 1);
		
		// Jump Height Multiplier
		this.jumpMultiplier = this.config.getInt("JumpHeight", 1);
		
		// Register handlers.
		if (this.config.contains("Handlers")) {
			this.registerHandlers();
		}
		
		// Required Tools
		this.requiredTools = this.config.getList("RequiredTools");		
		
	}
	
	private void registerHandlers() {
		for (String eventType : this.config.getConfigurationSection("Handlers").getKeys(false)) {
			for (Object handlerEntry : this.config.getList("Handlers." + eventType)) {
				@SuppressWarnings("unchecked")
				Map<String, Object> handlerConfig = (Map<String, Object>) handlerEntry;
				if (this.plugin.getHandlerManager().getHandler((String) handlerConfig.get("Name")) != null) {
					this.plugin.getHandlerManager().registerHandler(eventType, this.getCustomId(), handlerConfig);
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
		
		if (material == null) {
			material = this.plugin.getSmpManager().getMaterial(null, drop);
			if (material != null) {
				this.plugin.getUtilsManager().log("Multi-SMP based ItemDrop within [" + smpName + "] for item [" + drop + "] created.", Level.INFO);
			}
		}
		
		if (material != null) {
			this.setItemDrop(new SpoutItemStack(material, dropCount));
		}
	}

	public String getSmpName() {
		return this.smpName;
	}

	public String getMaterialName() {
		return this.materialName;
	}
	
	public Integer getBurnTime() {
		return this.burnTime;
	}
	
	public void setBurnTime(int newBurnTime) {
		this.burnTime = newBurnTime;
	}
	
	public Integer getSpeedMultiplier() {
		return this.speedMultiplier;
	}
	
	public void setSpeedMultiplier(int newSpeedMultiplier) {
		this.jumpMultiplier = newSpeedMultiplier;
	}
	
	public String getDisplayName() {
		return this.getDisplayName();
	}
	
	public YamlConfiguration getConfig() {
		return this.config;
	}
	
	public Integer getJumpMultiplier() {
		return this.jumpMultiplier;
	}
	
	public void setJumpMultiplier(int newJumpHeight) {
		this.jumpMultiplier = newJumpHeight;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getRequiredTools() {
		return (List<String>) requiredTools;
	}
}
