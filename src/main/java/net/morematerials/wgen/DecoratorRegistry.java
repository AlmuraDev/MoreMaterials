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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Stores all {@link Decorator}s created on the server.
 */
public class DecoratorRegistry {
	private final List<Decorator> decorators = new LinkedList<>();

	public Decorator add(Decorator decorator) {
		if (decorator == null) {
			throw new IllegalArgumentException("Cannot add a null instance of a Decorator to the registry!");
		}
		decorators.add(decorator);
		return decorator;
	}

	public Collection<Decorator> addAll(Collection<Decorator> decorators) {
		if (decorators == null) {
			throw new IllegalArgumentException("Cannot add a Collection of null instances of a Decorator to the registry!");
		}

		decorators.addAll(decorators);
		return Collections.unmodifiableCollection(decorators);
	}

	public Decorator get(String identifier) {
		if (identifier == null || identifier.isEmpty()) {
			throw new IllegalArgumentException("Cannot get a Decorator based on an identifier which is a null or empty String reference!");
		}
		for (Decorator decorator : decorators) {
			if (decorator.getIdentifier().equals(identifier)) {
				return decorator;
			}
		}
		return null;
	}

	public Collection<Decorator> getAllByType(Class<? extends Decorator> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("Cannot get a collection of Decorators based on a null class reference!");
		}
		final List<Decorator> children = new ArrayList<>();
		for (Decorator decorator : decorators) {
			//Check for exact or parent class match
			if (decorator.getClass() == clazz || decorator.getClass().getSuperclass() == clazz) {
				children.add(decorator);
			}
		}

		return Collections.unmodifiableCollection(children);
	}

	public Decorator remove(String identifier) {
		if (identifier == null || identifier.isEmpty()) {
			throw new IllegalArgumentException("Cannot remove a Decorator based on an identifier which is a null or empty String reference!");
		}

		final Iterator<Decorator> iterator = decorators.iterator();
		Decorator toRemove = null;
		while (iterator.hasNext()) {
			toRemove = iterator.next();
			if (toRemove.getIdentifier().equals(identifier)) {
				iterator.remove();
			}
		}
		return toRemove;
	}
}
