package net.spoutmaterials.spoutmaterials.cmds;

import java.util.ArrayList;
import net.spoutmaterials.spoutmaterials.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.getspout.commons.ChatColor;

/**
 *
 * @author ZNickq
 */
public class SMExecutor implements CommandExecutor{
	private Main plugin;
	private String authors;
	public SMExecutor(Main aThis) {
		plugin=aThis;
		authors = "";
		ArrayList<String> as=plugin.getDescription().getAuthors();
		for(String author: as) {
			authors+=author+", ";
		}
		authors = authors.substring(0,authors.length()-3);
	}

	public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
		if(strings.length==0) {
			cs.sendMessage(ChatColor.GREEN+"[SpoutMaterials] "+ChatColor.YELLOW+"This server is running SpoutMaterials v"+plugin.getDescription().getVersion()+"! Credits to "+authors+"!");
			return true;
		}
		String first=strings[0];
		if(first.equalsIgnoreCase("?")||first.equalsIgnoreCase("help")) {
			cs.sendMessage(ChatColor.GREEN+"SpoutMaterials help page");
			cs.sendMessage(ChatColor.AQUA+"---------------------------------");
			cs.sendMessage(ChatColor.YELLOW+"/sm -> "+ChatColor.GOLD+"Basic informations, and help!");
			cs.sendMessage(ChatColor.YELLOW+"/smgive -> "+ChatColor.GOLD+"Commands to give any custom material!");
			cs.sendMessage(ChatColor.YELLOW+"/smadmin -> "+ChatColor.GOLD+"Administration commands!");
		}
		return true;
	}
	
}
