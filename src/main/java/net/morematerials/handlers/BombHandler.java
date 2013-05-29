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
	private int bombSize = 4;
	private int bombDelay = 2;
	private boolean setFire = false;
	private boolean showMessage = false;
	private String message = " ";

	public void init(MoreMaterials plugin) {
		this.plugin = plugin;
	}

	public void shutdown() {
	}

	@Override
	public void onActivation(Event event, Map<String, Object> config) {
		
		if (!(event instanceof PlayerInteractEvent)) {  //Always do this.
			return;
		}
		// Setup Player Environment
		PlayerInteractEvent playerEvent = (PlayerInteractEvent) event;    	

		// Setup Player Environment if we got here.       
		Player sPlayer = playerEvent.getPlayer();   

		// Check Player Permissions
		if (!sPlayer.hasPermission("morematerials.handlers.bomb")) {
			return;
		}

		if (config.containsKey("bombsize")) {
			bombSize = (Integer) config.get("bombsize");	
		} 

		if (config.containsKey("delay")) {
			bombDelay = (Integer) config.get("delay");	
		}

		if (config.containsKey("showmessage")) {
			showMessage = (Boolean) config.get("showmessage");	
		}

		if (config.containsKey("message")) {
			message = (String) config.get("message");	
		}
		
		if (config.containsKey("setfire")) {
			setFire = (Boolean) config.get("setfire");	
		}

		// Prevent Dumb User Mistakes
		if (bombSize < 1 || bombSize >51) {
			return;
		}
		
		if (bombDelay < 1 || bombSize > 60) {
			return;
		}

		final SpoutBlock block = (SpoutBlock)playerEvent.getClickedBlock();

		if (block == null) {
			return;
		}

		final Location location = playerEvent.getClickedBlock().getLocation();

		if (showMessage) {
			sPlayer.sendMessage(message);
		}
		
		if (setFire) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {				
					block.getWorld().createExplosion(location, (float)bombSize, true);		
				}
			}, ((long)bombDelay * 20));
		} else {
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {				
					block.getWorld().createExplosion(location, (float)bombSize);		
				}
			}, ((long)bombDelay * 20));
		}
	}
}