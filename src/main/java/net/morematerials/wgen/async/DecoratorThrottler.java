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
package net.morematerials.wgen.async;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import net.morematerials.wgen.Decorator;
import net.morematerials.wgen.Timer;
import org.bukkit.Chunk;
import org.bukkit.World;

public class DecoratorThrottler extends Thread {
	private final Queue<DecorableEntry> queue;
	private final Timer timer;
	private final World world;
	private volatile boolean running = false;

	public DecoratorThrottler(int tps, World world) {
		timer = new Timer(tps);
		this.world = world;
		queue = new LinkedBlockingQueue<>();
		setDaemon(true);
	}

	@Override
	public void run() {
		timer.start();
		running = true;
		long lastTime = System.nanoTime() - (long) (1f / timer.getTps() * 1000000000), currentTime;
		while (running) {
			currentTime = System.nanoTime() - lastTime;
			final DecorableEntry entry = queue.poll();
			if (entry != null) {
				entry.getDecorator().decorate(world, entry.getChunk(), entry.getRandom());
				System.out.println("Decorated: [" + entry.getChunk() + "]");
			}
			lastTime = currentTime;
			timer.sync();
		}
	}

	public void terminate() {
		running = false;
	}

	public void offer(Decorator decorator, Chunk chunk, Random random) {
		if (chunk.getWorld() != world) {
			throw new RuntimeException("Attempt to throttle decoration of a chunk within another world!");
		}
		queue.offer(new DecorableEntry(decorator, chunk, random));
	}
}
