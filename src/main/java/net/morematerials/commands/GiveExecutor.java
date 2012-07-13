package net.morematerials.commands;

import java.util.ArrayList;
import java.util.logging.Level;

import net.morematerials.MoreMaterials;
import net.morematerials.materials.MMCustomBlock;
import net.morematerials.materials.MMCustomItem;

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

	@Override
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
		
		ArrayList<Material> materials = this.plugin.getSmpManager().getMaterial(args[0]);
		if (materials.isEmpty()) {
			// No material found.
			sender.sendMessage(this.plugin.getUtilsManager().getMessage("No materials found for: " + args[0]));
		} else if (materials.size() == 1) {
			// One material found.
			((SpoutPlayer) sender).getInventory().addItem(new SpoutItemStack(materials.get(0), amount));
			sender.sendMessage(this.plugin.getUtilsManager().getMessage("You received " + amount + " of " + args[0] + "."));
		} else {
			// More materials found.
			sender.sendMessage(this.plugin.getUtilsManager().getMessage("Material " + args[0] + " multiple times found, please specify!", Level.WARNING));
			for (Integer i = 0; i < materials.size(); i++) {
				String smp = materials.get(i) instanceof MMCustomBlock ? ((MMCustomBlock) materials.get(i)).getSmpName() : ((MMCustomItem) materials.get(i)).getSmpName();
				String mat = materials.get(i) instanceof MMCustomBlock ? ((MMCustomBlock) materials.get(i)).getMaterialName() : ((MMCustomItem) materials.get(i)).getMaterialName();
				sender.sendMessage(this.plugin.getUtilsManager().getMessage("- " + smp + "." + mat));
			}
		}
		
		return true;
	}

}
