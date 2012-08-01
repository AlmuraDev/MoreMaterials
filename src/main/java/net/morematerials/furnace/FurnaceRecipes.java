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

package net.morematerials.furnace;

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class FurnaceRecipes {

	public static HashMap<Short, ItemStack> spoutreciperesult = new LinkedHashMap<Short, ItemStack>();
	public static HashMap<Short, ItemStack> dyereciperesult = new LinkedHashMap<Short, ItemStack>();
	public static HashMap<Short, ItemStack> woolreciperesult = new LinkedHashMap<Short, ItemStack>();
	public static HashMap<Short, ItemStack> woodreciperesult = new LinkedHashMap<Short, ItemStack>();
	public static HashMap<Short, ItemStack> planksreciperesult = new LinkedHashMap<Short, ItemStack>();
	public static HashMap<Short, ItemStack> leavesreciperesult = new LinkedHashMap<Short, ItemStack>();
	public static HashMap<Short, ItemStack> sapplingreciperesult = new LinkedHashMap<Short, ItemStack>();
	public static HashMap<Short, ItemStack> coalreciperesult = new LinkedHashMap<Short, ItemStack>();
	public static HashMap<Short, ItemStack> slabreciperesult = new LinkedHashMap<Short, ItemStack>();
	public static HashMap<Short, ItemStack> stonebrickreciperesult = new LinkedHashMap<Short, ItemStack>();
	public static HashMap<Short, ItemStack> spawneggreciperesult = new LinkedHashMap<Short, ItemStack>();

	public static void CustomFurnaceRecipe(ItemStack result, int id, int data) {
		if (data != 0) {
			if (id == 318) {
				spoutreciperesult.put((short) data, result);
			} else if (id == 351) {
				dyereciperesult.put((short) data, result);
			} else if (id == 35) {
				woolreciperesult.put((short) data, result);
			} else if (id == 17) {
				woodreciperesult.put((short) data, result);
			} else if (id == 5) {
				planksreciperesult.put((short) data, result);
			} else if (id == 18) {
				leavesreciperesult.put((short) data, result);
			} else if (id == 6) {
				sapplingreciperesult.put((short) data, result);
			} else if (id == 263) {
				coalreciperesult.put((short) data, result);
			} else if (id == 44) {
				slabreciperesult.put((short) data, result);
			} else if (id == 98) {
				stonebrickreciperesult.put((short) data, result);
			} else if (id == 383) {
				spawneggreciperesult.put((short) data, result);
			}

			FurnaceRecipe fr = new FurnaceRecipe(result, (new MaterialData(id, (byte) data)));
			Bukkit.getServer().addRecipe(fr);
		} else {
			FurnaceRecipe fr = new FurnaceRecipe(result, Material.getMaterial(id));
			Bukkit.getServer().addRecipe(fr);
		}
	}
}
