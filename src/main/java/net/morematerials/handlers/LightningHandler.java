package net.morematerials.handlers;

import java.util.Map;

import net.morematerials.MoreMaterials;
import net.morematerials.handlers.GenericHandler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import org.getspout.spoutapi.particle.Particle;
import org.getspout.spoutapi.particle.Particle.ParticleType;

public class LightningHandler extends GenericHandler {
	
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
        if (!sPlayer.hasPermission("morematerials.handlers.lightning")) {
        	return;
        }
              
        final Location location = playerEvent.getClickedBlock().getLocation();
              	
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				// Striking lightning and effect on block location
		     	sPlayer.getWorld().strikeLightning(location);
		     	sPlayer.getWorld().strikeLightningEffect(location);
		     		
		     	// Playing yellow explosion particles on 1 above block location
		     	Location loc2 = location;
		     	loc2.setY(loc2.getY() + 1.0D);
		     	Particle smokeParticle = new Particle(ParticleType.EXPLODE, loc2, new Vector(0.5D, 3.0D, 0.5D));
		     	smokeParticle.setParticleBlue(0.0F).setParticleGreen(1.0F).setParticleRed(1.0F);
		     	smokeParticle.setMaxAge(60).setAmount(15).setGravity(1.1F);
		     	smokeParticle.spawn();				
			}
		}, 20L);		
	}	
}