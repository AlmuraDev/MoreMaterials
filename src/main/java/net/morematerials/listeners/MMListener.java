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

import net.morematerials.MoreMaterials;
import net.morematerials.handlers.GenericHandler;
import net.morematerials.materials.MMCustomBlock;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.getspout.spout.block.SpoutCraftBlock;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.Block;
import org.getspout.spoutapi.material.block.GenericCustomBlock;
import org.getspout.spoutapi.material.item.GenericCustomTool;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MMListener implements Listener {
	
	private MoreMaterials plugin;

	public MMListener(MoreMaterials plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		
		GenericHandler handler = this.plugin.getHandlerManager().getHandler("PlaySound");
		if (handler != null) {
			try {
				handler.onActivation(event.getBlock().getLocation(), (SpoutPlayer) event.getPlayer());
			} catch (Exception exception) {
				System.out.println(exception.getMessage());
			}
		}
		
		// Make sure we have a valid event.
		if (event.getPlayer() == null || event.getPlayer().getGameMode() == GameMode.CREATIVE) {
			return;
		}
		
		SpoutPlayer player = (SpoutPlayer) event.getPlayer();

		// Check for durability and ItemDropRequired.
		if (player.getItemInHand() != null) {
			SpoutItemStack stack = new SpoutItemStack(player.getItemInHand());
			
			// Check for ItemDropRequired
			Block block = ((SpoutCraftBlock) event.getBlock()).getBlockType();
			if (block instanceof GenericCustomBlock) {
				GenericCustomBlock customBlock = (GenericCustomBlock) block;
				Object material = this.plugin.getSmpManager().getMaterial(customBlock.getCustomId());
				
				// Make sure this is an MoreMaterials block.
				if (material != null && material instanceof MMCustomBlock) {
					if (((MMCustomBlock) material).getItemDropRequired()) {
						// Forbid tools without modifier
						Boolean prevent = !(stack.getMaterial() instanceof GenericCustomTool);
						if (!prevent) {
							prevent = ((GenericCustomTool) stack.getMaterial()).getStrengthModifier(customBlock) <= 1.0;
						}
						
						if (prevent) {
							event.setCancelled(true);
							event.getBlock().setType(Material.AIR);
						}
					}
				}
			}
			
			if (stack.isCustomItem() && stack.getMaterial() instanceof GenericCustomTool) {
				GenericCustomTool tool = (GenericCustomTool) stack.getMaterial();
				
				// Do durability stuff durability.
				if (tool.getMaxDurability() == 0) {
					return;
				} else if (GenericCustomTool.getDurability(stack) + 1 < tool.getMaxDurability()) {
					GenericCustomTool.setDurability(stack, (short) (GenericCustomTool.getDurability(stack) + 1));
					player.setItemInHand(stack);
				} else {
					player.setItemInHand(new ItemStack(Material.AIR));
					//FIXME must be added to spoutPlugin
					//SpoutManager.getSoundManager().playSoundEffect(player, SoundEffect.BREAK);
				}
			}
		}
	}

}
