/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spoutmaterials.spoutmaterials.listeners;

import net.spoutmaterials.spoutmaterials.ItemAction;
import net.spoutmaterials.spoutmaterials.SMCustomBlock;
import net.spoutmaterials.spoutmaterials.SMCustomItem;
import net.spoutmaterials.spoutmaterials.SmpManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 *
 * @author Nickq
 */
public class SMListener implements Listener{
	private SmpManager smpManager;
	public SMListener(SmpManager aThis) {
		smpManager=aThis;
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
		if (event.isCancelled()) {
			return;
		}

		SpoutPlayer player = (SpoutPlayer) event.getPlayer();
		Object object = this.smpManager.getMaterial(new SpoutItemStack(player.getItemInHand()));

		// We only check things for custom items.
		if (object == null || !(object instanceof SMCustomItem)) {
			return;
		}
		SMCustomItem item = (SMCustomItem) object;
		
		// Getting the correct item action.
		Action action = event.getAction();
		ItemAction itemAction = null;
		if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
			itemAction = item.getActionL();
		} else if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
			itemAction = item.getActionR();
		}
		
		// We dont need to go further, if there is no action
		if (itemAction == null) {
			return;
		}

		// Does it heal or damage the player?
		if (itemAction.getHealth() != 0) {
			int newHealth = player.getHealth() + itemAction.getHealth();
			
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
		if (itemAction.getHunger() != 0) {
			int newHunger = player.getFoodLevel() + itemAction.getHunger();
			
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
		if (itemAction.getAir() != 0) {
			int newAir = player.getRemainingAir() + itemAction.getAir();
			
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
		if (itemAction.getExperience() != 0) {
			player.giveExp(itemAction.getExperience());
		}

		// Does it return another item?
		if (itemAction.getReturnedItem() != null) {
			player.getInventory().addItem(new SpoutItemStack(itemAction.getReturnedItem(), 1));
			//FIXME if someone gets this warning removed here, i would be thankful!
			player.updateInventory();
		}

		// Playing sounds for items.
		if (itemAction.getSound() != null) {
			SpoutManager.getSoundManager().playGlobalCustomSoundEffect(
				this.smpManager.getPlugin(), itemAction.getSound(), false, player.getLocation(), 25
			);
		}

		// Let the player use a specific chat command.
		if (itemAction.getAction() != null) {
			player.chat(itemAction.getAction());
		}
		
		// Items can be consumed.
		if (itemAction.getConsume()) {
			ItemStack itemInHand = player.getItemInHand();
			itemInHand.setAmount(itemInHand.getAmount() - 1);
			if (itemInHand.getAmount() == 0) {
				itemInHand = null;
			}
			player.setItemInHand(itemInHand);
		}
	}
}
