/*
 The MIT License

 Copyright (c) 2012 Zloteanu Nichita (ZNickq) and Andre Mohren (IceReaper)

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 */

package net.spoutmaterials.spoutmaterials.cmds;

import java.util.Map;
import java.util.logging.Level;
import net.spoutmaterials.spoutmaterials.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.Material;
import org.getspout.spoutapi.player.SpoutPlayer;

public class GiveExecutor implements CommandExecutor {
	private Main instance;

	public GiveExecutor(Main plugin) {
		instance = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		// Sender has to be a player.
		if (!(sender instanceof Player)) {
			return true;
		}

		// Sender must also submit an object name.
		if (args.length < 1) {
			sender.sendMessage(this.instance.getMessage(command.getUsage(), Level.WARNING));
			return true;
		}

		// Amount of objects to give.
		int amount = 1;
		if (args.length > 1) {
			amount = Integer.parseInt(args[1]);
		}
		
		SpoutPlayer player = (SpoutPlayer) sender;
		Map<String, Material> material = instance.getSmpManager().getMaterial(args[0]);
		
		//Permission check
		if (!instance.hasPermission(sender, "spoutmaterials.give", true)
			&& !instance.hasPermission(sender, "spoutmaterials.give." + args[0], true)
		) {
			return true;
		}
		if (material.isEmpty()) {
			sender.sendMessage(this.instance.getMessage("Material " + args[0] + " not found!", Level.SEVERE));
		} else if (material.size() == 1) {
			for (String key : material.keySet()) {
				player.getInventory().addItem(new SpoutItemStack( material.get(key), amount));
				sender.sendMessage(this.instance.getMessage("You received " + amount + "object(s) of " + args[0] + "."));
			}
		} else {
			sender.sendMessage(this.instance.getMessage("Material " + args[0] + " multiple times found, please specify!", Level.WARNING));
			for (String key : material.keySet()) {
				player.sendMessage(this.instance.getMessage("- " + key));
			}
		}

		return true;
	}
}
