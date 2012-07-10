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

package net.morematerials.morematerials.manager;

import java.util.logging.Level;

import net.morematerials.morematerials.Main;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UtilsManager {

	private String pluginName;

	public UtilsManager(Main plugin) {
		this.pluginName = "[" + plugin.getDescription().getName() + "] ";
	}

	public boolean hasPermission(CommandSender sender, String perm) {
		// Console is allowed to do everything
		if (!(sender instanceof Player)) {
			return true;
		}

		Player player = (Player) sender;

		// OP is allowed to do everything
		if (player.isOp()) {
			return true;
		}

		// Players with the global permission is allowed to do everything
		if (sender.hasPermission("morematerials.*")) {
			return true;
		}

		// Player is only allowed to do this if he has the permission.
		return player.hasPermission(perm);
	}

	// Generalize all chat output!
	public String getMessage(String logMessage) {
		return this.getMessage(logMessage, Level.INFO);
	}

	public String getMessage(String msg, Level level) {
		if (level == Level.WARNING) {
			return ChatColor.GREEN + this.pluginName + ChatColor.YELLOW + msg;
		} else if (level == Level.SEVERE) {
			return ChatColor.GREEN + this.pluginName + ChatColor.RED + msg;
		}
		return ChatColor.GREEN + this.pluginName + ChatColor.WHITE + msg;
	}

	// Generalize all console output!
	public void log(String logMessage) {
		this.log(logMessage, Level.INFO);
	}

	public void log(String msg, Level level) {
		if (level == Level.WARNING) {
			System.out.println(this.pluginName + "Warning: " + msg);
		} else if (level == Level.SEVERE) {
			System.out.println(this.pluginName + "ERROR: " + msg);
		} else {
			System.out.println(this.pluginName + msg);
		}
	}

}
