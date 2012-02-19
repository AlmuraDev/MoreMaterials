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

package net.morematerials.morematerials.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import net.morematerials.morematerials.Main;
import net.morematerials.morematerials.furnaces.SpoutFurnaceRecipe;
import net.morematerials.morematerials.furnaces.SpoutFurnaceRecipes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Recipe;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.inventory.SpoutShapedRecipe;
import org.getspout.spoutapi.inventory.SpoutShapelessRecipe;
import org.getspout.spoutapi.material.Material;

public class LegacyManager {
	private List<SpoutFurnaceRecipe> furnaceRecipeList = new ArrayList<SpoutFurnaceRecipe>();
	private List<Recipe> craftingRecipeList = new ArrayList<Recipe>();
	private Main plugin;

	public LegacyManager(Main plugin) {
		this.plugin = plugin;
		this.load();
	}

	private void load() {
		File materials = new File(plugin.getDataFolder().getPath(), "legacyrecipes.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(materials);
		for (String itemId : config.getKeys(false)) {
			if (itemId.matches("^[0-9]+$")) {
				Material material = org.getspout.spoutapi.material.MaterialData.getMaterial(Integer.parseInt(itemId));
				if (material != null) {
					this.loadCraftingRecipe(itemId, material, config.getConfigurationSection(itemId));
				} else {
					MainManager.getUtils().log("LegacyManager: material not found: " + itemId, Level.WARNING);
				}
			} else {
				MainManager.getUtils().log("LegacyManager: incorrect value: " + itemId, Level.WARNING);
			}
		}
	}

	private void loadCraftingRecipe(String materialName, Material material, ConfigurationSection configurationSection) {
		List<?> recipes = configurationSection.getList("Recipes");
		// Make sure we have a valid list.
		if (recipes == null) {
			return;
		}
		// This allows us to have multiple recipes.
		for (Object orecipe : recipes) {
			// TODO unsafe cast warning remove
			Map<String, Object> recipe = (Map<String, Object>) orecipe;
			String type = (String) recipe.get("type");
			Integer amount = (Integer) recipe.get("amount");
			amount = amount == null ? 1 : amount;
			if (type.equalsIgnoreCase("furnace")) {
				String ingredientName = (String) recipe.get("ingredients");
				Material ingredient;
				if (ingredientName.matches("^[0-9]+$")) {
					ingredient = org.getspout.spoutapi.material.MaterialData.getMaterial(Integer.parseInt(ingredientName));
				} else {
					Map<String, Material> materialList = MainManager.getSmpManager().getMaterial(materialName);
					ingredient = materialList.get((String) materialList.keySet().toArray()[0]);
				}
				SpoutFurnaceRecipe fRecipe = new SpoutFurnaceRecipe(
					new SpoutItemStack(ingredient, 1),
					new SpoutItemStack(material, amount)
				);
				SpoutFurnaceRecipes.registerSpoutRecipe(fRecipe);
				this.furnaceRecipeList.add(fRecipe);
			} else if (type.equalsIgnoreCase("shaped")) {
				SpoutShapedRecipe sRecipe = new SpoutShapedRecipe(
					new SpoutItemStack(material, amount)
				).shape("abc", "def", "ghi");
				String ingredients = (String) recipe.get("ingredients");
				this.doRecipe(sRecipe, ingredients);
			} else if (type.equalsIgnoreCase("shapeless")) {
				SpoutShapelessRecipe sRecipe = new SpoutShapelessRecipe(
					new SpoutItemStack(material, amount)
				);
				String ingredients = (String) recipe.get("ingredients");
				this.doRecipe(sRecipe, ingredients);
			} else {
				MainManager.getUtils().log(
					"Couldn't load crafting recipe for " + materialName + ".png from legacyrecipes.yml.",
					Level.WARNING
				);
			}
		}
	}

	private void doRecipe(Recipe recipe, String ingredients) {
		Integer currentLine = 0;
		Integer currentColumn = 0;

		ingredients = ingredients.replaceAll("\\s{2,}", " ");
		for (String line : ingredients.split("\\r?\\n")) {
			// make sure we stop at the third line
			if (currentLine >= 3) {
				continue;
			}
			for (String ingredientitem : line.split(" ")) {
				// make sure we stop at the third entry in this line
				if (currentColumn >= 3) {
					continue;
				}

				// this character is required for matching the current material into the recipe
				char a = (char) ('a' + currentColumn + currentLine * 3);

				// getting the correct material
				Material ingredient;
				if (ingredientitem.matches("^[0-9]+$")) {
					ingredient = org.getspout.spoutapi.material.MaterialData.getMaterial(Integer.parseInt(ingredientitem));
				} else {
					Map<String, Material> materialList = MainManager.getSmpManager().getMaterial(ingredientitem);
					ingredient = materialList.get((String) materialList.keySet().toArray()[0]);
				}

				// Do not require an "air-block" in empty fields :D
				if (ingredient == null || ingredientitem.equals("0")) {
					currentColumn++;
					continue;
				}

				// adding the ingredient
				if (recipe instanceof SpoutShapedRecipe) {
					((SpoutShapedRecipe) recipe).setIngredient(a, ingredient);
				} else {
					((SpoutShapelessRecipe) recipe).addIngredient(ingredient);
				}

				currentColumn++;
			}
			currentColumn = 0;
			currentLine++;
		}

		// Putting the recipe into the register
		SpoutManager.getMaterialManager().registerSpoutRecipe(recipe);
		this.craftingRecipeList.add(recipe);
	}
}
