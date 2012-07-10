/*
 The MIT License

 Copyright (c) 2011 Zloteanu Nichita (ZNickq) and Andre Mohren (IceReaper)

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

package net.morematerials.morematerials.smp;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.zip.ZipFile;

import net.morematerials.morematerials.Main;

import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.Material;

public class SmpManager {
	private Main plugin;
	private Map<String, SmpPackage> smpPackages = new HashMap<String, SmpPackage>();

	public SmpManager(Main plugin) {
		this.plugin = plugin;
		this.loadAllPackages();
	}

	private void loadAllPackages() {
		// Getting all .smp files.
		File materials = new File(this.plugin.getDataFolder().getPath() + File.separator + "materials");
		for (String file : materials.list()) {
			if (file.endsWith(".smp")) {
				this.plugin.getUtilsManager().log("Loading " + file);
				ZipFile smpFile = getSmpHandle(file);
				// When file could not be loaded.
				if (smpFile == null) {
					continue;
				}
				this.smpPackages.put(
					file.replaceAll("\\.smp$", ""),
					new SmpPackage(this, smpFile, file.replaceAll("\\.smp$", ""))
				);
			}
		}
	}

	private ZipFile getSmpHandle(String smpFileName) {
		try {
			return new ZipFile(this.plugin.getDataFolder().getPath() + File.separator + "materials" + File.separator + smpFileName);
		} catch (IOException Exception) {
			this.plugin.getUtilsManager().log("Couldn't load " + smpFileName + ".", Level.SEVERE);
			return null;
		}
	}

	public Map<String, Material> getMaterial(String materialName) {
		Map<String, Material> materials = new HashMap<String, Material>();
		String[] parts = materialName.split("\\.");
		// in case we provide just an item name
		if (parts.length == 1) {
			for (String smpPackage : this.smpPackages.keySet()) {
				Material found = this.smpPackages.get(smpPackage).getMaterial(parts[0]);
				if (found != null) {
					materials.put(smpPackage + "." + parts[0], found);
				}
			}
		// in case we also provide the package name
		} else if (this.smpPackages.containsKey(parts[0])) {
			Material found = this.smpPackages.get(parts[0]).getMaterial(parts[1]);
			if (found != null) {
				materials.put(parts[0] + "." + parts[1], found);
			}
		}
		return materials;
	}

	public Material getMaterial(SpoutItemStack itemStack) {
		for (String smpPackage : this.smpPackages.keySet()) {
			Material found = this.smpPackages.get(smpPackage).getMaterial(itemStack);
			if (found != null) {
				return found;
			}
		}
		return null;
	}

	public Main getPlugin() {
		return this.plugin;
	}

}
