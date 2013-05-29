package net.morematerials.handlers;

import java.util.Map;

import net.morematerials.MoreMaterials;
import net.morematerials.handlers.GenericHandler;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class FireBallHandler extends GenericHandler {
	
	@SuppressWarnings("unused")
	private MoreMaterials plugin;
	private int yieldSize = 1;
	private boolean causeFire = false;

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
        if (!sPlayer.hasPermission("morematerials.handlers.fireball")) {
        	return;
        }
        
        if (config.containsKey("yieldsize")) {
			yieldSize = (Integer) config.get("yieldsize");	
		} 
        
        if (config.containsKey("causefire")) {
        	causeFire = (Boolean) config.get("causefire");	
		} 
        
        if (yieldSize < 1 || yieldSize > 100) {
        	return;
        }
        
        Vector direction = sPlayer.getEyeLocation().getDirection().multiply(2);
		Fireball fireball = (Fireball)sPlayer.getWorld().spawn(sPlayer.getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()), Fireball.class);
		fireball.setShooter(sPlayer);
		fireball.setVelocity(direction.multiply(0.25D));
		fireball.setYield((float)yieldSize);
		fireball.setIsIncendiary(causeFire);
	}
	
}