package me.Aisu.ClueScrolls;

import java.io.File;
import java.util.HashMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class FileUtil
{
	public static FileConfiguration Loc;
	public static File File;
	
	// Make list for Ranges
	public static  HashMap<String, Integer> ScrollList = new HashMap<String, Integer>();
	
	public void OnStart(File Config)
	{	

		
		
		
		File = Config;
		Loc = YamlConfiguration.loadConfiguration(File);
	}
	
	// Reload functions
	public static void reloadConfig()
	{
		Loc = YamlConfiguration.loadConfiguration(File);
	}
	
	
}
