package strat.game.commands;

import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.Army;
import strat.game.GameManager;
import strat.game.Nation;

public class UndoMove implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		if (tokens.length < 2) {
			return new Response("UndoMove: Must provide army number");
		}
		
		Nation n = gameManager.getNationByUser(senderID);
		
		if (n == null) {
			return null;
		}
		
		Army a = null;
		
		try {
			a = gameManager.getGame().getArmy(n.getNationID(), Integer.parseInt(tokens[1]));
		}
		catch (NumberFormatException e) {
			return new Response("UndoMove: Invalid army number: " + tokens[1]);
		}
		
		if (a == null) {
			return new Response("UndoMove: Invalid army number: " + tokens[1]);
		}
		
		if (a.getPendingMoves().isEmpty()) {
			return new Response("UndoMove: Army " + tokens[1] + " has no moves to undo.");
		}
		
		a.getPendingMoves().remove(a.getPendingMoves().size() - 1);
		a.setRemainingMoves(a.getRemainingMoves() + 1);
		
		return new Response("Backtracked Army " + tokens[1] + " by 1 tile.");
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.NATION;
	}

	@Override
	public String getName() {
		return "undomove";
	}

	@Override
	public String getUsage() {
		return "army_number";
	}

	@Override
	public String getInfo() {
		return "backtracks the army one tile";
	}

}
