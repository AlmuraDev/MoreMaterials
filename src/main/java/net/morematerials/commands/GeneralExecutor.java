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

import java.util.ArrayList;
import java.util.Map;

import net.morematerials.MoreMaterials;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

public class GeneralExecutor implements CommandExecutor {

	private MoreMaterials plugin;

	public GeneralExecutor(MoreMaterials plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			return this.showInfo(sender);
		}
		
		return this.showHelp(sender, args);
	}

	private boolean showInfo(CommandSender sender) {
		PluginDescriptionFile description = this.plugin.getDescription();

		// Show plugin line.
		sender.sendMessage(ChatColor.RED + description.getName() + ChatColor.GREEN + " v" + description.getVersion());

		// Show authors line.
		sender.sendMessage(ChatColor.WHITE + "Credits to: " + ChatColor.BLUE + StringUtils.join(description.getAuthors(), ", "));

		// Show thanks line.
		//TODO bukkit should have a method to read custom values
		sender.sendMessage(ChatColor.WHITE + "Special Thanks to: " + ChatColor.BLUE + "ZNickq");

		return true;
	}

	private boolean showHelp(CommandSender sender, String[] args) {
		Map<String, Map<String, Object>> commands = this.plugin.getDescription().getCommands();
		ArrayList<String> messages = new ArrayList<String>();

		if (args.length > 1 && commands.containsKey(args[1])) {
			// Show specified command.
			messages.add(ChatColor.RED + "Help page for /" + args[1]);
			messages.add(ChatColor.RED + "---------------------------------");
			for (String use : StringUtils.split((String) commands.get(args[1]).get("usage"), "\n")) {
				use = use.replaceAll("<command>", args[1] + ChatColor.GOLD);
				messages.add(ChatColor.GREEN + use);
			}
		} else {
			// Show all commands.
			messages.add(ChatColor.RED + "Help page");
			messages.add(ChatColor.RED + "---------------------------------");
			// Getting commands from plugin.yml
			for (String commandsEntry : commands.keySet()) {
				messages.add(ChatColor.RED + "/" + commandsEntry);
				messages.add("-> " + ChatColor.GOLD + commands.get(commandsEntry).get("description"));
			}
		}

		// Send all messages.
		for (Integer i = 0; i < messages.size(); i++) {
			sender.sendMessage(messages.get(i));
		}

		return true;
	}
}
