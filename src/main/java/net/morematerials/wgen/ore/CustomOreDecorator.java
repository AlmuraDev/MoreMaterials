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

import com.flowpowered.math.GenericMath;
import com.flowpowered.math.TrigMath;
import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;

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
    private static final Vector3f INVALID_POINT = new Vector3f(-1, -1, -1);
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
	public boolean canDecorate(World world, int cx, int cz, int bx, int by, int bz) {
		if (!super.canDecorate(world, cx, cz, bx, by, bz)) {
			return false;
		}
		final SpoutBlock block = (SpoutBlock) world.getBlockAt(bx, by, bz);
		boolean contains = replaceables.contains(block.getType()) && block.getCustomBlock() == null;
		if (!contains) {
			//System.out.println("Could not populate: " + x + "/" + y + "/" + z + "Block Type: " + block.getType().name() + " Custom: " + block.getCustomBlock());
		}
		return contains;
	}

	@Override
	public void decorate(World world, Chunk chunk, Random random) {		
		final int veinsPerChunk = random.nextInt(maxVeinsPerChunk - minVeinsPerChunk) + maxVeinsPerChunk;
		for (byte i = 0; i < veinsPerChunk; i++) {
			final int x = (chunk.getX() << 4) + random.nextInt(16);
			final int y = random.nextInt(maxHeight - minHeight) + minHeight;
			final int z = (chunk.getZ() << 4) + random.nextInt(16);
			final int veinSize = random.nextInt(maxVeinSize - minVeinSize) + minVeinSize;
			placeOre(world, chunk.getX(), chunk.getZ(), x, y, z, veinSize, random);
			//vectorPlaceOre(world, chunk, new Vector3f(x, y, z), veinSize, random);
		}		
	}

	@Override
	public String toString() {
		return "CustomOreDecorator {identifier= " + getIdentifier() + ", ore= " + ore.getFullName() + ", minHeight= " + minHeight + ", maxHeight= " + maxHeight + ", minVeinSize= " + minVeinSize +
				" maxVeinSize= " + maxVeinSize + ", minVeinsPerChunk= " + minVeinsPerChunk + ", maxVeinsPerChunk= " + maxVeinsPerChunk + "}";
	}

    public Vector3f calculatePoint(int bx, int by, int bz, int veinSize, Random random) {
        final float angle = random.nextFloat() * (float) Math.PI;
        final Vector2f offset = Vector2f.createDirection(angle).mul(veinSize).div(8);
        final float x1 = ((bx + 8) + offset.getX());
        final float x2 = ((bx + 8) - offset.getX());
        final float z1 = ((bz + 8) + offset.getY());
        final float z2 = ((bz + 8) - offset.getY());
        final float y1 = (by + random.nextInt(3) + 2);
        final float y2 = (by + random.nextInt(3) + 2);

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
                                    return new Vector3f(x, y, z);
                                }
                            }
                        }
                    }
                }
            }
        }
        return new Vector3f(-1, -1, -1);
    }
	
	private void vectorPlaceOre(World world, int cx, int cz, Vector3f origin, int veinSize, Random random) {
        // Generate dimensions of box
        final float angle = random.nextFloat() * (float) TrigMath.PI;
        final Vector2f offset = Vector2f.createDirection(angle).mul(veinSize).div(8);
        // Generate box min and max corner coordinates
        final Vector3f min = origin.sub(offset.getX() + 8, random.nextInt(3) + 2, offset.getY() + 8);
        //final Vector3f min = origin.sub(offset.getX(), random.nextInt(3) + 2, offset.getY());  // Old Line
        final Vector3f max = origin.add(offset.getX() + 8, random.nextInt(3) + 2, offset.getY() + 8);
        // Generate an ore sphere on each block on the diagonal from min to max
        for (int count = 0; count <= veinSize; count++) {
            // Calculate the percent of the count so far
            final int percent = count / veinSize;
            // Get the center point, which is on the diagonal in between the min and max corners
            final Vector3f center = GenericMath.lerp(min, max, percent);
            // Calculate the size of the ore sphere so that it increases as we approach the middle
            final float size = ((TrigMath.sin(percent * TrigMath.PI) + 1) * random.nextFloat() * veinSize / 16 + 1) / 2;
            // Compute the start and end point of the ore sphere
            final Vector3i start = center.sub(size, size, size).toInt();
            final Vector3i end = center.add(size, size, size).toInt();
            // Iterate inside the volume we just defined
            for (int x = start.getX(); x <= end.getX(); x++) {
                // Get the distance from the center on x, normalized
                float dx = (x + 0.5f - center.getX()) / size;
                dx *= dx;
                if (dx < 1) {
                    // Get the distance from the center on y, normalized
                    for (int y = start.getY(); y <= end.getY(); y++) {
                        float dy = (y + 0.5f - center.getY()) / size;
                        dy *= dy;
                        if (dx + dy < 1) {
                            // Get the distance from the center on z, normalized
                            for (int z = start.getZ(); z <= end.getZ(); z++) {
                                float dz = (z + 0.5f - center.getZ()) / size;
                                dz *= dz;
                                // Check if we are in the ore sphere
                                if (dx + dy + dz < 1) {
                                    // If so, generate the ore block
                                    if (canDecorate(world, cx, cz, x, y, z)) {
                                    	final SpoutBlock block = (SpoutBlock) world.getBlockAt(x, y, z);
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
	
	private void placeOre(World world, int cx, int cz, int bx, int by, int bz, int veinSize, Random random) {
        final Vector3f point = calculatePoint(bx, by, bz, veinSize, random);
        if (point.equals(INVALID_POINT)) {
            //do something, its bad
        }
        final int floorX = point.getFloorX();
        final int floorY = point.getFloorY();
        final int floorZ = point.getFloorZ();

        if (canDecorate(world, cx, cz, floorX, floorY, floorZ)) {
            ((SpoutBlock) world.getBlockAt(floorX, floorY, floorZ)).setCustomBlock(ore);
        }
    }
}
