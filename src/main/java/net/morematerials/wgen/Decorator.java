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

	/**
	 * Returns whether the (@link org.bukkit.Chunk) contained in the {@link org.bukkit.World} should be decorated.
	 *
	 * @param world The world that holds the chunk
	 * @param chunk The chunk to be decorated
	 * @return true if can be decorated, false if not
	 */
	public boolean canDecorate(World world, Chunk chunk, int x, int y, int z) {
		return chunk.getX() == (x >> 4) && chunk.getZ() == (x >> 4);
	}

	public void decorate(World world, int chunkX, int chunkZ, Random random) {
		decorate(world, world.getChunkAt(chunkX, chunkZ), random);
	}

	public abstract void decorate(World world, Chunk chunk, Random random);

	@Override
	public final boolean equals(Object o) {
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
	public final int hashCode() {
		return identifier.hashCode();
	}
}
