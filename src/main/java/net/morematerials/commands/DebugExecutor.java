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

package net.morematerials.commands;

import net.morematerials.MoreMaterials;
import net.morematerials.materials.MMCustomBlock;
import net.morematerials.materials.MMCustomItem;
import net.morematerials.materials.MMCustomTool;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.material.CustomItem;
import org.getspout.spoutapi.material.Material;

public class DebugExecutor implements CommandExecutor {

	private MoreMaterials plugin;

	public DebugExecutor(MoreMaterials plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			// This command is only useable by players with permission
			if (!this.plugin.getUtilsManager().hasPermission(sender, "morematerials.admin")) {
				return false;
			}
			
			// Make sure its a custom material
			SpoutItemStack handStack = new SpoutItemStack(((Player) sender).getItemInHand());
			if (!handStack.isCustomItem()) {
				return true;
			}
			
			// Get the cusotm id.
			Integer materialId;
			if (handStack.getMaterial() instanceof CustomBlock) {
				materialId = ((CustomBlock) handStack.getMaterial()).getCustomId();
			} else {
				materialId = ((CustomItem) handStack.getMaterial()).getCustomId();
			}
			
			// Only check MoreMaterials materials.
			Material material = this.plugin.getSmpManager().getMaterial(materialId);
			if (material == null) {
				return true;
			}
			
			// Get material information.
			String smpName;
			String matName;
			if (material instanceof MMCustomBlock) {
				smpName = ((MMCustomBlock) material).getSmpName();
				matName = ((MMCustomBlock) material).getMaterialName();
			} else if (material instanceof MMCustomTool) {
				smpName = ((MMCustomTool) material).getSmpName();
				matName = ((MMCustomTool) material).getMaterialName();
			} else {
				smpName = ((MMCustomItem) material).getSmpName();
				matName = ((MMCustomItem) material).getMaterialName();
			}
			
			// Display debug stuff.
			sender.sendMessage(this.plugin.getUtilsManager().getMessage("SMP Name: " + smpName));
			sender.sendMessage(this.plugin.getUtilsManager().getMessage("Material Name: " + matName));
			sender.sendMessage(this.plugin.getUtilsManager().getMessage("Custom ID: " + materialId));
		}
		return true;
	}

}
