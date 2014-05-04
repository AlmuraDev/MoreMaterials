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

import gnu.trove.map.hash.TLongObjectHashMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import net.morematerials.MoreMaterials;
import net.morematerials.wgen.Decorator;
import net.morematerials.wgen.ore.CustomOreDecorator;
import net.morematerials.wgen.thread.MaffThread;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DecorateExecutor implements CommandExecutor {
	private static final Random RANDOM = new Random();
	private MoreMaterials plugin;
	private String par1, par2, par3, par4, par5;
	private boolean canPlace = false;
	private int rand1, rand2, offsetX, offsetZ;
	private Map<UUID, TLongObjectHashMap<List<String>>> alreadyDecorated = new HashMap<>();

	public DecorateExecutor(MoreMaterials plugin) {
		this.plugin = plugin;
	}

	@SuppressWarnings("unused")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// Command Structure
		final World world = ((Player)sender).getWorld();
		// /mmpopulate intRadius CustomOreName OverPopulate
		if (!(sender instanceof Player)) {
			plugin.getLogger().severe("This command is only available to logged in players!");
			return true;
		}

		// Setup command arguments
		try {
			par1 = args[0]; //Command or Radius
			par2 = args[1]; //Ore Type or "All" or chunkCoord X
			par3 = args[2]; //Decorate existing chunks or ChunkCoord Z
		} catch (Exception e) {
			if (plugin.showDebug) {
				System.out.println(e);
				System.out.println("Par1: " + par1 + " Par2: " + par2 + " Par3: " + par3);
			}
		}

		if (par1 == null) {  // par1 cannot be null and continue.
			sender.sendMessage("Invalid command syntax.");
			return false;
		}

		// Handler triggering of debug mode.
		if (par1.equalsIgnoreCase("debug")) {
			if (!plugin.showDebug) {
				sender.sendMessage("[MoreMaterials] - Decorator Debug On");
				plugin.showDebug = true;
			} else {
				plugin.showDebug = false;
				sender.sendMessage("[MoreMaterials] - Decorator Debug Off");
			}
			return true;  // End Command.
		}

		if (par1.equalsIgnoreCase("pause")) {
			if (!plugin.getPlacer().paused) {
				plugin.getPlacer().pause();
				sender.sendMessage("[MoreMaterials] - Block Placer Paused, queue remaining: " + plugin.getPlacer().queue.size());
			} else {
				sender.sendMessage("[MoreMaterials] - Block Placer already Paused");
			}
			return true;  // End Command.
		}

		if (par1.equalsIgnoreCase("resume")) {
			if (plugin.getPlacer().paused) {
				plugin.getPlacer().resume();
				sender.sendMessage("[MoreMaterials] - Block Placer Resumed");
			} else {
				if (plugin.getPlacer().queue.size() > 0) {
					sender.sendMessage("[MoreMaterials] - Block Placer is already running.");
				} else {
					sender.sendMessage("[MoreMaterials] - Block Placer is already running but queue is empty.");
				}
			}
			return true;  // End Command.
		}

		if (par1.equalsIgnoreCase("save")) {
			plugin.save();
			sender.sendMessage("[MoreMaterials] - Saved Processed queue to file system.");
			return true;  // End Command.
		}

		if (par1.equalsIgnoreCase("status")) {			
			if (plugin.getPlacer() != null && plugin.getPlacer().queue != null) {	
				sender.sendMessage("[MoreMaterials] - Block Placer queue remaining: [" + ChatColor.AQUA + plugin.getPlacer().queue.size() + ChatColor.RESET + "].");
				//sender.sendMessage("[MoreMaterials] - Chunk.dat entires: " + plugin.fileSize());
				return true;  // End Command.
			}
		}

		if (par1.equalsIgnoreCase("check")) {
			final int checkX;
			final int checkZ;
			if (par2.equalsIgnoreCase("this") || par2 == null) {
				checkX = ((Player)sender).getLocation().getChunk().getX();
				checkZ = ((Player)sender).getLocation().getChunk().getZ();
			} else {
				checkX = Integer.parseInt(par2);
				checkZ = Integer.parseInt(par3);
			}
			if (plugin.containsAny(((Player)sender).getWorld(), checkX, checkZ)) {
				sender.sendMessage("[MoreMaterials] - Chunk already decorated.");
			} else {
				sender.sendMessage("[MoreMaterials] - Chunk has not been decorated.");
			}
			return true;  // End Command.
		}
		
		if (par1.equalsIgnoreCase("displayores")) {
			for (Decorator myOre : plugin.getDecoratorRegistry().getAll()) {
				sender.sendMessage("[MoreMaterials] - Ore: [" + ChatColor.DARK_AQUA + myOre.getIdentifier() + ChatColor.RESET + "].");
			}
			return true; //End Command.
		}
		
		
		
		// Setup current location, chunk and radius values.
		final Location myLocation = ((Player) sender).getLocation();
		final int chunkX = myLocation.getChunk().getX();
		final int chunkZ = myLocation.getChunk().getZ();
		final int radius = Integer.parseInt(par1);

		// Determine already decorated into our own map.
		final Map<UUID, TLongObjectHashMap<List<String>>> alreadyDecorated = plugin.getWorldsDecorated();
		
		// Startup Maff thread.
		MaffThread thread = plugin.getThreadRegistry().get(myLocation.getWorld());
		if (thread == null) {
			thread = plugin.getThreadRegistry().start(50000, myLocation.getWorld());
		}

		//Filter invalid command syntax.
		if (par2 == null || par3 == null) {
			sender.sendMessage("Invalid command syntax.");
			return false;
		}

		// Single Chunk Generation using all ores in objects.yml (ores)
		if (par2.equalsIgnoreCase("all") && radius == 0) {
			for (Decorator myOre : plugin.getDecoratorRegistry().getAll()) {
				if (myOre instanceof CustomOreDecorator) {
					// Tracking
					((CustomOreDecorator) myOre).toDecorateCount = 0;
					// Set replacement ore type.
					((CustomOreDecorator) myOre).replace(Material.STONE, Material.DIRT, Material.GRAVEL);

					// Calculate chance of decorate for this specific chunk and specific ore type.
					rand1 = RANDOM.nextInt(((CustomOreDecorator) myOre).getDecorateChance()) + 1;
					rand2 = ((CustomOreDecorator) myOre).getDecorateChance();

					if (rand1 == rand2) {
						if (par3.equalsIgnoreCase("true")) {
							canPlace = !plugin.contains(((Player) sender).getWorld(), chunkX, chunkZ, myOre.getIdentifier());
						} else {
							canPlace = !this.hasOres(world, chunkX, chunkZ);
						}
						if (canPlace) {
							thread.offer(myLocation.getWorld(), chunkX, chunkZ, myOre);
							plugin.put(((Player) sender).getWorld(), chunkX, chunkZ, myOre.getIdentifier());
							((CustomOreDecorator) myOre).toDecorateCount++;
						} else {
							if (plugin.showDebug) {
								//plugin.getLogger().info("Offer to Queue: " + myOre.getIdentifier() + " at: " + chunkX + " / " + chunkZ + " is already decorated.");
							}
						}
					} else {
						if (plugin.showDebug) {
							//plugin.getLogger().info("Offer to Queue: [" + myOre.getIdentifier() + "] failed chance calculation for manual decorate. Chance: " + rand1 + "/" + rand2);
						}
					}
					if (((CustomOreDecorator) myOre).toDecorateCount > 0) {
						if (plugin.showDebug) {
							plugin.getLogger().info("Queue Generation: " + ((CustomOreDecorator) myOre).toDecorateCount + " of: " + myOre.getIdentifier());
						}
						sender.sendMessage("[MoreMaterials] - Queued Generation of: [" + ChatColor.AQUA + ((CustomOreDecorator) myOre).toDecorateCount + ChatColor.RESET + "] chunk(s) of: [" + ChatColor.DARK_AQUA + myOre.getIdentifier() + ChatColor.RESET + "].");
					}
				}
			}
		}

		// Single Chunk Generation specified by arg[1]
		if (!par2.equalsIgnoreCase("all") && radius == 0) {
			System.out.println("Zero Radius Detected");
			Decorator myOre = this.plugin.getDecoratorRegistry().get(par2);
			if (myOre instanceof CustomOreDecorator) {
				// Tracking
				((CustomOreDecorator) myOre).toDecorateCount = 0;
				// Set replacement ore type.
				((CustomOreDecorator) myOre).replace(Material.STONE, Material.DIRT, Material.GRAVEL);

				// Calculate chance of decorate for this specific chunk and specific ore type.
				rand1 = RANDOM.nextInt(((CustomOreDecorator) myOre).getDecorateChance()) + 1;
				rand2 = ((CustomOreDecorator) myOre).getDecorateChance();
				if (rand1 == rand2) {
					if (par3.equalsIgnoreCase("true")) {
						canPlace = !plugin.contains(((Player) sender).getWorld(), chunkX, chunkZ, myOre.getIdentifier());
					} else {
						canPlace = !this.hasOres(world, chunkX, chunkZ);
					}
					if (canPlace) {
						thread.offer(myLocation.getWorld(), chunkX, chunkZ, myOre);
						plugin.put(((Player) sender).getWorld(), chunkX, chunkZ, myOre.getIdentifier());
						((CustomOreDecorator) myOre).toDecorateCount++;
					} else {
						if (plugin.showDebug) {
							//plugin.getLogger().info("Offer to Queue: " + myOre.getIdentifier() + " at: " + chunkX + " / " + chunkZ + " is already decorated.");
						}
					}
				} else {
					if (plugin.showDebug) {
						//plugin.getLogger().info("Offer to Queue: " + myOre.getIdentifier() + " failed chance calculation for manual decorate. Chance: " + rand1 + "/" + rand2);
					}
				}
				if (((CustomOreDecorator) myOre).toDecorateCount > 0) {
					if (plugin.showDebug) {
						plugin.getLogger().info("Queue Generation: " + ((CustomOreDecorator) myOre).toDecorateCount + " of: " + myOre.getIdentifier());
					}
					sender.sendMessage("[MoreMaterials] - Queued Generation of: [" + ChatColor.AQUA + ((CustomOreDecorator) myOre).toDecorateCount + ChatColor.RESET + "] chunk(s) of: [" + ChatColor.DARK_AQUA + myOre.getIdentifier() + ChatColor.RESET + "].");
				}
			}
		}

		// Multi-Chunk Generation using all ores within objects.yml (ores)
		if (par2.equalsIgnoreCase("all") && radius >= 1) {
			for (Decorator myOre : plugin.getDecoratorRegistry().getAll()) {
				if (myOre instanceof CustomOreDecorator) {
					// Tracking
					((CustomOreDecorator) myOre).toDecorateCount = 0;
					// Set replacement ore type.
					((CustomOreDecorator) myOre).replace(Material.STONE, Material.DIRT, Material.GRAVEL);

					// Calculate circular decorate based on current location.
					for (int x = -radius; x < radius; x++) {
						for (int j = -radius; j < radius; j++) {
							offsetX = chunkX + x;
							offsetZ = chunkZ + j;

							// Calculate chance of decorate for this specific chunk and specific ore type.
							rand1 = RANDOM.nextInt(((CustomOreDecorator) myOre).getDecorateChance()) + 1;
							rand2 = ((CustomOreDecorator) myOre).getDecorateChance();
							if (rand1 == rand2) {
								if (par3.equalsIgnoreCase("true")) {
									canPlace = !plugin.contains(((Player) sender).getWorld(), offsetX, offsetZ, myOre.getIdentifier());
								} else {
									canPlace = !this.hasOres(world, offsetX, offsetZ);
								}
								if (canPlace) {
									thread.offer(myLocation.getWorld(), offsetX, offsetZ, myOre);
									plugin.put(((Player) sender).getWorld(), offsetX, offsetZ, myOre.getIdentifier());
									((CustomOreDecorator) myOre).toDecorateCount++;
								} else {
									if (plugin.showDebug) {
										//plugin.getLogger().info("Offer to Queue: " + myOre.getIdentifier() + " at: " + offsetX + " / " + offsetZ + " is already decorated.");
									}
								}
							} else {
								if (plugin.showDebug) {
									//plugin.getLogger().info("Offer to Queue: " + myOre.getIdentifier() + " failed chance calculation for manual populate. Chance: " + rand1 + "/" + rand2);
								}
							}
						}
					}
					if (((CustomOreDecorator) myOre).toDecorateCount > 0) {
						if (plugin.showDebug) {
							plugin.getLogger().info("Queue Generation: " + ((CustomOreDecorator) myOre).toDecorateCount + " of: " + myOre.getIdentifier());
						}
						sender.sendMessage("[MoreMaterials] - Queued Generation of: [" + ChatColor.AQUA + ((CustomOreDecorator) myOre).toDecorateCount + ChatColor.RESET + "] chunk(s) of: [" + ChatColor.DARK_AQUA + myOre.getIdentifier() + ChatColor.RESET + "].");
					}
				}
			}
		}

		// Multi-Chunk Generation using specified args[1] ore.
		if (!par2.equalsIgnoreCase("all") && radius >= 1) {
			Decorator myOre = this.plugin.getDecoratorRegistry().get(par2);
			if (myOre instanceof CustomOreDecorator) {
				// Tracking
				((CustomOreDecorator) myOre).toDecorateCount = 0;
				// Set replacement ore type.
				((CustomOreDecorator) myOre).replace(Material.STONE, Material.DIRT, Material.GRAVEL);

				// Should replace the ore in the chunk you are standing in.
				for (int x = -radius; x < radius; x++) {
					for (int j = -radius; j < radius; j++) {
						offsetX = chunkX + x;
						offsetZ = chunkZ + j;
						rand1 = RANDOM.nextInt(((CustomOreDecorator) myOre).getDecorateChance()) + 1;
						rand2 = ((CustomOreDecorator) myOre).getDecorateChance();
						if (rand1 == rand2) {
							if (par3.equalsIgnoreCase("true")) {
								canPlace = !plugin.contains(((Player) sender).getWorld(), offsetX, offsetZ, myOre.getIdentifier());
							} else {
								canPlace = !this.hasOres(world, offsetX, offsetZ);
							}		
							if (canPlace) {
								thread.offer(myLocation.getWorld(), offsetX, offsetZ, myOre);
								plugin.put(((Player) sender).getWorld(), offsetX, offsetZ, myOre.getIdentifier());
								((CustomOreDecorator) myOre).toDecorateCount++;
							} else {
								if (plugin.showDebug) {
									//plugin.getLogger().info("Offer to Queue: " + myOre.getIdentifier() + " at: " + offsetX + " / " + offsetZ + " is already decorated.");
								}
							}
						} else {
							if (plugin.showDebug) {
								//plugin.getLogger().info("Offer to Queue: " + myOre.getIdentifier() + " failed chance calculation for manual decorate. Chance: " + rand1 + "/" + rand2);
							}
						}
					}
				}
				if (((CustomOreDecorator) myOre).toDecorateCount > 0) {
					if (plugin.showDebug) {
						plugin.getLogger().info("Queue Generation: " + ((CustomOreDecorator) myOre).toDecorateCount + " of: " + myOre.getIdentifier());
					}
					sender.sendMessage("[MoreMaterials] - Queued Generation of: [" + ChatColor.AQUA + ((CustomOreDecorator) myOre).toDecorateCount + ChatColor.RESET + "] chunk(s) of: [" + ChatColor.DARK_AQUA + myOre.getIdentifier() + ChatColor.RESET + "].");
				}
			}
		}
		sender.sendMessage("[MoreMaterials] - command completed sucessfully.");
		if (plugin.getPlacer() != null) {
			plugin.getPlacer().player = Bukkit.getPlayer(sender.getName());
		}
		return true;
	}
	
	public boolean hasOres(World world, int cx, int cz) {
		TLongObjectHashMap<List<String>> chunksDecorated = alreadyDecorated.get(world.getUID());
		if (chunksDecorated != null) {
			final long key = (((long) cx) << 32) | (((long) cz) & 0xFFFFFFFFL);
			return chunksDecorated.get(key) != null;
		}
		return false;
	}
}
