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

import net.morematerials.MoreMaterials;
import net.morematerials.wgen.Decorator;
import net.morematerials.wgen.task.DecoratorThrottler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class GeneratorListener implements Listener {
	private final MoreMaterials plugin;

	public GeneratorListener(MoreMaterials plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event) {		
		if (plugin.getConfig().getBoolean("PopulateNewChunks", false) && event.isNewChunk()) {
			
			Decorator myOre = this.plugin.getDecoratorRegistry().get("ore_o_bluestone");
			DecoratorThrottler throttler = plugin.getDecorationThrotters().get(event.getWorld());
			if (throttler == null) {
				throttler = plugin.getDecorationThrotters().start(5, event.getWorld());
			}
			if (!throttler.hasAnyQueued(event.getChunk().getX(), event.getChunk().getZ())) {
				throttler.offer(myOre, event.getChunk().getX(), event.getChunk().getZ());
			}
		}
	}
}