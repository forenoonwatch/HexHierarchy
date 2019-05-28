package strat.commands;

import strat.bot.BotUtils;
import strat.bot.DiscordBot;
import strat.bot.ImageManager;
import strat.game.Map;
import strat.game.Nation;

public class ViewCities implements Command {

	@Override
	public String execute(Map map, Nation sender, String[] tokens) {
		BotUtils.sendFile(DiscordBot.getGameChannel(), ImageManager.cityView);
		return null;
	}
	
	@Override
	public String getFormat() {
		return "vviewcities";
	}
	
	@Override
	public String getSynopsis() { 
		return "view the cities of the map";
	}
}
