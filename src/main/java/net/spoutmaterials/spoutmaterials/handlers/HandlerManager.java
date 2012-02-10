package net.spoutmaterials.spoutmaterials.handlers;

import java.io.File;
import net.spoutmaterials.spoutmaterials.Main;

/**
 *
 * @author ZNickq
 */
public class HandlerManager {
	public HandlerManager(Main instance) {
		File f = new File(instance.getDataFolder()+File.separator+"handlers");
		if(!f.exists()) f.mkdir();
		File[] fl=f.listFiles();
		for(File curHandler:fl) {
			
		}
	}
}
