package net.morematerials.handlers;

import java.lang.reflect.Method;
import java.util.Map;

import net.morematerials.MoreMaterials;
import net.morematerials.handlers.GenericHandler;
import net.morematerials.materials.CustomMaterial;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.material.Material;

public class PlaySoundHandler extends GenericHandler {
	
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
        final Player sPlayer = playerEvent.getPlayer();   
		
        // Check Player Permissions
        if (!sPlayer.hasPermission("morematerials.handlers.playsound")) {
        	return;
        }
		Material material = this.plugin.getSmpManager().getMaterial((Integer) config.get("materialId"));
		
		// We can safely cast, because this event is only triggered for MoreMaterials materials!
		String smpName = ((CustomMaterial) material).getSmpName();

		// Default location is the world spawn.
		Location location = this.plugin.getServer().getWorlds().get(0).getSpawnLocation();
		try {
			Method method = event.getClass().getMethod("getPlayer");
			Object player = method.invoke(event);
			if (player instanceof Player) {
				location = ((Player) player).getLocation();
			}
		} catch (Exception exception) {
		}

		// Now play the sound!
		String[] sound = ((String) config.get("Sound")).split("/");
		if (sound.length == 1) {
			SpoutManager.getSoundManager().playGlobalCustomSoundEffect(this.plugin, smpName + "_" + sound[0], false, location, 32, 100);
		} else {
			SpoutManager.getSoundManager().playGlobalCustomSoundEffect(this.plugin, sound[0] + "_" + sound[1], false, location, 32, 100);
		}
	}
	
}
