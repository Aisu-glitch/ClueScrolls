package me.Aisu.ClueScrolls;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin
{
	private static Main instance;
	private FileUtil FileUtil;
	
	public void onEnable()
	{
		// Initiating secondary class files
		instance = this;
		FileUtil = new FileUtil();
		new Events(this);
		// Check if there is a directory, if not make one
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}
		if (!new File(getDataFolder(), "config.yml").exists()) {
			saveDefaultConfig();
		}
		reloadConfig();
		getCommand("Clues").setExecutor(new CommandListener(getInstance()));
		FileUtil.OnStart(new File(getDataFolder(), "config.yml"));
	}
	
	public void onDisable()
	{
		instance = null;
	}
	
	public static Main getInstance(){
		return instance;
	}
	
	public FileUtil getFileUtil() {
		return FileUtil;	
	}
}