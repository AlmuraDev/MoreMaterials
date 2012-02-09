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

package net.spoutmaterials.spoutmaterials.materials;

import net.spoutmaterials.spoutmaterials.SmpPackage;
import org.bukkit.configuration.ConfigurationSection;
import org.getspout.spoutapi.material.item.GenericCustomItem;

public class SMCustomItem extends GenericCustomItem {
	
	private Integer damage = null;
	private Integer maxDurability = null;
	private MaterialAction actionL = null;
	private MaterialAction actionR = null;
	private boolean stackable = true;
	private SmpPackage smpPackage = null;
	private boolean keepEnchanting = false;

	public SMCustomItem(SmpPackage smpPackage, String name, String texture) {
		super(smpPackage.getSmpManager().getPlugin(), name, texture);
		this.smpPackage = smpPackage;
	}
	
	public void setConfig(ConfigurationSection config) {
		Integer ldamage = config.getInt("Damage");
		Boolean keepEnchanting = config.getBoolean("KeepEnchanting", false);
		// Unimplemented
		Integer durability = config.getInt("Durability");
		// Unimplemented
		Boolean lstackable = config.getBoolean("Stackable", true);
		
		if (ldamage != null && ldamage > 0) {
			this.damage = ldamage;
		}
		
		if (durability != null && durability > 0) {
			this.maxDurability = durability;
		}
		
		if (config.isConfigurationSection("Lclick")) {
			this.actionL = new MaterialAction(config.getConfigurationSection("Lclick"), this.smpPackage);
		}
		
		if (config.isConfigurationSection("Rclick")) {
			this.actionR = new MaterialAction(config.getConfigurationSection("Rclick"), this.smpPackage);
		}
		
		this.stackable = lstackable;
		this.keepEnchanting = keepEnchanting;
	}
	
	public MaterialAction getActionL() {
		return this.actionL;
	}
	
	public MaterialAction getActionR() {
		return this.actionR;
	}
	
	public Integer getMaxDurability() {
		return this.maxDurability;
	}
	
	public Integer getDamage() {
		return this.damage;
	}

	public boolean getStackable() {
		return this.stackable;
	}

	public boolean getKeepEnchanting() {
		return this.keepEnchanting;
	}
}
