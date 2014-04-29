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
package net.morematerials.wgen.task;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import net.morematerials.MoreMaterials;
import net.morematerials.wgen.Decorator;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class DecoratorThrottler extends BukkitRunnable {
	private static final Random RANDOM = new Random();
	private final Queue<DecorableEntry> queue;
	private final MoreMaterials plugin;
	private final World world;
	private int steps = 0;

	public DecoratorThrottler(MoreMaterials plugin, World world) {
		this.plugin = plugin;
		this.world = world;
		queue = new LinkedBlockingQueue<>();
	}

	@Override
	public void run() {
		while (++steps <= 5) {
			final DecorableEntry entry = queue.poll();
			if (entry != null) {
				final Chunk chunk = world.getChunkAt(entry.getChunkX(), entry.getChunkZ());
				entry.getDecorator().decorate(world, chunk, RANDOM);
				//System.out.println("Decorated: [" + chunk + "]");
			}
		}
		if (plugin.getConfig().getBoolean("DebugMode", false)) {
			plugin.getLogger().info("Queue Remaining: " + queue.size());
		}
		steps = 0;
	}

	public boolean offer(Decorator decorator, int chunkX, int chunkZ) {
		final DecorableEntry entry = new DecorableEntry(decorator, chunkX, chunkZ);
		return !queue.contains(entry) && queue.offer(new DecorableEntry(decorator, chunkX, chunkZ));
	}

	public boolean isQueued(Decorator decorator, int chunkX, int chunkZ) {
		return queue.contains(new DecorableEntry(decorator, chunkX, chunkZ));
	}

	public boolean hasAnyQueued(int chunkX, int chunkZ) {
		boolean any = false;
		for (Decorator decorator : plugin.getDecoratorRegistry().getAll()) {
			if (isQueued(decorator, chunkX, chunkZ)) {
				any = true;
				break;
			}
		}

		return any;
	}
}
