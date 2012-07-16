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
import org.getspout.spoutapi.material.Material;

import net.morematerials.MoreMaterials;
import net.morematerials.materials.CustomShape;
import net.morematerials.materials.MMCustomBlock;
import net.morematerials.materials.MMCustomItem;
import net.morematerials.materials.MMCustomTool;
import net.morematerials.materials.MMLegacyMaterial;

public class SmpManager {

	private MoreMaterials plugin;

	private ArrayList<MMCustomBlock> blocksList = new ArrayList<MMCustomBlock>();
	private ArrayList<MMCustomItem> itemsList = new ArrayList<MMCustomItem>();
	private ArrayList<MMCustomTool> toolsList = new ArrayList<MMCustomTool>();
	private ArrayList<MMLegacyMaterial> legacyList = new ArrayList<MMLegacyMaterial>();
	private ArrayList<CustomShape> shapesList = new ArrayList<CustomShape>();

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
					this.plugin.getUtilsManager().log("Cannot load " + file.getName(), Level.SEVERE);
				}
			}
		}
	}

	private void loadPackage(File file) throws Exception {
		HashMap<String, YamlConfiguration> materials = new HashMap<String, YamlConfiguration>();
		ZipFile smpFile = new ZipFile(file);

		// Get all material configurations
		Enumeration<? extends ZipEntry> entries = smpFile.entries();
		ZipEntry entry;
		YamlConfiguration yml;
		while (entries.hasMoreElements()) {
			entry = entries.nextElement();
			// Parse all .yml files in this .smp file.
			if (entry.getName().endsWith(".yml")) {
				yml = new YamlConfiguration();
				yml.load(smpFile.getInputStream(entry));
				materials.put(entry.getName().substring(0, entry.getName().lastIndexOf(".")), yml);
			} else if (entry.getName().endsWith(".shape")) {
				// Register .shape files.
				this.shapesList.add(new CustomShape(this.plugin, smpFile, entry));
			} else {
				// Add all other files as asset.
				this.plugin.getWebManager().addAsset(smpFile, entry);
			}
		}

		// First loop - Create all materials.
		for (String matName : materials.keySet()) {
			this.createMaterial(this.plugin.getUtilsManager().getName(smpFile.getName()), matName, materials.get(matName), smpFile);
		}
		
		// Second loop - Now we can reference all drops
		for (Integer i = 0; i < this.blocksList.size(); i++) {
			this.blocksList.get(i).configureDrops();
		}
	}

	private void createMaterial(String smpName, String matName, YamlConfiguration yaml, ZipFile smpFile) {
		// Allow reading of old .smp files.
		if (!yaml.contains("Texture")) {
			yaml = this.updateConfiguration(yaml, smpName, matName);
			this.plugin.getUtilsManager().log("Please update " + matName + ".yml", Level.WARNING);
		}

		// Create the actual materials.
		if (matName.matches("^[0-9]+$")) {
			MMLegacyMaterial material = this.getLegacyMaterial(Integer.parseInt(matName));
			if (material == null) {
				this.legacyList.add(new MMLegacyMaterial(yaml, smpName, matName));
			} else {
				material.configureBase(smpName, yaml);
			}
		} else if (yaml.getString("Type", "").equals("Block")) {
			this.blocksList.add(MMCustomBlock.create(this.plugin, yaml, smpName, matName));
		} else if (yaml.getString("Type", "").equals("Tool")) {
			this.toolsList.add(MMCustomTool.create(this.plugin, yaml, smpName, matName));
		} else {
			this.itemsList.add(MMCustomItem.create(this.plugin, yaml, smpName, matName));
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
			BufferedImage bufferedImage = this.plugin.getWebManager().getCachedImage(this.plugin.getWebManager().getAssetsUrl(smpName + "_" + matName + ".png"));
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
		for (Integer i = 0; i < this.shapesList.size(); i++) {
			shape = this.shapesList.get(i);
			if (shape.getSmpName().equals(smpName)) {
				if (shape.getMatName().equals(matName)) {
					return shape;
				}
			}
		}
		return null;
	}

	public ArrayList<Material> getMaterial(String smpName, String matName) {
		return this.getMaterial(smpName + "." + matName);
	}

	public ArrayList<Material> getMaterial(String fullName) {
		String[] nameParts = fullName.split("\\.");
		ArrayList<Material> found = new ArrayList<Material>();
		
		// First check for matching blocks.
		MMCustomBlock currentBlock;
		for (Integer i = 0; i < this.blocksList.size(); i++) {
			currentBlock = this.blocksList.get(i);
			if (nameParts.length == 1 && currentBlock.getMaterialName().equals(nameParts[0])) {
				found.add(currentBlock);
			} else if (currentBlock.getSmpName().equals(nameParts[0]) && currentBlock.getMaterialName().equals(nameParts[1])) {
				found.add(currentBlock);
			}
		}
		
		// Then also check for matching items.
		MMCustomItem currentItem;
		for (Integer i = 0; i < this.itemsList.size(); i++) {
			currentItem = this.itemsList.get(i);
			if (nameParts.length == 1 && currentItem.getMaterialName().equals(nameParts[0])) {
				found.add(currentItem);
			} else if (currentItem.getSmpName().equals(nameParts[0]) && currentItem.getMaterialName().equals(nameParts[1])) {
				found.add(currentItem);
			}
		}
		
		// Then also check for matching tools.
		MMCustomTool currentTool;
		for (Integer i = 0; i < this.toolsList.size(); i++) {
			currentTool = this.toolsList.get(i);
			if (nameParts.length == 1 && currentTool.getMaterialName().equals(nameParts[0])) {
				found.add(currentTool);
			} else if (currentTool.getSmpName().equals(nameParts[0]) && currentTool.getMaterialName().equals(nameParts[1])) {
				found.add(currentTool);
			}
		}
		
		return found;
	}

	public Material getMaterial(Integer materialId) {
		// First check for matching blocks.
		MMCustomBlock currentBlock;
		for (Integer i = 0; i < this.blocksList.size(); i++) {
			currentBlock = this.blocksList.get(i);
			if (currentBlock.getCustomId() == materialId) {
				return currentBlock;
			}
		}
		
		// Then also check for matching items.
		MMCustomItem currentItem;
		for (Integer i = 0; i < this.itemsList.size(); i++) {
			currentItem = this.itemsList.get(i);
			if (currentItem.getCustomId() == materialId) {
				return currentItem;
			}
		}
		
		// Then also check for matching tools.
		MMCustomTool currentTool;
		for (Integer i = 0; i < this.toolsList.size(); i++) {
			currentTool = this.toolsList.get(i);
			if (currentTool.getCustomId() == materialId) {
				return currentTool;
			}
		}
		
		return null;
	}

	public MMLegacyMaterial getLegacyMaterial(Integer materialId) {
		// Get correct legacy material.
		MMLegacyMaterial currentMaterial;
		for (Integer i = 0; i < this.legacyList.size(); i++) {
			currentMaterial = this.legacyList.get(i);
			if (currentMaterial.getMaterialId() == materialId) {
				return currentMaterial;
			}
		}
		return null;
	}

}
