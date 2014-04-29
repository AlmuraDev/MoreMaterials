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
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import com.flowpowered.math.TrigMath;
import com.flowpowered.math.vector.Vector2f;
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
	public int toDecorateCount = 0;
	private final ArrayList<Material> replaceables;
	private final int decorateChance;
	private final int minHeight, maxHeight;
	private final int minVeinSize, maxVeinSize;
	private final int minVeinsPerChunk, maxVeinsPerChunk;

	public CustomOreDecorator(String identifier, CustomBlock ore, int decorateChance, int minHeight, int maxHeight, int minVeinSize, int maxVeinSize, int minVeinsPerChunk, int maxVeinsPerChunk) {
		this(identifier, ore, decorateChance, new ArrayList<Material>(0), minHeight, maxHeight, minVeinSize, maxVeinSize, minVeinsPerChunk, maxVeinsPerChunk);
	}

	public CustomOreDecorator(String identifier, CustomBlock ore, int decorateChance, ArrayList<Material> replaceables, int minHeight, int maxHeight, int minVeinSize, int maxVeinSize, int minVeinsPerChunk, int maxVeinsPerChunk) {
		super(identifier);
		this.ore = ore;
		this.replaceables = replaceables;
		this.decorateChance = decorateChance;
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
		Collections.addAll(replaceables, materials);
		return this;
	}

	public Collection<Material> getReplaceables() {
		return Collections.unmodifiableCollection(replaceables);
	}

	public int getDecorateChance() {
		return decorateChance;
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
	public boolean canDecorate(World world, Chunk chunk, int x, int y, int z) {
		if (!super.canDecorate(world, chunk, x, y, z)) {
			return false;
		}
		final SpoutBlock block = (SpoutBlock) world.getBlockAt(x, y, z);
		return replaceables.contains(block.getType()) && block.getCustomBlock() == null;
	}

	@Override
	public void decorate(World world, Chunk chunk, Random random) {		
		final int veinsPerChunk = random.nextInt(maxVeinsPerChunk - minVeinsPerChunk) + maxVeinsPerChunk;
		for (byte i = 0; i < veinsPerChunk; i++) {
			final int x = (chunk.getX() << 4) + random.nextInt(16);
			final int y = random.nextInt(maxHeight - minHeight) + minHeight;
			final int z = (chunk.getZ() << 4) + random.nextInt(16);
			final int veinSize = random.nextInt(maxVeinSize - minVeinSize) + minVeinSize;
			placeOre(world, chunk, x, y, z, veinSize, random);
		}		
	}

	@Override
	public String toString() {
		return "CustomOreDecorator {identifier= " + getIdentifier() + ", ore= " + ore.getFullName() + ", minHeight= " + minHeight + ", maxHeight= " + maxHeight + ", minVeinSize= " + minVeinSize +
				" maxVeinSize= " + maxVeinSize + ", minVeinsPerChunk= " + minVeinsPerChunk + ", maxVeinsPerChunk= " + maxVeinsPerChunk + "}";
	}

	private void placeOre(World world, Chunk chunk, int originX, int originY, int originZ, int veinSize, Random random) {
		final float angle = random.nextFloat() * (float) Math.PI;
		final Vector2f offset = Vector2f.createDirection(angle).mul(veinSize).div(8);
		final float x1 = ((originX + 8) + offset.getX());
		final float x2 = ((originX + 8) - offset.getX());
		final float z1 = ((originZ + 8) + offset.getY());
		final float z2 = ((originZ + 8) - offset.getY());
		final float y1 = (originY + random.nextInt(3) + 2);
		final float y2 = (originY + random.nextInt(3) + 2);

		for (int count = 0; count <= veinSize; count++) {
			final float seedX = x1 + (x2 - x1) * count / veinSize;
			final float seedY = y1 + (y2 - y1) * count / veinSize;
			final float seedZ = z1 + (z2 - z1) * count / veinSize;
			final float size = ((TrigMath.sin(count * (float) Math.PI / veinSize) + 1) * random.nextFloat() * veinSize / 16 + 1) / 2;

			final int startX = (int) (seedX - size);
			final int startY = (int) (seedY - size);
			final int startZ = (int) (seedZ - size);
			final int endX = (int) (seedX + size);
			final int endY = (int) (seedY + size);
			final int endZ = (int) (seedZ + size);

			for (int x = startX; x <= endX; x++) {
				float sizeX = (x + 0.5f - seedX) / size;
				sizeX *= sizeX;

				if (sizeX < 1) {
					for (int y = startY; y <= endY; y++) {
						float sizeY = (y + 0.5f - seedY) / size;
						sizeY *= sizeY;

						if (sizeX + sizeY < 1) {
							for (int z = startZ; z <= endZ; z++) {
								float sizeZ = (z + 0.5f - seedZ) / size;
								sizeZ *= sizeZ;
								if (sizeX + sizeY + sizeZ < 1) {
									if (canDecorate(world, chunk, x, y, z)) {
										((SpoutChunk) chunk).setCustomBlock(x, y, z, ore);
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
