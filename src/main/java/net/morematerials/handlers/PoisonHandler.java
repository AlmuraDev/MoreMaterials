package net.morematerials.handlers;

import java.util.Map;

import net.morematerials.MoreMaterials;
import net.morematerials.handlers.GenericHandler;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.getspout.spoutapi.particle.Particle;
import org.getspout.spoutapi.particle.Particle.ParticleType;

public class PoisonHandler extends GenericHandler {
	
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
        final Player sPlayer = playerEvent.getPlayer();   
		
        // Check Player Permissions
        if (!sPlayer.hasPermission("morematerials.handlers.poison")) {
        	return;
        }
              
        // Add poison effect to player
  		sPlayer.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 100));
  		
  		// Playing green drip particles on player location
  		Location loc = sPlayer.getLocation();
  		Particle poisonParticle = new Particle(ParticleType.DRIPWATER, loc, new Vector(0.5D, 3.0D, 0.5D));
  		poisonParticle.setParticleBlue(0.0F).setParticleGreen(1.0F).setParticleRed(0.0F);
  		poisonParticle.setMaxAge(40).setAmount(15).setGravity(0.9F);
  		poisonParticle.spawn();
  	}             
}	
