/*
 The MIT License

 Copyright (c) 2012 Zloteanu Nichita (ZNickq) and Andre Mohren (IceReaper)

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

package net.morematerials.materials;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.morematerials.MoreMaterials;

import org.bukkit.configuration.file.YamlConfiguration;
import org.getspout.spoutapi.block.design.GenericBlockDesign;
import org.getspout.spoutapi.block.design.Quad;
import org.getspout.spoutapi.block.design.Texture;

public class CustomShape extends GenericBlockDesign {

	private String smpName;
	private String matName;
	private YamlConfiguration config;
	private MoreMaterials plugin;

	public CustomShape(MoreMaterials plugin, ZipFile smpFile, ZipEntry entry) {
		this.plugin = plugin;
		
		this.smpName = plugin.getUtilsManager().getName(smpFile.getName());
		Integer index = entry.getName().lastIndexOf(".");
		this.matName = entry.getName().substring(0, index);
		
		// Read the .shape file
		this.config = new YamlConfiguration();
		try {
			this.config.load(smpFile.getInputStream(entry));
		} catch (Exception exception) {
		}
		
		// If set, surrounded blocks will be drawn.
		if (this.config.contains("Transparent") && this.config.getBoolean("Transparent")) {
			this.setRenderPass(1);
		}
		
		String[] boundingBox = this.config.getString("BoundingBox").split(" ");

		// Bounding box
		int xMin = Integer.parseInt(boundingBox[0]);
		int yMin = Integer.parseInt(boundingBox[1]);
		int zMin = Integer.parseInt(boundingBox[2]);
		int xMax = Integer.parseInt(boundingBox[3]);
		int yMax = Integer.parseInt(boundingBox[4]);
		int zMax = Integer.parseInt(boundingBox[5]);
		setBoundingBox(xMin, yMin, zMin, xMax, yMax, zMax);
	}
	
	public void build(String textureUrl) {
		@SuppressWarnings("unchecked")
		Object check = ((Map<String, Object>) this.config.getList("Shapes").get(0)).get("Texture");
		Boolean oldShape = !(check instanceof String);
		
		if (oldShape) {
			this.plugin.getUtilsManager().log("Please update " + this.matName + ".shape", Level.WARNING);
			BufferedImage bufferedImage = this.plugin.getWebManager().getCachedImage(textureUrl);
			int textureCount = bufferedImage.getWidth() / bufferedImage.getHeight();
			Texture texture = new Texture(this.plugin, textureUrl, bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getHeight());
			this.setTexture(this.plugin, texture);
			int[] textureId = new int[textureCount];
			for (int i = 0; i < textureCount; i++) {
				textureId[i] = i;
			}
			
			// Building the shape together
			List<?> shapes = this.config.getList("Shapes");
			setQuadNumber(shapes.toArray().length);
			int i = 0;
			for (Object oshape : shapes) {
				@SuppressWarnings("unchecked")
				Map<String, Object> shape = (Map<String, Object>) oshape;
				String coords = (String) shape.get("Coords");
				Quad quad = new Quad(i, texture.getSubTexture(textureId[(Integer) shape.get("Texture")]));
				int j = 0;
				for (String line : coords.split("\\r?\\n")) {
					String[] coordLine = line.split(" ");
					quad.addVertex(j,
						Float.parseFloat(coordLine[0]),
						Float.parseFloat(coordLine[1]),
						Float.parseFloat(coordLine[2])
					);
					j++;
				}
				setLightSource(i, 0, 1, 0);
				setQuad(quad);
				i++;
			}
		} else {
			//TODO parse new shape format
		}
	}

	public String getMatName() {
		return this.matName;
	}

	public String getSmpName() {
		return this.smpName;
	}

}
