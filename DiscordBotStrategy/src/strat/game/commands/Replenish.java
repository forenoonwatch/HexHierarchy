package strat.game.commands;

import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.City;
import strat.game.GameManager;
import strat.game.GameRules;
import strat.game.Nation;

public class Replenish implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		if (tokens.length < 2) {
			return new Response("Replenish: must provide a city to replenish");
		}
		
		Nation sender = gameManager.getNationByUser(senderID);
		City targetCity = gameManager.findFirstCity(tokens[1]);
		
		if (targetCity == null) {
			return new Response("Hire: " + tokens[1] + " is not a valid city.");
		}
		else if (targetCity.getOwnerID() != sender.getNationID()) {
			return new Response("Hire: Can only hire at cities you own.");
		}
		
		int amount;
		
		if (tokens.length >= 3) {
			try {
				amount = Integer.parseInt(tokens[2]);
			}
			catch (NumberFormatException e) {
				return new Response("Replenish: must provide a valid amount of troops to replenish");
			}
			
			if (amount <= 0) {
				return new Response("Replenish: must provide a valid amount of troops to replenish");
			}
		}
		else {
			amount = targetCity.getGarrisonCapacity() - targetCity.getGarrisonUnits();
		}
		
		int cost = amount * GameRules.getRulei("replenishmentCost");
		
		if (cost > sender.getMoney()) {
			return new Response("Replenish: Cost of replenishment (" + cost + ") is greater than the funds in your possession.");
		}
		
		sender.setMoney(sender.getMoney() - cost);
		targetCity.replenishGarrison(amount);
		
		return new Response(String.format("Replenished garrison of %s with %d troops at a cost of %d.", 
				targetCity.getName(), amount, cost));
	}
	
	@Override
	public String getName() {
		return "replenish";
	}
	
	@Override
	public String getUsage() {
		return "city [amount]";
	}

	@Override
	public String getInfo() {
		return "replenish the garrison of a city, omit the amount to replenish fully";
	}
	
	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.NATION;
	}
}
