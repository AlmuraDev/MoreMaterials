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
	}

	@Override
	public void run() {
		running = true;
		long lastTime = System.nanoTime() - (long) (1f / timer.getTps() * 1000000000), currentTime;
		while (running) {
			currentTime = System.nanoTime() - lastTime;
			final DecorableEntry entry = queue.poll();
			entry.getDecorator().decorate(world, entry.getChunk(), entry.getRandom());
			System.out.println("Decorated: [" + entry.getChunk() + "]");
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
