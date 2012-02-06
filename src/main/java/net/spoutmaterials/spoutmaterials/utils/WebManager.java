/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spoutmaterials.spoutmaterials.utils;

import java.util.Collection;
import net.spoutmaterials.spoutmaterials.Main;
import net.spoutmaterials.spoutmaterials.SmpPackage;

/**
 *
 * @author ZNickq
 */
public class WebManager {
	private Main instance;
	public WebManager(Main i) {
		instance=i;
		String s=i.pdfile.getVersion();
		String actualVersion=i.getConfig().getString("Version","");
		if(actualVersion.equals("")) {
			i.getConfig().addDefault("Version", s);
			i.getConfig().options().copyDefaults(true);
			i.saveConfig();
		} else {
			if(!(actualVersion.equals(s))) {
				System.out.println("[SpoutMaterials] The plugin was updated to version "+s+" from "+actualVersion+"! Redownloading all smp packs...");
				redownloadAllSMPs();
			}
		}
	}
	
	public final void redownloadAllSMPs() {
		Collection<SmpPackage> sp=instance.smpManager.getAllPackages();
		for(SmpPackage smp:sp) {
			smp.reDownload();
		}
	}
}
