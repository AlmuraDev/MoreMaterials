package net.morematerials.handlers;

import java.util.Map;

import net.morematerials.MoreMaterials;
import net.morematerials.handlers.GenericHandler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.getspout.spoutapi.block.SpoutBlock;

public class BombHandler extends GenericHandler {
	
	private MoreMaterials plugin;

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
        Player sPlayer = playerEvent.getPlayer();   
		
        // Check Player Permissions
        if (!sPlayer.hasPermission("morematerials.handlers.bomb")) {
        	return;
        }
       
        final SpoutBlock block = (SpoutBlock)playerEvent.getClickedBlock();
        final Location location = playerEvent.getClickedBlock().getLocation();
        
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				block.getWorld().createExplosion(location, 7.0F);		
			}
		}, 60L);
		
	}
	
}