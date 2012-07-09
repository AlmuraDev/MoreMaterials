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

import net.morematerials.morematerials.handlers.GenericHandler;
import net.morematerials.morematerials.handlers.TheBasicHandler;
import net.morematerials.morematerials.manager.MainManager;
import net.morematerials.morematerials.smp.SmpPackage;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.material.item.GenericCustomTool;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SMCustomItem extends GenericCustomTool {
	private Integer damage = null;
	private MaterialAction actionL = null;
	private MaterialAction actionR = null;
	private SmpPackage smpPackage = null;
	private boolean keepEnchanting = false;
	private GenericHandler handlerL;
	private GenericHandler handlerR;

	@Override
	public boolean onItemInteract(SpoutPlayer player, SpoutBlock block, BlockFace face) {
		PlayerInteractEvent event = new PlayerInteractEvent(player,Action.RIGHT_CLICK_BLOCK,player.getItemInHand(),block,face);
		Bukkit.getPluginManager().callEvent(event);
		return event.isCancelled();
	}

	public SMCustomItem(SmpPackage smpPackage, String name, String texture) {
		super(smpPackage.getSmpManager().getPlugin(), name, texture);
		this.smpPackage = smpPackage;
	}
	
	public void setConfig(ConfigurationSection config) {
		Integer ldamage = config.getInt("Damage");
		Boolean lkeepEnchanting = config.getBoolean("KeepEnchanting", false);
		String rhandler = config.getString("Rclick.Handler", null);
		String lhandler = config.getString("Lclick.Handler", null);
		//TODO implement these two
		Short ldurability = (short) config.getInt("Durability", 0);
		Boolean lstackable = config.getBoolean("Durability", true);
		
		if (ldamage != null && ldamage > 0) {
			this.damage = ldamage;
		}
		
		if (config.isConfigurationSection("Lclick")) {
			this.actionL = new MaterialAction(config.getConfigurationSection("Lclick"), this.smpPackage);
		}
		
		if (config.isConfigurationSection("Rclick")) {
			this.actionR = new MaterialAction(config.getConfigurationSection("Rclick"), this.smpPackage);
		}
		
		if (lhandler != null) {
			Class<?> clazz = MainManager.getHandlerManager().getHandler(lhandler);
			if (clazz == null) {
				MainManager.getUtils().log("Invalid handler name: " + lhandler + "!");
			} else {
				try {
					this.handlerR = (GenericHandler) clazz.newInstance();
				} catch (Exception exceptions) {
				} 
			}
			this.handlerR.createAndInit(GenericHandler.MaterialType.ITEM, smpPackage.getSmpManager().getPlugin());
		}    
		if (rhandler != null) {
			Class<?> clazz = MainManager.getHandlerManager().getHandler(rhandler);
			if (clazz == null) {
				MainManager.getUtils().log("Invalid handler name: " + rhandler + "!");
			} else {
				try {
					this.handlerL = (GenericHandler) clazz.newInstance();
				} catch (Exception exceptions) {
				} 
			}
			this.handlerL.createAndInit(GenericHandler.MaterialType.ITEM, smpPackage.getSmpManager().getPlugin());
		}
		
		if (this.handlerR == null) {
			this.handlerR = new TheBasicHandler();
		}
		
		if (this.handlerL == null) {
			this.handlerL = new TheBasicHandler();
		}
		
		this.keepEnchanting = lkeepEnchanting;
	}
	
	public MaterialAction getActionL() {
		return this.actionL;
	}
	
	public MaterialAction getActionR() {
		return this.actionR;
	}
	
	public Integer getDamage() {
		return this.damage;
	}
	
	public boolean getKeepEnchanting() {
		return this.keepEnchanting;
	}
	
	public GenericHandler getHandlerR() {
		return this.handlerR;
	}
	
	public GenericHandler getHandlerL() {
		return this.handlerL;
	}
}
