/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spoutmaterials.spoutmaterials.reflection;

import org.getspout.spoutapi.inventory.SpoutItemStack;

/**
 *
 * @author ZNickq
 */
public class SpoutFurnaceRecipe {
	private SpoutItemStack ingredient,result;
	public SpoutFurnaceRecipe(SpoutItemStack ing, SpoutItemStack res) {
		ingredient=ing;
		result = res;
	}
	public SpoutItemStack getIngredient() {
		return ingredient;
	}
	
	public SpoutItemStack getResult() {
		return result;
	}
	
	
}
