package net.morematerials.handlers;
import java.util.Map;

import net.morematerials.MoreMaterials;
import net.morematerials.handlers.GenericHandler;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.getspout.spout.inventory.SimpleMaterialManager;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.material.block.GenericCustomBlock;

public class RotateHandler extends GenericHandler {
	

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
        if (!sPlayer.hasPermission("morematerials.handlers.rotate")) {
        	return;
        }
        
		if (!(event instanceof PlayerInteractEvent) && ((String) config.get("eventType")).startsWith("Hold")) {
			return;
		}
				
		Block block = playerEvent.getClickedBlock();
		
		if (block == null) {
			return;
		}
		
		block.setData((byte) (block.getData() + 1));
		playerEvent.getPlayer().sendMessage("Block now has Data: " + block.getData());
		
		if (block instanceof GenericCustomBlock) {
			CustomBlock customBlock = (CustomBlock) block;
			if (customBlock.canRotate()) {
				((SimpleMaterialManager) SpoutManager.getMaterialManager()).overrideBlock(block, customBlock, block.getData());
				playerEvent.getPlayer().sendMessage("Block now has Data: " + block.getData());
			}
		}
	}
	
}
