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
import java.util.logging.Level;

import net.morematerials.morematerials.commands.GiveExecutor;
import net.morematerials.morematerials.commands.GeneralExecutor;
import net.morematerials.morematerials.listeners.SMListener;
import net.morematerials.morematerials.manager.HandlerManager;
import net.morematerials.morematerials.manager.UtilsManager;
import net.morematerials.morematerials.manager.WebManager;
import net.morematerials.morematerials.metrics.MetricsLite;
import net.morematerials.morematerials.smp.SmpManager;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	private HandlerManager handlerManager;
	private SmpManager smpManager;
	private UtilsManager utilsManager;
	private WebManager webManager;

	@Override
	public void onEnable() {
		// Try to create the required folders.
		File file;
		String[] folders = { "materials", "handlers" };
		for (String folder : folders) {
			file = new File(this.getDataFolder().getPath(), folder);
			if (!file.exists()) {
				file.mkdirs();
			}
		}

		// Initialize all managers.
		this.utilsManager = new UtilsManager(this);
		this.webManager = new WebManager(this);
		this.smpManager = new SmpManager(this);
		this.handlerManager = new HandlerManager(this);

		// Metrics.
		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
			this.utilsManager.log("Stat tracking activated!");
		} catch (Exception exception) {
			this.utilsManager.log("Stat tracking error!", Level.SEVERE);
		}

		// Registered events.
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new SMListener(this), this);

		// Reigster chat commands.
		this.getCommand("mm").setExecutor(new GeneralExecutor(this));
		this.getCommand("mmgive").setExecutor(new GiveExecutor(this));
	}

	public HandlerManager getHandlerManager() {
		return this.handlerManager;
	}

	public SmpManager getSmpManager() {
		return this.smpManager;
	}

	public UtilsManager getUtilsManager() {
		return this.utilsManager;
	}

	public WebManager getWebManager() {
		return this.webManager;
	}

}
