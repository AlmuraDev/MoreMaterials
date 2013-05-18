package net.morematerials.handlers;

import java.util.Map;

import net.morematerials.MoreMaterials;
import net.morematerials.handlers.GenericHandler;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import org.getspout.spoutapi.particle.Particle;
import org.getspout.spoutapi.particle.Particle.ParticleType;

public class HealHandler extends GenericHandler {

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
		if (!sPlayer.hasPermission("morematerials.handlers.heal")) {
			return;
		}

		// Setting player health
		if (sPlayer.getHealth() == 20) {
			return;
		} else {
			if (sPlayer.getHealth() >= 15) {
				sPlayer.setHealth(20);
			} else {
				sPlayer.setHealth(sPlayer.getHealth() + 5);
			}

			// Playing red drip particles on player location
			Location loc = sPlayer.getLocation();
			Particle healParticle = new Particle(ParticleType.DRIPWATER, loc, new Vector(0.5D, 3.0D, 0.5D));
			healParticle.setParticleBlue(0.0F).setParticleGreen(0.0F).setParticleRed(1.0F);
			healParticle.setMaxAge(40).setAmount(15).setGravity(1.1F);
			healParticle.spawn();
		}

	}

}