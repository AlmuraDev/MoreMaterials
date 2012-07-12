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

package net.morematerials.manager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.bukkit.configuration.file.YamlConfiguration;

import net.morematerials.MoreMaterials;
import net.morematerials.materials.CustomShape;
import net.morematerials.materials.MMCustomBlock;
import net.morematerials.materials.MMCustomItem;

public class SmpManager {

	private MoreMaterials plugin;

	private ArrayList<MMCustomBlock> customBlocksList = new ArrayList<MMCustomBlock>();
	private ArrayList<MMCustomItem> customItemsList = new ArrayList<MMCustomItem>();
	private ArrayList<CustomShape> customShapesList = new ArrayList<CustomShape>();

	public SmpManager(MoreMaterials plugin) {
		this.plugin = plugin;
	}
	
	public void init() {
		// Load all .smp files.
		File dir = new File(this.plugin.getDataFolder().getPath(), "materials");
		for (File file : dir.listFiles()) {
			if (file.getName().endsWith(".smp")) {
				try {
					this.loadPackage(file);
				} catch (Exception exception) {
					String msg = "Cannot load " + file.getName();
					this.plugin.getUtilsManager().log(msg, Level.SEVERE);
				}
			}
		}
	}

	private void loadPackage(File file) throws Exception {
		HashMap<String, YamlConfiguration> materials = new HashMap<String, YamlConfiguration>();
		ZipFile smpFile = new ZipFile(file);
		String smpName = this.plugin.getUtilsManager().getName(smpFile.getName());

		// Get all material configurations
		Enumeration<? extends ZipEntry> entries = smpFile.entries();
		ZipEntry entry;
		YamlConfiguration yml;
		Integer index;
		while (entries.hasMoreElements()) {
			entry = entries.nextElement();
			// Parse all .yml files in this .smp file.
			if (entry.getName().endsWith(".yml")) {
				yml = new YamlConfiguration();
				yml.load(smpFile.getInputStream(entry));
				index = entry.getName().lastIndexOf(".");
				materials.put(entry.getName().substring(0, index), yml);
			} else if (entry.getName().endsWith(".shape")) {
				// Register .shape files.
				this.customShapesList.add(new CustomShape(this.plugin, smpFile, entry));
			} else {
				// Add all other files as asset.
				this.plugin.getWebManager().addAsset(smpFile, entry);
			}
		}
		
		// First loop - Create all materials.
		for (String matName : materials.keySet()) {
			YamlConfiguration material = materials.get(matName);
			this.createMaterial(smpName, matName, material, smpFile);
		}
	}

	private void createMaterial(String smpName, String matName, YamlConfiguration yaml, ZipFile smpFile) {
		// Allow reading of old .smp files.
		@Deprecated
		Boolean oldPackage = !yaml.contains("Texture");
		if (oldPackage) {
			yaml = this.updateConfiguration(yaml, smpName, matName);
			String message = "Please update " + matName + ".yml";
			this.plugin.getUtilsManager().log(message, Level.WARNING);
		}
		
		// Create the actual materials.
		if (yaml.getString("Type", "").equals("Block")) {
			MMCustomBlock block = MMCustomBlock.create(this.plugin, yaml, smpName, matName);
			this.customBlocksList.add(block);
		} else {
			MMCustomItem item = MMCustomItem.create(this.plugin, yaml, smpName, matName);
			this.customItemsList.add(item);
		}
	}

	@Deprecated
	private YamlConfiguration updateConfiguration(YamlConfiguration yaml, String smpName, String matName) {
		// Update old .yml configurations to newer format.
		yaml.set("Texture", matName + ".png");
		if (((String) yaml.get("Type")).equals("Block")) {
			yaml.set("Shape", matName + ".shape");
			yaml.set("BaseId", yaml.get("BlockID"));
			// Creating the texture map list.
			ArrayList<String> coordList = new ArrayList<String>();
			String textureUrl = this.plugin.getWebManager().getAssetsUrl(smpName + "_" +  matName + ".png");
			BufferedImage bufferedImage = this.plugin.getWebManager().getCachedImage(textureUrl);
			if (bufferedImage.getWidth() > bufferedImage.getHeight()) {
				for (Integer i = 0; i < bufferedImage.getWidth() / bufferedImage.getHeight(); i++) {
					coordList.add(bufferedImage.getHeight() * i + " 0 " + bufferedImage.getHeight() + " " + bufferedImage.getHeight());
				}
			} else {
				coordList.add("0 0 " + bufferedImage.getWidth() + " " + bufferedImage.getHeight());
			}
			yaml.set("Coords", coordList);
		}
		return yaml;
	}

	public CustomShape getShape(String smpName, String matName) {
		CustomShape shape;
		// Search for the correct shape
		for (Integer i = 0; i < this.customShapesList.size(); i++) {
			shape = this.customShapesList.get(i);
			if (shape.getSmpName().equals(smpName) && shape.getMatName().equals(matName)) {
				return shape;
			}
		}
		return null;
	}

}
