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

package net.morematerials.morematerials;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import net.morematerials.morematerials.cmds.AdminExecutor;
import net.morematerials.morematerials.cmds.GiveExecutor;
import net.morematerials.morematerials.cmds.SMExecutor;
import net.morematerials.morematerials.furnaces.SpoutFurnaceRecipes;
import net.morematerials.morematerials.listeners.SMListener;
import net.morematerials.morematerials.manager.MainManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	private static FileConfiguration config;

	@Override
	public void onEnable() {
		// Workaround for hooking into FurnaceRecipes, because bukkit doesn't support this.
		try {
			SpoutFurnaceRecipes.hook(this);
		} catch (Throwable exception) {
			MainManager.getUtils().log("Could not hook into the notchian furnace! This means the cb you're using doesn't support furnace recipes!", Level.SEVERE);
		}

		// Regind the config
		try {
			this.readConfig();
		} catch (Exception exception) {
			MainManager.getUtils().log("Error reading configuration file!", Level.SEVERE);
		}

		// Let the plugin check for updates and initialize all files and folders.
		try {
			checkIntegrity();
		} catch (IOException exception) {
			MainManager.getUtils().log("Could not access default files!", Level.SEVERE);
		}

		// Our super all-you-can-eat manager :D
		new MainManager(this);

		// Registered events for all Materials in this manager.
		this.getServer().getPluginManager().registerEvents(new SMListener(this), this);

		// Chat command stuff
		getCommand("mm").setExecutor(new SMExecutor(this));
		getCommand("mmgive").setExecutor(new GiveExecutor());
		getCommand("mmadmin").setExecutor(new AdminExecutor());
	}

	private void readConfig() throws Exception {
		// First we parse our config file and merge with defaults.
		config = this.getConfig();
		config.addDefault("Port", 8180);
		config.addDefault("Hostname", Bukkit.getServer().getIp());
		config.addDefault("Use-WebServer", true);
		config.addDefault("DebugMode", false);
		config.options().copyDefaults(true);
		// Then we save our config
		this.saveConfig();
		// This option should not be saved into the file
		config.set("ApiUrl", "http://www.morematerials.net/api.php");
	}

	private void checkIntegrity() throws IOException {
		// Create all used files and folders if not present.
		File file;
		// Contains all smp files.
		file = new File(this.getDataFolder().getPath() + File.separator + "materials");
		if (!file.exists()) {
			file.mkdirs();
			// TODO We should download the default.smp here
		}
		// Contains all legacy item crafting stuff.
		file = new File(this.getDataFolder().getPath(), "legacyrecipes.yml");
		if (!file.exists()) {
			file.createNewFile();
		}
		// Contains all wgen stuff.
		file = new File(this.getDataFolder().getPath(), "wgen.yml");
		if (!file.exists()) {
			file.createNewFile();
		}
	}

	public static FileConfiguration getConf() {
		return config;
	}
}
