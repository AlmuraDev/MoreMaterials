package net.morematerials.handlers;

import java.lang.reflect.Field;
import java.util.Map;

import net.morematerials.MoreMaterials;
import net.morematerials.handlers.GenericHandler;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkEvent;
import org.getspout.spout.block.SpoutCraftChunk;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.SpoutWorld;
import org.getspout.spoutapi.chunkstore.SimpleChunkDataManager;
import org.getspout.spoutapi.player.SpoutPlayer;

public class ChunkRegenerateHandler extends GenericHandler {
	
	@SuppressWarnings("unused")
	private MoreMaterials plugin;
	private String message = "[MoreMaterials] - Chunk Regenerated.";
	
	public void init(MoreMaterials plugin) {
		this.plugin = plugin;
	}

	public void shutdown() {
	}

	@Override
	public void onActivation(Event event, Map<String, Object> config) {
		  // Setup Player Environment
    	PlayerInteractEvent playerEvent = (PlayerInteractEvent) event;    	
    	
        // Setup Player Environment if we got here.       
        SpoutPlayer sPlayer = (SpoutPlayer) playerEvent.getPlayer();   
		
        // Check Player Permissions
        if (!sPlayer.hasPermission("morematerials.handlers.chunkregen")) {
        	return;
        }
       
        SpoutWorld world = new SpoutWorld(sPlayer.getWorld());        
        world.regenerateChunk(sPlayer.getLocation().getChunk().getX(), sPlayer.getLocation().getChunk().getZ());
        	
        sPlayer.sendMessage(message);
	}
	
}