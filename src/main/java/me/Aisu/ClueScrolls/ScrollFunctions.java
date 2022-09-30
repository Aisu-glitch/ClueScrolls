package me.Aisu.ClueScrolls;

public class ScrollFunctions
{
	
	// Get the range of a scroll as an integer
	public static Integer getScrollRange(String str)
	{
		// Read chancelist from the config
		for (String Scroll: FileUtil.Loc.getConfigurationSection("Ranges").getKeys(false))
		{
			FileUtil.ScrollList.put(Scroll, Integer.parseInt(FileUtil.Loc.getString("Ranges." + Scroll)));
		}
		// Set default range to 0
		Integer ScrollRange = 0;
		ScrollRange = FileUtil.ScrollList.get(str);
		return ScrollRange;
	}
}
