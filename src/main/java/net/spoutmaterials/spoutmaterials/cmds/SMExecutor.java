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

import java.util.ArrayList;
import net.spoutmaterials.spoutmaterials.Main;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SMExecutor implements CommandExecutor {
	private Main plugin;
	private String authors;

	public SMExecutor(Main plugin) {
		this.plugin = plugin;
		this.authors = "";
		ArrayList<String> authors = this.plugin.getDescription().getAuthors();
		for (String author : authors) {
			this.authors += ", " + author;
		}
		this.authors = this.authors.substring(3);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(
				ChatColor.GREEN + "[SpoutMaterials] " +
				ChatColor.YELLOW + "This server is running SpoutMaterials " +
				"v" +plugin.getDescription().getVersion() + "! " +
				"Credits to " + authors + "!"
			);
			return true;
		}
		String first = args[0];
		if (first.equalsIgnoreCase("?") || first.equalsIgnoreCase("help")) {
			sender.sendMessage(ChatColor.GREEN + "SpoutMaterials help page");
			sender.sendMessage(ChatColor.AQUA + "---------------------------------");
			sender.sendMessage(ChatColor.YELLOW + "/sm -> " +
				ChatColor.GOLD + "Basic informations, and help!"
			);
			sender.sendMessage(ChatColor.YELLOW + "/smgive -> " +
				ChatColor.GOLD + "Commands to give any custom material!"
			);
			sender.sendMessage(ChatColor.YELLOW + "/smadmin -> " +
				ChatColor.GOLD + "Administration commands!"
			);
		}
		return true;
	}

}
