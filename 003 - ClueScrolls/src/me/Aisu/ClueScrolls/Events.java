package me.Aisu.ClueScrolls;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import me.Aisu.ClueScrolls.Main;
import me.Aisu.RewardPools.RewardManager;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_14_R1.PacketPlayOutChat;
import net.minecraft.server.v1_14_R1.ChatMessageType;
import net.minecraft.server.v1_14_R1.IChatBaseComponent.ChatSerializer;

public class Events implements Listener {
	public Events(Main plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public static void ActionBar(Player player, String text) {
		PacketPlayOutChat packet = new PacketPlayOutChat(
				ChatSerializer.a("{\"text\":\"" + text + "\"}"),
				ChatMessageType.GAME_INFO);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	public void CSStart(ItemStack istack, String ScrollType, Player player) {
		ItemStack	newStack	= new ItemStack(Material.PAPER);
		ItemMeta	newMeta		= newStack.getItemMeta();
		Integer		ScrollRange	= Main.getScrollRange(ScrollType);
		if (ScrollRange == null)
			return;
		int	ELx		= new Random().nextInt(ScrollRange) + 1;
		int	ELz		= new Random().nextInt(ScrollRange) + 1;
		int	PosNeg	= new Random().nextInt(4);
		switch (PosNeg) {
			case 0 : {
			}
			case 1 : {
				ELx *= -1;
			}
			case 2 : {
				ELz *= -1;
			}
			case 3 : {
				ELx	*= -1;
				ELz	*= -1;
			}
		}
		String	Loc		= ELx + ":" + ELz;
		String	type	= ScrollType.split("Scroll")[0];
		Integer	CLx		= (int) player.getLocation().getX();
		Integer	CLz		= (int) player.getLocation().getZ();
		Double	A		= (double) (ELx - CLx);
		Double	B		= (double) (ELz - CLz);
		if (A < 0)
			A = A * -1;
		if (B < 0)
			B = B * -1;
		// Calculating distance
		Integer			C		= (int) Math.round(Math.sqrt(A * A + B * B));
		List<String>	strLore	= new ArrayList<String>();
		strLore.add(ChatColor.BLUE + "ScrollType : " + ChatColor.AQUA + type);
		strLore.add(ChatColor.BLUE + "Status : " + ChatColor.GREEN + "ACTIVE");
		newMeta.setDisplayName(ChatColor.AQUA + type + ChatColor.BLUE + " Scroll");
		newMeta.setLore(strLore);
		newMeta.getPersistentDataContainer().set(Main.getKey("ScrollType"),PersistentDataType.STRING, ScrollType);
		newMeta.getPersistentDataContainer().set(Main.getKey("EndLoc"),PersistentDataType.STRING, Loc);
		newMeta.getPersistentDataContainer().set(Main.getKey("Distance"),PersistentDataType.INTEGER, C);
		ActionBar(player,ChatColor.GOLD + "You are " + Math.round(C) + " Blocks away.");
		newStack.setItemMeta(newMeta);
		if (istack.getAmount() >= 2) {
			istack.setAmount(istack.getAmount() - 1);
			player.getInventory().setItemInMainHand(istack);
			player.getInventory().addItem(newStack);
		} else {
			player.getInventory().setItemInMainHand(newStack);
		}
	}

	// interact events
	@EventHandler
	public void clickevent(PlayerInteractEvent event) {
		// Get player
		Player player = event.getPlayer();
		// if players hand is empty prevent it from activating
		if ((player.getInventory().getItem(player.getInventory().getHeldItemSlot()) == null)
				|| (player.getInventory().getItem(player.getInventory().getHeldItemSlot()).getType() == Material.AIR))
			return;
		// If a player is holding paper
		if (player.getInventory()
				.getItem(player.getInventory().getHeldItemSlot())
				.getType() == Material.PAPER) {
			// Get the paperstack
			ItemStack	IStack	= player.getInventory()
					.getItem(player.getInventory().getHeldItemSlot());
			ItemMeta	iMeta	= IStack.getItemMeta();
			// If the er was an action involving any click event
			if ((event.getAction().equals(Action.RIGHT_CLICK_AIR)
					|| event.getAction().equals(Action.LEFT_CLICK_AIR)
					|| event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
					|| event.getAction().equals(Action.LEFT_CLICK_BLOCK))
					&& event.getHand().toString().equalsIgnoreCase("hand")) {
				// Get if the scroll is activated
				if (!iMeta.getPersistentDataContainer().has(
						Main.getKey("Endloc"), PersistentDataType.STRING)) {
					if (!iMeta.getPersistentDataContainer().has(
							Main.getKey("ScrollType"),
							PersistentDataType.STRING))
						return;
					CSStart(IStack,
							iMeta.getPersistentDataContainer().get(
									Main.getKey("ScrollType"),
									PersistentDataType.STRING),
							player);
					player.sendMessage("Scroll Started");
					return;
				}
				// Setting variables for calculating and storing distances on the item
				Integer	ELx	= Integer.parseInt(iMeta.getPersistentDataContainer().get(Main.getKey("Endloc"),PersistentDataType.STRING).split(":")[0]);
				Integer	ELz	= Integer.parseInt(iMeta.getPersistentDataContainer().get(Main.getKey("Endloc"),PersistentDataType.STRING).split(":")[1]);
				Integer	CLx	= (int) player.getLocation().getX();
				Integer	CLz	= (int) player.getLocation().getZ();
				Double	A	= (double) (ELx - CLx);
				Double	B	= (double) (ELz - CLz);
				if (A < 0)
					A = A * -1;
				if (B < 0)
					B = B * -1;
				// Calculating distance
				Integer C = (int) Math.round(Math.sqrt(A * A + B * B));
				// If you are at the end of the scroll remove the scroll and hand out rewards
				if (C == 0) {
					player.sendMessage("The scroll makes a blinding flash, causing you to drop it.");
					player.sendMessage("When you regain sight you notice its gone but you have been left rewards instead.");
					player.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(30, 1));
					player.getInventory().setItemInMainHand(new ItemStack(Material.AIR, 1));
					RewardManager.RewardGiveTable(player,iMeta.getPersistentDataContainer().get(Main.getKey("ScrollType"),PersistentDataType.STRING));
					return;
					// If the scroll isnt ended
				} else {
					// Get current and old location
					Integer O = iMeta.getPersistentDataContainer().get(Main.getKey("Distance"),PersistentDataType.INTEGER);
					// Tell the player their current status
					String message = null;
					if (C - O == 0) {
						message = ChatColor.GOLD+ "You are at the same distance - ";
					} else if (C < O) {
						message = ChatColor.GREEN + "You are getting closer - ";
					} else if (C > O) {
						message = ChatColor.RED+ "You are going further away - ";
					}
					message += "You are " + Math.round(C) + " Blocks away.";
					ActionBar(player, message);
					// Save the new distance to the meta
					iMeta.getPersistentDataContainer().set(Main.getKey("Distance"), PersistentDataType.INTEGER,C);
					// Save the produced meta to the stack
					IStack.setItemMeta(iMeta);
					// place the stack with the new meta in the players
					// inventory
					player.getInventory().setItemInMainHand(IStack);
					return;
				}
			}
		}
	}
}