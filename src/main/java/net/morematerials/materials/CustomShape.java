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

package net.morematerials.materials;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.morematerials.MoreMaterials;

import org.bukkit.configuration.file.YamlConfiguration;
import org.getspout.spoutapi.block.design.GenericBlockDesign;
import org.getspout.spoutapi.block.design.Quad;
import org.getspout.spoutapi.block.design.SubTexture;
import org.getspout.spoutapi.block.design.Texture;

public class CustomShape extends GenericBlockDesign {

	private String smpName;
	private String matName;
	private YamlConfiguration config;
	private MoreMaterials plugin;
	private String format;

	public CustomShape(MoreMaterials plugin, ZipFile smpFile, ZipEntry entry) {
		this.plugin = plugin;

		this.smpName = plugin.getUtilsManager().getName(smpFile.getName());
		this.matName = entry.getName().substring(0, entry.getName().lastIndexOf("."));
		this.format = entry.getName().substring(entry.getName().lastIndexOf(".") + 1);

		// Read the .shape file
		this.config = new YamlConfiguration();
		try {
			this.config.load(smpFile.getInputStream(entry));
		} catch (Exception exception) {
		}
	}
	
	public CustomShape(MoreMaterials plugin, String smpName, String matName, YamlConfiguration config) {
		this.plugin = plugin;
		this.smpName = smpName;
		this.matName = matName;
		this.config = config;
	}

	public CustomShape(MoreMaterials plugin) {
		this.plugin = plugin;
		// Create a default cube
		this.config = new YamlConfiguration();
		try {
			this.config.load(this.plugin.getResource("cube.shape"));
		} catch (Exception exception) {
		}
	}
	
	public void build(String textureUrl, List<String> coordList) {
		if (format.equals("shape")) {
			this.buildFromShape(textureUrl, coordList);
		} else if (format.equals("obj")) {
			//TODO implement this :D
		} else if (format.equals("ply")) {
			//TODO implement this :D
		}
	}

	public void buildFromShape(String textureUrl, List<String> coordList) {
		// Surrounded blocks will always be drawn.
		this.setRenderPass(1);

		// Default settings.
		this.setMinBrightness(0.0F);
		this.setBrightness(0.5F);
		this.setMaxBrightness(1.0F);

		String[] boundingBox = this.config.getString("BoundingBox").split(" ");

		// Bounding box
		Float xMin = Float.parseFloat("0" + boundingBox[0]);
		Float yMin = Float.parseFloat("0" + boundingBox[1]);
		Float zMin = Float.parseFloat("0" + boundingBox[2]);
		Float xMax = Float.parseFloat("0" + boundingBox[3]);
		Float yMax = Float.parseFloat("0" + boundingBox[4]);
		Float zMax = Float.parseFloat("0" + boundingBox[5]);
		setBoundingBox(xMin, yMin, zMin, xMax, yMax, zMax);
		
		// Get texture.
		BufferedImage bufferedImage = this.plugin.getWebManager().getCachedImage(textureUrl);
		Texture texture = new Texture(this.plugin, textureUrl, bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getHeight());
		this.setTexture(this.plugin, texture);

		// Building subtextures.
		ArrayList<SubTexture> subTextures = new ArrayList<SubTexture>();
		String[] coords;
		for (Integer i = 0; i < coordList.size(); i++) {
			coords = coordList.get(i).split("[\\s]+");
			//FIXME spout reads Y from the lower left - this needs to be fixed!
			SubTexture subtex = new SubTexture(texture, Integer.parseInt(coords[0]), bufferedImage.getHeight() - (Integer.parseInt(coords[1]) + Integer.parseInt(coords[3])), Integer.parseInt(coords[2]), Integer.parseInt(coords[3]));
			subTextures.add(subtex);
		}

		// Building the shape together
		List<?> shapes = this.config.getList("Shapes");
		setQuadNumber(shapes.toArray().length);
		int i = 0;
		for (Object oshape : shapes) {
			@SuppressWarnings("unchecked")
			Map<String, Object> shape = (Map<String, Object>) oshape;
			Integer subId = (Integer) shape.get("Texture");
			Quad quad = new Quad(i, subTextures.get((subTextures.size() > subId ? subId : 0)));
			int j = 0;
			String[] coordLine = null;
			for (String line : ((String) shape.get("Coords")).split("\\r?\\n")) {
				coordLine = line.split(" ");
				quad.addVertex(j,
					Float.parseFloat("0" + coordLine[0]),
					Float.parseFloat("0" + coordLine[1]),
					Float.parseFloat("0" + coordLine[2])
				);
				j++;
			}
			// Allow triangles.
			if (j == 3) {
				quad.addVertex(j,
					Float.parseFloat("0" + coordLine[0]),
					Float.parseFloat("0" + coordLine[1]),
					Float.parseFloat("0" + coordLine[2])
				);
			}
			setQuad(quad);
			i++;
		}
	}

	public String getMatName() {
		return this.matName;
	}

	public String getSmpName() {
		return this.smpName;
	}

	public CustomShape createInstance() {
		return new CustomShape(this.plugin, this.smpName, this.matName, this.config);
	}

}
