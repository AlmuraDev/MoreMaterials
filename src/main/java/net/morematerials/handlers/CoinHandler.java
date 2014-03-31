package net.morematerials.handlers;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import net.milkbowl.vault.economy.Economy;
import net.morematerials.MoreMaterials;
import net.morematerials.handlers.GenericHandler;

/* CoinHandler
 * Author: Dockter, AlmuraDev � 2014
 * Version: 1.0
 * Updated: 3/23/2014
 */

public class CoinHandler extends GenericHandler {

	private static Economy economy;
	private boolean consumeItem = false;
	private boolean playerFeedback = false;
	private Double coinValue = 0.0;	
	private String coinName = " ";
	private String coinType = " ";
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
    	
        // Setup Player Environment if we got here.       
        Player sPlayer = playerEvent.getPlayer();        
        ItemStack itemToConsume = sPlayer.getInventory().getItemInHand();
         
        // Check Player Permissions & whether or not player is in creative, prevent usage
        if (!sPlayer.hasPermission("morematerials.handlers.coin") || sPlayer.getGameMode() == GameMode.CREATIVE) {
        	return;
        }
        
        economy.depositPlayer(sPlayer.getName(), coinValue);
        
        // Pull Configuration Options              
        if (config.containsKey("coinValue")) {
        	coinValue = (Double) config.get("coinValue");
        } else {
        	coinValue = 0.0;
        }
       
        // Modify Item quantity in hand that was just consumed

        if (sPlayer.getItemInHand().getAmount() > 1) {        			
        	itemToConsume.setAmount(itemToConsume.getAmount()-1);       			
        } else {
        	sPlayer.setItemInHand(new ItemStack(Material.AIR));        			
        }


    }
  
    @Override
	public void shutdown() {
		// Nothing to do here but required by handler.		
	}
}