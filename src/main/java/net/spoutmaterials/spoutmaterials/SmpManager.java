/*
 The MIT License

 Copyright (c) 2011 Zloteanu Nichita (ZNickq), Sean Porter (Glitchfinder),
 Jan Tojnar (jtojnar, Lisured) and Andre Mohren (IceReaper)

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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import net.spoutmaterials.spoutmaterials.listeners.SMListener;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.Material;

public class SmpManager {
	private JavaPlugin plugin;
	private Map<String, SmpPackage> smpPackages = new HashMap<String, SmpPackage>();

	// This class can be used in every plugin and contains only smp files for the actual plugin.
	public SmpManager(JavaPlugin plugin) {
		this.plugin = plugin;
		this.setupFiles();
		this.loadAllActiveMaterials();
		
		// Registered events for all Materials in this manager.
		this.plugin.getServer().getPluginManager().registerEvents(new SMListener(this), plugin);
	}
	
	private void setupFiles() {
		// Create folder for SpoutMaterialsPacks if not present.
		File smpFolder = new File(plugin.getDataFolder().getPath() + File.separator + "materials");
		if (!smpFolder.exists()) {
			try {
				smpFolder.createNewFile();
			} catch (Exception e) {
				String logMessage = "[" + this.plugin.getDescription().getName() + "]";
				logMessage += " SpoutMaterials: Couldn't write materials folder.";
				Logger.getLogger("Minecraft").info(logMessage);
			}
		}
		
		// Create new materials file, if none found.
		File active = new File(plugin.getDataFolder().getPath() + File.separator + "materials.yml");
		if (!active.exists()) {
			try {
				active.createNewFile();
			} catch (Exception e) {
				String logMessage = "[" + this.plugin.getDescription().getName() + "]";
				logMessage += " SpoutMaterials: Couldn't write materials.yml.";
				Logger.getLogger("Minecraft").info(logMessage);
			}
		}
	}
	
	private void loadAllActiveMaterials() {
		// Getting all smp files which are listed in the materials.yml file.
		try {
			DataInputStream in = new DataInputStream(new FileInputStream(
				new File(plugin.getDataFolder().getPath() + File.separator + "materials.yml")
			));
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null)   {
				this.loadSmp(strLine, false);
			}
			in.close();
		} catch (Exception e) {
			String logMessage = "[" + this.plugin.getDescription().getName() + "]";
			logMessage += " SpoutMaterials: Couldn't read materials.yml.";
			Logger.getLogger("Minecraft").info(logMessage);
		}
	}
	
	public void loadSmp(String smpFile) {
		this.loadSmp(smpFile, true);
		this.save();
	}

	private void loadSmp(String smpFile, Boolean save) {
		if (this.smpPackages.containsKey(smpFile)) {
			String logMessage = "[" + this.plugin.getDescription().getName() + "]";
			logMessage += " SpoutMaterials: Couldn't load " + smpFile + ", already loaded.";
			Logger.getLogger("Minecraft").info(logMessage);
		} else {
			this.smpPackages.put(smpFile, new SmpPackage(this, smpFile));
		}
	}

	public void unloadSmp(String smpFile) {
		this.smpPackages.get(smpFile).unload();
		this.smpPackages.remove(smpFile);
		this.save();
	}
	
	private void save() {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(
				plugin.getDataFolder().getPath() + File.separator + "materials.yml"
			));
			Object[] activeSmpFiles = this.smpPackages.keySet().toArray();
			for (int i = 0; i < activeSmpFiles.length; i++) {
				if (i > 0) {
					out.write("\n");
				}
				out.write(activeSmpFiles[i].toString());
			}
			out.close();
		} catch (Exception e) {
			String logMessage = "[" + this.plugin.getDescription().getName() + "]";
			logMessage += " SpoutMaterials: Couldn't write materials.yml file.";
			Logger.getLogger("Minecraft").info(logMessage);
		}
	}
	
	public Map<String, Material> getMaterial(String materialName) {
		Map<String, Material> materials = new HashMap<String, Material>();
		String[] parts = materialName.split("\\.");
		// in case we provide just an item name
		if (parts.length == 1) {
			for (String smpPackage : this.smpPackages.keySet()) {
				Material found = this.smpPackages.get(smpPackage).getMaterial(parts[0]);
				if (found != null) {
					materials.put(smpPackage + "." + parts[0], found);
				}
			}
		// in case we also provide the package name
		} else if (this.smpPackages.containsKey(parts[0])) {
			Material found = this.smpPackages.get(parts[0]).getMaterial(parts[1]);
			if (found != null) {
				materials.put(parts[0] + "." + parts[1], found);
			}
		}
		return materials;
	}
	
	public Object getMaterial(SpoutItemStack itemStack) {
		for (String smpPackage : this.smpPackages.keySet()) {
			Object found = this.smpPackages.get(smpPackage).getMaterial(itemStack);
			if (found != null) {
				return found;
			}
		}
		return null;
	}
	
	public JavaPlugin getPlugin() {
		return this.plugin;
	}
}
