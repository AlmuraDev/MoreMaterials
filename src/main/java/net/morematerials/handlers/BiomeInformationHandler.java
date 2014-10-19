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
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.player.SpoutPlayer;

public class BiomeInformationHandler extends GenericHandler {
	
	public void init(MoreMaterials plugin) {}

	public void shutdown() {}

	@SuppressWarnings("deprecation")
	@Override
	public void onActivation(Event event, Map<String, Object> config) {
		
		// Setup Player Environment
		PlayerInteractEvent playerEvent = (PlayerInteractEvent) event;    	

		// Setup Player Environment if we got here.
		final SpoutPlayer sPlayer = (SpoutPlayer) playerEvent.getPlayer();
		final Player player = playerEvent.getPlayer();

		// Check Player Permissions
		if (!sPlayer.hasPermission("morematerials.handlers.information")) {
			return;
		}        		
		Block block = playerEvent.getClickedBlock();
		SpoutBlock sBlock = (SpoutBlock) playerEvent.getClickedBlock();
		
		if (sBlock != null) {
			playerEvent.getPlayer().sendMessage("Biome Info: " + "\n" + ChatColor.DARK_GREEN + "Biome: " + ChatColor.DARK_AQUA + sBlock.getBiome() + "\n" + ChatColor.DARK_GREEN + "Tempeture: " + ChatColor.DARK_AQUA + sBlock.getTemperature() + "\n" + ChatColor.DARK_GREEN + "Rainfall: " + ChatColor.DARK_AQUA + sBlock.getHumidity()  + "\n" + ChatColor.DARK_GREEN + "Block Data Value: " + ChatColor.DARK_AQUA + sBlock.getData() + "\n" + ChatColor.DARK_GREEN + "Sky Light Level: " + ChatColor.DARK_AQUA + sBlock.getLightFromSky() + "\n" + ChatColor.DARK_GREEN + "SpoutBlock Location: " + ChatColor.DARK_AQUA + sBlock.getLocation()  + "\n" + ChatColor.DARK_GREEN + "Bukkit Block Location: " + ChatColor.DARK_AQUA + block.getLocation() + "\n" + ChatColor.DARK_GREEN + "SpoutPlayer Location: " + ChatColor.DARK_AQUA + sPlayer.getLocation()  + "\n" + ChatColor.DARK_GREEN + "Bukkit Player Location: " + ChatColor.DARK_AQUA + player.getLocation());
			
			
		}		
	}
}
