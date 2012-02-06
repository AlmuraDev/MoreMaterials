package net.spoutmaterials.spoutmaterials.cmds;

import net.spoutmaterials.spoutmaterials.Main;
import net.spoutmaterials.spoutmaterials.SmpPackage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author ZNickq
 */
public class UpdateExecutor implements CommandExecutor {

	private Main i;

	public UpdateExecutor(Main plugin) {
		i = plugin;
	}

	public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
		if (strings.length == 0) {
			return false;
		}
		if (!i.hasPermission(cs, "spoutmaterials.update")) {
			cs.sendMessage(ChatColor.GREEN + "[SpoutMaterials]" + ChatColor.RED + "You don't have permission to do that!");
			return true;
		}
		SmpPackage sp = i.smpManager.getPackage(strings[0]);
		if (sp == null) {
			cs.sendMessage(ChatColor.GREEN + "[SpoutMaterials]" + ChatColor.RED + "Invalid package name!");
			return true;
		}
		sp.reDownload();
		return true;
	}
}
