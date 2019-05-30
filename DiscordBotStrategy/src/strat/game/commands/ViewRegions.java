package strat.game.commands;

import strat.bot.BotUtils;
import strat.bot.DiscordBot;
import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.GameManager;

public class ViewRegions implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		BotUtils.sendFile(DiscordBot.getGameChannel(), gameManager.getRenderer().getRegionView());
		return null;
	}

	@Override
	public String getName() {
		return "viewregions";
	}
	
	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getInfo() {
		return "view the regions of the map";
	}
	
	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.ALL;
	}
}
