package net.morematerials.wgen;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

public class DecoratorPopulator extends BlockPopulator {
	private final Decorator[] decorators;

	public DecoratorPopulator(Decorator... decorators) {
		this.decorators = decorators;
	}

	@Override
	public void populate(World world, Random random, Chunk chunk) {
		for (Decorator decorator : decorators) {
			decorator.decorate(world, chunk, random);
		}
	}
}
