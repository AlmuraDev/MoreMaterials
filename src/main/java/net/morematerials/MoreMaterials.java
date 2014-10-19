/*
 * This file is part of MoreMaterials, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2013 AlmuraDev <http://www.almuradev.com/>
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

import gnu.trove.map.hash.TLongObjectHashMap;
import net.morematerials.commands.DebugExecutor;
import net.morematerials.commands.DecorateExecutor;
import net.morematerials.commands.GeneralExecutor;
import net.morematerials.commands.GiveExecutor;
import net.morematerials.handlers.BiomeInformationHandler;
import net.morematerials.handlers.BombHandler;
import net.morematerials.handlers.ChestHandler;
import net.morematerials.handlers.ChunkRegenerateHandler;
import net.morematerials.handlers.ClearCustomDataHandler;
import net.morematerials.handlers.CommandHandler;
import net.morematerials.handlers.ConsumeHandler;
import net.morematerials.handlers.FeedAnimalHandler;
import net.morematerials.handlers.FireBallHandler;
import net.morematerials.handlers.GiveHandler;
import net.morematerials.handlers.HealHandler;
import net.morematerials.handlers.InformationHandler;
import net.morematerials.handlers.ItemReturnHandler;
import net.morematerials.handlers.LightningHandler;
import net.morematerials.handlers.PlaySoundHandler;
import net.morematerials.handlers.PoisonHandler;
import net.morematerials.handlers.RakeHandler;
import net.morematerials.handlers.RotateHandler;
import net.morematerials.listeners.CustomListener;
import net.morematerials.listeners.DecorateListener;
import net.morematerials.listeners.MMListener;
import net.morematerials.manager.AssetManager;
import net.morematerials.manager.FurnaceRecipeManager;
import net.morematerials.manager.HandlerManager;
import net.morematerials.manager.SmpManager;
import net.morematerials.manager.UpdateManager;
import net.morematerials.manager.UtilsManager;
import net.morematerials.metrics.Metrics;
import net.morematerials.metrics.Metrics.Graph;
import net.morematerials.metrics.Metrics.Plotter;
import net.morematerials.wgen.DecoratorLoader;
import net.morematerials.wgen.DecoratorRegistry;
import net.morematerials.wgen.task.BlockPlacer;
import net.morematerials.wgen.task.ThreadRegistry;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class MoreMaterials extends JavaPlugin {
	public static Random RANDOM = new Random();
	private BlockPlacer placer;
	private HandlerManager handlerManager;
	private SmpManager smpManager;
	private UtilsManager utilsManager;
	private AssetManager assetManager;
	private UpdateManager updateManager;
	private FurnaceRecipeManager furnaceRecipeManager;
	private DecoratorRegistry decoratorRegistry;
	private ThreadRegistry maffThreads;
	private List<String> decorateWorldList;
	public boolean showDebug = false;
	public boolean jobsEnabled = false;
	public Map<UUID, TLongObjectHashMap<List<String>>> worldsDecorated = new HashMap<>();

	@Override
	public void onDisable() {
		save();
		maffThreads.stopAll(true);
		getServer().getScheduler().cancelTasks(this);
	}

	@Override
	public void onEnable() {
		// Try to create the required folders.
		File file;
		this.getDataFolder().mkdirs();
		String[] folders = {"materials", "handlers", "handlers/src", "handlers/bin", "cache"};
		for (String folder : folders) {
			file = new File(this.getDataFolder(), folder);
			if (!file.exists()) {
				file.mkdirs();
			}
		}

		// Configuration
		load();
		decorateWorldList = this.getConfig().getStringList("DecorateWorlds");

		// Initialize all managers.
		this.utilsManager = new UtilsManager(this);
		this.assetManager = new AssetManager(this);
		this.handlerManager = new HandlerManager(this);

		// Inject Handler Classes
		this.handlerManager.inject(BiomeInformationHandler.class);
		this.handlerManager.inject(BombHandler.class);
		this.handlerManager.inject(ChestHandler.class);
		this.handlerManager.inject(ChunkRegenerateHandler.class);
		this.handlerManager.inject(ClearCustomDataHandler.class);
		this.handlerManager.inject(CommandHandler.class);
		this.handlerManager.inject(ConsumeHandler.class);
		this.handlerManager.inject(FeedAnimalHandler.class);
		this.handlerManager.inject(FireBallHandler.class);
		this.handlerManager.inject(GiveHandler.class);
		this.handlerManager.inject(HealHandler.class);
		this.handlerManager.inject(InformationHandler.class);
		this.handlerManager.inject(ItemReturnHandler.class);
		this.handlerManager.inject(LightningHandler.class);
		this.handlerManager.inject(PlaySoundHandler.class);
		this.handlerManager.inject(PoisonHandler.class);
		this.handlerManager.inject(RakeHandler.class);
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
		
		if (this.getConfig().getBoolean("DebugMode")) {
			showDebug = true;
		}
		
		if (this.getConfig().getBoolean("Jobs")) {
            jobsEnabled = true;
        }

		// Registered events.
		this.getServer().getPluginManager().registerEvents(new MMListener(this), this);
		this.getServer().getPluginManager().registerEvents(new CustomListener(this), this);
		this.getServer().getPluginManager().registerEvents(new DecorateListener(this), this);

		// Initialize Decorator
		decoratorRegistry = new DecoratorRegistry();
		final DecoratorLoader loader = new DecoratorLoader(this);
		loader.onEnable(getDataFolder());
		loader.load();
		placer = new BlockPlacer(this);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, placer, 0, 20);
		maffThreads = new ThreadRegistry(this, placer);

		// Register chat commands.
		this.getCommand("mm").setExecutor(new GeneralExecutor(this));
		this.getCommand("mmdecorate").setExecutor(new DecorateExecutor(this));
		this.getCommand("mmdebug").setExecutor(new DebugExecutor(this));
		this.getCommand("mmgive").setExecutor(new GiveExecutor(this));
	}

	public DecoratorRegistry getDecoratorRegistry() {
		return decoratorRegistry;
	}

	public ThreadRegistry getThreadRegistry() {
		return maffThreads;
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

	public List<String> getDecorateWorldList() {
		return decorateWorldList;
	}

	public Map<UUID, TLongObjectHashMap<List<String>>> cloneWorldsDecorated() {
		final Map<UUID, TLongObjectHashMap<List<String>>> cloned = new HashMap<>();

		for (Map.Entry<UUID, TLongObjectHashMap<List<String>>> entry : worldsDecorated.entrySet()) {
			final TLongObjectHashMap<List<String>> chunksCloned = new TLongObjectHashMap<>();

			for (Long chunkEntry : entry.getValue().keys()) {
				final List<String> decorationsCloned = new LinkedList<>();

				for (String stringEntry : entry.getValue().get(chunkEntry)) {
					decorationsCloned.add(stringEntry);
				}

				chunksCloned.put(chunkEntry, decorationsCloned);
			}

			cloned.put(entry.getKey(), chunksCloned);
		}
		return cloned;
	}

	public void put(World world, int cx, int cz, String decoratorID) {
		TLongObjectHashMap<List<String>> chunksDecorated = worldsDecorated.get(world.getUID());
		if (chunksDecorated == null) {
			chunksDecorated = new TLongObjectHashMap<>();
			worldsDecorated.put(world.getUID(), chunksDecorated);
		}
		final long key = (((long) cx) << 32) | (((long) cz) & 0xFFFFFFFFL);
		List<String> decorators = chunksDecorated.get(key);
		if (decorators == null) {
			decorators = new ArrayList<>();
			chunksDecorated.put(key, decorators);
		}
		decorators.add(decoratorID);
	}

	public TLongObjectHashMap<List<String>> getChunkDecorations(World world) {
		return worldsDecorated.get(world.getUID());
	}

	public List<String> getDecorationsInChunk(World world, int cx, int cz) {
		final TLongObjectHashMap<List<String>> chunksDecorated = worldsDecorated.get(world.getUID());
		if (chunksDecorated != null) {
			final long key = (((long) cx) << 32) | (((long) cz) & 0xFFFFFFFFL);
			return chunksDecorated.get(key);
		}
		return null;
	}

	public void remove(World world, int cx, int cz, String decoratorID) {
		TLongObjectHashMap<List<String>> chunksDecorated = worldsDecorated.get(world.getUID());
		if (chunksDecorated != null) {
			final long key = (((long) cx) << 32) | (((long) cz) & 0xFFFFFFFFL);
			List<String> decorators = chunksDecorated.get(key);
			if (decorators != null) {
				decorators.remove(decoratorID);
				if (decorators.isEmpty()) {
					chunksDecorated.remove(key);
					if (chunksDecorated.isEmpty()) {
						worldsDecorated.remove(world.getUID());
					}
				}
			}
		}
	}

	public boolean containsAny(World world, int cx, int cz) {
		TLongObjectHashMap<List<String>> chunksDecorated = worldsDecorated.get(world.getUID());
		if (chunksDecorated != null) {
			final long key = (((long) cx) << 32) | (((long) cz) & 0xFFFFFFFFL);
			return chunksDecorated.get(key) != null;
		}
		return false;
	}

	public boolean contains(World world, int cx, int cz, String decoratorID) {
		TLongObjectHashMap<List<String>> chunksDecorated = worldsDecorated.get(world.getUID());
		if (chunksDecorated != null) {
			final long key = (((long) cx) << 32) | (((long) cz) & 0xFFFFFFFFL);
			final List<String> decorators = chunksDecorated.get(key);
			if (decorators != null) {
				return decorators.contains(decoratorID);
			}
		}
		return false;
	}

	private void load() {
		try {
			worldsDecorated = SaveAndLoad.load("plugins/MoreMaterials/Wgen/Chunks.dat");
		} catch (Exception e) {
			worldsDecorated = new HashMap<>();
		}
	}

	public void save() {
		try {
			SaveAndLoad.save(worldsDecorated, ("plugins/MoreMaterials/Wgen/Chunks.dat"));
		} catch (Exception e) {
			String path = this.getDataFolder() + "/Wgen/";
			new File(path).mkdirs();
			File file = new File(path, "Chunks.dat");
			try {
				file.createNewFile();
			} catch (IOException f) {
			}
			try {
				SaveAndLoad.save(worldsDecorated, ("plugins/MoreMaterials/Wgen/Chunks.dat"));
			} catch (Exception g) {
			}
		}
	}
	
	public int fileSize() {
		return worldsDecorated.size();
	}

	public BlockPlacer getPlacer() {
		return placer;
	}
}
