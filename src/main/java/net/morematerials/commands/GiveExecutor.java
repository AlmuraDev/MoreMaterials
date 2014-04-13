/*
 * This file is part of MoreMaterials.
 * 
 * Copyright (c) 2012 Andre Mohren (IceReaper)
 * 
 * The MIT License
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

package net.morematerials.commands;

import net.morematerials.MoreMaterials;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.Material;
import org.getspout.spoutapi.player.SpoutPlayer;

public class GiveExecutor implements CommandExecutor {
	
	private MoreMaterials plugin;

	public GiveExecutor(MoreMaterials plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// This command is only for players
		if (!(sender instanceof Player)) {
			return false;
		}
		
		// Also this command is only useable by players with permission
		if (!this.plugin.getUtilsManager().hasPermission(sender, "morematerials.admin")) {
			return false;
		}
		
		// Show usage on incorrect usage.
		if (args.length == 0) {
			((Player) sender).chat("/mm mmgive");
			return true;
		}
		
		// How many materials should we add?
		Integer amount = args.length > 1 ? Integer.parseInt(args[1]) : 1;
		
		Material material = null;
		if (args[0].matches("^[0-9]+$")) {
			material = this.plugin.getSmpManager().getMaterial(Integer.parseInt(args[0]));
		} else {
			String[] matString = args[0].split("\\.");
			if (matString.length > 1) {
				material = this.plugin.getSmpManager().getMaterial(matString[0], matString[1]);
			}
			if (material == null) {
				material = this.plugin.getSmpManager().getMaterial(matString[0]);
			}
		}
		
		if (material == null) {
			// No material found.
			sender.sendMessage(this.plugin.getUtilsManager().getMessage("No material found for: " + args[0]));
		} else {
			// Material found.
			((SpoutPlayer) sender).getInventory().addItem(new SpoutItemStack(material, amount));
			sender.sendMessage(this.plugin.getUtilsManager().getMessage("You received " + amount + " of " + args[0] + "."));
		}
		
		return true;
	}

}
