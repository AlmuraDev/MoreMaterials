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

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.bukkit.Bukkit;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.material.MaterialData;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.Material;

public class FurnaceRecipeManager {
	
	public HashMap<Material, SpoutItemStack> recipes = new LinkedHashMap<Material, SpoutItemStack>();

	public void registerRecipe(SpoutItemStack result, Material ingredient) {
		this.recipes.put(ingredient, result);
        System.out.println("Ingredient: " + ingredient);
        System.out.println("Result: " + result);
		Bukkit.getServer().addRecipe(new FurnaceRecipe(result, (new MaterialData(ingredient.getRawId(), (byte) ingredient.getRawId()))));
        System.out.println("Add Recipe: " + Bukkit.getServer().addRecipe(new FurnaceRecipe(result, (new MaterialData(ingredient.getRawId(), (byte) ingredient.getRawId())))));
	}

	public SpoutItemStack getResult(SpoutItemStack source) {
		if (this.recipes.containsKey(source.getMaterial())) {
            System.out.println(source.getMaterial());
			return this.recipes.get(source.getMaterial());
		}
		return null;
	}
	
}
