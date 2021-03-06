package strat.game.commands;

import strat.bot.BotUtils;
import strat.bot.DiscordBot;
import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.GameManager;

public class ViewNations implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		BotUtils.sendFile(DiscordBot.getGameChannel(), gameManager.getRenderer().getNationView());
		return null;
	}

	@Override
	public String getName() {
		return "viewnations";
	}
	
	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getInfo() {
		return "view what nations own each region";
	}
	
	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.ALL;
	}
}
