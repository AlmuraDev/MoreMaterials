package net.morematerials.handlers;

import java.util.Map;

import net.morematerials.MoreMaterials;
import net.morematerials.handlers.GenericHandler;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.util.Vector;
import org.getspout.spoutapi.particle.Particle;
import org.getspout.spoutapi.particle.Particle.ParticleType;

public class HealHandler extends GenericHandler {

	@SuppressWarnings("unused")
	private MoreMaterials plugin;
	private int healAmount = 1;

	public void init(MoreMaterials plugin) {
		this.plugin = plugin;
	}

	public void shutdown() {
	}

	@Override
	public void onActivation(Event event, Map<String, Object> config) {
		
		//Setup Player Environment		
		PlayerInteractEntityEvent playerEntity = (PlayerInteractEntityEvent) event;
					
		// Setup Player Environment if we got here.       
		Player sPlayer = playerEntity.getPlayer();   

		// Check Player Permissions
		if (!sPlayer.hasPermission("morematerials.handlers.heal")) {
			return;
		}
		
		if (config.containsKey("healAmount")) {
			healAmount = (Integer) config.get("healAmount");	
		} 
		
		LivingEntity myTarget = (LivingEntity) playerEntity.getRightClicked();
		
		if (myTarget == null) {
			return;
		}	
		
		if (!(myTarget.getMaxHealth() == myTarget.getHealth())) {
			if ((myTarget.getHealth()+healAmount) > myTarget.getMaxHealth()) {
				myTarget.setHealth(myTarget.getMaxHealth());
			} else {
				myTarget.setHealth(myTarget.getHealth()+healAmount);
			}
			Location loc = myTarget.getLocation();
			Particle healParticle = new Particle(ParticleType.DRIPWATER, loc, new Vector(0.5D, 3.0D, 0.5D));
			healParticle.setParticleBlue(0.0F).setParticleGreen(0.0F).setParticleRed(1.0F);
			healParticle.setMaxAge(40).setAmount(15).setGravity(1.1F);
			healParticle.spawn();
		}
	}
}