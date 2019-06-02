package strat.game.commands.admin;

import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.GameManager;

public class Save implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		if (tokens.length > 1) {
			gameManager.saveGame(tokens[1]);
			return new Response("Successfully saved game to " + tokens[1]);
		}
		else {
			gameManager.save();
			return new Response("Successfully saved game");
		}
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.OWNER;
	}

	@Override
	public String getName() {
		return "save";
	}

	@Override
	public String getUsage() {
		return "[file]";
	}

	@Override
	public String getInfo() {
		return "saves the game to file";
	}

}
