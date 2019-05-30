package strat.game.commands;

import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.GameManager;
import strat.game.Nation;

public class Complete implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		Nation sender = gameManager.getNationByUser(senderID);
		
		if (sender == null) {
			return null;
		}
		
		if (gameManager.setTurnCompleted(sender)) {
			return new Response(String.format("%s has completed their turn. %d/%d", 
					sender.getName(), gameManager.getNumRequiredToComplete(), gameManager.getNumOwnedNations()));
		}
		
		return new Response("You have already marked yourself as complete!");
	}

	@Override
	public String getName() {
		return "complete";
	}
	
	@Override
	public String getUsage() {
		return "";
	}

	@Override
	public String getInfo() {
		return "marks you as having completed the turn. If everyone is complete, it advances the turn.";
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.NATION;
	}
}
