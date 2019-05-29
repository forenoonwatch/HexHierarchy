package strat.commands;

import strat.game.City;
import strat.game.GameRules;
import strat.game.Map;
import strat.game.Nation;

public class Replenish implements Command {

	@Override
	public String execute(Map map, Nation sender, String[] tokens) {
		if (tokens.length < 2) {
			return "Replenish: must provide a city to replenish";
		}
		
		City targetCity = null;
		
		for (City c : map.getCities()) {
			if (c.getName().toLowerCase().equals(tokens[1])) {
				targetCity = c;
			}
		}
		
		if (targetCity == null) {
			return "Hire: " + tokens[1] + " is not a valid city.";
		}
		else if (targetCity.getOwnerID() != sender.getNationID()) {
			return "Hire: Can only hire at cities you own.";
		}
		
		int amount;
		
		if (tokens.length >= 3) {
			try {
				amount = Integer.parseInt(tokens[2]);
			}
			catch (NumberFormatException e) {
				return "Replenish: must provide a valid amount of troops to replenish";
			}
			
			if (amount <= 0) {
				return "Replenish: must provide a valid amount of troops to replenish";
			}
		}
		else {
			amount = targetCity.getGarrisonCapacity() - targetCity.getGarrisonUnits();
		}
		
		int cost = amount * GameRules.getRulei("replenishmentCost");
		
		if (cost > sender.getMoney()) {
			return "Replenish: Cost of replenishment (" + cost + ") is greater than the funds in your possession.";
		}
		
		sender.setMoney(sender.getMoney() - cost);
		targetCity.replenishGarrison(amount);
		
		return String.format("Replenished garrison of %s with %d troops at a cost of %d.", 
				targetCity.getName(), amount, cost);
	}

	@Override
	public String getFormat() {
		return "replenish city [amount]";
	}

	@Override
	public String getSynopsis() {
		return "replenish the garrison of a city, omit the amount to replenish fully";
	}

}
