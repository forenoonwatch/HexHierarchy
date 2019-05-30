package strat.game.commands;

import strat.commands.Command;
import strat.commands.CommandRegistry;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.commands.ResponseType;
import strat.game.GameManager;

public class Help implements Command {
	
	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		return new Response(ResponseType.PUBLIC, "**COMMANDS**",
				CommandRegistry.getHelpString(PermissionLevel.NATION), Response.DEFAULT_COLOR);
	}

	@Override
	public String getName() {
		return "help";
	}
	
	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getInfo() {
		return "shows a list of commands";
	}
	
	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.ALL;
	}
}
