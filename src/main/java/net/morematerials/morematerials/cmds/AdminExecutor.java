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

package net.morematerials.morematerials.cmds;

import java.util.Set;
import net.morematerials.morematerials.Main;
import net.morematerials.morematerials.manager.MainManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AdminExecutor implements CommandExecutor {

	private Main instance;

	public AdminExecutor(Main plugin) {
		instance = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!MainManager.getUtils().hasPermission(sender, "morematerials.admin", true)) {
			return false;
		}
		// Parameter is "install"
		if (args.length > 1 && "install".equals(args[0])) {
			if (args.length > 2) {
				MainManager.getSmpManager().install(args[1], args[2]);
			} else {
				MainManager.getSmpManager().install(args[1], "-1");
			}
		}

		// Parameter is "uninstall"
		if (args.length >= 2&& "uninstall".equals(args[0])) {
			MainManager.getSmpManager().uninstall(args[1]);
		}
		if (args.length == 0) {
			return false;
		}
		// Parameter is "list"
		if ("list".equals(args[0])) {
			Set<String> packages = MainManager.getSmpManager().getPackages();
			for (String smpName : packages) {
				sender.sendMessage(MainManager.getUtils().getMessage(smpName));
			}
		}

		// Parameter is "updates"
		if ("updates".equals(args[0])) {
			//TODO check all smp versions for newer ones. (found in _version_smp file)
		}

		return true;
	}
}
