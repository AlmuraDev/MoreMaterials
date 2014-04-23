package net.morematerials.wgen;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.World;

/**
 * Represents a generator object which can be populated/injected into a chunk
 */
public interface GeneratorObject {
	public String getIdentifier();

	public void populate(World world, int chunkX, int chunkZ, Random random);

	public void populate(World world, Chunk chunk, Random random);
}
