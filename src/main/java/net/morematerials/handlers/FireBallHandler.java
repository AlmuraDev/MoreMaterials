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
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class FireBallHandler extends GenericHandler {
	
	@SuppressWarnings("unused")
	private MoreMaterials plugin;
	private int yieldSize = 1;
	private boolean causeFire = false;

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
        Player sPlayer = playerEvent.getPlayer();   
		
        // Check Player Permissions
        if (!sPlayer.hasPermission("morematerials.handlers.fireball")) {
        	return;
        }
        
        if (config.containsKey("yieldsize")) {
			yieldSize = (Integer) config.get("yieldsize");	
		} 
        
        if (config.containsKey("causefire")) {
        	causeFire = (Boolean) config.get("causefire");	
		} 
        
        if (yieldSize < 1 || yieldSize > 100) {
        	return;
        }
        
        Vector direction = sPlayer.getEyeLocation().getDirection().multiply(2);
		Fireball fireball = (Fireball)sPlayer.getWorld().spawn(sPlayer.getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()), Fireball.class);
		fireball.setShooter(sPlayer);
		fireball.setVelocity(direction.multiply(0.25D));
		fireball.setYield((float)yieldSize);
		fireball.setIsIncendiary(causeFire);
	}
	
}