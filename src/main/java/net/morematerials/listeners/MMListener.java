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

package net.morematerials.listeners;

import net.morematerials.MoreMaterials;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.getspout.spout.block.SpoutCraftBlock;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.Block;
import org.getspout.spoutapi.material.block.GenericCustomBlock;
import org.getspout.spoutapi.material.item.GenericCustomItem;

public class MMListener implements Listener {
	
	private MoreMaterials plugin;

	public MMListener(MoreMaterials plugin) {
		this.plugin = plugin;
	}

	@EventHandler
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
	public void onPlayerMove(PlayerMoveEvent event) {
		Location location = event.getPlayer().getLocation();
		Block block = ((SpoutCraftBlock) location.getWorld().getBlockAt(location)).getBlockType();
		
		// Touch represents a block you are standing in.
		if (block instanceof GenericCustomBlock) {
			this.plugin.getHandlerManager().triggerHandlers("Touch", ((GenericCustomBlock) block).getCustomId(), event);
		}

		block = ((SpoutCraftBlock) location.getWorld().getBlockAt(location.subtract(0, 1, 0))).getBlockType();
		
		// Walkover represents the block under your position.
		if (block instanceof GenericCustomBlock) {
			this.plugin.getHandlerManager().triggerHandlers("Walkover", ((GenericCustomBlock) block).getCustomId(), event);
		}
	}
}
