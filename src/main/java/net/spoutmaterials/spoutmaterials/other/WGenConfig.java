package net.spoutmaterials.spoutmaterials.other;

import java.util.List;
import java.util.Map;
import java.util.Set;
import net.spoutmaterials.spoutmaterials.Main;
import net.spoutmaterials.spoutmaterials.wgen.WGenData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.material.Material;
import org.getspout.spoutapi.material.MaterialData;

/**
 *
 * @author ZNickq
 */
public class WGenConfig {
	//CustomBlock block, int minY, int maxY, int chance, int maxVeins, int maxVeinSize

	public static void addBlocks(Main instance, YamlConfiguration which) {
		Set<String> allBlocks = which.getKeys(false);
		for (String curBlock : allBlocks) {
			Material mat;
			Map<String, Material> materialList = instance.smpManager.getMaterial(curBlock);
			mat = materialList.get((String) materialList.keySet().toArray()[0]);
			if (mat == null || !(mat instanceof CustomBlock)) {
				System.out.println("[SpoutMaterials] Invalid WGEN material name: " + curBlock + "!");
				return;
			}
			int minY, maxY, chance, maxVeins, maxVeinSize;
			String worldName;
			minY = which.getInt(curBlock + ".minY", 0);
			maxY = which.getInt(curBlock + ".minY", 0);
			chance = which.getInt(curBlock + ".minY", 0);
			maxVeins = which.getInt(curBlock + ".minY", 0);
			maxVeinSize = which.getInt(curBlock + ".minY", 0);
			worldName = which.getString(curBlock + ".minY", "all");
			WGenData wgd = new WGenData((CustomBlock) mat, minY, maxY, chance, maxVeins, maxVeinSize);
			if (worldName.equals("all")) {
				List<World> lw = instance.getServer().getWorlds();
				for (World curw : lw) {
					curw.getPopulators().add(wgd);
				}
			} else {
				World w = Bukkit.getWorld(worldName);
				if (w == null) {
					System.out.println("[SpoutMaterials] Invalid World name for material " + curBlock + ", there's no world called " + worldName + "!");
					return;
				}
				w.getPopulators().add(wgd);
			}

		}
	}
}
