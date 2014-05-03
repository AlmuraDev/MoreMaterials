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
package net.morematerials.wgen.task;

import net.morematerials.wgen.Decorator;
import org.bukkit.World;

public class DecorableEntry {
	private final World world;
	private final int cx, cz;
	private final Decorator decorator;

	public DecorableEntry(World world, int cx, int cz, Decorator decorator) {
		this.world = world;
		this.cx = cx;
		this.cz = cz;
		this.decorator = decorator;
	}

	public World getWorld() {
		return world;
	}

	public int getChunkX() {
		return cx;
	}

	public int getChunkZ() {
		return cz;
	}

	public Decorator getDecorator() {
		return decorator;
	}

	@Override
	public int hashCode() {
		int result = world.hashCode();
		result = 31 * result + cx;
		result = 31 * result + cz;
		result = 31 * result + decorator.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		final DecorableEntry that = (DecorableEntry) o;

		return cx == that.cx && cz == that.cz && decorator.equals(that.decorator) && world.equals(that.world);
	}
}
