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

import java.lang.reflect.Method;
import java.util.Map;

import net.morematerials.MoreMaterials;
import net.morematerials.handlers.GenericHandler;
import net.morematerials.materials.CustomMaterial;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.material.Material;

public class PlaySoundHandler extends GenericHandler {
	
	private MoreMaterials plugin;

	public void init(MoreMaterials plugin) {
		this.plugin = plugin;
	}

	public void shutdown() {
	}

	@Override
	public void onActivation(Event event, Map<String, Object> config) {
		// Setup Player Environment
    	PlayerInteractEvent playerEvent = (PlayerInteractEvent) event;    	
    	
        // Setup Player Environment if we got here.       
        final Player sPlayer = playerEvent.getPlayer();   
		
        // Check Player Permissions
        if (!sPlayer.hasPermission("morematerials.handlers.playsound")) {
        	return;
        }
		Material material = this.plugin.getSmpManager().getMaterial((Integer) config.get("materialId"));
		
		// We can safely cast, because this event is only triggered for MoreMaterials materials!
		String smpName = ((CustomMaterial) material).getSmpName();

		// Default location is the world spawn.
		Location location = this.plugin.getServer().getWorlds().get(0).getSpawnLocation();
		try {
			Method method = event.getClass().getMethod("getPlayer");
			Object player = method.invoke(event);
			if (player instanceof Player) {
				location = ((Player) player).getLocation();
			}
		} catch (Exception exception) {
		}

		// Now play the sound!
		String[] sound = ((String) config.get("Sound")).split("/");
		if (sound.length == 1) {
			SpoutManager.getSoundManager().playGlobalCustomSoundEffect(this.plugin, smpName + "_" + sound[0], false, location, 32, 100);
		} else {
			SpoutManager.getSoundManager().playGlobalCustomSoundEffect(this.plugin, sound[0] + "_" + sound[1], false, location, 32, 100);
		}
	}
	
}
