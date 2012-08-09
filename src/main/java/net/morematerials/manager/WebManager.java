/*
 * This file is part of MoreMaterials.
 * 
 * Copyright (c) 2012 Andre Mohren (IceReaper)
 * 
 * The MIT License
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import org.getspout.spoutapi.SpoutManager;

import net.morematerials.MoreMaterials;
import net.morematerials.http.MMHttpHandler;

import com.sun.net.httpserver.HttpServer;

public class WebManager {

	private String assetsUrl;
	private MoreMaterials plugin;
	private HashMap<String, BufferedImage> imageCache = new HashMap<String, BufferedImage>();

	public WebManager(MoreMaterials plugin) {
		this.plugin = plugin;

		// Get an unused port.
		Integer port = this.plugin.getConfig().getInt("Port", 8080);
		for (Integer i = port; i < port + 100; i++) {
			try {
				new ServerSocket(i).close();
				port = i;
				break;
			} catch (IOException exception) {
			}
		}

		// Get the hostname of this machine.
		String hostname = this.plugin.getConfig().getString("Hostname");
		if (hostname == null) {
			hostname = "127.0.0.1";
			if (!plugin.getServer().getIp().equals("127.0.0.1")) {
				hostname = plugin.getServer().getIp();
			} else {
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("http://automation.whatismyip.com/n09230945.asp").openStream()));
					hostname = reader.readLine();
				} catch (IOException exception) {
				}
			}
		}

		// Store assetsUrl
		this.assetsUrl = hostname + ":" + port;

		// Create assets-server.
		try {
			HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
			server.createContext("/", new MMHttpHandler(plugin));
			server.setExecutor(null);
			server.start();
			plugin.getUtilsManager().log("Listening: " + this.assetsUrl);
		} catch (IOException exception) {
			plugin.getUtilsManager().log("Assets server error!", Level.SEVERE);
		}
	}

	public String getAssetsUrl(String asset) {
		// Fix for avoid using the anchor character.
		asset = asset.replace("#", "_");
		return "http://" + this.assetsUrl + "/" + asset;
	}

	public BufferedImage getCachedImage(String cacheFileName) {
		// Fix for avoid using the anchor character.
		cacheFileName = cacheFileName.replace("#", "_");
		if (this.imageCache.containsKey(cacheFileName)) {
			return this.imageCache.get(cacheFileName);
		}
		return null;
	}

	public void addAsset(ZipFile smpFile, ZipEntry entry) {
		String cacheFileName = this.plugin.getUtilsManager().getName(smpFile.getName()) + "_" + entry.getName();
		// Fix for avoid using the anchor character.
		cacheFileName = cacheFileName.replace("#", "_");
		String path = this.getAssetsUrl(cacheFileName);

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
			this.imageCache.put(path, bufferedImage);
		}
		// Add file to spout cache.
		SpoutManager.getFileManager().addToCache(this.plugin, path);
	}

}
