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

package net.morematerials.listeners;

import java.util.ArrayList;

import net.morematerials.MoreMaterials;
import net.morematerials.materials.MMCustomBlock;
import net.morematerials.materials.MMCustomTool;
import net.morematerials.materials.MMLegacyMaterial;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.item.GenericCustomTool;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.sound.SoundEffect;

public class MMListener implements Listener {

	private MoreMaterials plugin;

	public MMListener(MoreMaterials plugin) {
		this.plugin = plugin;
		// TODO this listener should trigger all handlers
		// TODO implement stackable
	}
	
	@EventHandler
	public void onBlockDamage(BlockDamageEvent event) {
		// TODO implement tools break-speed multiplicator
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		// Make sure we have a valid event.
		if (event.getPlayer() == null) {
			return;
		}
		
		SpoutPlayer player = (SpoutPlayer) event.getPlayer();

		// Check for durability.
		if (player.getItemInHand() != null) {
			SpoutItemStack stack = new SpoutItemStack(player.getItemInHand());
			
			if (stack.isCustomItem() && stack.getMaterial() instanceof GenericCustomTool) {
				GenericCustomTool tool = (GenericCustomTool) stack.getMaterial();
				
				// Only for materials with durability.
				if (tool.getMaxDurability() == 0) {
					return;
				} else if (GenericCustomTool.getDurability(stack) + 1 < tool.getMaxDurability()) {
					GenericCustomTool.setDurability(stack, (short) (GenericCustomTool.getDurability(stack) + 1));
					player.setItemInHand(stack);
				} else {
					player.setItemInHand(new ItemStack(Material.AIR));
					// TODO implement correct break sound
					SpoutManager.getSoundManager().playSoundEffect(player, SoundEffect.CLICK);
				}
			}
		}
		
		// Check if this block drops
		if (event.getBlock() != null) {
			if (((SpoutBlock) event.getBlock()).getCustomBlock() != null) {
				Integer blockId = ((SpoutBlock) event.getBlock()).getCustomBlock().getCustomId();
				org.getspout.spoutapi.material.Material material = this.plugin.getSmpManager().getMaterial(blockId);
				
				// This only applies for custom blocks by MoreMaterials!
				if (material instanceof MMCustomBlock) {
					MMCustomBlock mmBlock = (MMCustomBlock) material;
					if (!mmBlock.getRequiredTools().isEmpty()) {
						ArrayList<String> requiredTools = mmBlock.getRequiredTools();
						Boolean canBreak = false;
						String toolName;
						
						// Now check all tool groups
						for (Integer i = 0; i < requiredTools.size(); i++) {
							toolName = requiredTools.get(i);
							
							if (toolName.matches("^[0-9]+$")) {
								// Numeric values are vanilla items.
								if (player.getItemInHand().getTypeId() == Integer.parseInt(toolName)) {
									canBreak = true;
								}
							} else if (player.getItemInHand() != null) {
								// String values are item names in the same .smp package.
								SpoutItemStack stack = new SpoutItemStack(player.getItemInHand());
								if (stack.isCustomItem() && stack.getMaterial() instanceof GenericCustomTool) {
									org.getspout.spoutapi.material.Material item = this.plugin.getSmpManager().getMaterial(((GenericCustomTool) stack.getMaterial()).getCustomId());
									if (item != null && ((MMCustomTool) item).getSmpName().equals(mmBlock.getSmpName()) && ((MMCustomTool) item).getMaterialName().equals(mmBlock.getMaterialName())) {
										canBreak = true;
									}
								}
							}
							
							// We dont need to check any further
							if (canBreak) {
								break;
							}
						}
						
						// We can be sure, this block cant be dropped with current tool.
						if (!canBreak) {
							event.setCancelled(true);
							event.getBlock().setType(Material.AIR);
						}
					}
				}
			} else if (player.getItemInHand() != null) {
				// Kind of complex check to make sure legacy drops can be dropped with custom tools.
				MMLegacyMaterial material = this.plugin.getSmpManager().getLegacyMaterial(event.getBlock().getTypeId());
				if (material != null) {
					SpoutItemStack stack = new SpoutItemStack(player.getItemInHand());
					// Legacy drops by legacy items are already builtin.
					if (stack.isCustomItem() && stack.getMaterial() instanceof GenericCustomTool) {
						org.getspout.spoutapi.material.Material customTool = this.plugin.getSmpManager().getMaterial(((GenericCustomTool) stack.getMaterial()).getCustomId());
						// Only for MoreMaterials tools.
						if (customTool != null) {
							MMCustomTool mmCustomTool = (MMCustomTool) customTool;
							ArrayList<String> requiredTools = material.getRequiredTools();
							String toolName;
							for (Integer i = 0; i < requiredTools.size(); i++) {
								toolName = requiredTools.get(i);
								// Make sure its the right tool
								if (toolName.equals(mmCustomTool.getSmpName() + "_" + mmCustomTool.getMaterialName())) {
									ItemStack[] drops = (ItemStack[]) event.getBlock().getDrops().toArray();
									for (Integer j = 0; j < drops.length; j++) {
										// Drop correct stuff.
										event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), drops[i]);
									}
									break;
								}
							}
						}
					}
				}
			}
		}
	}

}
