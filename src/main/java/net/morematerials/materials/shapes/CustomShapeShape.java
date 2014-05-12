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
package net.morematerials.materials.shapes;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.morematerials.MoreMaterials;

import org.bukkit.configuration.file.YamlConfiguration;
import org.getspout.spoutapi.block.design.GenericBlockDesign;
import org.getspout.spoutapi.block.design.Quad;
import org.getspout.spoutapi.block.design.SubTexture;
import org.getspout.spoutapi.block.design.Texture;

public class CustomShapeShape extends GenericBlockDesign {

	public CustomShapeShape(MoreMaterials plugin, String shapeData, String textureUrl, List<String> coordList) {
		// Load the .shape format string.
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.loadFromString(shapeData);
		} catch (Exception exception) {
		}
		
		// Surrounded blocks will always be drawn.
		this.setRenderPass(1);

		// Default settings.
		this.setMinBrightness(0.0F);
		this.setBrightness(0.5F);
		this.setMaxBrightness(1.0F);

		String[] boundingBox = config.getString("BoundingBox").split(" ");

		// Bounding box
		Float xMin = Float.parseFloat("0" + boundingBox[0]);
		Float yMin = Float.parseFloat("0" + boundingBox[1]);
		Float zMin = Float.parseFloat("0" + boundingBox[2]);
		Float xMax = Float.parseFloat("0" + boundingBox[3]);
		Float yMax = Float.parseFloat("0" + boundingBox[4]);
		Float zMax = Float.parseFloat("0" + boundingBox[5]);
		setBoundingBox(xMin, yMin, zMin, xMax, yMax, zMax);
		
		// Get texture.
		BufferedImage bufferedImage = plugin.getAssetManager().getCachedImage(textureUrl);
		Texture texture = new Texture(plugin, textureUrl, bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getHeight());
		this.setTexture(plugin, texture);

		// Building subtextures.
		ArrayList<SubTexture> subTextures = new ArrayList<SubTexture>();
		String[] coords;
		for (Integer i = 0; i < coordList.size(); i++) {
			coords = coordList.get(i).split("[\\s]+");
			//FIXME spout reads Y from the lower left - this needs to be fixed!
			SubTexture subtex = new SubTexture(texture, Integer.parseInt(coords[0]), bufferedImage.getHeight() - (Integer.parseInt(coords[1]) + Integer.parseInt(coords[3])), Integer.parseInt(coords[2]), Integer.parseInt(coords[3]));
			subTextures.add(subtex);
		}
		
		// If no coords are set, whole texture is used.
		if (coordList.size() == 0) {
			subTextures.add(new SubTexture(texture, 0, 0, texture.getWidth(), texture.getHeight()));
		}

		// Building the shape together
		List<?> shapes = config.getList("Shapes");
		this.setQuadNumber(shapes.toArray().length);
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
			//FIXME implement non-quads into spout!
			if (j == 3) {
				quad.addVertex(j,
					Float.parseFloat("0" + coordLine[0]),
					Float.parseFloat("0" + coordLine[1]),
					Float.parseFloat("0" + coordLine[2])
				);
			}
			//setLightSource(i,0,1,0);
			setQuad(quad);
			i++;
		}
		this.calculateLightSources();
	}
	
}
