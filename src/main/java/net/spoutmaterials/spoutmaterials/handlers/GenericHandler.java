package net.spoutmaterials.spoutmaterials.handlers;

import net.spoutmaterials.spoutmaterials.Main;
import org.bukkit.Location;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 *
 * @author ZNickq
 */
public abstract class GenericHandler {
	public abstract void onActivation(Location loc, SpoutPlayer splr);
	public abstract void init(Main instance);
	public abstract void shutdown();
}
