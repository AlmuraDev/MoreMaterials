package net.morematerials.wgen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Stores all {@link net.morematerials.wgen.GeneratorObject}s created on the server.
 */
public class GeneratorRegistry {
	private final List<GeneratorObject> objects = new ArrayList<>();

	public GeneratorObject add(GeneratorObject object) {
		if (object == null) {
			throw new IllegalArgumentException("Cannot add a null instance of a GeneratorObject to the registry!");
		}
		objects.add(object);
		return object;
	}

	public GeneratorObject get(String identifier) {
		if (identifier == null || identifier.isEmpty()) {
			throw new IllegalArgumentException("Cannot get a GeneratorObject based on an identifier which is a null or empty String reference!");
		}
		for (GeneratorObject object : objects) {
			if (object.getIdentifier().equals(identifier)) {
				return object;
			}
		}
		return null;
	}

	public Collection<GeneratorObject> getAllByType(Class<? extends GeneratorObject> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("Cannot get a collection of GeneratorObjects based on a null class reference!");
		}
		final List<GeneratorObject> children = new ArrayList<>();
		for (GeneratorObject object : objects) {
			//Check for exact or parent class match
			if (object.getClass() == clazz || object.getClass().getSuperclass() == clazz) {
				children.add(object);
			}
		}

		return Collections.unmodifiableCollection(children);
	}

	public GeneratorObject remove(String identifier) {
		if (identifier == null || identifier.isEmpty()) {
			throw new IllegalArgumentException("Cannot remove a GeneratorObject based on an identifier which is a null or empty String reference!");
		}

		final Iterator<GeneratorObject> iterator = objects.iterator();
		GeneratorObject toRemove = null;
		while (iterator.hasNext()) {
			toRemove = iterator.next();
			if (toRemove.getIdentifier().equals(identifier)) {
				iterator.remove();
			}
		}
		return toRemove;
	}
}
