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

import java.util.logging.Level;
import java.util.logging.Logger;
import net.spoutmaterials.spoutmaterials.cmds.DebugExecutor;
import net.spoutmaterials.spoutmaterials.cmds.GeneralExecutor;
import net.spoutmaterials.spoutmaterials.cmds.GiveExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	public static final Logger log = Logger.getLogger("Minecraft");
	public PluginDescriptionFile pdfile;
	// Used for handling smp files.
	public SmpManager smpManager = null;

	@Override
	public void onDisable() {
		log.log(Level.INFO, "{0} was disabled!", pdfile.getFullName());
	}

	@Override
	public void onEnable() {
		pdfile = this.getDescription();
		log.log(Level.INFO, "{0} was enabled!", pdfile.getFullName());

		// Initialize all custom objects. This is all a plugin must do to have custom materials implemented.
		this.smpManager = new SmpManager(this);

		// Chat command stuff
		getCommand("sm").setExecutor(new GeneralExecutor(this));
		getCommand("smgive").setExecutor(new GiveExecutor(this));
		getCommand("smdebug").setExecutor(new DebugExecutor(this));
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
}
