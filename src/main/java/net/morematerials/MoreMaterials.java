/*
 * This file is part of MoreMaterials.
 * 
 * Copyright (c) 2012 Andre Mohren (IceReaper)
 * 
 * The MIT License
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.morematerials;

import java.io.File;
import java.util.logging.Level;

import net.morematerials.commands.DebugExecutor;
import net.morematerials.commands.GeneralExecutor;
import net.morematerials.commands.GiveExecutor;
import net.morematerials.handlers.BombHandler;
import net.morematerials.handlers.CommandHandler;
import net.morematerials.handlers.ConsumeHandler;
import net.morematerials.handlers.FireBallHandler;
import net.morematerials.handlers.GiveHandler;
import net.morematerials.handlers.HealHandler;
import net.morematerials.handlers.ItemReturnHandler;
import net.morematerials.handlers.LightningHandler;
import net.morematerials.handlers.PlaySoundHandler;
import net.morematerials.handlers.PoisonHandler;
import net.morematerials.handlers.RotateHandler;
import net.morematerials.listeners.CustomListener;
import net.morematerials.listeners.MMListener;
import net.morematerials.manager.FurnaceRecipeManager;
import net.morematerials.manager.HandlerManager;
import net.morematerials.manager.SmpManager;
import net.morematerials.manager.UpdateManager;
import net.morematerials.manager.UtilsManager;
import net.morematerials.manager.AssetManager;
import net.morematerials.metrics.Metrics;
import net.morematerials.metrics.Metrics.Graph;
import net.morematerials.metrics.Metrics.Plotter;

import org.bukkit.plugin.java.JavaPlugin;

public class MoreMaterials extends JavaPlugin {

	private HandlerManager handlerManager;
	private SmpManager smpManager;
	private UtilsManager utilsManager;
	private AssetManager assetManager;
	private UpdateManager updateManager;
	private FurnaceRecipeManager furnaceRecipeManager;

	@Override
	public void onEnable() {
		// Try to create the required folders.
		File file;
		this.getDataFolder().mkdirs();
		String[] folders = { "materials", "handlers", "handlers/src", "handlers/bin", "cache" };
		for (String folder : folders) {
			file = new File(this.getDataFolder(), folder);
			if (!file.exists()) {
				file.mkdirs();
			}
		}

		// Initialize all managers.
		this.utilsManager = new UtilsManager(this);
		this.assetManager = new AssetManager(this);
		this.handlerManager = new HandlerManager(this);
		
		// Inject Handler Classes
		this.handlerManager.inject(BombHandler.class);
		this.handlerManager.inject(CommandHandler.class);
		this.handlerManager.inject(ConsumeHandler.class);
		this.handlerManager.inject(FireBallHandler.class);
		this.handlerManager.inject(GiveHandler.class);
		this.handlerManager.inject(HealHandler.class);
		this.handlerManager.inject(ItemReturnHandler.class);
		this.handlerManager.inject(LightningHandler.class);
		this.handlerManager.inject(PlaySoundHandler.class);
		this.handlerManager.inject(PoisonHandler.class);
		this.handlerManager.inject(RotateHandler.class);
		
		// Finish managers
		this.furnaceRecipeManager = new FurnaceRecipeManager();
		this.smpManager = new SmpManager(this);
		this.updateManager = new UpdateManager(this);
		this.smpManager.init();

		// Metrics.
		if (this.getConfig().getBoolean("Metrics", true)) {
			try {
				Metrics metrics = new Metrics(this);

				// It might be interesting to see how many custom materials we have at all!
				Graph materialGraph = metrics.createGraph("Number of custom materials");
				materialGraph.addPlotter(new Plotter("Materials") {
					@Override
					public int getValue() {
						return getSmpManager().getTotalMaterials();
					}
				});
				
				int totalPackages = 0;
				// At last - this is interesting for the SMP creators, we can show which SMP files are used how often!
				Graph packagesGraph = metrics.createGraph("Most used SMP packages");
				for (String fileName : (new File(this.getDataFolder().getPath(), "materials")).list()) {
					if (fileName.endsWith(".smp")) {
						totalPackages++;
						packagesGraph.addPlotter(new Plotter(fileName.substring(0, fileName.lastIndexOf("."))) {
							@Override
							public int getValue() {
								return 1;
							}
						});
					}
				}

				// Also its nice to see how many packages people have installed at once.
				Graph packageCountGraph = metrics.createGraph("Number of SMP packages");
				final int showPackages = totalPackages;
				packageCountGraph.addPlotter(new Plotter("Packages") {
					@Override
					public int getValue() {
						return showPackages;
					}
				});
				
				metrics.start();
				this.utilsManager.log("Stat tracking activated!");
			} catch (Exception exception) {
				this.utilsManager.log("Stat tracking error!", Level.SEVERE);
			}
		}

		// Registered events.
		this.getServer().getPluginManager().registerEvents(new MMListener(this), this);
		this.getServer().getPluginManager().registerEvents(new CustomListener(this), this);

		// Register chat commands.
		this.getCommand("mm").setExecutor(new GeneralExecutor(this));
		this.getCommand("mmdebug").setExecutor(new DebugExecutor(this));
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

	public AssetManager getAssetManager() {
		return this.assetManager;
	}
	
	public UpdateManager getUpdateManager() {
		return this.updateManager;
	}

	public FurnaceRecipeManager getFurnaceRecipeManager() {
		return this.furnaceRecipeManager;
	}

}
