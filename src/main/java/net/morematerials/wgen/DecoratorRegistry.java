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
