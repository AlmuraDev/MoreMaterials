package net.morematerials.handlers;

import java.util.Map;

import net.morematerials.MoreMaterials;
import net.morematerials.handlers.GenericHandler;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.material.block.GenericCustomBlock;

public class ChestHandler extends GenericHandler {
	

	public void init(MoreMaterials plugin) {
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
		if (!sPlayer.hasPermission("morematerials.handlers.chest")) {
			return;
		}        		

		Block block = playerEvent.getClickedBlock();
		if (block != null) {		
			Chest chest = (Chest)block.getState();
			if (chest != null) {
				Inventory inv = chest.getBlockInventory();
				if (inv != null) {
					sPlayer.openInventory(inv);		
				}
			}		
		}
	}
}
