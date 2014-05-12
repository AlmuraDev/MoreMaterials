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
package net.morematerials.listeners;

import net.morematerials.MoreMaterials;
import net.morematerials.materials.MMCustomBlock;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.getspout.spout.block.SpoutCraftBlock;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.Block;
import org.getspout.spoutapi.material.block.GenericCustomBlock;
import org.getspout.spoutapi.material.item.GenericCustomItem;
import org.getspout.spoutapi.player.SpoutPlayer;

public class MMListener implements Listener {
	
	private MoreMaterials plugin;
	private String fullName;
	
	public MMListener(MoreMaterials plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onBlockBreak(BlockBreakEvent event) {
		// Make sure we have a valid event.
		if (event.getPlayer() == null || event.getPlayer().getGameMode() == GameMode.CREATIVE) {
			return;
		}
		
		// Events for broken custom blocks.
		Block block = ((SpoutCraftBlock) event.getBlock()).getBlockType();
		if (block instanceof GenericCustomBlock) {
			this.plugin.getHandlerManager().triggerHandlers("BlockBreak", ((GenericCustomBlock) block).getCustomId(), event);
		}

		// Events for the item held while breaking a block.
		SpoutItemStack stack = new SpoutItemStack(event.getPlayer().getItemInHand());
		if (stack.getMaterial() instanceof GenericCustomItem) {
			this.plugin.getHandlerManager().triggerHandlers("HoldBlockBreak", ((GenericCustomItem) stack.getMaterial()).getCustomId(), event);
		}
	}
		
	@EventHandler
	public void InventoryCraft(CraftItemEvent event) {
		if (event.getInventory().getResult() == null) {
			return;
		}

		Player player = (Player) event.getWhoClicked();
		
		if (player.hasPermission("morematerials.craft")) {
			return; // has permission to craf this.
		}

		// Getting the object we want to craft.
		SpoutItemStack spoutItemStack = new SpoutItemStack(event.getInventory().getResult());
		if (spoutItemStack != null) {
			if (spoutItemStack.isCustomBlock() || spoutItemStack.isCustomItem()) {
				fullName = ((GenericCustomItem) spoutItemStack.getMaterial()).getFullName();
				if (fullName == null) {
					fullName = ((GenericCustomBlock) spoutItemStack.getMaterial()).getFullName();
				}

				String pluginName = fullName.split("\\.")[0];
				if (pluginName.equalsIgnoreCase("MoreMaterials")) {
					String smpName = fullName.split("\\.")[1];
					String smpItem = fullName.split("\\.")[2];
					String permissionName = smpName + "." + smpItem;

					if (!player.hasPermission("morematerials.craft." + permissionName)) {
						player.sendMessage("You do not have permission to craft: " + permissionName);
						event.setCancelled(true);
						return;
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		// The click events for hold item.
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			SpoutItemStack stack = new SpoutItemStack(event.getPlayer().getItemInHand());
			if (stack.getMaterial() instanceof GenericCustomItem) {
				this.plugin.getHandlerManager().triggerHandlers("HoldLeftClick", ((GenericCustomItem) stack.getMaterial()).getCustomId(), event);
			} else if (stack.getMaterial() instanceof GenericCustomBlock) {
				this.plugin.getHandlerManager().triggerHandlers("HoldLeftClick", ((GenericCustomBlock) stack.getMaterial()).getCustomId(), event);
			}
		} else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			SpoutItemStack stack = new SpoutItemStack(event.getPlayer().getItemInHand());
			if (stack.getMaterial() instanceof GenericCustomItem) {
				this.plugin.getHandlerManager().triggerHandlers("HoldRightClick", ((GenericCustomItem) stack.getMaterial()).getCustomId(), event);
			} else if (stack.getMaterial() instanceof GenericCustomBlock) {
				this.plugin.getHandlerManager().triggerHandlers("HoldRightClick", ((GenericCustomBlock) stack.getMaterial()).getCustomId(), event);
			}
		}
		
		// The click event for the block you clicked on
		if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = ((SpoutCraftBlock) event.getClickedBlock()).getBlockType();
			if (block instanceof GenericCustomBlock) {
				if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
					this.plugin.getHandlerManager().triggerHandlers("LeftClick", ((GenericCustomBlock) block).getCustomId(), event);
				} else {
					this.plugin.getHandlerManager().triggerHandlers("RightClick", ((GenericCustomBlock) block).getCustomId(), event);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {	
		SpoutItemStack stack = new SpoutItemStack(event.getPlayer().getItemInHand());
		if (stack.getMaterial() instanceof GenericCustomItem) {
			this.plugin.getHandlerManager().triggerHandlers("RightClickEntity", ((GenericCustomItem) stack.getMaterial()).getCustomId(), event);
		} else if (stack.getMaterial() instanceof GenericCustomBlock) {
			this.plugin.getHandlerManager().triggerHandlers("RightClickEntity", ((GenericCustomBlock) stack.getMaterial()).getCustomId(), event);
		}		
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Location location = event.getPlayer().getLocation();
		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		
		Object block = null;		 
		 try {
			 block = ((SpoutCraftBlock) location.getWorld().getBlockAt(location)).getBlockType();	
			} catch (Exception exception) {
				// Catch Chunk Regen Exception and ignore it
				return;
			} 
		
		// Touch represents a block you are standing in.
		if (block instanceof GenericCustomBlock) {
			this.plugin.getHandlerManager().triggerHandlers("Touch", ((GenericCustomBlock) block).getCustomId(), event);
		}

		block = ((SpoutCraftBlock) location.getWorld().getBlockAt(location.subtract(0, 1, 0))).getBlockType();
		
		// Getting the Spout Block below the player
		SpoutBlock sBlock = (SpoutBlock) sPlayer.getWorld().getBlockAt(sPlayer.getLocation().add(0, -1, 0));
				
		// This only applies for custom blocks
		Object item = null;
		if (sBlock.isCustomBlock()) {
			String pluginName = sBlock.getCustomBlock().getBlockItem().getFullName().split("\\.")[0];
			if (pluginName.equalsIgnoreCase("MoreMaterials")) {
				String smpName = sBlock.getCustomBlock().getBlockItem().getFullName().split("\\.")[1];
				String smpItem = sBlock.getCustomBlock().getBlockItem().getFullName().split("\\.")[2];
				item = this.plugin.getSmpManager().getMaterial(smpName, smpItem);
			}
		}
		
		// Setting the player walkspeed.
		if (item != null && item instanceof MMCustomBlock && ((MMCustomBlock) item).getSpeedMultiplier() != 1) {
			sPlayer.setAirSpeedMultiplier(((MMCustomBlock) item).getSpeedMultiplier());
			sPlayer.setWalkingMultiplier(((MMCustomBlock) item).getSpeedMultiplier());
		} else {
			sPlayer.setAirSpeedMultiplier(1);
			sPlayer.setWalkingMultiplier(1);
		}
		
		// Setting the player jumpheight.
		if (item != null && item instanceof MMCustomBlock && ((MMCustomBlock) item).getJumpMultiplier() != 1) {
			sPlayer.setJumpingMultiplier(((MMCustomBlock) item).getJumpMultiplier());
		} else {
			sPlayer.setJumpingMultiplier(1);
		}
		
		// Walkover represents the block under your position.
		if (block instanceof GenericCustomBlock) {
			this.plugin.getHandlerManager().triggerHandlers("Walkover", ((GenericCustomBlock) block).getCustomId(), event);
		
		}
	}
}
