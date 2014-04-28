package net.morematerials.listeners;

import java.util.Random;

import net.morematerials.MoreMaterials;
import net.morematerials.wgen.Decorator;
import net.morematerials.wgen.task.DecoratorThrottler;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class GeneratorListener implements Listener {
	
	private MoreMaterials plugin;
	private static final Random random = new Random();

	public GeneratorListener(MoreMaterials plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event) {		
		if (plugin.getConfig().getBoolean("PopulateNewChunks", false) && event.isNewChunk()) {
			Decorator myOre = this.plugin.getDecoratorRegistry().get("ore_o_bluestone");
			DecoratorThrottler throttler = plugin.getDecorationThrotters().get(event.getWorld());
			if (throttler == null) {
				throttler = plugin.getDecorationThrotters().start(5, event.getWorld());
			} else {
				throttler.offer(myOre, event.getChunk().getX(), event.getChunk().getZ(), random);
			}
		}
	}
	
}