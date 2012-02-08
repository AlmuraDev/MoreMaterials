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
import net.spoutmaterials.spoutmaterials.listeners.SMListener;
import net.spoutmaterials.spoutmaterials.reflection.SpoutFurnaceRecipes;
import net.spoutmaterials.spoutmaterials.utils.WebManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	// Used for handling smp files.
	private SmpManager smpManager = null;
	// Used for website related stuff.
	private WebManager webmanager;
	// Used for legacy material related stuff
	private LegacyManager legacyManager;

	@Override
	public void onDisable() {
		this.smpManager.unload();
		this.legacyManager.unload();
	}

	@Override
	public void onEnable() {
		try {
			// Let the plugin check for updates and initialize all files and folders.
			checkIntegrityAndUpdate();
		} catch (IOException exception) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, exception);
		}
		
		// Workaround for hooking into FurnaceRecipes, because spout doesn't support this.
		SpoutFurnaceRecipes.hook();

		// Initialize all managers we need.
		this.smpManager = new SmpManager(this);
		this.legacyManager = new LegacyManager(this);
		this.webmanager = new WebManager(this);
		new WGenManager(this);

		// Registered events for all Materials in this manager.
		this.getServer().getPluginManager().registerEvents(new SMListener(this), this);

		// Chat command stuff
		getCommand("smgive").setExecutor(new GiveExecutor(this));
		getCommand("smadmin").setExecutor(new AdminExecutor(this));
	}

	public boolean hasPermission(CommandSender sender, String perm) {
		// Allow console
		if (!(sender instanceof Player)) {
			return true;
			// Or players with this permission
		} else if (((Player) sender).hasPermission(perm)) {
			return true;
		}
		return false;
	}

	private void checkIntegrityAndUpdate() throws IOException {
		// Update the plugin.
		if (this.webmanager.updateAvailable()) {
			this.webmanager.updatePlugin();
			//TODO somehow reload plugin here
		}

		// Create all used files and folders if not present.
		File file = null;
		// Contains all smp files.
		file = new File(this.getDataFolder().getPath() + File.separator + "materials");
		if (!file.exists()) {
			file.mkdirs();
		}
		// Contains all legacy item crafting stuff.
		file = new File(this.getDataFolder().getPath() + File.separator + "legacyrecipes.yml");
		if (!file.exists()) {
			file.createNewFile();
		}
		// Contains all wgen stuff.
		file = new File(this.getDataFolder().getPath() + File.separator + "wgen.yml");
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
}
