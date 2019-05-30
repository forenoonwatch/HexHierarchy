package strat.game.commands;

import strat.bot.BotUtils;
import strat.bot.DiscordBot;
import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.GameManager;

public class ViewCities implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		BotUtils.sendFile(DiscordBot.getGameChannel(), gameManager.getRenderer().getCityView());
		return null;
	}
	
	@Override
	public String getName() {
		return "viewcities";
	}
	
	@Override
	public String getUsage() {
		return "";
	}
	
	@Override
	public String getInfo() { 
		return "view the cities of the map";
	}
	
	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.ALL;
	}
}
