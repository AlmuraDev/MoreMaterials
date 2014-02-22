package net.morematerials.handlers;

import java.util.Map;

import net.morematerials.MoreMaterials;
import net.morematerials.handlers.GenericHandler;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.getspout.spout.block.SpoutCraftBlock;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;

public class ChestHandler extends GenericHandler {
	
	public void init(MoreMaterials plugin) {}

	public void shutdown() {}

	@Override
	public void onActivation(Event event, Map<String, Object> config) {
		// Setup Player Environment
		PlayerInteractEvent playerEvent = (PlayerInteractEvent) event;    	

		// Setup Player Environment if we got here.
		final SpoutPlayer sPlayer = (SpoutPlayer) playerEvent.getPlayer();   

		// Check Player Permissions
		if (!sPlayer.hasPermission("morematerials.handlers.chest")) {
			return;
		}        		

		Block block = playerEvent.getClickedBlock();
		ClaimedResidence res = Residence.getResidenceManager().getByLoc(block.getLocation());
		if (res != null) {
			if (res.getPermissions().playerHas(sPlayer.getName(),"container", true)) {				
				if (block != null) {		
					try {
						// Its possible for a class cast exception to be thrown if part of the custom block data is missing, fixed this by ignoring the exception
						Chest chest = (Chest)block.getState();
						if (chest != null) {
							Inventory inv = chest.getBlockInventory();
							if (inv != null) {
								sPlayer.openInventory(inv);
							}
						}	
					} catch (Exception exception) {
						// Catch Class Cast Exception and ignore it
						return;
					} 

				}
			}
		}
	}
}
