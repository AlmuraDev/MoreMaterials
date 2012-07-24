package net.morematerials.commands;

import net.morematerials.MoreMaterials;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.Material;
import org.getspout.spoutapi.player.SpoutPlayer;

public class GiveExecutor implements CommandExecutor {
	
	private MoreMaterials plugin;

	public GiveExecutor(MoreMaterials plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// This command is only for players
		if (!(sender instanceof Player)) {
			return false;
		}
		
		// Also this command is only useable by players with permission
		if (this.plugin.getUtilsManager().hasPermission(sender, "morematerials.admin")) {
			return false;
		}
		
		// Show usage on incorrect usage.
		if (args.length == 0) {
			((Player) sender).chat("/mm mmgive");
			return true;
		}
		
		// How many materials should we add?
		Integer amount = args.length > 1 ? Integer.parseInt(args[1]) : 1;
		
		Material material = this.plugin.getSmpManager().getMaterial(Integer.parseInt(args[0]));
		if (material != null) {
			// No material found.
			sender.sendMessage(this.plugin.getUtilsManager().getMessage("No material found for: " + args[0]));
		} else {
			// Material found.
			((SpoutPlayer) sender).getInventory().addItem(new SpoutItemStack(material, amount));
			sender.sendMessage(this.plugin.getUtilsManager().getMessage("You received " + amount + " of " + args[0] + "."));
		}
		
		return true;
	}

}
