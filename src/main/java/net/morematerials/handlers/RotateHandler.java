/*
 * This file is part of MoreMaterials, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2013 AlmuraDev <http://www.almuradev.com/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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

	@SuppressWarnings("deprecation")
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
