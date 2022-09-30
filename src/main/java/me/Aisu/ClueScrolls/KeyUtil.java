package me.Aisu.ClueScrolls;

import org.bukkit.NamespacedKey;

public class KeyUtil
{
		// Convert a string to a namespacekey
	public static NamespacedKey getKey(String Keyname)
	{
		NamespacedKey key = new NamespacedKey(Main.getPlugin(Main.class), Keyname);
		return key;
	}
	
}
