package strat.commands;

import strat.bot.BotUtils;
import strat.bot.DiscordBot;
import strat.bot.ImageManager;
import strat.game.Map;
import strat.game.Nation;

public class ViewPolitical implements Command {

	@Override
	public String execute(Map map, Nation sender, String[] tokens) {
		BotUtils.sendFile(DiscordBot.getGameChannel(), ImageManager.politicalView);
		return null;
	}

	@Override
	public String getFormat() {
		return "viewpolitical";
	}

	@Override
	public String getSynopsis() {
		return "view what nations own each region";
	}

}
