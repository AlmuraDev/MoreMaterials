package net.spoutmaterials.spoutmaterials.other;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import net.spoutmaterials.spoutmaterials.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.material.MaterialData;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.inventory.SpoutShapedRecipe;
import org.getspout.spoutapi.inventory.SpoutShapelessRecipe;
import org.getspout.spoutapi.material.Material;

/**
 *
 * @author ZNickq
 */
public class LegacyCrafting {

	public static void loadCraftingRecipes(Main instance, YamlConfiguration config) {
		List<Object> recipes = config.getList("Recipes");
		// Make sure we have a valid list.
		if (recipes == null) {
			return;
		}
		// This allows us to have multiple recipes.
		for (Object orecipe : recipes) {
			Map<String, Object> recipe = (Map<String, Object>) orecipe;
			String type = (String) recipe.get("type");
			String materialName = (String) recipe.get("name");
			Material material;
			if (materialName.matches("^[0-9]+$")) {
					material = org.getspout.spoutapi.material.MaterialData.getMaterial(Integer.parseInt(materialName));
				} else {
					material=org.getspout.spoutapi.material.MaterialData.getMaterial(materialName);
				}
			if(material==null) {
				System.out.println("[SpoutMaterials] Invalid legacy material name: "+materialName);
				continue;
			}
			Integer amount = (Integer) recipe.get("amount");
			amount = amount == null ? 1 : amount;
			
			if (type.equalsIgnoreCase("furnace")) {
				String ingredientName = (String) recipe.get("ingredients");
				Material ingredient;
				if (ingredientName.matches("^[0-9]+$")) {
					ingredient = org.getspout.spoutapi.material.MaterialData.getMaterial(Integer.parseInt(ingredientName));
				} else {
					Map<String, Material> materialList = instance.smpManager.getMaterial(materialName);
					ingredient = materialList.get((String) materialList.keySet().toArray()[0]);
				}
				FurnaceRecipe fRecipe;
				fRecipe = new FurnaceRecipe(
								new SpoutItemStack(material, amount),
								new MaterialData(new MaterialData(ingredient.getRawId()).getItemType()));

				Bukkit.addRecipe(fRecipe);
			} else if (type.equalsIgnoreCase("shaped")) {
				SpoutShapedRecipe sRecipe = new SpoutShapedRecipe(
								new SpoutItemStack(material, amount)).shape("abc", "def", "ghi");
				String ingredients = (String) recipe.get("ingredients");
				doRecipe(instance, sRecipe, ingredients);
			} else if (type.equalsIgnoreCase("shapeless")) {
				SpoutShapelessRecipe sRecipe = new SpoutShapelessRecipe(new SpoutItemStack(material, amount));
				String ingredients = (String) recipe.get("ingredients");
				doRecipe(instance, sRecipe, ingredients);
			} else {
				String logMessage = "[" + instance.getDescription().getName() + "]";
				logMessage += " SpoutMaterials: Couldn't load crafting recipe for " + materialName + "!";
				Logger.getLogger("Minecraft").info(logMessage);
			}
		}
	}

	private static void doRecipe(Main instance, Recipe recipe, String ingredients) {
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
					Map<String, Material> materialList = instance.smpManager.getMaterial(ingredientitem);
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
	}
}
