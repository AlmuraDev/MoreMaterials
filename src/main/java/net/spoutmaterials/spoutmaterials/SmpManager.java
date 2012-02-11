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

package net.spoutmaterials.spoutmaterials;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.zip.ZipFile;
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
		String[] files = materials.list();
		for (String file : files) {
			if (file.endsWith(".smp")) {
				try {
					ZipFile smpFile = getSmpHandle(file);
				
					// When file could not be loaded or version information is missing.
					if (smpFile == null) {
						continue;
					}
					
					// Ignore private files.
					if (smpFile.getEntry("_version_plugin") == null) {
						this.smpPackages.put(file.replaceAll("\\.smp$", ""), new SmpPackage(this, smpFile, file.replaceAll("\\.smp$", "")));
						continue;
					}
				
					// Getting smp version.
					InputStream versionStream = smpFile.getInputStream(smpFile.getEntry("_version_plugin"));
					String version = "";
					int rChar;
					while ((rChar = versionStream.read()) != -1) {
						version += (char)rChar;
					}

					// Checking if an update is required.
					if (!this.plugin.getDescription().getVersion().equals(version)) {
						
						// Update this .smp file.
						smpFile.close();
						File delete = new File(this.plugin.getDataFolder().getPath() + File.separator + "materials" + File.separator + file);
						delete.delete();
						this.install(file.replaceAll("\\.smp$", ""), "-1");
						
					// Load the .smp file.
					} else if (smpFile != null) {
						this.smpPackages.put(file.replaceAll("\\.smp$", ""), new SmpPackage(this, smpFile, file.replaceAll("\\.smp$", "")));
					}
				} catch (IOException exception) {
				}
			}
		}
	}
	
	private ZipFile getSmpHandle(String smpFileName) {
		try {
			return new ZipFile(
				this.plugin.getDataFolder().getPath() + File.separator + "materials" + File.separator + smpFileName
			);
		} catch(IOException Exception) {
			String logMessage = "[" + this.plugin.getDescription().getName() + "]";
			logMessage += " SpoutMaterials: Couldn't load " + smpFileName + ".";
			Logger.getLogger("Minecraft").info(logMessage);
			return null;
		}
	}

	public void install(String smpName, String version) {
		if (!this.smpPackages.containsKey(smpName)) {
			this.plugin.getWebManager().downloadSmp(smpName, version);
			ZipFile smpFile = getSmpHandle(smpName + ".smp");
			this.smpPackages.put(smpName, new SmpPackage(this, smpFile, smpName));
			this.plugin.getLegacyManager().reload();
		}
	}

	public void uninstall(String smpName) {
		if (this.smpPackages.containsKey(smpName)) {
			this.smpPackages.get(smpName).unload();
			this.smpPackages.remove(smpName);
			File delete = new File(this.plugin.getDataFolder().getPath() + File.separator + "materials" + File.separator + smpName + ".smp");
			delete.delete();
			this.plugin.getLegacyManager().reload();
		}
	}
	
	public void unload() {
		for (String smpName : this.smpPackages.keySet()) {
			this.smpPackages.get(smpName).unload();
		}
		this.smpPackages.clear();
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
	
	public Set<String> getPackages() {
		return this.smpPackages.keySet();
	}
	
	public int getMaterialNumber() {
		int toRet=0;
		for(SmpPackage sp:smpPackages.values()) {
			toRet+=sp.getMaterialNumber();
		}
		return toRet;
	}
}
