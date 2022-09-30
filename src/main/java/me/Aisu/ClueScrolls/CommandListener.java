package me.Aisu.ClueScrolls;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import me.Aisu.RewardManager.RewardManager;

public class CommandListener implements CommandExecutor
{
	private Main main;
	public CommandListener(Main plugin)
	{
		this.main = plugin;
	}
	// Command listener
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lab, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("Clues"))
		{
			if (sender instanceof Player)
			{
				// Getting player
				Player player = (Player)sender;
				if (player.hasPermission("Clues.Manager"))
				{
					switch (args.length)
					{
						case 0 :
							Bukkit.getConsoleSender().getServer().dispatchCommand(player, "help ClueScrolls");
							return true;
						// Check if there is one argument
						case 1 : {
							// Check if the argument is Reload
							if (args[0].equalsIgnoreCase("Reload"))
							{
								main.reloadConfig();
								FileUtil.ScrollList.clear();
								for (String strScrollName: FileUtil.Loc.getConfigurationSection("Ranges").getKeys(false))
								{
									FileUtil.ScrollList.put(strScrollName, Integer.parseInt(FileUtil.Loc.getString("Ranges." + strScrollName)));
								}
								// Getting all players on the server and sending all Random Events Managers a warning of a reload
								for(Player plrTempP : Bukkit.getServer().getOnlinePlayers())
								{
									if (plrTempP.hasPermission("CS.Manager"))
									{
										// "ratting out" who issued the reload
										plrTempP.sendMessage(player.getName() + " issued a reload of the ClueScroll Ranges");
									}
								}
							}
							else
							{
								player.sendMessage(ChatColor.RED + "incorrect command please check /help CS");
							}
							return true;
						}
						case 2 :
						{
							if (args[0].equalsIgnoreCase("Give"))
							{
								Boolean Scrollexists = false;
								// Check if the cluescrolltype exists
								for (String strScrollType: FileUtil.Loc.getConfigurationSection("Ranges").getKeys(false))
								{
									if (strScrollType.equalsIgnoreCase(args[1]))
									{
										Scrollexists = true;
										ItemStack newscroll = new ItemStack(Material.PAPER);
										ItemMeta scrollmeta = newscroll.getItemMeta();
										String type = strScrollType.split("Scroll")[0];
										List<String> strLore = new ArrayList<String>();
										strLore.add(ChatColor.BLUE + "ScrollType : " + ChatColor.AQUA + type);
										strLore.add(ChatColor.BLUE + "Status : " +ChatColor.RED + "INACTIVE");
										scrollmeta.setDisplayName(ChatColor.AQUA + type + ChatColor.BLUE +  " Scroll");
										scrollmeta.setLore(strLore);
										scrollmeta.getPersistentDataContainer().set(KeyUtil.getKey("ScrollType"), PersistentDataType.STRING, strScrollType);
										newscroll.setItemMeta(scrollmeta);
										player.getInventory().addItem(newscroll);
									}
								}
								if (!Scrollexists)
								{
									player.sendMessage(ChatColor.RED + "Scroll Does not exist");
								}
							}
							else if (args[0].equalsIgnoreCase("Reward"))
							{
								RewardManager.Give(player, player.getName(), args[1]);
							}
							else
							{
								player.sendMessage(ChatColor.RED + "incorrect command please check /help CS");
							}
							return true;
						}
						case 3 :
						{
							// Check if the argument is Give
							if (args[0].equalsIgnoreCase("Give"))
							{
								Boolean Playerexists = false;
								Boolean Scrollexists = true;
								Player p = Bukkit.getPlayer(args[1]);
								if(p.isOnline())
								{
									Playerexists = true;
									Scrollexists = false;
									// Check if the cluescrolltype exists
									for (String strScrollType: FileUtil.Loc.getConfigurationSection("Ranges").getKeys(false))
									{
										if (strScrollType.equalsIgnoreCase(args[2]))
										{
											Scrollexists = true;
											ItemStack newscroll = new ItemStack(Material.PAPER);
											ItemMeta scrollmeta = newscroll.getItemMeta();
											String type = strScrollType.split("Scroll")[0];
											List<String> strLore = new ArrayList<String>();
											strLore.add(ChatColor.BLUE + "ScrollType : " + ChatColor.AQUA + type);
											strLore.add(ChatColor.BLUE + "Status : " +ChatColor.RED + "INACTIVE");
											scrollmeta.setDisplayName(ChatColor.AQUA + type + ChatColor.BLUE +  " Scroll");
											scrollmeta.setLore(strLore);
											scrollmeta.getPersistentDataContainer().set(KeyUtil.getKey("ScrollType"), PersistentDataType.STRING, strScrollType);
											newscroll.setItemMeta(scrollmeta);
											p.getInventory().addItem(newscroll);
											return true;
										}
									}
									if (!Scrollexists) player.sendMessage(ChatColor.RED + "Scroll Does not exist");
								}
								if (!Playerexists) player.sendMessage(ChatColor.RED + "Player does not exist");
								return true;
							}
						}
					}
				}
			}
			else
			{
				switch (args.length)
				{
					case 3 : 
					{
						if (args[0].equalsIgnoreCase("Reward")) RewardManager.Give(sender, args[1], args[2]);
					}
					case 4 :
					{
						if (args[0].equalsIgnoreCase("Reward")) if (args[1].equalsIgnoreCase("Table")) RewardManager.GiveTable(sender, args[2], args[3]);
					}
				}
			}
		}
		return false;
	}
}
