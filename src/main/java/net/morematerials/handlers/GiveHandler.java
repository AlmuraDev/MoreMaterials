package net.morematerials.handlers;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.MaterialData;

import net.morematerials.MoreMaterials;
import net.morematerials.handlers.GenericHandler;

/* GiveHandler
 * Author: Dockter, AlmuraDev � 2013
 * Version: 1.0
 * Updated: 5/28/2013
 */

public class GiveHandler extends GenericHandler {

	private int quantity = 0;	
	private final boolean showMessage = true;
	private String itemName = " ";	
	private String message = " ";
	private ItemStack returnItem = null;


	@Override
	public void init(MoreMaterials arg0) {		
		// Nothing to do here.
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onActivation(Event event, Map<String, Object> config) {		
		if (!(event instanceof PlayerInteractEvent)) {  //Always do this.
			return;
		}

		switch (((PlayerInteractEvent) event).getAction()) {		
		case RIGHT_CLICK_BLOCK:
			// Exit this method if player clicking on chest, door, button, etc.
			switch (((PlayerInteractEvent) event).getClickedBlock().getType()) {
			case CHEST:
			case WOOD_BUTTON:
			case STONE_BUTTON:
			case WOOD_DOOR:
			case IRON_DOOR:
			case IRON_DOOR_BLOCK:
			case FENCE_GATE:
			case BREWING_STAND:
			case FURNACE:
			case BURNING_FURNACE:
			case WOODEN_DOOR:
			case DISPENSER:
				return;
			default:
				break;
			}
		default:
			break;
		}

		// Setup Player Environment
		PlayerInteractEvent playerEvent = (PlayerInteractEvent) event;

		// Setup Player Environment if we got here.       
		Player sPlayer = playerEvent.getPlayer();        

		// Check Player Permissions
		if (!sPlayer.hasPermission("morematerials.handlers.give")) {
			return;
		}
		// Pull Configuration Options             

		if (config.containsKey("quantity")) {
			quantity = (Integer) config.get("quantity");	
		} else {
			quantity = 1;
		}

		if (config.containsKey("itemName")) {
			itemName = (String) config.get("itemName");	
		} else {
			itemName = "";
		}

		if (config.containsKey("message")) {
			message = (String) config.get("message");	
		} else {
			message = "";
		}

		Block block  = (Block) sPlayer.getLocation().getBlock(); 

		if (!itemName.isEmpty()) {
			final org.getspout.spoutapi.material.Material customMaterial = MaterialData.getCustomItem(itemName);
			if (customMaterial == null) {
				final Material material = Material.getMaterial(itemName.toUpperCase());
				if (material != null) {			
					final ItemStack stack = new ItemStack(material, quantity);
					Inventory playerInvt = sPlayer.getInventory();
					if (playerInvt != null) {				
						if (playerInvt.firstEmpty() == -1) {		
							block.getWorld().dropItemNaturally(block.getLocation(), stack);
						} else {
							playerInvt.addItem(stack);
						}
						sPlayer.updateInventory();
					}
				}
			} else {
				final SpoutItemStack spoutStack = new SpoutItemStack(customMaterial, quantity);
				Inventory playerInvt = sPlayer.getInventory();
				if (playerInvt != null) {				
					if (playerInvt.firstEmpty() == -1) {		
						block.getWorld().dropItemNaturally(block.getLocation(), spoutStack);
					} else {
						playerInvt.addItem(spoutStack);
					}
					sPlayer.updateInventory();
				}
			}			
		}

		// Player Feedback        
		if (!message.equalsIgnoreCase(" ") && showMessage && returnItem != null) {        	
			sPlayer.sendMessage(message);        	     	        	
		} else {

		}
	}

	@Override
	public void shutdown() {
		// Nothing to do here but required by handler.		
	}
}