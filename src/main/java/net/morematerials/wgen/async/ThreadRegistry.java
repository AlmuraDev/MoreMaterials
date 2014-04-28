package net.morematerials.wgen.async;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;

public class ThreadRegistry {
	private final Map<World, DecoratorThrottler> throttlers = new HashMap<>();

	public DecoratorThrottler get(World world) {
		return throttlers.get(world);
	}

	public DecoratorThrottler start(int tps, World world) {
		final DecoratorThrottler throtter = new DecoratorThrottler(tps, world);
		throttlers.put(world, throtter);
		throtter.start();
		return throtter;
	}

	public void stop(World world) {
		throttlers.remove(world).terminate();
	}

	public void stopAll(boolean clear) {
		for (DecoratorThrottler throttler : throttlers.values()) {
			throttler.terminate();
		}
		if (clear) {
			throttlers.clear();
		}
	}
}
