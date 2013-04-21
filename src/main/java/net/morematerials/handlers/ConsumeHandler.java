package net.morematerials.handlers;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
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
 * Author: Dockter, AlmuraDev � 2013
 * Version: 1.4
 * Updated: 4/17/2013
 */

public class ConsumeHandler extends GenericHandler {

	private boolean consumeItem = false;
	private boolean playerFeedback = false;
	private int foodChange = 0;
	private int healthChange = 0;
	private int oxygenChange = 0;
	private String itemName = " ";
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
    	 
        // Setup Player Environment
    	PlayerInteractEvent playerEvent = (PlayerInteractEvent) event;	
    	Location location = playerEvent.getClickedBlock().getLocation();
    	
    	// Check Location for typically right clicked item and exit handler if found
    	if (location != null) {
    		Material mat = location.getBlock().getType();
    		if (mat.equals(Material.WOODEN_DOOR) || mat.equals(Material.IRON_DOOR) || mat.equals(Material.TRAP_DOOR) || mat.equals(Material.WOOD_DOOR) || mat.equals(Material.FENCE_GATE) || mat.equals(Material.IRON_DOOR_BLOCK)) {
    			return;
    		}
    	}  	
    	
        // Setup Player Environment if we got here.       
        Player sPlayer = playerEvent.getPlayer();        
        ItemStack itemToConsume = sPlayer.getInventory().getItemInHand();
         
        // Pull Configuration Options              
        consumeItem = (Boolean) config.get("consumeItem");
        playerFeedback = (Boolean) config.get("playerFeedback");
        foodChange = (Integer) config.get("foodChange");        
        healthChange = (Integer) config.get("healthChange");
        oxygenChange = (Integer) config.get("oxygenChange");
        itemName = (String) config.get("itemName");
        additionalMessage = (String) config.get("additionalMessage");
                    
        // Consume Food Item
        if (foodChange > 0) {
        	// Check users food level, return if already 20.
        	if (sPlayer.getFoodLevel() != 20) {
        		if (sPlayer.getFoodLevel()+foodChange > 20) {
        			sPlayer.setFoodLevel(20);
        		} else {
        			sPlayer.setFoodLevel(sPlayer.getFoodLevel()+foodChange);        	
        		}

        		FoodLevelChangeEvent expEvent = new FoodLevelChangeEvent(sPlayer, (int) sPlayer.getFoodLevel());  
        		Bukkit.getPluginManager().callEvent(expEvent); // Must call the event since setFoodLevel doesn't.
        	}
        }

        if (foodChange < 0) {
        	// Check users food level, return if already 0.
        	if (sPlayer.getFoodLevel() != 0) {
        		if (sPlayer.getFoodLevel()+foodChange < 0) {        		
        			sPlayer.setFoodLevel(0);
        		} else {
        			sPlayer.setFoodLevel(sPlayer.getFoodLevel()-foodChange);        	
        		}

        		FoodLevelChangeEvent expEvent = new FoodLevelChangeEvent(sPlayer, (int) sPlayer.getFoodLevel());  
        		Bukkit.getPluginManager().callEvent(expEvent); // Must call the event since setFoodLevel doesn't.
        	}
        }

        
        // Health Change based on Config.
    	if (healthChange < 0) {
    		if (healthChange < -20) {
    			sPlayer.damage(20);
    		} else {
    			sPlayer.damage((healthChange*-1));
    		}    		
    	}
    	
    	if (healthChange > 0) {
    		if ((healthChange+sPlayer.getHealth()) > 20) {
    			sPlayer.setHealth(20);    			
    		} else {
    			sPlayer.setHealth(sPlayer.getHealth()+healthChange);
    		}
    		EntityRegainHealthEvent expEvent = new EntityRegainHealthEvent(sPlayer, 0, RegainReason.CUSTOM);
    		Bukkit.getPluginManager().callEvent(expEvent); // Must call the event since setHealth doesn't.
    	}
    	
    	  // Oxygen Change based on Config.
    	if (oxygenChange > 0 && sPlayer.getRemainingAir()<300) {
    		if (oxygenChange+sPlayer.getRemainingAir() > 300) {
    			sPlayer.setRemainingAir(300);
    		} else {
    			sPlayer.setRemainingAir(sPlayer.getRemainingAir()+oxygenChange);
    		}    		
    	}
    	    	
    	if (oxygenChange < 0 && sPlayer.getRemainingAir()<300) {
    		if ((sPlayer.getRemainingAir()-healthChange) < 0) {
    			sPlayer.setRemainingAir(0);    			
    		} else {
    			sPlayer.setRemainingAir(sPlayer.getRemainingAir()-oxygenChange);
    		}
    		EntityRegainHealthEvent expEvent = new EntityRegainHealthEvent(sPlayer, 0, RegainReason.CUSTOM);
    		Bukkit.getPluginManager().callEvent(expEvent); // Must call the event since setHealth doesn't.
    	}
    	

        // Player Feedback        
        if (!itemName.equalsIgnoreCase(" ") && playerFeedback) {
        	sPlayer.sendMessage("You have consumed " + ChatColor.GOLD + itemName + ChatColor.WHITE + ".");        	
        }
        
        if (!additionalMessage.equalsIgnoreCase(" ") && playerFeedback) {
    		sPlayer.sendMessage(additionalMessage);
    	}
        
        // Modify Item quantity in hand that was just consumed
        if (consumeItem) {
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