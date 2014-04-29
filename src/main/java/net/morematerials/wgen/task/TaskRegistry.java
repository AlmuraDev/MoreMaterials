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
import org.bukkit.Bukkit;
import org.bukkit.World;

public class TaskRegistry {
	private final MoreMaterials plugin;
	private final Map<World, DecoratorThrottler> throttlers = new HashMap<>();

	public TaskRegistry(MoreMaterials plugin) {
		this.plugin = plugin;
	}

	public DecoratorThrottler get(World world) {
		return throttlers.get(world);
	}

	public DecoratorThrottler start(long delay, World world) {
		final DecoratorThrottler throtter = new DecoratorThrottler(plugin, world);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, throtter, 0L, delay);
		throttlers.put(world, throtter);
		return throtter;
	}

	public void stop(World world, boolean clear) {
		final DecoratorThrottler task = clear ? throttlers.remove(world) : throttlers.get(world);

		if (task == null) {
			return;
		}

		task.cancel();
	}

	public void stopAll(boolean clear) {
		for (DecoratorThrottler throttler : throttlers.values()) {
			throttler.cancel();
		}
		if (clear) {
			throttlers.clear();
		}
	}
}
