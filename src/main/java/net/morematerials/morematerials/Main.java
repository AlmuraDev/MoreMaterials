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
import net.morematerials.morematerials.manager.LegacyManager;
import net.morematerials.morematerials.manager.MainManager;
import net.morematerials.morematerials.smp.SmpManager;
import net.morematerials.morematerials.stats.StatHooker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	// Used for handling smp files.
	private SmpManager smpManager;
	// Used for website related stuff.
	//private WebManager webmanager;
	// Used for legacy material related stuff
	private LegacyManager legacyManager;
	private int port;
	private String hostname;
	private boolean useAssetsServer;
	private String apiUrl = "http://www.morematerials.net/api.php";
	private static boolean debug = false;
	
	@Override
	public void onDisable() {
		this.smpManager.unload();
		this.legacyManager.unload();
	}

	@Override
	public void onEnable() {
		// Workaround for hooking into FurnaceRecipes, because bukkit doesn't support this.	
		try{
			SpoutFurnaceRecipes.hook(this);
		} catch(Throwable ex) { // Not exception!
			this.log("Could not hook into the notchian furnace! This means the cb you're using doesn't support furnace recipes!", Level.SEVERE);
		}
		
		try {
			this.readConfig();
		} catch (Exception e) {
		}
		

		try {
			// Let the plugin check for updates and initialize all files and folders.
			checkIntegrityAndUpdate();
		} catch (IOException exception) {
		}
		
		new MainManager(this);

		// Registered events for all Materials in this manager.
		this.getServer().getPluginManager().registerEvents(new SMListener(this), this);

		// Chat command stuff
		getCommand("mm").setExecutor(new SMExecutor(this));
		getCommand("mmgive").setExecutor(new GiveExecutor(this));
		getCommand("mmadmin").setExecutor(new AdminExecutor(this));
	}

	private void readConfig() throws Exception {
		FileConfiguration cfg = this.getConfig();
		cfg.addDefault("Port", 8180);
		cfg.addDefault("Hostname", Bukkit.getServer().getIp());
		cfg.addDefault("Use-WebServer", true);
		cfg.addDefault("DebugMode", false);
		cfg.options().copyDefaults(true);
		this.saveConfig();

		//TODO Do we realy need defaults here? Should already be set above!
		this.port = cfg.getInt("Port", 8180);
		this.hostname = cfg.getString("Hostname", Bukkit.getServer().getIp());
		this.useAssetsServer = cfg.getBoolean("Use-WebServer",true);
		debug = cfg.getBoolean("DebugMode",false);
	}

	public boolean hasPermission(CommandSender sender, String perm, boolean verbose) {
		// Allow console
		if (!(sender instanceof Player)) {
			return true;
			// Or players with this permission
		} else if (sender.hasPermission("morematerials.*")) {
			return true;
		} else if (((Player) sender).hasPermission(perm)) {
			return true;
		}
		if (verbose) {
			sender.sendMessage(this.getMessage("You do not have permission to do that! You need " + perm + "!", Level.SEVERE));
		}
		return false;
	}

	private void checkIntegrityAndUpdate() throws IOException {
		//this.webmanager.updateAvailable();

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
	public String getAssetsUrl() {
		return "http://" + this.hostname + ":" + this.getPort() + "/";
	}

	public int getPort() {
		return this.port;
	}
	
	// Generalize all console or chat output!
	public String getMessage(String logMessage) {
		return this.getMessage(logMessage, Level.INFO);
	}
	
	public String getMessage(String logMessage, Level level) {
		if (level == Level.WARNING) {
			return ChatColor.GREEN + "[" + this.getDescription().getName() + "] " + ChatColor.YELLOW + logMessage;
		} else if (level == Level.SEVERE) {
			return ChatColor.GREEN + "[" + this.getDescription().getName() + "] " + ChatColor.RED + logMessage;
		}
		return ChatColor.GREEN + "[" + this.getDescription().getName() + "] " + ChatColor.WHITE + logMessage;
	}
	
	public void log(String logMessage) {
		this.log(logMessage, Level.INFO);
	}
	
	public void log(String logMessage, Level level) {
		if (level == Level.WARNING) {
			//TODO add console text color yellow
			System.out.println("[" + this.getDescription().getName() + "] Warning: " + logMessage);
		} else if (level == Level.SEVERE) {
			//TODO add console text color red
			System.out.println("[" + this.getDescription().getName() + "] ERROR: " + logMessage);
		} else {
			//TODO add console text color normal
			System.out.println("[" + this.getDescription().getName() + "] " + logMessage);
		}
	}
	
	public boolean useAssetsServer() {
		return useAssetsServer;
	}
	
	public static boolean isDebugging() {
		return debug;
	}

	public String getApiUrl() {
		return this.apiUrl ;
	}
}
