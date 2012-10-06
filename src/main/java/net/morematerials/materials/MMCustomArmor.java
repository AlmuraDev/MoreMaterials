/*
 * This file is part of MoreMaterials.
 * 
 * Copyright (c) 2012 Andre Mohren (IceReaper)
 * 
 * The MIT License
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

import java.util.Map;

import net.morematerials.MoreMaterials;

import org.bukkit.configuration.file.YamlConfiguration;
//import org.getspout.spoutapi.material.item.GenericCustomArmor;
import org.getspout.spoutapi.material.item.GenericCustomTool;

//public class MMCustomArmor extends GenericCustomArmor implements CustomFuel, CustomMaterial {
public class MMCustomArmor extends GenericCustomTool implements CustomFuel, CustomMaterial {

	private String materialName;
	private String smpName;
	private YamlConfiguration config;
	private MoreMaterials plugin;
	private Integer burnTime;

	public static MMCustomArmor create(MoreMaterials plugin, YamlConfiguration yaml, String smpName, String matName) {
		String itemTexture = yaml.getString("ItemTexture", "");
		String armorTexture = yaml.getString("ArmorTexture", "");
		
		// Allow to reference textures from other .smp files.
		String[] fileNameParts = itemTexture.split("/");
		if (fileNameParts.length == 2) {
			itemTexture = fileNameParts[0] + "_" + fileNameParts[1];
		} else {
			itemTexture = smpName + "_" + itemTexture;
		}
		fileNameParts = armorTexture.split("/");
		if (fileNameParts.length == 2) {
			armorTexture = fileNameParts[0] + "_" + fileNameParts[1];
		} else {
			armorTexture = smpName + "_" + armorTexture;
		}
		
		// TODO use texture Coords (looks like SpoutPlugin needs a patch)
		
		// Build the armor.
		return new MMCustomArmor(plugin, yaml, itemTexture, armorTexture, smpName, matName, getSlotId(yaml.getString("Slot", "")));
	}

	private static short getSlotId(String slot) {
		if (slot.equals("Head")) {
			return 5;
		} else if (slot.equals("Chest")) {
			return 6;
		} else if (slot.equals("Legs")) {
			return 7;
		} else if (slot.equals("Feet")) {
			return 8;
		}
		return 0;
	}

	public MMCustomArmor(MoreMaterials plugin, YamlConfiguration config, String itemTexture, String armorTexture, String smpName, String matName, short slot) {
//		super(plugin, smpName + "." + matName, itemTexture, armorTexture, slot);
		super(plugin, smpName + "." + matName, itemTexture);
		this.smpName = smpName;
		this.materialName = matName;
		this.config = config;
		this.plugin = plugin;
		this.setName(this.config.getString("Title", matName));
		
		// Fuel support
		this.burnTime = this.config.getInt("BurnTime", 0);
		
		// Set the items durability
		this.setMaxDurability((short) this.config.getInt("Durability", 0));
		
		// Set the armor value
		//this.setMaxArmor((short) this.config.getInt("Armor", 1));
		
		// Armor is never stackable!
		this.setStackable(false);
		
		// Register handlers.
		if (this.config.contains("Handlers")) {
			this.registerHandlers();
		}
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

	public String getSmpName() {
		return this.smpName;
	}

	public String getMaterialName() {
		return this.materialName;
	}
	
	public Integer getBurnTime() {
		return this.burnTime;
	}

	public String getDisplayName() {
		return this.getDisplayName();
	}
	
	public YamlConfiguration getConfig() {
		return this.config;
	}

}
