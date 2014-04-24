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
package net.morematerials.manager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import org.getspout.spoutapi.SpoutManager;

import net.morematerials.MoreMaterials;

public class AssetManager {

	private MoreMaterials plugin;
	private HashMap<String, BufferedImage> imageCache = new HashMap<String, BufferedImage>();

	public AssetManager(MoreMaterials plugin) {
		this.plugin = plugin;
	}

	public BufferedImage getCachedImage(String cacheFileName) {
		// Fix for avoid using the anchor character.
		if (this.imageCache.containsKey(cacheFileName)) {
			return this.imageCache.get(cacheFileName);
		}
		return null;
	}
	
	public void freeImageCacheMemory() {
		this.imageCache.clear();
	}

	public void addAsset(ZipFile smpFile, ZipEntry entry) {
		String cacheFileName = this.plugin.getUtilsManager().getName(smpFile.getName()) + "_" + entry.getName();

		// Extract files to cache dir.
		File cacheFile = new File(new File(this.plugin.getDataFolder().getPath(), "cache"), cacheFileName);
		try {
			InputStream inputStream = smpFile.getInputStream(entry);
			OutputStream out = new FileOutputStream(cacheFile);
			int read;
			byte[] bytes = new byte[1024];
			while ((read = inputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
			inputStream.close();
		} catch (Exception exception) {
		}
		cacheFile.deleteOnExit();

		// Cache all image buffers for performance.
		if (entry.getName().endsWith(".png")) {
			BufferedImage bufferedImage = null;
			try {
				bufferedImage = ImageIO.read(cacheFile);
			} catch (Exception exception) {
			}
			this.imageCache.put(cacheFileName, bufferedImage);
		}
		
		// Add file to spout cache.
		SpoutManager.getFileManager().addToPreLoginCache(this.plugin, cacheFile);
	}

}
