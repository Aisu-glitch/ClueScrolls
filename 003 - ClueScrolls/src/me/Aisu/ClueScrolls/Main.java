package me.Aisu.ClueScrolls;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import me.Aisu.RewardPools.RewardManager;


public class Main extends JavaPlugin implements Listener {
	
	// Variables needed to do config file management
	public static FileConfiguration ConfigLoc;
	public static File ConfigFile;
	public static FileConfiguration DataLoc;
	public static File DataFile;
	
	public void onEnable() {
		// Initiating the Events class
		new Events(this);
		// Check if there is a directory, if not make one
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		// If there is no default config, create it
		ConfigFile = new File(getDataFolder(), "config.yml");
		if (!ConfigFile.exists()) {
			saveDefaultConfig();
		}
		DataFile = new File(getDataFolder(), "Data.yml");
		if (!DataFile.exists()) {
			try {
				DataFile.createNewFile();
			} catch (IOException e) {
				Bukkit.getServer().getConsoleSender().sendMessage("[ClueScrolls] " + ChatColor.RED + "Could not create the Data.yml file");
			}
		}
		reloadConfig();
		// Create a interactable copy of the config
		ConfigLoc = YamlConfiguration.loadConfiguration(ConfigFile);
		DataLoc = YamlConfiguration.loadConfiguration(DataFile);
	}
	
	public void onDisable() {}
	
	// Make list for Ranges
	public static HashMap<String, Integer> ScrollList = new HashMap<String, Integer>();
	
	// Reload functions
	public void reloadConfig() {
		ConfigLoc = YamlConfiguration.loadConfiguration(ConfigFile);
	}
	public void reloadData() {
		DataLoc = YamlConfiguration.loadConfiguration(DataFile);
	}
	
	// Save functions
	public static void saveData() {
		try {
			DataLoc.save(DataFile);
		} catch(IOException e) {
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Could not save the Config.yml file");
		}
	}
	
	public static NamespacedKey getKey(String Keyname) {
		NamespacedKey key = new NamespacedKey(Main.getPlugin(Main.class), Keyname);
		return key;
	}
	
	
	// Function to return a double being the chance of a reward
	public static Integer getScrollRange(String str) {
		// Read chancelist from the config
		for (String Scroll: ConfigLoc.getConfigurationSection("Ranges").getKeys(false)) {
			ScrollList.put(Scroll, Integer.parseInt(ConfigLoc.getString("Ranges." + Scroll)));
		}
		// Set default chance to 0
		Integer ScrollRange = 0;
		ScrollRange = ScrollList.get(str);
		return ScrollRange;
	}

	// Randomizer function for chances
	public static boolean ChanceRNG(double Chance) {
		// Make roll variable
		int roll;
		// Store the chance
		double chance = 1000000000*Chance;
		// Create random chance
		roll = new Random().nextInt(1000000000) +1;
		// If the roll is succesfull
		if (roll <= chance) {
			return true;
		}
		return false;
	}
    
	// Command listener
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// Checking if command was issued towards this plugin
		if (cmd.getName().equalsIgnoreCase("Clues") && !(sender instanceof Player)) {
			switch (args.length) {
				case 3 : {
					if (args[0].equalsIgnoreCase("Reward")) {
						RewardManager.RewardGive(sender, args[1], args[2]);
					}
				}
				case 4 : {
					if (args[0].equalsIgnoreCase("Reward")) {
						if (args[1].equalsIgnoreCase("Table")) {
							RewardManager.RewardGiveTable(sender, args[2], args[3]);
						}
					}
				}
			}
		}
		if (cmd.getName().equalsIgnoreCase("Clues") && (sender instanceof Player)) {
			// Getting player
			Player player = (Player)sender;
			if (player.hasPermission("Clues.Manager")) {
				switch (args.length) {
					case 0 : {
						Bukkit.getConsoleSender().getServer().dispatchCommand(player, "help ClueScrolls");
						return true;
					}
					// Check if there is one argument
					case 1 : {
						// Check if the argument is Reload
						if (args[0].equalsIgnoreCase("Reload")) {
							reloadConfig();
							ScrollList.clear();
							for (String strScrollName: ConfigLoc.getConfigurationSection("Ranges").getKeys(false)) {
								ScrollList.put(strScrollName, Integer.parseInt(ConfigLoc.getString("Ranges." + strScrollName)));
							}
							// Getting all players on the server and sending all Random Events Managers a warning of a reload
							for(Player plrTempP : Bukkit.getServer().getOnlinePlayers()) {
								if (plrTempP.hasPermission("CS.Manager")) {
									// "ratting out" who issued the reload
									plrTempP.sendMessage(player.getName() + " issued a reload of the ClueScroll Ranges");
								}
							}
						} else {
							player.sendMessage(ChatColor.RED + "incorrect command please check /help CS");
						}
						return true;
					}
					case 2 : {
						if (args[0].equalsIgnoreCase("Give")) {
							Boolean Scrollexists = false;
							// Check if the cluescrolltype exists
							for (String strScrollType: ConfigLoc.getConfigurationSection("Ranges").getKeys(false)) {
								if (strScrollType.equalsIgnoreCase(args[1])) {
									Scrollexists = true;
									ItemStack newscroll = new ItemStack(Material.PAPER);
									ItemMeta scrollmeta = newscroll.getItemMeta();
									String type = strScrollType.split("Scroll")[0];
									List<String> strLore = new ArrayList<String>();
									strLore.add(ChatColor.BLUE + "ScrollType : " + ChatColor.AQUA + type);
									strLore.add(ChatColor.BLUE + "Status : " +ChatColor.RED + "INACTIVE");
									scrollmeta.setDisplayName(ChatColor.AQUA + type + ChatColor.BLUE +  " Scroll");
									scrollmeta.setLore(strLore);
									scrollmeta.getPersistentDataContainer().set(getKey("ScrollType"), PersistentDataType.STRING, strScrollType);
									newscroll.setItemMeta(scrollmeta);
									player.getInventory().addItem(newscroll);
								}
							}
							if (!Scrollexists) {
								player.sendMessage(ChatColor.RED + "Scroll Does not exist");
							}
						} else if (args[0].equalsIgnoreCase("Reward")) {
							RewardManager.RewardGive(player, args[1]);
						} else {
							player.sendMessage(ChatColor.RED + "incorrect command please check /help CS");
						}
						return true;
					}
					case 3 : {
						// Check if the argument is Give
						if (args[0].equalsIgnoreCase("Give")) {
							Boolean Playerexists = false;
							Boolean Scrollexists = true;
							Player p = Bukkit.getPlayer(args[1]);
							if(p.isOnline()) {
								Playerexists = true;
								Scrollexists = false;
								// Check if the cluescrolltype exists
								for (String strScrollType: ConfigLoc.getConfigurationSection("Ranges").getKeys(false)) {
									if (strScrollType.equalsIgnoreCase(args[2])) {
										Scrollexists = true;
										ItemStack newscroll = new ItemStack(Material.PAPER);
										ItemMeta scrollmeta = newscroll.getItemMeta();
										String type = strScrollType.split("Scroll")[0];
										List<String> strLore = new ArrayList<String>();
										strLore.add(ChatColor.BLUE + "ScrollType : " + ChatColor.AQUA + type);
										strLore.add(ChatColor.BLUE + "Status : " +ChatColor.RED + "INACTIVE");
										scrollmeta.setDisplayName(ChatColor.AQUA + type + ChatColor.BLUE +  " Scroll");
										scrollmeta.setLore(strLore);
										scrollmeta.getPersistentDataContainer().set(getKey("ScrollType"), PersistentDataType.STRING, strScrollType);
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
		return false;
	}
}