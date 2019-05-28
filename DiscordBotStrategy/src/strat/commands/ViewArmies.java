package strat.commands;

import strat.bot.BotUtils;
import strat.bot.DiscordBot;
import strat.bot.ImageManager;
import strat.game.Map;
import strat.game.Nation;

public class ViewArmies implements Command {

	@Override
	public String execute(Map map, Nation sender, String[] tokens) {
		BotUtils.sendFile(DiscordBot.getDMFromNation(sender), ImageManager.getArmyView(sender));
		return null;
	}
	
	@Override
	public String getFormat() {
		return "viewarmies";
	}
	
	@Override
	public String getSynopsis() { 
		return "privately messages a diagram of your armies and their paths";
	}
}
