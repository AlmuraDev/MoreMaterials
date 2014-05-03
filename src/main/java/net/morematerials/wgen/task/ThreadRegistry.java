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

import java.util.HashMap;
import java.util.Map;

import net.morematerials.MoreMaterials;
import net.morematerials.wgen.thread.MaffThread;
import org.bukkit.World;

public class ThreadRegistry {
	private final MoreMaterials plugin;
    private final BlockPlacer placer;
	private final Map<World, MaffThread> threads = new HashMap<>();

	public ThreadRegistry(MoreMaterials plugin, BlockPlacer placer) {
		this.plugin = plugin;
        this.placer = placer;
	}

	public MaffThread get(World world) {
		return threads.get(world);
	}

	public MaffThread start(int tps, World world) {
		final MaffThread thread = new MaffThread(plugin, placer, tps);
        threads.put(world, thread);
		return thread;
	}

	public void stop(World world, boolean clear) {
		final MaffThread thread = clear ? threads.remove(world) : threads.get(world);

		if (thread == null) {
			return;
		}

        thread.terminate();
	}

	public void stopAll(boolean clear) {
		for (MaffThread thread : threads.values()) {
			thread.terminate();
		}
		if (clear) {
			threads.clear();
		}
	}
}
