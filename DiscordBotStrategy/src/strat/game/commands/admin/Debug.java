package strat.game.commands.admin;

import strat.bot.BotUtils;
import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.GameManager;

public class Debug implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		System.out.println(rawMessage);
		System.out.println(BotUtils.parseUserID(tokens[1]));
		return null;
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.OWNER;
	}

	@Override
	public String getName() {
		return "debug";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getInfo() {
		return "";
	}

}
