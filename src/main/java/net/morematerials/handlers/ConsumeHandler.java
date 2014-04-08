package net.morematerials.handlers;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.morematerials.MoreMaterials;
import net.morematerials.handlers.GenericHandler;

/* ConsumeFoodHandler
 * Author: Dockter, AlmuraDev � 2014
 * Version: 1.6
 * Updated: 4/1/2014
 */

public class ConsumeHandler extends GenericHandler {

	private boolean consumeItem, usedItem = false;
	private boolean playerFeedback = false;
	private int foodChange = 0;
	private int saturationChange = 0;
	private int healthChange = 0;
	private int oxygenChange = 0;
	private String itemName = " ";
	private String itemType = " ";
	private String additionalMessage = " ";
	
	
	@Override
	public void init(MoreMaterials arg0) {		
		// Nothing to do here.
	}
       
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
        ItemStack itemToConsume = sPlayer.getInventory().getItemInHand();
         
        // Check Player Permissions
        if (!sPlayer.hasPermission("morematerials.handlers.consume")) {
        	return;
        }
        // Pull Configuration Options              
        if (config.containsKey("consumeItem")) {
        	consumeItem = (Boolean) config.get("consumeItem");
        } else {
        	consumeItem = false;
        }
        
        if (config.containsKey("playerFeedback")) {
        	playerFeedback = (Boolean) config.get("playerFeedback");	
        } else {
        	playerFeedback = false;
        }
        
        if (config.containsKey("foodChange")) {
        	foodChange = (Integer) config.get("foodChange");	
        } else {
        	foodChange = 0;
        }
        
        if (config.containsKey("saturationChange")) {
        	saturationChange = (Integer) config.get("saturationChange");	
        } else {
        	saturationChange = 0;
        }
                
        if (config.containsKey("healthChange")) {
        	healthChange = (Integer) config.get("healthChange");	
        } else {
        	healthChange = 0;
        }
        
        if (config.containsKey("oxygenChange")) {
        	oxygenChange = (Integer) config.get("oxygenChange");	
        } else {
        	oxygenChange = 0;
        }
        
        if (config.containsKey("itemType")) {
        	itemType = (String) config.get("itemType");	
        } else {
        	itemType = "";
        }
        
        if (config.containsKey("itemName")) {
        	itemName = (String) config.get("itemName");	
        } else {
        	itemName = "";
        }
        
        if (config.containsKey("additionalMessage")) {
        	additionalMessage = (String) config.get("additionalMessage");	
        } else {
        	additionalMessage = "";
        }    
        
        // Consume Food Item [Food Level+]
        if (foodChange > 0) {
        	// Check users food level, return if already 20.
        	if (sPlayer.getFoodLevel() != 20) {
        		if (sPlayer.getFoodLevel()+foodChange > 20) {
        			sPlayer.setFoodLevel(20);
        		} else {
        			sPlayer.setFoodLevel(sPlayer.getFoodLevel()+foodChange);        			
        		}
        		usedItem = true;
        		FoodLevelChangeEvent expEvent = new FoodLevelChangeEvent(sPlayer, (int) sPlayer.getFoodLevel());  
        		Bukkit.getPluginManager().callEvent(expEvent); // Must call the event since setFoodLevel doesn't.
        	}
        }
        
     // Consume Food Item [Saturaton+]
        if (saturationChange > 0) {
        	// Check users food level, return if already 20.
        	if (sPlayer.getSaturation() != 20) {
        		if (sPlayer.getSaturation()+(float)saturationChange > 20) {
        			sPlayer.setSaturation(20);
        		} else {
        			sPlayer.setSaturation(sPlayer.getSaturation()+(float)saturationChange);
        		}
        		usedItem = true;
        	} 
        }

        // Consume Food Item [Food Level-]
        if (foodChange < 0) {
        	// Check users food level, return if already 0.
        	if (sPlayer.getFoodLevel() != 0) {
        		if (sPlayer.getFoodLevel()+foodChange < 0) {
        			sPlayer.setFoodLevel(0);
        		} else {
        			sPlayer.setFoodLevel(sPlayer.getFoodLevel()-foodChange);
        		}
        		usedItem = true;
        		FoodLevelChangeEvent expEvent = new FoodLevelChangeEvent(sPlayer, (int) sPlayer.getFoodLevel());
        		Bukkit.getPluginManager().callEvent(expEvent); // Must call the event since setFoodLevel doesn't.
        	} 
        }
        
        // Consume Food Item [Saturaton-]
        if (saturationChange < 0) {
        	// Check users food level, return if already 0.
        	if (sPlayer.getSaturation() != 0) {
        		if (sPlayer.getSaturation()+saturationChange < 0) {
        			sPlayer.setSaturation(0);
        		} else {
        			sPlayer.setSaturation(sPlayer.getSaturation()-saturationChange);
        		}
        		usedItem = true;
        		FoodLevelChangeEvent expEvent = new FoodLevelChangeEvent(sPlayer, (int) sPlayer.getFoodLevel());
        		Bukkit.getPluginManager().callEvent(expEvent); // Must call the event since setFoodLevel doesn't.
        	}
        }
        
        // Health Change based on Config.
    	if (healthChange < 0) {
    		if (healthChange < -20) {
    			sPlayer.damage(20);
    			usedItem = true;
    		} else {
    			sPlayer.damage((healthChange*-1));
    			usedItem = true;
    		}    		
    	}
    	
    	if (healthChange > 0) {
    		if ((healthChange+sPlayer.getHealth()) > 20) {
    			sPlayer.setHealth(20);    			
    		} else {
    			sPlayer.setHealth(sPlayer.getHealth()+healthChange);
    		}
    		usedItem = true;
    		EntityRegainHealthEvent expEvent = new EntityRegainHealthEvent(sPlayer, 0, RegainReason.CUSTOM);
    		Bukkit.getPluginManager().callEvent(expEvent); // Must call the event since setHealth doesn't.
    	}
    	
    	  // Oxygen Change based on Config.
    	if (oxygenChange > 0 && sPlayer.getRemainingAir()<300) {
    		if (oxygenChange+sPlayer.getRemainingAir() > 300) {
    			sPlayer.setRemainingAir(300);
    			usedItem = true;
    		} else {
    			usedItem = true;
    			sPlayer.setRemainingAir(sPlayer.getRemainingAir()+oxygenChange);
    		}    		
    	}
    	    	
    	if (oxygenChange < 0 && sPlayer.getRemainingAir()<300) {
    		if ((sPlayer.getRemainingAir()-healthChange) < 0) {
    			sPlayer.setRemainingAir(0);    			
    		} else {
    			sPlayer.setRemainingAir(sPlayer.getRemainingAir()-oxygenChange);
    		}
    		usedItem = true;
    		EntityRegainHealthEvent expEvent = new EntityRegainHealthEvent(sPlayer, 0, RegainReason.CUSTOM);
    		Bukkit.getPluginManager().callEvent(expEvent); // Must call the event since setHealth doesn't.
    	}
    	

        // Player Feedback        
        if (!itemName.equalsIgnoreCase(" ") && playerFeedback) {
        	if (itemType.equalsIgnoreCase("Food")) {
        		if (consumeItem && usedItem) {
        			sPlayer.sendMessage("You have consumed " + ChatColor.GOLD + itemName + ChatColor.WHITE + ".");
        		}
        	} else {
        		sPlayer.sendMessage("You have consumed " + ChatColor.GOLD + itemName + ChatColor.WHITE + ".");
        	}
        	        	
        }
        
        if (!additionalMessage.equalsIgnoreCase(" ") && playerFeedback) {
    		if (itemType.equalsIgnoreCase("Food")) {
    			if (consumeItem) {
    				sPlayer.sendMessage(additionalMessage);		
    			}
    		} else {
    			sPlayer.sendMessage(additionalMessage);
    		}        	
    	}
        
        // Modify Item quantity in hand that was just consumed
        if (consumeItem && usedItem) {
        	if (!(sPlayer.getGameMode() == GameMode.CREATIVE)) {
        		if (sPlayer.getItemInHand().getAmount() > 1) {        			
        			itemToConsume.setAmount(itemToConsume.getAmount()-1);       			
        		} else {
        			sPlayer.setItemInHand(new ItemStack(Material.AIR));        			
        		}
        	}
        } 
    }
  
    @Override
	public void shutdown() {
		// Nothing to do here but required by handler.		
	}
}