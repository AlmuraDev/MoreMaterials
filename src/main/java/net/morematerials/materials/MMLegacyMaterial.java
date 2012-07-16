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

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

public class MMLegacyMaterial {
	
	private Material material;
	private ArrayList<YamlConfiguration> configs = new ArrayList<YamlConfiguration>();
	private ArrayList<String> requiredTools = new ArrayList<String>();

	public MMLegacyMaterial(YamlConfiguration config, String smpName, String materialId) {
		this.material = Material.getMaterial(Integer.parseInt(materialId));
		this.configureBase(smpName, config);
	}

	public void configureBase(String smpName, YamlConfiguration config) {
		this.configs.add(config);
		// Set the required tools to make this block drop
		if (config.contains("RequiredTools") && this.material.isBlock()) {
			String[] tools = config.getString("RequiredTools").split("[\\s]+");
			for (Integer i = 0; i < tools.length; i++) {
				this.requiredTools.add(smpName + "_" + tools[i]);
			}
		}
	}

	public Integer getMaterialId() {
		return this.material.getId();
	}

	public ArrayList<String> getRequiredTools() {
		return this.requiredTools;
	}

}
