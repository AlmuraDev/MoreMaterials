package net.morematerials.manager;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
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
import org.getspout.spoutapi.SpoutManager;

import net.morematerials.MoreMaterials;

public class UpdateManager {
	
	private MoreMaterials plugin;
	private File tempDir;
	private ArrayList<String> itemMap = new ArrayList<String>();

	public UpdateManager(MoreMaterials plugin) {
		this.plugin = plugin;
		this.tempDir = new File(this.plugin.getDataFolder().getPath(), "updater");
		
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
				
				// Copy over recipes - nothing changed here!
				if (oldYaml.contains("Recipes")) {
					newYaml.set("Recipes", oldYaml.getList("Recipes"));
				}
				
				// Finally store the new .yml file.
				String yamlString = newYaml.saveToString();
				BufferedWriter out = new BufferedWriter(new FileWriter(new File(this.tempDir, materialName + ".yml")));
				out.write(this.fixYamlProblems(yamlString));
				out.close();
				
				// Also update itemmap entry!
				for (Integer i = 0; i < this.itemMap.size(); i++) {
					String oldMaterial = this.itemMap.get(i).replaceAll("^[0-9]+:MoreMaterials.", "");
					if (oldMaterial.equals(newYaml.getString("Title"))) {
						this.itemMap.set(i, this.itemMap.get(i).replaceAll("^([0-9]+:MoreMaterials.).+$", "$1" + smpName + "." + materialName));
						break;
					}
				}
				
				// And we need to tell SpoutPlugin that this material must be renamed!
				SpoutManager.getMaterialManager().renameMaterialKey(this.plugin, newYaml.getString("Title"), smpName + "." + materialName);
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
		} else {
			// At last, close the file handle.
			smpFile.close();
		}
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
		
		// Also the drop stuff...
		if (oldYaml.contains("ItemDrop")) {
			newYaml.set("ItemDrop", oldYaml.getString("ItemDrop"));
		}
		if (oldYaml.contains("ItemDropAmount")) {
			newYaml.set("ItemDropAmount", oldYaml.getInt("ItemDropAmount"));
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

	private String fixYamlProblems(String yamlString) {
		// Fixes for crafting recipes, which may be lower case, because we simply copied them above.
		yamlString = yamlString.replaceAll("([\r\n][\\- ]+)ingredients:", "$1Ingredients:");
		yamlString = yamlString.replaceAll("([\r\n][\\- ]+)type: shaped", "$1Type: Shaped");
		yamlString = yamlString.replaceAll("([\r\n][\\- ]+)type: shapeless", "$1Type: Shapeless");
		yamlString = yamlString.replaceAll("([\r\n][\\- ]+)type: furnace", "$1Type: Furnace");
		yamlString = yamlString.replaceAll("([\r\n][\\- ]+)amount:", "$1Amount:");
		
		// This fixes the invalid recipes format.
		yamlString = yamlString.replaceAll("(Ingredients: )'([0-9 ]+)\\s+([0-9 ]+)\\s+([0-9 ]+)\\s+'", "$1|\n    $2\n    $3\n    $4");
		return yamlString;
	}
	
}
