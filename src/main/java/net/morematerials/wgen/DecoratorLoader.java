/*
 * This file is part of MoreMaterials, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2013 AlmuraDev <http://www.almuradev.com/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.morematerials.wgen;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import net.morematerials.MoreMaterials;
import net.morematerials.wgen.ore.CustomOreDecorator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.getspout.spoutapi.material.CustomBlock;
import org.getspout.spoutapi.material.MaterialData;

/**
 * Creates {@link Decorator}s from a yaml configuration file
 */
public class DecoratorLoader {
	private final MoreMaterials plugin;
	private File folderSrc;

	public DecoratorLoader(MoreMaterials plugin) {
		this.plugin = plugin;
	}

	public void onEnable(File folderSrc) {
		this.folderSrc = folderSrc;
		try {
			Files.createDirectories(folderSrc.toPath());
			plugin.saveResource("objects.yml", false);
		} catch (IOException e) {
			plugin.getLogger().severe("Could not create " + folderSrc.getPath() + "! Disabling...");
			//plugin.getServer().getPluginManager().disablePlugin(plugin);
		}
	}

	public void load() {
		try {
			Files.walkFileTree(Paths.get(folderSrc.getPath() + File.separator + "objects.yml"), new FileLoadingVisitor(plugin));
		} catch (IOException ignore) {
			plugin.getLogger().severe("Encountered a major issue while attempting to find objects.yml in " + folderSrc.toPath() + ". Disabling...");
			//plugin.getServer().getPluginManager().disablePlugin(plugin);
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
		final List<Decorator> toInject = createObjects(path.toFile());
		if (toInject == null) {
			plugin.getLogger().severe("Could not load: " + path.getFileName() + ".");
			return FileVisitResult.TERMINATE;
		}
		plugin.getDecoratorRegistry().addAll(toInject);
		return FileVisitResult.TERMINATE;
	}

	private List<Decorator> createObjects(File yml) {
		final YamlConfiguration reader = YamlConfiguration.loadConfiguration(yml);
		final List<Decorator> objects = new ArrayList<>();

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
				final ConfigurationSection blockSourceSection = oresSection.getConfigurationSection(nameRaw);
				final String[] split = initialBlockSource.split("\\.");				
				final String identifier = split[split.length - 1];				
				final int minHeight = blockSourceSection.getInt("min-height", 1);
				final int maxHeight = blockSourceSection.getInt("max-height", 1);
				final int minVeinSize = blockSourceSection.getInt("min-vein-size", 1);
				final int maxVeinSize = blockSourceSection.getInt("max-vein-size", 1);
				final int minVeinsPerChunk = blockSourceSection.getInt("min-veins-per-chunk", 1);
				final int maxVeinsPerChunk = blockSourceSection.getInt("max-veins-per-chunk", 1);
				objects.add(new CustomOreDecorator(identifier, ore, minHeight, maxHeight, minVeinSize, maxVeinSize, minVeinsPerChunk, maxVeinsPerChunk));
			}
		}
		return objects;
	}

	private String replacePeriodWithBackslash(String raw) {
		return raw.replace("\\", ".");
	}
}
