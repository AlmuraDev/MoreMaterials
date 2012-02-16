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

package net.morematerials.morematerials.listeners;

import java.util.Map;
import java.util.logging.Level;

import net.morematerials.morematerials.Main;
import net.morematerials.morematerials.SmpManager;
import net.morematerials.morematerials.materials.MaterialAction;
import net.morematerials.morematerials.materials.SMCustomBlock;
import net.morematerials.morematerials.materials.SMCustomItem;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.event.inventory.InventoryCraftEvent;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.Material;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SMListener implements Listener {

	private SmpManager smpManager;

	public SMListener(Main plugin) {
		this.smpManager = plugin.getSmpManager();
	}

	@EventHandler
	public void BlockRedstone(BlockRedstoneEvent event) {
		Block redstone = event.getBlock();
		Boolean on = redstone.getData() == 0;

		// Getting the block below the redstone
		SpoutBlock block = (SpoutBlock) redstone.getLocation().getWorld().getBlockAt(
			redstone.getX(), redstone.getY() - 1, redstone.getZ()
		);

		if (block.isCustomBlock()) {
			SMCustomBlock customBlock = (SMCustomBlock) this.smpManager.getMaterial(new SpoutItemStack(block.getCustomBlock().getBlockItem(), 1));
			//TODO block toggle
		}
	}

	@EventHandler
	public void PlayerJoin(PlayerJoinEvent event) {
		if (event.getPlayer().isOp() && this.smpManager.getPlugin().updateAvailable) {
			event.getPlayer().sendMessage(this.smpManager.getPlugin().getMessage("An Update is available!", Level.WARNING));
		}
	}

	@EventHandler
	public void InventoryCraft(InventoryCraftEvent event) {
		if (event.getResult() == null) {
			return;
		}
		if (Main.isDebugging()) {
			event.getPlayer().sendMessage(this.smpManager.getPlugin().getMessage("You just crafted " + event.getResult().getType().name() + "!", Level.WARNING));
		}
		SpoutItemStack spoutItemStack = new SpoutItemStack(event.getResult());
		Map<String, Material> materials = this.smpManager.getMaterial(spoutItemStack.getMaterial().getName());
		for (String materialName : materials.keySet()) {
			Material material = materials.get(materialName);
			if (material == spoutItemStack.getMaterial()) {
				if (!(event.getPlayer().hasPermission("spoutmaterials.craft")) || !event.getPlayer().hasPermission("spoutmaterials.craft." + materialName)) {
					event.getPlayer().sendMessage(this.smpManager.getPlugin().getMessage("You do not have permission to do that!", Level.SEVERE));
					event.setCancelled(true);
					return;
				} else if (Main.isDebugging()) {
					event.getPlayer().sendMessage(this.smpManager.getPlugin().getMessage("You are allowed to craft that!", Level.WARNING));
				}
				if (material instanceof SMCustomItem && ((SMCustomItem) material).getKeepEnchanting()) {
					ItemStack result = event.getResult();
					for (ItemStack[] ingredients : event.getRecipe()) {
						for (ItemStack ingredient : ingredients) {
							result.addEnchantments(ingredient.getEnchantments());
						}
					}
					event.setResult(result);
				}
			}
		}

	}

	@EventHandler
	public void EntityDamage(EntityDamageEvent event) {
		// If any other plugin already canceled this event
		if (event.isCancelled()) {
			return;
		}

		// Only applies for falldamage of players!
		if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL) && event.getEntity() instanceof SpoutPlayer) {
			SpoutPlayer player = (SpoutPlayer) event.getEntity();

			// Getting the block below the player
			SpoutBlock block = (SpoutBlock) player.getWorld().getBlockAt(
				player.getLocation().getBlockX(),
				player.getLocation().getBlockY() - 1,
				player.getLocation().getBlockZ()
			);

			// This only applies for custom blocks
			if (block.isCustomBlock()) {
				Object item = this.smpManager.getMaterial(new SpoutItemStack(block.getCustomBlock(), 1));
				if (item != null && item instanceof SMCustomBlock && ((SMCustomBlock) item).getFallMultiplier() != 1) {
					event.setDamage((int) (event.getDamage() * ((SMCustomBlock) item).getFallMultiplier()));
				}
				if (event.getDamage() == 0) {
					// Prevent hurt sound!
					event.setCancelled(true);
				}
			}
		}

		// Make sure an entity does damage
		if (!(event instanceof EntityDamageByEntityEvent)) {
			return;
		}

		EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
		Entity damager = damageEvent.getDamager();

		// Make sure the entity is a player
		if (!(damager instanceof Player)) {
			return;
		}

		SpoutPlayer player = (SpoutPlayer) damager;
		SpoutItemStack itemStack = new SpoutItemStack(player.getItemInHand());

		// Make sure the player holds an custom item in hand
		if (!itemStack.isCustomItem()) {
			return;
		}

		Object item = this.smpManager.getMaterial(itemStack);

		// Do damage if valid.
		if (item != null && item instanceof SMCustomItem && ((SMCustomItem) item).getDamage() != null) {
			event.setDamage(((SMCustomItem) item).getDamage());
		}
	}

	@EventHandler
	public void PlayerMove(PlayerMoveEvent event) {
		// If any other plugin already canceled this event
		if (event.isCancelled()) {
			return;
		}

		SpoutPlayer player = (SpoutPlayer) event.getPlayer();

		// Getting the block below the player
		SpoutBlock block = (SpoutBlock) player.getWorld().getBlockAt(
			player.getLocation().getBlockX(),
			player.getLocation().getBlockY() - 1,
			player.getLocation().getBlockZ()
		);

		// This only applies for custom blocks
		Object item = null;
		if (block.isCustomBlock()) {
			item = this.smpManager.getMaterial(new SpoutItemStack(block.getCustomBlock().getBlockItem(), 1));
		}

		// Setting the player walkspeed.
		if (item != null && item instanceof SMCustomBlock && ((SMCustomBlock) item).getSpeedMultiplier() != 1) {
			player.setAirSpeedMultiplier(((SMCustomBlock) item).getSpeedMultiplier());
			player.setWalkingMultiplier(((SMCustomBlock) item).getSpeedMultiplier());
		} else {
			player.setAirSpeedMultiplier(1);
			player.setWalkingMultiplier(1);
		}

		// Setting the player jumpheight.
		if (item != null && item instanceof SMCustomBlock && ((SMCustomBlock) item).getJumpMultiplier() != 1) {
			player.setJumpingMultiplier(((SMCustomBlock) item).getJumpMultiplier());
		} else {
			player.setJumpingMultiplier(1);
		}
	}

	@EventHandler
	public void PlayerInteract(PlayerInteractEvent event) {
		// If any other plugin already canceled this event
		if (event.isCancelled() && event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_AIR) {
			return;
		}

		SpoutPlayer player = (SpoutPlayer) event.getPlayer();
		Object object = this.smpManager.getMaterial(new SpoutItemStack(player.getItemInHand()));

		SMCustomItem item = null;

		if (object instanceof SMCustomItem) {
			item = (SMCustomItem) object;
		}
		SMCustomBlock block = null;

		// Getting the correct item action.
		Action action = event.getAction();
		MaterialAction useAction = null;
		if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
			if (item != null) {
				useAction = item.getActionL();
			}
			if (action == Action.LEFT_CLICK_BLOCK) {
				if (((SpoutBlock) event.getClickedBlock()).getCustomBlock() != null) {
					Object blockMaterial = this.smpManager.getMaterial(
						new SpoutItemStack(((SpoutBlock) event.getClickedBlock()).getCustomBlock().getBlockItem(), 1)
					);
					if (blockMaterial instanceof SMCustomBlock) {
						block = (SMCustomBlock) blockMaterial;
						useAction = block.getActionL();
					}
				}
			}
		} else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
			if (item != null) {
				useAction = item.getActionR();
			}
			if (action == Action.RIGHT_CLICK_BLOCK) {
				if (((SpoutBlock) event.getClickedBlock()).getCustomBlock() != null) {
					Object blockMaterial = this.smpManager.getMaterial(
									new SpoutItemStack(((SpoutBlock) event.getClickedBlock()).getCustomBlock().getBlockItem(), 1));
					if (blockMaterial instanceof SMCustomBlock) {
						block = (SMCustomBlock) blockMaterial;
						useAction = block.getActionR();
					}
				}
			}
		}

		// We dont need to go further, if there is no action
		if (useAction == null) {
			return;
		}

		// Adding bypass permission
		if (useAction.getPermissionsBypass() != null) {
			player.addAttachment(smpManager.getPlugin(), useAction.getPermissionsBypass(), true);
		}

		// Does it heal or damage the player?
		if (useAction.getHealth() != 0) {
			int newHealth = player.getHealth() + useAction.getHealth();

			// Make sure player has valid health information.
			if (newHealth > player.getMaxHealth()) {
				newHealth = player.getMaxHealth();
			} else if (newHealth < 0) {
				newHealth = 0;
			}

			// Setting the new player health.
			player.setHealth(newHealth);
		}

		// Does it affect hunger?
		if (useAction.getHunger() != 0) {
			int newHunger = player.getFoodLevel() + useAction.getHunger();

			// Make sure player has valid hunger information.
			if (newHunger > 20) {
				newHunger = 20;
			} else if (newHunger < 0) {
				newHunger = 0;
			}

			// Setting the new player hunger.
			player.setFoodLevel(newHunger);
		}

		// Does it affect air?
		if (useAction.getAir() != 0) {
			int newAir = player.getRemainingAir() + useAction.getAir();

			// Make sure player has valid air information.
			if (newAir > player.getMaximumAir()) {
				newAir = player.getMaximumAir();
			} else if (newAir < 0) {
				newAir = 0;
			}

			// Setting the new player air.
			player.setRemainingAir(newAir);
		}

		// Does it affect Experience?
		if (useAction.getExperience() != 0) {
			//TODO when using negative xp, we should also substract player level!
			player.giveExp(useAction.getExperience());
		}

		// Does it return another item?
		if (useAction.getReturnedItem() != null) {
			player.getInventory().addItem(new SpoutItemStack(useAction.getReturnedItem(), 1));
			//TODO deprecated but required - remove asap
			player.updateInventory();
		}

		// Playing sounds for items.
		if (useAction.getSound() != null) {
			SpoutManager.getSoundManager().playCustomSoundEffect(this.smpManager.getPlugin(), player, useAction.getSound(), false);
		}

		// Let the player use a specific chat command.
		if (useAction.getAction() != null) {
			player.chat(useAction.getAction());
		}

		// Items can be consumed.
		if (useAction.getConsume()) {
			if (block != null) {
				event.getClickedBlock().setType(org.bukkit.Material.AIR);
			} else {
				ItemStack itemInHand = player.getItemInHand();
				itemInHand.setAmount(itemInHand.getAmount() - 1);
				if (itemInHand.getAmount() == 0) {
					itemInHand = null;
				}
				player.setItemInHand(itemInHand);
			}
		}

		// Removing bypass permission
		if (useAction.getPermissionsBypass() != null) {
			player.addAttachment(smpManager.getPlugin(), useAction.getPermissionsBypass(), false);
		}
	}
}
