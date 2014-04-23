package net.morematerials.wgen.ore;

import java.util.Random;

import net.morematerials.wgen.GeneratorObject;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.block.SpoutChunk;
import org.getspout.spoutapi.material.CustomBlock;

public class CustomOre implements GeneratorObject {
	private final String identifier;
	private final CustomBlock ore;
	private final int minHeight, maxHeight;
	private final int minVeinSize, maxVeinSize;
	private final int minVeinsPerChunk, maxVeinsPerChunk;

	public CustomOre(String identifier, CustomBlock ore, int minHeight, int maxHeight, int minVeinSize, int maxVeinSize, int minVeinsPerChunk, int maxVeinsPerChunk) {
		this.identifier = identifier;
		this.ore = ore;
		this.minHeight = minHeight;
		this.maxHeight = maxHeight;
		this.minVeinSize = minVeinSize;
		this.maxVeinSize = maxVeinSize;
		this.minVeinsPerChunk = minVeinsPerChunk;
		this.maxVeinsPerChunk = maxVeinsPerChunk;
	}

	public CustomBlock getOre() {
		return ore;
	}

	public int getMinHeight() {
		return minHeight;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public int getMinVeinSize() {
		return minVeinSize;
	}

	public int getMaxVeinSize() {
		return maxVeinSize;
	}

	public int getMinVeinsPerChunk() {
		return minVeinsPerChunk;
	}

	public int getMaxVeinsPerChunk() {
		return maxVeinsPerChunk;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public void populate(World world, Chunk chunk, int x, int y, int z, Random random) {
		final int veinsPerChunk = random.nextInt(maxVeinsPerChunk - minVeinsPerChunk) + maxVeinsPerChunk;
		for (int i = 0; i < veinsPerChunk; i++) {
			final int xx = random.nextInt(16);
			final int yy = random.nextInt(maxHeight - minHeight) + minHeight;
			final int zz = random.nextInt(16);
			final int veinSize = random.nextInt(maxVeinSize - minVeinSize) + minVeinSize;

			placeOre(world, chunk, xx, yy, zz, veinSize, random);
		}
	}

	private void placeOre(World world, Chunk chunk, int x, int y, int z, int veinSize, Random random) {
		final double angle = random.nextDouble() * Math.PI;
		final double x1 = ((x + 8) + Math.sin(angle) * veinSize / 8);
		final double x2 = ((y + 8) - Math.sin(angle) * veinSize / 8);
		final double z1 = ((z + 8) + Math.cos(angle) * veinSize / 8);
		final double z2 = ((z + 8) - Math.cos(angle) * veinSize / 8);
		final double y1 = (y + random.nextInt(3) + 2);
		final double y2 = (y + random.nextInt(3) + 2);

		for (int i = 0; i < veinSize; i++) {
			final double seedX = x1 + (x2 - x1) * i / veinSize;
			final double seedY = y1 + (y2 - y1) * i / veinSize;
			final double seedZ = z1 + (z2 - z1) * i / veinSize;
			final double size = ((Math.sin(i * Math.PI / veinSize) + 1) * random.nextDouble() * veinSize / 16 + 1) / 2;

			final int startX = (int) (seedX - size);
			final int startY = (int) (seedY - size);
			final int startZ = (int) (seedZ - size);
			final int endX = (int) (seedX + size);
			final int endY = (int) (seedY + size);
			final int endZ = (int) (seedZ + size);

			for (int xx = startX; xx <= endX; xx++) {
				double sizeX = (xx + 0.5 - seedX) / size;
				sizeX *= sizeX;

				if (sizeX < 1) {
					for (int yy = startY; yy <= endY; yy++) {
						double sizeY = (yy + 0.5 - seedY) / size;
						sizeY *= sizeY;

						if (sizeX + sizeY < 1) {
							for (int zz = startZ; zz <= endZ; zz++) {
								double sizeZ = (zz + 0.5 - seedZ) / size;
								sizeZ *= sizeZ;
								if (sizeX + sizeY + sizeZ < 1) {
									final SpoutBlock block = (SpoutBlock) world.getBlockAt(xx, yy, zz);
									if (block.getType().equals(Material.STONE) && block.getCustomBlock() != null) {
										SpoutChunk spoutChunk = (SpoutChunk) chunk;
										spoutChunk.setCustomBlock(xx, yy, zz, ore);
									}
								}
							}
						}
					}
				}
			}
		}
	}
}
