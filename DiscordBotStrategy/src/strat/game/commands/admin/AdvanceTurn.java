package strat.game.commands.admin;

import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.GameManager;

public class AdvanceTurn implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		gameManager.advanceTurn();
		return new Response("Advanced game turn!");
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.OWNER;
	}

	@Override
	public String getName() {
		return "advanceturn";
	}

	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getInfo() {
		return "advances to the next turn";
	}

}
