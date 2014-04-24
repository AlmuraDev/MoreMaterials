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

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.util.Vector;
import org.getspout.spoutapi.particle.Particle;
import org.getspout.spoutapi.particle.Particle.ParticleType;

public class HealHandler extends GenericHandler {

	@SuppressWarnings("unused")
	private MoreMaterials plugin;
	private int healAmount = 1;

	public void init(MoreMaterials plugin) {
		this.plugin = plugin;
	}

	public void shutdown() {
	}

	@Override
	public void onActivation(Event event, Map<String, Object> config) {
		
		//Setup Player Environment		
		PlayerInteractEntityEvent playerEntity = (PlayerInteractEntityEvent) event;
					
		// Setup Player Environment if we got here.       
		Player sPlayer = playerEntity.getPlayer();   

		// Check Player Permissions
		if (!sPlayer.hasPermission("morematerials.handlers.heal")) {
			return;
		}
		
		if (config.containsKey("healAmount")) {
			healAmount = (Integer) config.get("healAmount");	
		} 
		
		LivingEntity myTarget = (LivingEntity) playerEntity.getRightClicked();
		
		if (myTarget == null) {
			return;
		}	
		
		if (!(myTarget.getMaxHealth() == myTarget.getHealth())) {
			if ((myTarget.getHealth()+healAmount) > myTarget.getMaxHealth()) {
				myTarget.setHealth(myTarget.getMaxHealth());
			} else {
				myTarget.setHealth(myTarget.getHealth()+healAmount);
			}
			Location loc = myTarget.getLocation();
			Particle healParticle = new Particle(ParticleType.DRIPWATER, loc, new Vector(0.5D, 3.0D, 0.5D));
			healParticle.setParticleBlue(0.0F).setParticleGreen(0.0F).setParticleRed(1.0F);
			healParticle.setMaxAge(40).setAmount(15).setGravity(1.1F);
			healParticle.spawn();
		}
	}
}