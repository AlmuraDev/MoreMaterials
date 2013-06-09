package net.morematerials.handlers;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.material.MaterialData;
import org.getspout.spoutapi.inventory.SpoutItemStack;

import net.morematerials.MoreMaterials;
import net.morematerials.handlers.GenericHandler;

/* ItemReturnHandler
 * Author: Dockter, AlmuraDev � 2013
 * Version: 1.0
 * Updated: 6/8/2013
 */

public class ItemReturnHandler extends GenericHandler {

	private int quantity_a = 0;	
	private int quantity_b = 0;
	private int quantity_c = 0;
	private final boolean showMessage = true;
	private String itemName_a = " ";
	private String itemName_b = " ";
	private String itemName_c = " ";
	private String message = " ";
	@Override
	public void init(MoreMaterials arg0) {		
		// Nothing to do here.
	}

	@Override
	public void onActivation(Event event, Map<String, Object> config) {		
		
		// Setup Player Environment
		BlockBreakEvent blockBreakEvent = (BlockBreakEvent) event;

		// Setup Player Environment if we got here.       
		Player sPlayer = blockBreakEvent.getPlayer();        

		// Check Player Permissions
		if (!sPlayer.hasPermission("morematerials.handlers.itemreturn")) {
			return;
		}
		// Pull Configuration Options             

		if (config.containsKey("quantity-a")) {
			quantity_a = (Integer) config.get("quantity-a");	
		} else {
			quantity_a = 1;
		}
		
		if (config.containsKey("quantity-b")) {
			quantity_b = (Integer) config.get("quantity-b");	
		} else {
			quantity_b = 1;
		}

		if (config.containsKey("quantity-c")) {
			quantity_c = (Integer) config.get("quantity-c");	
		} else {
			quantity_c = 1;
		}
		
		if (config.containsKey("itemName-a")) {
			itemName_a = (String) config.get("itemName-a");	
		} else {
			itemName_a = "";
		}
		
		if (config.containsKey("itemName-b")) {
			itemName_b = (String) config.get("itemName-b");	
		} else {
			itemName_b = "";
		}
		
		if (config.containsKey("itemName-c")) {
			itemName_c = (String) config.get("itemName-c");	
		} else {
			itemName_c = "";
		}

		if (config.containsKey("message")) {
			message = (String) config.get("message");	
		} else {
			message = "";
		}
		
		Block block  = blockBreakEvent.getBlock();
		
		if (itemName_a != null) {
			final org.getspout.spoutapi.material.Material customMaterial = MaterialData.getCustomItem(itemName_a);
			if (customMaterial == null) {
				final Material material = Material.getMaterial(itemName_a.toUpperCase());
				if (material != null) {			
					final ItemStack stack = new ItemStack(material, quantity_a);
					block.getWorld().dropItemNaturally(block.getLocation(), stack);
				}
			} else {
				final SpoutItemStack spoutStack = new SpoutItemStack(customMaterial, quantity_a);
				block.getWorld().dropItemNaturally(block.getLocation(), spoutStack);
			}
		}

		if (itemName_b != null) {
			final org.getspout.spoutapi.material.Material customMaterial = MaterialData.getCustomItem(itemName_b);
			if (customMaterial == null) {
				final Material material = Material.getMaterial(itemName_b.toUpperCase());
				if (material != null) {			
					final ItemStack stack = new ItemStack(material, quantity_b);
					block.getWorld().dropItemNaturally(block.getLocation(), stack);
				}
			} else {
				final SpoutItemStack spoutStack = new SpoutItemStack(customMaterial, quantity_b);
				block.getWorld().dropItemNaturally(block.getLocation(), spoutStack);
			}
		}
		
		if (itemName_c != null) {
			final org.getspout.spoutapi.material.Material customMaterial = MaterialData.getCustomItem(itemName_c);
			if (customMaterial == null) {
				final Material material = Material.getMaterial(itemName_c.toUpperCase());
				if (material != null) {			
					final ItemStack stack = new ItemStack(material, quantity_c);
					block.getWorld().dropItemNaturally(block.getLocation(), stack);
				}
			} else {
				final SpoutItemStack spoutStack = new SpoutItemStack(customMaterial, quantity_c);
				block.getWorld().dropItemNaturally(block.getLocation(), spoutStack);
			}
		}
		
		// Player Feedback        
		if (!message.equalsIgnoreCase(" ") && showMessage) {        	
			sPlayer.sendMessage(message);        	     	        	
		} else {

		}
	}

	@Override
	public void shutdown() {
		// Nothing to do here but required by handler.		
	}
}