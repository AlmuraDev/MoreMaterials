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
	public int speed = 15;
	private boolean done, finished = false;

	public DecoratorThrottler(MoreMaterials plugin, World world) {
		this.plugin = plugin;
		this.world = world;
		queue = new LinkedBlockingQueue<>();
	}

	@Override
	public void run() {
		while (++steps <= speed) {
			final DecorableEntry entry = queue.poll();
			if (entry != null) {				
				final boolean exists = world.loadChunk(entry.getChunkX(), entry.getChunkZ(), false);
				if (!exists && !entry.willCreate()) {
					//System.out.println("Skipped decoration because the chunk: " + entry.getChunkX() + " / " + entry.getChunkZ() + " doesn't exist.");
					continue;
				}
				final Chunk chunk = world.getChunkAt(entry.getChunkX(), entry.getChunkZ());				
				entry.getDecorator().decorate(world, chunk, RANDOM);
			}
		}
		if (plugin.showDebug) {
			if (queue.size()>1) {
				plugin.getLogger().info("Queue Remaining: " + queue.size());
				done = false;
				finished = false;
			} else {
				done = true;
				if (done && !finished) {
					plugin.getLogger().info("Decorate Task has completed.");
					finished = true;
				}
			}
		}
		steps = 0;
	}

	public boolean offer(Decorator decorator, int chunkX, int chunkZ, boolean create) {
		final DecorableEntry entry = new DecorableEntry(decorator, chunkX, chunkZ, create);
		return !queue.contains(entry) && queue.offer(new DecorableEntry(decorator, chunkX, chunkZ, create));
	}

	public boolean isQueued(Decorator decorator, int chunkX, int chunkZ, boolean create) {
		return queue.contains(new DecorableEntry(decorator, chunkX, chunkZ, create));
	}

	public boolean hasAnyQueued(int chunkX, int chunkZ) {
		boolean any = false;
		for (Decorator decorator : plugin.getDecoratorRegistry().getAll()) {
			if (isQueued(decorator, chunkX, chunkZ, false) || isQueued(decorator, chunkX, chunkZ, true)) {
				any = true;
				break;
			}
		}
		return any;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public void setSpeed(int newSpeed) {
		speed = newSpeed;
	}
}
