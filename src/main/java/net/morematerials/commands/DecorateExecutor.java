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

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DecorateExecutor implements CommandExecutor {
	private static final Random RANDOM = new Random();
	private MoreMaterials plugin;

	public DecorateExecutor(MoreMaterials plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// Command Structure
		// /mmpopulate intRadius CustomOreName ReplaceBlock
		if (!(sender instanceof Player)) {
			plugin.getLogger().severe("This command is only available to logged in players!");
			return true;
		}

		// Handler triggering of debug mode.
		String myDebug = args[0];
		if (myDebug.equalsIgnoreCase("debug")) {
			if (!plugin.showDebug) {
				sender.sendMessage("[MoreMaterials] - Decorator Debug On");
				plugin.showDebug = true;
			} else {
				plugin.showDebug = false;
				sender.sendMessage("[MoreMaterials] - Decorator Debug Off");
			}
			return true;  // End Command.
		}

		// Setup current location, chunk and radius values.
		final Location myLocation = ((Player) sender).getLocation();
		final int chunkX = myLocation.getChunk().getX();
		final int chunkZ = myLocation.getChunk().getZ();
		final int radius = Integer.parseInt(args[0]);
		
		// Startup Throttler Thread.
		DecoratorThrottler throttler = plugin.getDecorationThrotters().get(myLocation.getWorld());
		if (throttler == null) {
			throttler = plugin.getDecorationThrotters().start(5, myLocation.getWorld());
		}	

		// Single Chunk Generation using all ores in objects.yml (ores)
		if (args[1].equalsIgnoreCase("all") && radius == 0) {			
			for (Decorator myOre : plugin.getDecoratorRegistry().getAll()) {	
				if (myOre instanceof CustomOreDecorator) {
					// Tracking
					((CustomOreDecorator)myOre).toDecorateCount = 0;
					// Set replacement ore type.
					((CustomOreDecorator) myOre).replace(Material.STONE, Material.DIRT, Material.GRAVEL);

					// Calculate chance of decorate for this specific chunk and specific ore type.					
					int rand1 = RANDOM.nextInt(((CustomOreDecorator)myOre).getDecorateChance()) + 1;
					int rand2 = ((CustomOreDecorator)myOre).getDecorateChance();					
					if (rand1 == rand2) {								
						throttler.offer(myOre, chunkX, chunkZ, false);
						((CustomOreDecorator)myOre).toDecorateCount++;
					} else {
						if (plugin.showDebug) {
							//plugin.getLogger().info("Offer to Queue: [" + myOre.getIdentifier() + "] failed chance calculation for manual decorate. Chance: " + rand1 + "/" + rand2);
						}
					}
					if (plugin.showDebug) {
						plugin.getLogger().info("Queue Generation: " + ((CustomOreDecorator) myOre).toDecorateCount + " of: " + myOre.getIdentifier());
					}
				}
			}
		}

		// Single Chunk Generation specified by arg[1]
		if (!args[1].equalsIgnoreCase("all") && radius == 0) {
			System.out.println("Zero Radius Detected");
			Decorator myOre = this.plugin.getDecoratorRegistry().get(args[1]);	
			if (myOre instanceof CustomOreDecorator) {
				// Tracking
				((CustomOreDecorator)myOre).toDecorateCount = 0;
				// Set replacement ore type.
				((CustomOreDecorator) myOre).replace(Material.STONE, Material.DIRT, Material.GRAVEL);

				// Calculate chance of decorate for this specific chunk and specific ore type.
				int rand1 = RANDOM.nextInt(((CustomOreDecorator)myOre).getDecorateChance()) + 1;
				int rand2 = ((CustomOreDecorator)myOre).getDecorateChance();					
				if (rand1 == rand2) {								
					throttler.offer(myOre, chunkX, chunkZ, false);
					((CustomOreDecorator)myOre).toDecorateCount++;
				} else {
					if (plugin.showDebug) {
						//plugin.getLogger().info("Offer to Queue: " + myOre.getIdentifier() + " failed chance calculation for manual decorate. Chance: " + rand1 + "/" + rand2);
					}
				}
				if (plugin.showDebug) {
					plugin.getLogger().info("Queue Generation: " + ((CustomOreDecorator) myOre).toDecorateCount + " of: " + myOre.getIdentifier());
				}
			}

		}

		// Multi-Chunk Generation using all ores within objects.yml (ores)
		if (args[1].equalsIgnoreCase("all") && radius >= 1) {
			for (Decorator myOre : plugin.getDecoratorRegistry().getAll()) {
				if (myOre instanceof CustomOreDecorator) {
					// Tracking
					((CustomOreDecorator)myOre).toDecorateCount = 0;
					// Set replacement ore type.
					((CustomOreDecorator) myOre).replace(Material.STONE, Material.DIRT, Material.GRAVEL);

					// Calculate circular decorate based on current location.
					for (int x = -radius; x < radius; x++) {
						for (int j = -radius; j < radius; j++) {
							int offsetX = chunkX+x;
							int offsetZ = chunkZ+j;

							// Calculate chance of decorate for this specific chunk and specific ore type.
							int rand1 = RANDOM.nextInt(((CustomOreDecorator) myOre).getDecorateChance()) + 1;
							int rand2 = ((CustomOreDecorator)myOre).getDecorateChance();					
							if (rand1 == rand2) {								
								throttler.offer(myOre, offsetX, offsetZ, false);
								((CustomOreDecorator)myOre).toDecorateCount++;
							} else {
								if (plugin.showDebug) {
									//plugin.getLogger().info("Offer to Queue: " + myOre.getIdentifier() + " failed chance calculation for manual populate. Chance: " + rand1 + "/" + rand2);
								}			
							}
						}
					}
					if (plugin.showDebug) {
						plugin.getLogger().info("Queue Generation: " + ((CustomOreDecorator)myOre).toDecorateCount + " of: " + myOre.getIdentifier());
					}
					sender.sendMessage("[MoreMaterials] -  Queue Generation: " + ((CustomOreDecorator)myOre).toDecorateCount + " of: " + myOre.getIdentifier());
				}
			}
		}

		// Multi-Chunk Generation using specified args[1] ore.
		if (!args[1].equalsIgnoreCase("all") && radius >= 1) {
			Decorator myOre = this.plugin.getDecoratorRegistry().get(args[1]);		
			if (myOre instanceof CustomOreDecorator) {
				// Tracking
				((CustomOreDecorator)myOre).toDecorateCount = 0;				
				// Set replacement ore type.
				((CustomOreDecorator) myOre).replace(Material.STONE, Material.DIRT, Material.GRAVEL);

				// Should replace the ore in the chunk you are standing in.
				for (int x = -radius; x < radius; x++) {
					for (int j = -radius; j < radius; j++) {
						int offsetX = chunkX+x;
						int offsetZ = chunkZ+j;
						int rand1 = RANDOM.nextInt(((CustomOreDecorator)myOre).getDecorateChance()) + 1;
						int rand2 = ((CustomOreDecorator)myOre).getDecorateChance();					
						if (rand1 == rand2) {								
							throttler.offer(myOre, offsetX, offsetZ, false);
							((CustomOreDecorator)myOre).toDecorateCount++;
						} else {
							if (plugin.showDebug) {
								//plugin.getLogger().info("Offer to Queue: " + myOre.getIdentifier() + " failed chance calculation for manual decorate. Chance: " + rand1 + "/" + rand2);
							}
						}
					}
				}
				if (plugin.showDebug) {
					plugin.getLogger().info("Queue Generation: " + ((CustomOreDecorator) myOre).toDecorateCount + " of: " + myOre.getIdentifier());
				}
			}
		} 
		return true;
	}
}
