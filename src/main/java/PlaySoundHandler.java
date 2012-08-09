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

import java.util.Map;

import net.morematerials.MoreMaterials;
import net.morematerials.handlers.GenericHandler;
import net.morematerials.materials.CustomMaterial;

import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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
		Material material = this.plugin.getSmpManager().getMaterial((Integer) config.get("__materialID__"));
		
		// We can safely cast, because this event is only triggered for MoreMaterials materials!
		String smpName = ((CustomMaterial) material).getSmpName();

		// Default location is the world spawn.
		Location location = this.plugin.getServer().getWorlds().get(0).getSpawnLocation();
		if (event instanceof PlayerInteractEvent) {
			location = ((PlayerInteractEvent) event).getPlayer().getLocation();
		} else if (event instanceof PlayerMoveEvent) {
			location = ((PlayerMoveEvent) event).getPlayer().getLocation();
		}

		// Now play the sound!
		String url = this.plugin.getWebManager().getAssetsUrl(smpName + "_" + (String) config.get("Sound"));
		SpoutManager.getSoundManager().playGlobalCustomSoundEffect(this.plugin, url, false, location, 32, 100);
	}
	
}
