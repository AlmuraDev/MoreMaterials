package net.morematerials.wgen;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.World;

/**
 * Represents a generator object which can decorate a {@link org.bukkit.Chunk}.
 */
public abstract class Decorator {
	private final String identifier;

	public Decorator(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void decorate(World world, int chunkX, int chunkZ, Random random) {
		decorate(world, world.getChunkAt(chunkX, chunkZ), random);
	}

	public abstract void decorate(World world, Chunk chunk, Random random);

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof Decorator)) {
			return false;
		}

		final Decorator decorator = (Decorator) o;

		return identifier.equals(decorator.identifier);
	}

	@Override
	public int hashCode() {
		return identifier.hashCode();
	}
}
