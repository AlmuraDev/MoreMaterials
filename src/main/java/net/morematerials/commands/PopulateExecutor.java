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
package net.morematerials.commands;

import java.util.Random;

import net.morematerials.MoreMaterials;
import net.morematerials.wgen.Decorator;
import net.morematerials.wgen.task.DecoratorThrottler;
import net.morematerials.wgen.ore.CustomOreDecorator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PopulateExecutor implements CommandExecutor {
	
	private MoreMaterials plugin;
	private static final Random random = new Random();
	
	public PopulateExecutor(MoreMaterials plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// Command Structure
		// /mmpopulate intRadius CustomOreName ReplaceBlock
		
		
		
		// This command is only for players
		if (!(sender instanceof Player)) {
			return false;
		}
		
		// Material Verification
		org.getspout.spoutapi.material.Material material = null;
		if (args[1].matches("^[0-9]+$")) {
			// Invalid CustomBlock ID, cannot be numeric
			//material = this.plugin.getSmpManager().getMaterial(Integer.parseInt(args[0]));
		} else {
			String matString = args[1];		
			material = this.plugin.getSmpManager().getMaterial(matString);			
			if (material == null) {
				return false;
				// Invalid Material Specified
				// Send Player Feedback
			}
		}
		
		Location myLocation = ((Player)sender).getLocation();
		int chunkX = myLocation.getChunk().getX();
		int chunkZ = myLocation.getChunk().getZ();
		int radius = Integer.parseInt(args[0]);	
		
		
		// For Loop for Range		
		Decorator myOre = this.plugin.getDecoratorRegistry().get(args[1]);		
		if (myOre != null && myOre instanceof CustomOreDecorator) {
			// Tracking
			((CustomOreDecorator)myOre).chunkCount = 0;
			((CustomOreDecorator)myOre).generatedOre = 0;

			//((CustomOreDecorator)myOre).replace(Material.STONE, Material.AIR);
			// Set replacement ore type.
			((CustomOreDecorator)myOre).replace(Material.STONE);

			DecoratorThrottler throttler = plugin.getDecorationThrotters().get(myLocation.getWorld());
			if (throttler == null) {
				throttler = plugin.getDecorationThrotters().start(5, myLocation.getWorld());
			}

			// Should replace the ore in the chunk you are standing in.
			for (int x = -radius; x < radius; x++) {
				for (int j = -radius; j < radius; j++) {
					int offsetX = chunkX+x;
					int offsetZ = chunkZ+j;
					throttler.offer(myOre, offsetX, offsetZ, random);
				}
			}			
			sender.sendMessage("Generated: " + ((CustomOreDecorator)myOre).generatedOre + " of: " + args[1] + " within: " + ((CustomOreDecorator)myOre).chunkCount + " chunks.");
		} else {
			sender.sendMessage("The specified ore could not be located within the ore decorator");
			sender.sendMessage("[0] = " + args[0] + " [1] = " + args[1] + " [2] = " + args[2]);
		}
		
		// Also this command is only useable by players with permission
		if (!this.plugin.getUtilsManager().hasPermission(sender, "morematerials.admin")) {
			return false;
		}
			
		
		
		//myOre.de
		return true;
	}

}
