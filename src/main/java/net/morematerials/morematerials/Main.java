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

import net.morematerials.morematerials.cmds.GiveExecutor;
import net.morematerials.morematerials.cmds.SMExecutor;
import net.morematerials.morematerials.listeners.SMListener;
import net.morematerials.morematerials.manager.MainManager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	private static FileConfiguration config;

	@Override
	public void onEnable() {
		// First initialize all managers.
		new MainManager(this);

		// Then read the config
		try {
			this.readConfig();
		} catch (Exception exception) {
			MainManager.getUtils().log("Error reading config!", Level.SEVERE);
		}

		// Let the plugin initialize all files and folders.
		try {
			this.checkIntegrity();
		} catch (IOException exception) {
			MainManager.getUtils().log("Couldn't access files!", Level.SEVERE);
		}

		// Start the magic...
		MainManager.init();

		// Registered events for all Materials in this manager.
		this.getServer().getPluginManager().registerEvents(new SMListener(this), this);

		// Chat command stuff
		getCommand("mm").setExecutor(new SMExecutor(this));
		getCommand("mmgive").setExecutor(new GiveExecutor());
	}

	private void readConfig() throws Exception {
		// First we parse our config file and merge with defaults.
		config = this.getConfig();
		config.addDefault("PublicPort", 8180);
		config.addDefault("BindPort", 8180);
		String defaultIp = Bukkit.getServer().getIp();
		if (defaultIp.equals("")) {
			defaultIp = "127.0.0.1";
		}
		config.addDefault("Hostname", defaultIp);
		config.addDefault("Use-WebServer", true);
		config.options().copyDefaults(true);
		// Then we save our config
		this.saveConfig();
	}

	private void checkIntegrity() throws IOException {
		// Create all used files and folders if not present.
		File file;
		String path = this.getDataFolder().getPath();
		// Contains all smp files.
		file = new File(path, "materials");
		if (!file.exists()) {
			file.mkdirs();
		}
		// Contains all legacy item crafting stuff.
		file = new File(path, "legacyrecipes.yml");
		if (!file.exists()) {
			file.createNewFile();
		}
	}

	public static FileConfiguration getConf() {
		return config;
	}
}
