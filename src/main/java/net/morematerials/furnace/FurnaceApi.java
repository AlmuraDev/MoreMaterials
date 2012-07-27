package net.morematerials.furnace;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class FurnaceApi extends JavaPlugin {
	public static FurnaceApi plugin;

	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(new FurnaceListener(this), this);
	}

	public void onDisable() {

	}
}
