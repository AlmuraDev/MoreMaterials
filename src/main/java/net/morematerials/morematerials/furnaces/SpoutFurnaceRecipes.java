/*
 The MIT License

 Copyright (c) 2011 Zloteanu Nichita (ZNickq) and Andre Mohren (IceReaper)

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
package net.morematerials.morematerials.furnaces;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.server.FurnaceRecipes;
import net.morematerials.morematerials.Main;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class SpoutFurnaceRecipes extends FurnaceRecipes {

	private static Map<String, net.minecraft.server.ItemStack> spoutMap = new HashMap<String, net.minecraft.server.ItemStack>();

	public SpoutFurnaceRecipes() {
		super();// Important to do, to preserve original furnace recipes!
	}

	@Override
	public net.minecraft.server.ItemStack a(net.minecraft.server.ItemStack i) {
		String str = i.getData() + " " + i.id;
		if (spoutMap.containsKey(str)) {
			return spoutMap.get(str);
		}
		return super.a(i);
	}

	public static void registerSpoutRecipe(ItemStack ingredient, ItemStack result) {
		registerSpoutRecipe(getNotchStack(ingredient), getNotchStack(result));
	}

	private static void registerSpoutRecipe(net.minecraft.server.ItemStack i, net.minecraft.server.ItemStack result) {
		String str = i.getData() + " " + i.id;
		spoutMap.put(str, result);

	}

	private static net.minecraft.server.ItemStack getNotchStack(ItemStack is) {
		return (new CraftItemStack(is)).getHandle();
	}

	public static void registerSpoutRecipe(SpoutFurnaceRecipe fRecipe) {
		registerSpoutRecipe(fRecipe.getIngredient(), fRecipe.getResult());
	}

	public static void hook(Main plugin) {
		plugin.log("Attempting to hook furnace recipes...");
		FurnaceRecipes.a = new SpoutFurnaceRecipes();
		plugin.log("Furnace recipes hooked!");
	}
}
