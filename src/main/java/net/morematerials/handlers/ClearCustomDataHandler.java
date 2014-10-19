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

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.player.SpoutPlayer;

public class ClearCustomDataHandler extends GenericHandler {
	
	public void init(MoreMaterials plugin) {}

	public void shutdown() {}

	@SuppressWarnings("deprecation")
	@Override
	public void onActivation(Event event, Map<String, Object> config) {
		
		// Setup Player Environment
		PlayerInteractEvent playerEvent = (PlayerInteractEvent) event;    	

		// Setup Player Environment if we got here.
		final SpoutPlayer sPlayer = (SpoutPlayer) playerEvent.getPlayer();   

		// Check Player Permissions
		if (!sPlayer.hasPermission("morematerials.handlers.cleardata")) {
			return;
		}        		

		SpoutBlock sBlock = (SpoutBlock) playerEvent.getClickedBlock();
		Location loc = sPlayer.getLocation();
		int changes = 0;

		for (int xx = -100; xx < 100; xx++) {
		    for (int yy = 0; yy < 256; yy++) {
		        for (int zz = -100; zz < 100; zz++) {       
		            SpoutBlock block = (SpoutBlock) loc.getWorld().getBlockAt(xx + loc.getBlockX(), yy, zz + loc.getBlockZ());
		            if (block.getType() == Material.AIR) {
		                if (block.isCustomBlock()) {
		                    block.setCustomBlock(null);
		                    changes++;
		                }
		            }
		        }
		    }
		}
		sPlayer.sendMessage("[MoreMaterials] - Block Data Cleared: " + changes);
	}
}
