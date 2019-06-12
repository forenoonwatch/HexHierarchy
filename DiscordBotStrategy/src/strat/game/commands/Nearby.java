package strat.game.commands;

import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.commands.ResponseType;
import strat.game.Army;
import strat.game.City;
import strat.game.GameManager;
import strat.game.GameRules;
import strat.game.Nation;

public class Nearby implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		if (tokens.length < 2) {
			return new Response("Nearby: Must provide army number.");
		}
		
		Nation sender = gameManager.getNationByUser(senderID);
		Army senderA = null;
		
		try {
			senderA = gameManager.getGame().getArmy(sender.getNationID(), Integer.parseInt(tokens[1]));
		}
		catch (NumberFormatException e) {
			return new Response("Nearby: Invalid army number.");
		}
		
		if (senderA == null) {
			return new Response("Nearby: Invalid army number.");
		}
		
		String title = String.format(":telescope: **NEARBY %s ARMY %s**", sender.getName().toUpperCase(), tokens[1]);
		StringBuilder info = new StringBuilder();
		
		info.append("**ARMIES**\n");
		
		for (Army a : gameManager.getGame().getArmies()) {
			if (a.getOwnerID() != sender.getNationID()
					&& a.getHexagon().distanceFrom(senderA.getHexagon()) <= GameRules.getRulei("movesPerTurn") + 1) {
				info.append(a.getOwner().getName()).append(" army ").append(a.getArmyNumber()).append(":\n");
				
				for (String unit : GameRules.getUnitTypes()) {
					info.append("*").append(a.getUnits(unit)).append(" ").append(unit).append("*\n");
				}
			}
		}
		
		info.append("\n**CITIES**\n");
		
		for (City c : gameManager.getGame().getMap().getCities()) {
			if (c.getOwnerID() != sender.getNationID()
					&& c.getHexagon().distanceFrom(senderA.getHexagon()) <= GameRules.getRulei("movesPerTurn") + 1) {
				info.append(c.getName()).append(" - ").append(c.getOwner().getName()).append("\n");
				info.append(c.getInfo());
			}
		}
		
		return new Response(ResponseType.PRIVATE, title, info.toString(), sender.getRGB());
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.NATION;
	}

	@Override
	public String getName() {
		return "nearby";
	}

	@Override
	public String getUsage() {
		return "army_number";
	}

	@Override
	public String getInfo() {
		return "privately messages everything near the given army";
	}

}
