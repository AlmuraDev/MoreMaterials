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
        Vector direction = sPlayer.getEyeLocation().getDirection().multiply(2);
		Fireball fireball = (Fireball)sPlayer.getWorld().spawn(sPlayer.getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()), Fireball.class);
		fireball.setShooter(sPlayer);
		fireball.setVelocity(direction.multiply(0.25D));
		fireball.setYield(7.0F);
		fireball.setIsIncendiary(false);
	}
	
}