package net.morematerials.manager;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import net.morematerials.MoreMaterials;

public class UpdateManager {
	
	private MoreMaterials plugin;
	private File tempDir;
	private ArrayList<String> itemMap = new ArrayList<String>();

	public UpdateManager(MoreMaterials plugin) {
		this.plugin = plugin;
		this.tempDir = new File(this.plugin.getDataFolder().getPath(), "updater");
		
		// This file needs to be updated, or the wrong items will show up!
		File itemMap = new File(this.plugin.getDataFolder().getParent(), "Spout/itemMap.txt");
		try {
			InputStream stream = new FileInputStream(itemMap);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				this.itemMap.add(line.trim());
			}
			stream.close();
		} catch (Exception exception) {
		}
		
		// Get all .smp files.
		File dir = new File(this.plugin.getDataFolder().getPath(), "materials");
		for (File file : dir.listFiles()) {
			if (file.getName().endsWith(".smp")) {
				try {
					this.updateSmp(file);
				} catch (Exception exception) {
				}
			}
		}
		
		// At last store the new item-map file!
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(new File(this.plugin.getDataFolder().getParent(), "Spout/NEW_itemMap.txt")));
			StringBuilder stringBuilder = new StringBuilder();
			for (String line : this.itemMap) {
				stringBuilder.append(line + "\n");
			}
			out.write(stringBuilder.toString());
			out.close();
		} catch (Exception exception) {
		}
	}
	
	private void updateSmp(File file) throws Exception {
		ZipFile smpFile = new ZipFile(file);
		Enumeration<? extends ZipEntry> entries = smpFile.entries();
		String smpName = file.getName().substring(0, file.getName().lastIndexOf("."));
		
		// First we need to know what files are in this .smp file, because the old format uses magic filename matching.
		ArrayList<String> containedFiles = new ArrayList<String>();
		HashMap<String, YamlConfiguration> materials = new HashMap<String, YamlConfiguration>();

		// Now we walk through the file once and store every file.
		ZipEntry entry;
		YamlConfiguration yaml;
		while (entries.hasMoreElements()) {
			entry = entries.nextElement();
			
			// Only if its a .yml file
			if (entry.getName().endsWith(".yml")) {
				// Load the .yml file
				yaml = new YamlConfiguration();
				yaml.load(smpFile.getInputStream(entry));
				
				// Texture is required for new package format.
				if (!yaml.contains("Texture")) {
					materials.put(entry.getName().substring(0, entry.getName().lastIndexOf(".")), yaml);
				} else {
					containedFiles.add(entry.getName());
				}
			} else {
				containedFiles.add(entry.getName());
			}
		}
		
		// If this map contains any entry, we need to convert something.
		if (materials.size() > 0) {
			this.plugin.getUtilsManager().log("Deprecated .smp found: " + file.getName() + ". Updating...");
			
			// We need a temporary directory to update the .smp in.
			this.tempDir.mkdir();
			
			// First extract all untouched assets:
			for (String filename : containedFiles) {
				InputStream in = smpFile.getInputStream(smpFile.getEntry(filename));
				OutputStream out = new FileOutputStream(new File(this.tempDir, filename));
				int read;
				byte[] bytes = new byte[1024];
				while ((read = in.read(bytes)) != -1) {
					out.write(bytes, 0, read);
				}
				out.flush();
				out.close();
				in.close();
			}
			
			// Now convert each .yml file in this archive.
			YamlConfiguration oldYaml;
			YamlConfiguration newYaml;
			for (String materialName : materials.keySet()) {
				oldYaml = materials.get(materialName);
				newYaml = new YamlConfiguration();

				// Required "Type" which is Block or Item. Old format didnt support Tools anyway.
				newYaml.set("Type", oldYaml.getString("Type"));
				// Title is now required and falls back to filename.
				newYaml.set("Title", oldYaml.getString("Title", materialName));
				
				// Now call the converter methods.
				if (newYaml.getString("Type").equals("Block")) {
					this.convertBlock(oldYaml, newYaml, materialName, containedFiles);
					this.convertBlockHandlers(oldYaml, newYaml);
				} else if (newYaml.getString("Type").equals("Item")) {
					this.convertItem(oldYaml, newYaml, materialName, containedFiles);
					this.convertItemHandlers(oldYaml, newYaml);
				}
				
				// Finally store the new .yml file.
				newYaml.save(new File(this.tempDir, materialName + ".yml"));
				
				// Also update itemmap entry!
				for (Integer i = 0; i < this.itemMap.size(); i++) {
					String oldMaterial = this.itemMap.get(i).replaceAll("^[0-9]+:MoreMaterials.", "");
					if (oldMaterial.equals(newYaml.getString("Title"))) {
						this.itemMap.set(i, this.itemMap.get(i).replaceAll("^([0-9]+:MoreMaterials.).+$", "$1" + smpName + "." + materialName));
						break;
					}
				}
			}
			
			// First remove old .smp file
			smpFile.close();
			file.delete();
			
			// Then repack the new .smp file
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(file));
			for (File entryFile : this.tempDir.listFiles()) {
				FileInputStream in = new FileInputStream(entryFile);
				out.putNextEntry(new ZipEntry(entryFile.getName()));
				Integer len;
				byte[] buf = new byte[1024];
		        while ((len = in.read(buf)) > 0) {
		            out.write(buf, 0, len);
		        }
		        out.closeEntry();
		        in.close();
			}
			out.close();
			
			// At last remove the temp directory.
			FileUtils.deleteDirectory(this.tempDir);
			this.plugin.getUtilsManager().log("-----------------------------------");
			this.plugin.getUtilsManager().log("Convert completed! You should now be able to use all old .smp packs.");
			this.plugin.getUtilsManager().log("If this is not a new world, all placed blocks are broken.");
			this.plugin.getUtilsManager().log("In this case, shutdown your server.");
			this.plugin.getUtilsManager().log("Then rename NEW_itemMap.txt to itemMap.txt in your Spout folder.");
			this.plugin.getUtilsManager().log("-----------------------------------");
		} else {
			// At last, close the file handle.
			smpFile.close();
		}
		
		//TODO convert legacyrecipes.yml if found
	}

	private void convertBlock(YamlConfiguration oldYaml, YamlConfiguration newYaml, String materialName, ArrayList<String> containedFiles) throws Exception {
		// This one is renamed because in the future items can fork items.
		if (oldYaml.contains("BlockID")) {
			newYaml.set("BaseId", oldYaml.getInt("BlockID"));
		}
		
		// Rotation was never officially, but we will support 1.7.1 changes.
		if (oldYaml.contains("Rotate")) {
			newYaml.set("Rotation", oldYaml.getBoolean("Rotate"));
		}
		
		// Texture was a magic-function before. Replacing it now by correct one.
		newYaml.set("Texture", materialName + ".png");
		
		// Also there were no coords before, so we need to automatically calculate them for now.
		ArrayList<String> coords = new ArrayList<String>();
		BufferedImage bufferedImage = ImageIO.read(new File(this.tempDir, materialName + ".png"));
		Integer textureCount = bufferedImage.getWidth() / bufferedImage.getHeight();
		for (Integer i = 0; i < textureCount; i++) {
			coords.add("" + (i * bufferedImage.getHeight()) + " 0 " + bufferedImage.getHeight() + " " + bufferedImage.getHeight());
		}
		newYaml.set("Coords", coords);
		
		// Shape was also a magic function before, replacing that now.
		if (containedFiles.contains(materialName + ".shape")) {
			newYaml.set("Shape", materialName + ".shape");
		}
		
		// We can simply copy hardness...
		if (oldYaml.contains("Hardness")) {
			newYaml.set("Hardness", oldYaml.getDouble("Hardness"));
		}
		
		// Also the friction...
		if (oldYaml.contains("Friction")) {
			newYaml.set("Friction", oldYaml.getDouble("Friction"));
		}
		
		// And at last the lightlevel!
		if (oldYaml.contains("LightLevel")) {
			newYaml.set("LightLevel", oldYaml.getDouble("LightLevel"));
		}
		
		// Cannot convert handlers! (They are coded, so we really can't!)
		if (oldYaml.contains("WalkAction.Handler") || oldYaml.contains("Rclick.Handler") || oldYaml.contains("Lclick.Handler") || oldYaml.contains("RedstonePowered.Handler")) {
			this.plugin.getUtilsManager().log(" - " + materialName + " uses handlers, they cannot be converted automatically.");
		}
	}

	private void convertItem(YamlConfiguration oldYaml, YamlConfiguration newYaml, String materialName, ArrayList<String> containedFiles) throws Exception {
		// Texture was a magic-function before. Replacing it now by correct one.
		newYaml.set("Texture", materialName + ".png");
		
		// Also there were no coords before, so we need to automatically calculate them for now.
		ArrayList<String> coords = new ArrayList<String>();
		BufferedImage bufferedImage = ImageIO.read(new File(this.tempDir, materialName + ".png"));
		coords.add("0 0 " + bufferedImage.getWidth() + " " + bufferedImage.getHeight());
		newYaml.set("Coords", coords);
		
		// Cannot convert handlers! (They are coded, so we really can't!)
		if (oldYaml.contains("Rclick.Handler") || oldYaml.contains("Lclick.Handler")) {
			this.plugin.getUtilsManager().log(" - " + materialName + " uses handlers, they cannot be converted automatically.");
		}
	}
	
	private void convertBlockHandlers(YamlConfiguration oldYaml, YamlConfiguration newYaml) {
		//TODO (all handlers)
		// StepSound
		// WalkSpeed
		// JumpHeight
		// FallDamage
		// WalkAction
		// Rclick
		// Lclick
		// RedstonePowered
	}
	
	private void convertItemHandlers(YamlConfiguration oldYaml, YamlConfiguration newYaml) {
		//TODO (all handlers)
		// Damage
		// KeepEnchanting
		// Rclick
		// Lclick
	}
	
}