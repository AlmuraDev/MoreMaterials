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

package net.spoutmaterials.spoutmaterials;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;
import net.spoutmaterials.spoutmaterials.materials.SMCustomBlock;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.generator.BlockPopulator;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.material.CustomBlock;

public class WGenManager extends BlockPopulator {
	private Main plugin;

	//TODO Add list type to remove warning
	private Map<String, List> populations = new HashMap<String, List>();

	public WGenManager(Main plugin) {
		this.plugin = plugin;
		
		// Create new materials file, if none found.
		File active = new File(plugin.getDataFolder().getPath() + File.separator + "wgen.yml");
		if (!active.exists()) {
			try {
				active.createNewFile();
			} catch (Exception e) {
				String logMessage = "[" + this.plugin.getDescription().getName() + "]";
				logMessage += " SpoutMaterials: Couldn't write wgen.yml.";
				Logger.getLogger("Minecraft").info(logMessage);
			}
		}

		// Loading the wgen.yml
		File wgenYml = new File(plugin.getDataFolder().getPath() + File.separator + "wgen.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(wgenYml);
		for (String worldName : config.getKeys(false)) {
			this.populations.put(worldName, config.getList(worldName));
		}
		
		// Registering for worlds.
		for (World world : this.plugin.getServer().getWorlds()) {
			if (this.populations.containsKey(world)) {
				world.getPopulators().add(this);
			}
		}
	}

	@Override
	public void populate(World world, Random random, Chunk chunk) {
		if (!this.populations.containsKey(world.getName())) {
			return;
		}
		for (Object worldEntry : this.populations.get(world.getName())) {
			//TODO unsafe cast warning remove
			Map<String, Object> entry = (Map<String, Object>) worldEntry;
			Object material = this.plugin.getSmpManager().getMaterial((String) entry.get("Material"));
			
			// If given material was not found
			if (!(material instanceof SMCustomBlock)) {
				continue;
			}
			
			// Getting values
			Integer chance = entry.containsKey("Chance") ? (Integer) entry.get("Chance") : 0;
			Integer minY = entry.containsKey("MinY") ? (Integer) entry.get("MinY") : 0;
			Integer maxY = entry.containsKey("MaxY") ? (Integer) entry.get("MaxY") : 127;
			Integer veins = entry.containsKey("Veins") ? (Integer) entry.get("Veins") : 4;
			Integer veinSize = entry.containsKey("VeinSize") ? (Integer) entry.get("VeinSize") : 8;
			String replaces = entry.containsKey("Replaces") ? (String) entry.get("Replaces") : "1 3 13";

			// Calculate chance that blocks will be present in this chunk.
			int rn = random.nextInt(100);
			if (rn > chance) {
				return;
			}
			
			// Calculate how many veins will be present in this chunk.
			int howMany = random.nextInt(veins - 1) + 1;
			// Calculate how large a vein may become.
			int howLarge = random.nextInt(veinSize - 1) + 1;
			
			int x = 0;
			int y = 0;
			int z = 0;
			Block curBlock = null;
			
			for (int i = 0; i < howMany; i++) {
				
				// Get a random block in this chunk.
				x = random.nextInt(16);
				y = random.nextInt(maxY - minY) + minY;
				z = random.nextInt(16);
				
				curBlock = chunk.getBlock(x, y, z);
				
				// Generate this vein.
				for (int j = 1; j < howLarge; j++) {
					for (String replacement : replaces.split(" ")) {
						if (curBlock.getTypeId() == Integer.parseInt(replacement)) {
							SpoutManager.getMaterialManager().overrideBlock(curBlock, (CustomBlock) material);
						}
					}
					rn = random.nextInt(BlockFace.values().length);
					curBlock = curBlock.getRelative(BlockFace.values()[rn]);
				}
			}

		}
	}

}