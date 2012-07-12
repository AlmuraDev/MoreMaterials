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

import java.util.List;

import net.morematerials.MoreMaterials;

import org.bukkit.configuration.file.YamlConfiguration;
import org.getspout.spoutapi.block.design.GenericBlockDesign;
import org.getspout.spoutapi.material.block.GenericCustomBlock;

public class MMCustomBlock extends GenericCustomBlock {

	private String materialName;
	private String smpName;

	@SuppressWarnings("unchecked")
	public static MMCustomBlock create(MoreMaterials plugin, YamlConfiguration yaml, String smpName, String matName) {
		String name = yaml.getString("Title", matName);
		Boolean rotate = yaml.getBoolean("Rotation", false);
		String texture = yaml.getString("Texture");
		texture = plugin.getWebManager().getAssetsUrl(smpName + "_" + texture);
		Integer baseId = yaml.getInt("BaseId", 1);

		// Getting the correct model for this block.
		GenericBlockDesign design;
		String shapeFile = yaml.getString("Shape");
		Integer index = shapeFile.lastIndexOf(".");
		shapeFile = shapeFile.substring(0, index);
		CustomShape customDesign;
		if (shapeFile != null && plugin.getSmpManager().getShape(smpName, shapeFile) != null) {
			customDesign = plugin.getSmpManager().getShape(smpName, shapeFile);
		} else {
			customDesign = new CustomShape(plugin);
		}
		customDesign.build(texture, (List<String>) yaml.getList("Coords"));
		design = customDesign;
		
		return new MMCustomBlock(plugin, name, texture, smpName, matName, design, rotate, baseId);
	}

	public MMCustomBlock(MoreMaterials plugin, String name, String texture, String smpName, String matName, GenericBlockDesign design, Boolean rotate, Integer baseId) {
		super(plugin, name, baseId, design, rotate);
		this.smpName = smpName;
		this.materialName = matName;
	}
	
	public String getSmpName() {
		return this.smpName;
	}
	
	public String getMaterialName() {
		return this.materialName;
	}

}
