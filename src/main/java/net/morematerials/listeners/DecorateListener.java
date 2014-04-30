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
package net.morematerials.listeners;

import java.util.Random;

import net.morematerials.MoreMaterials;
import net.morematerials.wgen.Decorator;
import net.morematerials.wgen.ore.CustomOreDecorator;
import net.morematerials.wgen.task.DecoratorThrottler;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;

public class DecorateListener implements Listener {
	private final MoreMaterials plugin;
	private static final Random RANDOM = new Random();

	public DecorateListener(MoreMaterials plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onChunkPopulate(ChunkPopulateEvent event) {		
		if (plugin.getConfig().getBoolean("DecorateNewChunks") && plugin.getDecorateWorldList().contains(event.getWorld().getName())) {
			DecoratorThrottler throttler = plugin.getDecorationThrotters().get(event.getWorld());
			if (throttler == null) {
				throttler = plugin.getDecorationThrotters().start(5, event.getWorld());
			}
			// Slow down Trottler thread.
			throttler.setSpeed(10);
			
			for (Decorator myOre : plugin.getDecoratorRegistry().getAll()) {
				if (throttler.isQueued(myOre, event.getChunk().getX(), event.getChunk().getZ())) {
					continue;
				}
				if (myOre instanceof CustomOreDecorator) {
					// Tracking
					((CustomOreDecorator) myOre).toDecorateCount = 0;
				
					// Set replacement ore type.
					((CustomOreDecorator) myOre).replace(Material.STONE, Material.DIRT, Material.GRAVEL);
					//Normally the formula for inclusive low/high is nextInt(high - (low - 1) + 1 but seeing as low is always 1 and 1-1 is 0, we can omit that.
					int rand1 = RANDOM.nextInt(((CustomOreDecorator)myOre).getDecorateChance()) + 1;
					int rand2 = ((CustomOreDecorator)myOre).getDecorateChance();					
					if (rand1 == rand2) {
						if (throttler.offer(myOre, event.getChunk().getX(), event.getChunk().getZ())) {
							// Count total chunks to populate.
							((CustomOreDecorator) myOre).toDecorateCount++;
							if (plugin.showDebug) {
								//plugin.getLogger().info("Queue Generation of Chunk at: X: " + event.getChunk().getX() + " Z: " + event.getChunk().getZ() + " with ore: " + myOre.getIdentifier());
							}
						}
					} else {
						if (plugin.showDebug) {
							// Spam
							//plugin.getLogger().info("Offer to Queue: " + myOre.getIdentifier() + " failed chance calculation for new chunk populate. Chance: " + rand1 + "/" + rand2);
						}
					}
				}
			}
		} 
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event) {	 // Don't try and decorate new chunks here.	
		if (plugin.getConfig().getBoolean("DecorateExistingChunks") && !event.isNewChunk() && plugin.getDecorateWorldList().contains(event.getWorld().getName())) {
		// TODO:
		}
	}
}