package net.spoutmaterials.spoutmaterials.reflection;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.server.FurnaceRecipes;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class SpoutFurnaceRecipes extends FurnaceRecipes {

	/*
	 * This is a reflection fix for allowing custom item furnace recipes...
	 */
	private static Map<String, ItemStack> spoutMap = new HashMap<String, ItemStack>();

	
	
	public SpoutFurnaceRecipes() {
		super();
		FurnaceRecipes.a=this;
	}

	@Override
	public net.minecraft.server.ItemStack a(net.minecraft.server.ItemStack i) {
		String str = i.getData()+"000"+i.id;
		System.out.println("Called! "+str);
		if (spoutMap.containsKey(str)) {
			return (new CraftItemStack(spoutMap.get(str))).getHandle();
			
		}
		return super.a(i);
	}

	public static void registerSpoutRecipe(ItemStack ingredient, ItemStack result) {
		spoutMap.put(ingredient.getData().getData()+"000"+ingredient.getTypeId(), result);
	}
	public static void registerSpoutRecipe(SpoutFurnaceRecipe fRecipe) {
		System.out.println("Registered "+fRecipe.getIngredient().getMaterial().getName());
		registerSpoutRecipe(fRecipe.getIngredient(),fRecipe.getResult());
	}
}
