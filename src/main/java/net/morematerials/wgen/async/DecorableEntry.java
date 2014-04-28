package net.morematerials.wgen.async;

import java.util.Random;

import net.morematerials.wgen.Decorator;
import org.bukkit.Chunk;

public class DecorableEntry {
	private final Decorator decorator;
	private final Chunk chunk;
	private final Random random;

	public DecorableEntry(Decorator decorator, Chunk chunk, Random random) {
		this.decorator = decorator;
		this.chunk = chunk;
		this.random = random;
	}

	public Decorator getDecorator() {
		return decorator;
	}

	public Chunk getChunk() {
		return chunk;
	}

	public Random getRandom() {
		return random;
	}
}
