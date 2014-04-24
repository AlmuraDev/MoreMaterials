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
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.getspout.spoutapi.block.SpoutBlock;

public class BombHandler extends GenericHandler {

	private MoreMaterials plugin;
	private int bombSize = 4;
	private int bombDelay = 2;
	private boolean setFire = false;
	private boolean showMessage = false;
	private String message = " ";

	public void init(MoreMaterials plugin) {
		this.plugin = plugin;
	}

	public void shutdown() {
	}

	@Override
	public void onActivation(Event event, Map<String, Object> config) {
		
		if (!(event instanceof PlayerInteractEvent)) {  //Always do this.
			return;
		}
		// Setup Player Environment
		PlayerInteractEvent playerEvent = (PlayerInteractEvent) event;    	

		// Setup Player Environment if we got here.       
		Player sPlayer = playerEvent.getPlayer();   

		// Check Player Permissions
		if (!sPlayer.hasPermission("morematerials.handlers.bomb")) {
			return;
		}

		if (config.containsKey("bombsize")) {
			bombSize = (Integer) config.get("bombsize");	
		} 

		if (config.containsKey("delay")) {
			bombDelay = (Integer) config.get("delay");	
		}

		if (config.containsKey("showmessage")) {
			showMessage = (Boolean) config.get("showmessage");	
		}

		if (config.containsKey("message")) {
			message = (String) config.get("message");	
		}
		
		if (config.containsKey("setfire")) {
			setFire = (Boolean) config.get("setfire");	
		}

		// Prevent Dumb User Mistakes
		if (bombSize < 1 || bombSize >51) {
			return;
		}
		
		if (bombDelay < 1 || bombSize > 60) {
			return;
		}

		final SpoutBlock block = (SpoutBlock)playerEvent.getClickedBlock();

		if (block == null) {
			return;
		}

		final Location location = playerEvent.getClickedBlock().getLocation();

		if (showMessage) {
			sPlayer.sendMessage(message);
		}
		
		if (setFire) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {				
					block.getWorld().createExplosion(location, (float)bombSize, true);		
				}
			}, ((long)bombDelay * 20));
		} else {
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {				
					block.getWorld().createExplosion(location, (float)bombSize);		
				}
			}, ((long)bombDelay * 20));
		}
	}
}