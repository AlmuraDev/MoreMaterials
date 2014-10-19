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
package net.morematerials.handlers;

import java.util.Map;

import net.morematerials.MoreMaterials;
import net.morematerials.handlers.GenericHandler;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.item.GenericCustomTool;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.sound.SoundEffect;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;

public class RakeHandler extends GenericHandler {
    
    private MoreMaterials plugin;
	
	public void init(MoreMaterials plugin) {
	    this.plugin = plugin;
	}

	public void shutdown() {}

	@SuppressWarnings("deprecation")
    @Override
	public void onActivation(Event event, Map<String, Object> config) {
		
		// Setup Player Environment
		PlayerInteractEvent playerEvent = (PlayerInteractEvent) event;    	

		// Setup Player Environment if we got here.
		final SpoutPlayer player = (SpoutPlayer) playerEvent.getPlayer();   

		// Check Player Permissions
		if (!player.hasPermission("morematerials.handlers.rake")) {
			return;
		}        		

		Material material = playerEvent.getClickedBlock().getType();

		if (material == Material.DIRT || material == Material.GRASS) {
		 // Residence Flag Checker
		    org.bukkit.block.Block block = playerEvent.getClickedBlock();
		    if (!Bukkit.getPluginManager().isPluginEnabled("Residence")) {
	            ClaimedResidence res = Residence.getResidenceManager().getByLoc(block.getLocation());
	            if (res != null) {
	                if (!res.getPermissions().playerHas(player.getName(),"build", true)) {
	                    return;
	                }
	            }
	        }
		    
		    block.setType(Material.SOIL);		    
		    block.setData((byte)7);
		    
		    if (plugin.jobsEnabled) {
		        net.morematerials.manager.JobsWorker.jobsPlace(playerEvent.getPlayer(), block);
		    }
		    
		    SpoutItemStack stack = new SpoutItemStack(player.getItemInHand());
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
                    SpoutManager.getSoundManager().playSoundEffect(player, SoundEffect.BREAK);
                }
            }
		}
	}
}
