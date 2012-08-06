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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.morematerials.MoreMaterials;
import net.morematerials.materials.shapes.CustomObjShape;
import net.morematerials.materials.shapes.CustomShapeShape;

import org.getspout.spoutapi.block.design.GenericBlockDesign;

public class CustomShapeTemplate {
	
	private String format;
	private String shapeData;
	private MoreMaterials plugin;
	
	public CustomShapeTemplate(MoreMaterials plugin, ZipFile smpFile, ZipEntry entry) {
		this.plugin = plugin;
		
		// We can specify the format by fileending.
		this.format = entry.getName().substring(entry.getName().lastIndexOf('.') + 1);
		
		// Now we need to extract the format data.
		try {
			InputStream stream = smpFile.getInputStream(entry);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
			StringBuilder stringBuilder = new StringBuilder();
			
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line + "\n");
			}
			
			this.shapeData = stringBuilder.toString();
			stream.close();
		} catch (Exception exception) {
		}
	}

	public CustomShapeTemplate(MoreMaterials plugin) {
		this.plugin = plugin;
		
		// We can specify the format by fileending.
		this.format = "shape";
		
		// Now we need to extract the format data.
		try {
			InputStream stream = this.plugin.getResource("cube.shape");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
			StringBuilder stringBuilder = new StringBuilder();
			
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line + "\n");
			}
			
			this.shapeData = stringBuilder.toString();
			stream.close();
		} catch (Exception exception) {
		}
	}

	public GenericBlockDesign createInstance(String textureUrl, List<String> coordList) {
		if (this.format.equals("shape")) {
			return new CustomShapeShape(this.plugin, shapeData, textureUrl, coordList);
		} else if (this.format.equals("obj")) {
			try {
				return new CustomObjShape(this.plugin, shapeData, textureUrl, coordList);
			} catch (Exception exception) {
				this.plugin.getUtilsManager().log("Cannot load the shape file: " + shapeData ,  Level.WARNING);
			}
		}
		return null;
	}

}
