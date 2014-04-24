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
package net.morematerials.wgen.ore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.morematerials.wgen.Decorator;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.block.SpoutChunk;
import org.getspout.spoutapi.material.CustomBlock;

/**
 * Decorates notchian {@link org.bukkit.block.Block}s with {@link org.getspout.spoutapi.material.CustomBlock}s.
 *
 * This class mimics the behavior of ore veins found in caves.
 */
public class CustomOreDecorator extends Decorator {
	private final CustomBlock ore;
	private final ArrayList<Material> replaceables;
	private final int minHeight, maxHeight;
	private final int minVeinSize, maxVeinSize;
	private final int minVeinsPerChunk, maxVeinsPerChunk;

	public CustomOreDecorator(String identifier, CustomBlock ore, int minHeight, int maxHeight, int minVeinSize, int maxVeinSize, int minVeinsPerChunk, int maxVeinsPerChunk) {
		this(identifier, ore, new ArrayList<Material>(0), minHeight, maxHeight, minVeinSize, maxVeinSize, minVeinsPerChunk, maxVeinsPerChunk);
	}

	public CustomOreDecorator(String identifier, CustomBlock ore, ArrayList<Material> replaceables, int minHeight, int maxHeight, int minVeinSize, int maxVeinSize, int minVeinsPerChunk, int maxVeinsPerChunk) {
		super(identifier);
		this.ore = ore;
		this.replaceables = replaceables;
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

	public Decorator replace(Material... materials) {
		for (Material material : materials) {
			replaceables.add(material);
		}
		return this;
	}

	public Collection<Material> getReplaceables() {
		return Collections.unmodifiableCollection(replaceables);
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
	public void decorate(World world, Chunk chunk, Random random) {
		final int veinsPerChunk = random.nextInt(maxVeinsPerChunk - minVeinsPerChunk) + maxVeinsPerChunk;
		for (int i = 0; i < veinsPerChunk; i++) {			
			final int x = random.nextInt(16);
			final int y = random.nextInt(maxHeight - minHeight) + minHeight;
			final int z = random.nextInt(16);
			
			final int veinSize = random.nextInt(maxVeinSize - minVeinSize) + minVeinSize;

			placeOre(world, chunk, x, y, z, veinSize, random);
		}
	}

	@Override
	public String toString() {
		return "CustomOreDecorator {identifier= " + getIdentifier() + ", ore= " + ore.getFullName() + ", minHeight= " + minHeight + ", maxHeight= " + maxHeight + ", minVeinSize= " + minVeinSize +
				" maxVeinSize= " + maxVeinSize + ", minVeinsPerChunk= " + minVeinsPerChunk + ", maxVeinsPerChunk= " + maxVeinsPerChunk + "}";
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
									if (replaceables.contains(block.getType()) && block.getCustomBlock() == null) {
										//SpoutChunk spoutChunk = (SpoutChunk) chunk;
										//spoutChunk.setCustomBlock(xx, yy, zz, ore);
										System.out.println("Populate at: x:" + xx + " y: " + yy + " z: " + zz);
										block.setCustomBlock(ore);
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
