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
import java.util.concurrent.ConcurrentLinkedQueue;

import net.morematerials.MoreMaterials;
import net.morematerials.wgen.ore.CustomOreDecorator;
import net.morematerials.wgen.thread.DecorablePoint;
import org.bukkit.scheduler.BukkitRunnable;
import org.getspout.spoutapi.block.SpoutBlock;

public class BlockPlacer extends BukkitRunnable {
	private final Queue<DecorablePoint> queue;
	private final MoreMaterials plugin;
	public int speed = 25;
	private int steps = 0;
	private boolean finished = false, paused = false, hasPauseRan = false;

	public BlockPlacer(MoreMaterials plugin) {
		this.plugin = plugin;
		queue = new ConcurrentLinkedQueue<>();
	}

	@Override
	public void run() {
		if (paused) {
			if (!hasPauseRan) {
				plugin.getLogger().info("Setting custom blocks has been paused. Queue remaining: " + queue.size());
				hasPauseRan = true;
			}
			return;
		}
		while (++steps <= speed) {
			final DecorablePoint entry = queue.poll();
			if (entry != null && entry.getDecorator() instanceof CustomOreDecorator) {
				if (entry.getDecorator().canDecorate(entry.getWorld(), entry.getChunkX(), entry.getChunkZ(), entry.getX(), entry.getY(), entry.getZ())) {
					final SpoutBlock block = (SpoutBlock) entry.getWorld().getBlockAt(entry.getX(), entry.getY(), entry.getZ());
					boolean shouldPlace = ((CustomOreDecorator) entry.getDecorator()).getReplaceables().contains(block.getType()) && block.getCustomBlock() == null;
					if (!shouldPlace) {
						//System.out.println("Could not populate: " + x + "/" + y + "/" + z + "Block Type: " + block.getType().name() + " Custom: " + block.getCustomBlock());
					} else {
						block.setCustomBlock(((CustomOreDecorator) entry.getDecorator()).getOre());
					}
				}
			}
		}
		if (plugin.showDebug) {
			if (queue.size() > 1) {
				plugin.getLogger().info("Queue Remaining: " + queue.size());
				finished = false;
			} else {
				if (!finished) {
					plugin.getLogger().info("All blocks have been placed.");
					plugin.save();
					finished = true;
				}
			}
		}
		steps = 0;
	}

	public void offer(DecorableEntry decorated, int bx, int by, int bz) {
		queue.offer(new DecorablePoint(decorated.getWorld(), decorated.getChunkX(), decorated.getChunkZ(), bx, by, bz, decorated.getDecorator()));
	}

	public void pause() {
		paused = true;
	}

	public void resume() {
		paused = false;
	}
}
