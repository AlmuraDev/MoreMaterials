package net.morematerials.listeners;

import net.morematerials.MoreMaterials;
import net.morematerials.materials.CustomFuel;
import net.morematerials.materials.MMCustomBlock;
import net.morematerials.materials.MMCustomTool;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.ItemStack;
import org.getspout.spout.block.SpoutCraftBlock;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.inventory.SpoutItemStack;
import org.getspout.spoutapi.material.Block;
import org.getspout.spoutapi.material.block.GenericCustomBlock;
import org.getspout.spoutapi.material.item.GenericCustomTool;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.sound.SoundEffect;

// This class implements fake methods for properties which do not work in SpoutPlugin.
public class CustomListener implements Listener {
	
	private MoreMaterials plugin;

	public CustomListener(MoreMaterials plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		// This is for the Damage property
		if (event instanceof EntityDamageByEntityEvent) {
			Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
			// If a player caused the damage
			if (damager instanceof Player) {
				SpoutItemStack stack = new SpoutItemStack(((Player) damager).getItemInHand());
				// If player holds a custom tool in hand.
				if (stack.getMaterial() instanceof GenericCustomTool) {
					// Get the material
					Integer customId = ((GenericCustomTool) stack.getMaterial()).getCustomId();
					MMCustomTool tool = (MMCustomTool) this.plugin.getSmpManager().getMaterial(customId);
					// If this tool is a MM tool, get its damage.
					if (tool != null && tool.getConfig().contains("Damage")) {
						event.setDamage(tool.getConfig().getInt("Damage"));
					}
				}
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		// Make sure we have a valid event.
		if (event.getPlayer() == null || event.getPlayer().getGameMode() == GameMode.CREATIVE) {
			return;
		}
		
		SpoutPlayer player = (SpoutPlayer) event.getPlayer();

		// Check for durability and ItemDropRequired.
		if (player.getItemInHand() != null) {
			SpoutItemStack stack = new SpoutItemStack(player.getItemInHand());
			
			// Check for ItemDropRequired
			Block block = ((SpoutCraftBlock) event.getBlock()).getBlockType();
			if (block instanceof GenericCustomBlock) {
				GenericCustomBlock customBlock = (GenericCustomBlock) block;
				Object material = this.plugin.getSmpManager().getMaterial(customBlock.getCustomId());
				
				// Make sure this is an MoreMaterials block.
				if (material != null && material instanceof MMCustomBlock) {
					if (((MMCustomBlock) material).getItemDropRequired()) {
						// Forbid tools without modifier
						Boolean prevent = !(stack.getMaterial() instanceof GenericCustomTool);
						if (!prevent) {
							prevent = ((GenericCustomTool) stack.getMaterial()).getStrengthModifier(customBlock) <= 1.0;
						}
						
						if (prevent) {
							event.setCancelled(true);
							SpoutBlock spoutBlock = (SpoutBlock) event.getBlock();
							spoutBlock.setType(org.bukkit.Material.AIR);
							spoutBlock.setCustomBlock(null);
						}
					}
				}
			}
			
			if (stack.isCustomItem() && stack.getMaterial() instanceof GenericCustomTool) {
				GenericCustomTool tool = (GenericCustomTool) stack.getMaterial();
				
				// Do durability stuff durability.
				if (tool.getMaxDurability() == 0) {
					return;
				} else if (GenericCustomTool.getDurability(stack) + 1 < tool.getMaxDurability()) {
					GenericCustomTool.setDurability(stack, (short) (GenericCustomTool.getDurability(stack) + 1));
					player.setItemInHand(stack);
				} else {
					player.setItemInHand(new ItemStack(Material.AIR));
					SpoutManager.getSoundManager().playSoundEffect(player, SoundEffect.BREAK);
				}
			}
		}
	}

	@EventHandler
	public void onFurnaceBurn(FurnaceBurnEvent event) {
		SpoutItemStack item = new SpoutItemStack(event.getFuel());
		if (item.getMaterial() instanceof CustomFuel && ((CustomFuel)item.getMaterial()).getBurnTime() > 0) {
			event.setBurning(true);
			event.setBurnTime(((CustomFuel)item.getMaterial()).getBurnTime());
		}
	}
	
	@EventHandler
	public void onPlayerSmelt(FurnaceSmeltEvent event) {
		SpoutItemStack itemStack = this.plugin.getFurnaceRecipeManager().getResult(new SpoutItemStack(event.getSource()));
        if (itemStack != null && event.getResult() != null) {
            event.setResult(itemStack);
            System.out.println("Set result!");
        } else {
            System.out.println("Cancelling event!");
            event.setCancelled(true);
		}
	}
}
