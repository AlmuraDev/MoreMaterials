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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.spoutmaterials.spoutmaterials.cmds.AdminExecutor;
import net.spoutmaterials.spoutmaterials.cmds.GiveExecutor;
import net.spoutmaterials.spoutmaterials.cmds.SMExecutor;
import net.spoutmaterials.spoutmaterials.listeners.SMListener;
import net.spoutmaterials.spoutmaterials.reflection.SpoutFurnaceRecipes;
import net.spoutmaterials.spoutmaterials.stats.StatHooker;
import net.spoutmaterials.spoutmaterials.utils.WebManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	public Boolean updateAvailable = false;
	// Used for handling smp files.
	private SmpManager smpManager;
	// Used for website related stuff.
	private WebManager webmanager;
	// Used for legacy material related stuff
	private LegacyManager legacyManager;
	private int port;
	private String hostname;

	@Override
	public void onDisable() {
		this.smpManager.unload();
		this.legacyManager.unload();
	}

	@Override
	public void onEnable() {
		// Workaround for hooking into FurnaceRecipes, because spout doesn't support this.	
		try{
			SpoutFurnaceRecipes.hook();
		} catch(Throwable ex) {//Not exception!
			System.out.println("[SpoutMaterials] ERROR ===========> Could not hook into the notchian furnace! This means the cb you're using doesn't support furnace recipes!");
		}
		
		try {
			this.readConfig();
		} catch (Exception e) {
		}

		this.webmanager = new WebManager(this);

		try {
			// Let the plugin check for updates and initialize all files and folders.
			checkIntegrityAndUpdate();
		} catch (IOException exception) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, exception);
		}



		// Initialize all managers we need.
		this.smpManager = new SmpManager(this);
		this.legacyManager = new LegacyManager(this);
		new WGenManager(this);

		// Registered events for all Materials in this manager.
		this.getServer().getPluginManager().registerEvents(new SMListener(this), this);

		// Chat command stuff
		getCommand("sm").setExecutor(new SMExecutor(this));
		getCommand("smgive").setExecutor(new GiveExecutor(this));
		getCommand("smadmin").setExecutor(new AdminExecutor(this));
		new StatHooker(this);
	}

	private void readConfig() throws Exception {
		FileConfiguration cfg = this.getConfig();
		//TODO Do we realy need to set defaults when we also have them below?
		cfg.addDefault("Port", 8180);
		cfg.addDefault("Hostname", Bukkit.getServer().getIp());
		cfg.options().copyDefaults(true);
		this.saveConfig();

		this.port = cfg.getInt("Port", 8180);
		this.hostname = cfg.getString("Hostname", Bukkit.getServer().getIp());
	}

	public boolean hasPermission(CommandSender sender, String perm, boolean verbose) {
		// Allow console
		if (!(sender instanceof Player)) {
			return true;
			// Or players with this permission
		} else if (sender.hasPermission("spoutmaterials.*")) {
			return true;
		} else if (((Player) sender).hasPermission(perm)) {
			return true;
		}
		if (verbose) {
			sender.sendMessage(
				ChatColor.GREEN + "[SpoutMaterials] " +
				ChatColor.RED + "You do not have permission to do that! You need " + perm + "!"
			);
		}
		return false;
	}

	private void checkIntegrityAndUpdate() throws IOException {
		// Update the plugin.
		if (this.webmanager.updateAvailable()) {
			this.updateAvailable = true;
		}

		// Create all used files and folders if not present.
		File file;
		// Contains all smp files.
		file = new File(this.getDataFolder().getPath() + File.separator + "materials");
		if (!file.exists()) {
			file.mkdirs();
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

	public SmpManager getSmpManager() {
		return this.smpManager;
	}

	public LegacyManager getLegacyManager() {
		return this.legacyManager;
	}

	public WebManager getWebManager() {
		return this.webmanager;
	}

	public String getAssetsUrl() {
		return "http://" + this.hostname + ":" + this.getPort() + "/";
	}

	public int getPort() {
		return this.port;
	}
}
