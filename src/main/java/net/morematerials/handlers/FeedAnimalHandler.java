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
import java.util.Random;

import net.minecraft.server.v1_6_R3.EntityLiving;
import net.minecraft.server.v1_6_R3.PathEntity;
import net.minecraft.server.v1_6_R3.EntityWolf;
import net.morematerials.MoreMaterials;
import net.morematerials.handlers.GenericHandler;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class FeedAnimalHandler extends GenericHandler {

    @SuppressWarnings("unused")
    private MoreMaterials plugin;
    private String animalType;
    private Random random;

    public void init(MoreMaterials plugin) {
        this.plugin = plugin;
    }

    public void shutdown() {
    }

    @Override
    public void onActivation(Event event, Map<String, Object> config) {

        //Setup Player Environment		
        PlayerInteractEvent playerEvent = (PlayerInteractEvent) event;   


        // Setup Player Environment if we got here.       

        final Player sPlayer = playerEvent.getPlayer();   

        // Check Player Permissions
        if (!sPlayer.hasPermission("morematerials.handlers.feedanimal")) {
            return;
        }

        if (config.containsKey("animalType")) {
            animalType= (String) config.get("animalType");	
        } 
        
        

        if (event instanceof PlayerInteractEntityEvent) {
            PlayerInteractEntityEvent playerEntity = (PlayerInteractEntityEvent) event;


            LivingEntity myTarget = (LivingEntity) playerEntity.getRightClicked();

            if (myTarget == null) {
                return;
            }	

            if (myTarget.getType() == EntityType.WOLF && animalType.equalsIgnoreCase("wolf")) {		    
                EntityWolf wolf = (EntityWolf) myTarget;
                if (!(wolf.isTamed())) {
                    if (this.random.nextInt(3) == 0) {
                        wolf.setTamed(true);
                        wolf.setPathEntity((PathEntity) null);
                        wolf.setGoalTarget((EntityLiving) null);
                        wolf.setSitting(true);
                        wolf.setHealth(wolf.getMaxHealth()); // CraftBukkit - 20.0 -> getMaxHealth()
                        wolf.setOwnerName(sPlayer.getName());                
                        wolf.world.broadcastEntityEffect(wolf, (byte) 7);
                    } else {                
                        wolf.world.broadcastEntityEffect(wolf, (byte) 6);
                    }		    
                } else {
                    wolf.setHealth(wolf.getMaxHealth());
                    wolf.world.broadcastEntityEffect(wolf, (byte) 7);
                }
            }
        }
        /*
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
         */ 
    }
}