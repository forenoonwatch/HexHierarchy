package strat.game.commands;

import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.Army;
import strat.game.GameManager;
import strat.game.GameRules;
import strat.game.Nation;

public class Split implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		if (tokens.length < 2) {
			return new Response("Split: Must provide army number.");
		}
		
		Nation sender = gameManager.getNationByUser(senderID);
		Army a = null;
		
		try {
			a = gameManager.getGame().getArmy(sender.getNationID(), Integer.parseInt(tokens[1]));
		}
		catch (NumberFormatException | NullPointerException e) {
			return new Response("Split: Invalid army number.");
		}
		
		if (a == null) {
			return new Response("Split: Invalid army number.");
		}
		
		sender.setSpawnedArmies(sender.getSpawnedArmies() + 1);
		
		Army newArmy = new Army(a.getMap(), a.getOwnerID(), sender.getSpawnedArmies(), a.getQ(), a.getR());
		int totalUnits = 0;
		
		for (String unit : GameRules.getUnitTypes()) {
			int units = a.getUnits(unit);
			int half = units / 2;
			int rem = units % 2;
			totalUnits += half;
			
			a.setUnits(unit, half + rem);
			newArmy.setUnits(unit, half);
		}
		
		if (totalUnits == 0) {
			return new Response("Split: Cannot split army " + a.getArmyID() + " in half. Army too small.");
		}
		
		gameManager.getGame().addArmy(newArmy);
		
		return new Response(String.format("Successfully split army %d into new army %d", a.getArmyID(), newArmy.getArmyID()));
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.NATION;
	}

	@Override
	public String getName() {
		return "split";
	}

	@Override
	public String getUsage() {
		return "army_number";
	}

	@Override
	public String getInfo() {
		return "splits the given army into two armies";
	}

}
