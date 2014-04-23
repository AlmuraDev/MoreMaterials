package net.morematerials.wgen;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import net.morematerials.MoreMaterials;
import net.morematerials.wgen.ore.CustomOre;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.material.MaterialData;

/**
 * Creates {@link net.morematerials.wgen.GeneratorObject}s from a yaml configuration file
 */
public class GeneratorReader {
	private final MoreMaterials plugin;
	private File dir;

	public GeneratorReader(MoreMaterials plugin) {
		this.plugin = plugin;
	}

	public void onEnable(File dir) {
		this.dir = dir;
		try {
			Files.createDirectories(dir.toPath());
			Files.createFile(Paths.get(dir.getPath() + File.separator + "objects.yml"));
		} catch (FileAlreadyExistsException ignore) {
		} catch (IOException e) {
			plugin.getLogger().severe("Could not create " + dir.getPath() + "! Disabling...");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
		}
	}

	public void load() {
		try {
			Files.walkFileTree(Paths.get(dir.getPath() + File.separator + "objects.yml"), new FileLoadingVisitor(plugin));
		} catch (IOException ignore) {
			plugin.getLogger().severe("Encountered a major issue while attempting to find " + dir.toPath() + ". Disabling...");
			plugin.getServer().getPluginManager().disablePlugin(plugin);
		}
	}
}

class FileLoadingVisitor extends SimpleFileVisitor<Path> {
	private final MoreMaterials plugin;

	public FileLoadingVisitor(MoreMaterials plugin) {
		this.plugin = plugin;
	}

	@Override
	public FileVisitResult visitFileFailed(Path path, IOException ioe) {
		return FileVisitResult.TERMINATE;
	}

	@Override
	public FileVisitResult visitFile(Path path, BasicFileAttributes attr) {
		final List<GeneratorObject> toInject = createObjects(path.toFile());
		if (toInject == null) {
			plugin.getLogger().severe("Could not load: " + path.getFileName() + ".");
			return FileVisitResult.TERMINATE;
		}
		//TODO Add to Generator object registry
		return FileVisitResult.TERMINATE;
	}

	private List<GeneratorObject> createObjects(File yml) {
		final YamlConfiguration reader = YamlConfiguration.loadConfiguration(yml);
		final List<GeneratorObject> objects = new ArrayList<>();

		//We'll do ores first
		final ConfigurationSection oresSection = reader.getConfigurationSection("ores");
		if (oresSection != null) {
			for (String nameRaw : oresSection.getKeys(false)) {
				//BLOCK SOURCE
				final String initialRawBlockSource = replacePeriodWithBackslash(nameRaw);
				final String initialBlockSource = replacePeriodWithBackslash(initialRawBlockSource);
				final CustomBlock ore = MaterialData.getCustomBlock(initialBlockSource);
				if (ore == null) {
					plugin.getLogger().warning("The block source [" + initialBlockSource + "] is not a SpoutPlugin Custom Block. Skipping...");
					continue;
				}
				//ATTRIBUTES
				final ConfigurationSection blockSourceSection = reader.getConfigurationSection(nameRaw);
				final String name = blockSourceSection.getString("identifier");
				final int minHeight = blockSourceSection.getInt("min-height", 1);
				final int maxHeight = blockSourceSection.getInt("max-height", 1);
				final int minVeinSize = blockSourceSection.getInt("min-vein-size", 1);
				final int maxVeinSize = blockSourceSection.getInt("max-vein-size", 1);
				final int minVeinsPerChunk = blockSourceSection.getInt("min-veins-per-chunk", 1);
				final int maxVeinsPerChunk = blockSourceSection.getInt("max-veins-per-chunk", 1);
				objects.add(new CustomOre(name, ore, minHeight, maxHeight, minVeinSize, maxVeinSize, minVeinsPerChunk, maxVeinsPerChunk));
			}
		}
		return objects;
	}

	private String replacePeriodWithBackslash(String raw) {
		return raw.replace("\\", ".");
	}
}
