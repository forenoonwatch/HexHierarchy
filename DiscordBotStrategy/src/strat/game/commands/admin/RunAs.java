package strat.game.commands.admin;

import strat.bot.BotUtils;
import strat.commands.Command;
import strat.commands.CommandRegistry;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.GameManager;

public class RunAs implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		if (tokens.length < 3) {
			return new Response("RunAs: Invalid number of arguments.");
		}
		
		long userID;
		
		try {
			userID = BotUtils.parseUserID(tokens[1]);
		}
		catch (NumberFormatException e) {
			return new Response("Invalid user");
		}
		
		String cmd = null;
		
		for (int i = tokens[0].length(); i < rawMessage.length(); ++i) {
			if (lowerMessage.substring(i).startsWith(tokens[1])) {
				cmd = rawMessage.substring(i + tokens[1].length());
				break;
			}
		}
		
		if (cmd == null) {
			return new Response("Could not find command.");
		}
		
		cmd = cmd.trim();
		
		return CommandRegistry.executeCommand(gameManager, cmd, userID, getInputLevel());
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.ADMIN;
	}

	@Override
	public String getName() {
		return "runas";
	}

	@Override
	public String getUsage() {
		return "user cmd";
	}

	@Override
	public String getInfo() {
		return "runs cmd as user";
	}

}
