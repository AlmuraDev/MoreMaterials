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

public class PopulateListener implements Listener {
	private final MoreMaterials plugin;
	private static final Random RANDOM = new Random();

	public PopulateListener(MoreMaterials plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event) {
		if (plugin.getConfig().getBoolean("PopulateNewChunks") && event.isNewChunk() && plugin.getPopulateWorldList().contains(event.getWorld().getName())) {
			DecoratorThrottler throttler = plugin.getDecorationThrotters().get(event.getWorld());
			if (throttler == null) {
				throttler = plugin.getDecorationThrotters().start(5, event.getWorld());
			}
			for (Decorator myOre : plugin.getDecoratorRegistry().getAll()) {
				if (throttler.isQueued(myOre, event.getChunk().getX(), event.getChunk().getZ())) {
					continue;
				}
				if (myOre instanceof CustomOreDecorator) {
					// Tracking
					((CustomOreDecorator) myOre).toPopulateCount = 0;

					//((CustomOreDecorator)myOre).replace(Material.STONE, Material.AIR);
					// Set replacement ore type.
					((CustomOreDecorator) myOre).replace(Material.STONE);								
					int rand1 = RANDOM.nextInt(((CustomOreDecorator)myOre).getDecorateChance() - 0) + 1;
					int rand2 = ((CustomOreDecorator)myOre).getDecorateChance();					
					if (rand1 == rand2) {
						if (throttler.offer(myOre, event.getChunk().getX(), event.getChunk().getZ())) {
							// Count total chunks to populate.
							((CustomOreDecorator) myOre).toPopulateCount++;
							if (plugin.showDebug) {
								System.out.println("[MoreMaterials] -  Queue Generation of Chunk at: X: " + event.getChunk().getX() + " Z: " + event.getChunk().getZ() + " with ore: " + myOre.getIdentifier());
							}
						}
					} else {
						if (plugin.showDebug) {
							System.out.println("[MoreMaterials] -  Offer to Queue: " + ((CustomOreDecorator)myOre).getIdentifier() + " failed chance caluclation for new chunk populate. Chance: " + rand1 + "/" + rand2);
						}
					}
				}
			}
		}
	}
}