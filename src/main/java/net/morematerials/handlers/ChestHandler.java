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
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;

public class ChestHandler extends GenericHandler {

	public void init(MoreMaterials plugin) {}

	public void shutdown() {}

	@Override
	public void onActivation(Event event, Map<String, Object> config) {
		// Setup Player Environment
		PlayerInteractEvent playerEvent = (PlayerInteractEvent) event;    	

		// Setup Player Environment if we got here.
		final SpoutPlayer sPlayer = (SpoutPlayer) playerEvent.getPlayer();   

		// Check Player Permissions
		if (!sPlayer.hasPermission("morematerials.handlers.chest")) {
			return;
		}

		Block block = playerEvent.getClickedBlock();

		// Residence Flag Checker
		if (!Bukkit.getPluginManager().isPluginEnabled("Residence")) {
			ClaimedResidence res = Residence.getResidenceManager().getByLoc(block.getLocation());
			if (res != null) {
				if (!res.getPermissions().playerHas(sPlayer.getName(),"container", true)) {
					return;
				}
			}
		}
		
		if (block != null) {
			try {
				// Its possible for a class cast exception to be thrown if part of the custom block data is missing, fixed this by ignoring the exception
				Chest chest = (Chest)block.getState();
				if (chest != null) {
					Inventory inv = chest.getBlockInventory();
					if (inv != null) {
						sPlayer.openInventory(inv);
					}
				}	
			} catch (Exception exception) {
				// Catch Class Cast Exception and ignore it
				return;
			}
		}
	}
}
