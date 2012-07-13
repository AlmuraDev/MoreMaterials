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

import net.morematerials.MoreMaterials;

import org.bukkit.configuration.file.YamlConfiguration;
import org.getspout.spoutapi.material.item.GenericCustomTool;

public class MMCustomItem extends GenericCustomTool {

	private String materialName;
	private String smpName;

	public static MMCustomItem create(MoreMaterials plugin, YamlConfiguration yaml, String smpName, String matName) {
		String texture = yaml.getString("Texture");
		texture = plugin.getWebManager().getAssetsUrl(smpName + "_" + texture);
		// TODO use texture Coords (looks like SpoutPlugin needs a patch)
		
		// Build the item.
		MMCustomItem item = new MMCustomItem(plugin, yaml.getString("Title", matName), texture, smpName, matName);
		item.configureBase(yaml);
		return item;
	}

	public MMCustomItem(MoreMaterials plugin, String name, String texture, String smpName, String matName) {
		super(plugin, name, texture);
		this.smpName = smpName;
		this.materialName = matName;
	}

	private void configureBase(YamlConfiguration config) {
		// Set the items durability
		this.setMaxDurability((short) config.getInt("Durability", 0));
		
		// Set the items stackability
		if (config.contains("Stackable")) {
			this.setStackable(config.getBoolean("Stackable"));
		}
	}

	public String getSmpName() {
		return this.smpName;
	}

	public String getMaterialName() {
		return this.materialName;
	}

}
