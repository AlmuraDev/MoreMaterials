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
package net.morematerials.wgen.thread;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.flowpowered.math.vector.Vector3f;
import net.morematerials.MoreMaterials;
import net.morematerials.wgen.Decorator;
import net.morematerials.wgen.ore.CustomOreDecorator;
import net.morematerials.wgen.task.BlockPlacer;
import net.morematerials.wgen.task.DecorableEntry;
import org.bukkit.World;

public class MaffThread extends Thread {
	private static final Random RANDOM = new Random();
	private final MoreMaterials plugin;
	private final BlockPlacer replacer;
	private final Timer timer;
	private final ConcurrentLinkedQueue<DecorableEntry> queue = new ConcurrentLinkedQueue<>();
	private volatile boolean running = false;

	public MaffThread(MoreMaterials plugin, BlockPlacer replacer, int tps) {
		this.plugin = plugin;
		this.replacer = replacer;
		timer = new Timer(tps);
	}

	@Override
	public void run() {
		running = true;
		timer.start();

		while (running) {
			final DecorableEntry entry = queue.poll();
			if (entry != null && entry.getDecorator() instanceof CustomOreDecorator) {
				final CustomOreDecorator oreDecorator = (CustomOreDecorator) entry.getDecorator();
				final int veinsPerChunk = RANDOM.nextInt(oreDecorator.getMaxVeinsPerChunk() - oreDecorator.getMinVeinsPerChunk()) + oreDecorator.getMaxVeinsPerChunk();
				for (byte i = 0; i < veinsPerChunk; i++) {
					final int x = (entry.getChunkX() << 4) + RANDOM.nextInt(16);
					final int y = RANDOM.nextInt(oreDecorator.getMaxHeight() - oreDecorator.getMinHeight()) + oreDecorator.getMinHeight();
					final int z = (entry.getChunkZ() << 4) + RANDOM.nextInt(16);
					final int veinSize = RANDOM.nextInt(oreDecorator.getMaxVeinSize() - oreDecorator.getMinVeinSize()) + oreDecorator.getMinVeinSize();
					final Vector3f point = ((CustomOreDecorator) entry.getDecorator()).calculatePoint(x, y, z, veinSize, RANDOM);
					//plugin.getLogger().info("[MoreMaterials] Maff Thread -> Calculated: " + point);
					replacer.offer(entry, point.getFloorX(), point.getFloorY(), point.getFloorZ());
				}
			}
			timer.sync();
		}
	}

	public void terminate() {
		running = false;
	}

	public boolean offer(World world, int cx, int cz, Decorator decorator) {
		return queue.offer(new DecorableEntry(world, cx, cz, decorator));
	}

	public boolean hasAnyQueued(World world, int cx, int cz) {
		boolean any = false;
		for (Decorator decorator : plugin.getDecoratorRegistry().getAll()) {
			if (isQueued(world, cx, cz, decorator)) {
				any = true;
				break;
			}
		}
		return any;
	}

	public boolean isQueued(World world, int cx, int cz, Decorator decorator) {
		for (DecorableEntry entry : queue) {
			if (entry.getWorld() == world && entry.getChunkX() == cx && entry.getChunkZ() == cz && entry.getDecorator().equals(decorator)) {
				return true;
			}
		}
		return false;
	}
}
