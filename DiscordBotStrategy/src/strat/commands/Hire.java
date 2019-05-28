package strat.commands;

import strat.game.City;
import strat.game.Map;
import strat.game.Nation;
import strat.game.TurnLog;

public class Hire implements Command {
	public static final String RAW_FORMAT = "hire [infantry|cavalry|artillery] city amount";
	public static final String FORMAT = "\nFormat: " + RAW_FORMAT;
	
	@Override
	public String execute(Map map, Nation sender, String[] tokens) {
		if (tokens.length != 4) {
			return "Hire: Invalid number of arguments: " + (tokens.length - 1) + FORMAT;
		}
		
		if (!tokens[1].equals("infantry") && !tokens[1].equals("cavalry") && !tokens[1].equals("artillery")) {
			return "Hire: Must hire either infantry, cavalry, or artillery" + FORMAT;
		}
		
		City targetCity = null;
		
		for (City c : map.getCities()) {
			if (c.getName().toLowerCase().equals(tokens[2])) {
				targetCity = c;
			}
		}
		
		if (targetCity == null) {
			return "Hire: " + tokens[2] + " is not a valid city.";
		}
		else if (targetCity.getOwnerID() != sender.getNationID()) {
			return "Hire: Can only hire at cities you own.";
		}
		
		int amount;
		
		try {
			amount = Integer.parseInt(tokens[3]);
		}
		catch (NumberFormatException e) {
			return "Hire: Invalid amount" + FORMAT;
		}
		
		if (amount <= 0) {
			return "Hire: Invalid amount" + FORMAT;
		}
		
		int cost;
		int maxAmount;
		
		if (tokens[1].equals("infantry")) {
			cost = amount * City.INFANTRY_COST;
			maxAmount = targetCity.getInfantryCapacity();
		}
		else if (tokens[1].equals("cavalry")) {
			cost = amount * City.CAVALRY_COST;
			maxAmount = targetCity.getCavalryCapacity();
		}
		else {
			cost = amount * City.ARTILLERY_COST;
			maxAmount = targetCity.getArtilleryCapacity();
		}
		
		if (cost > sender.getMoney()) {
			return String.format("Hire: The cost of your purchase (%d) is greater than the amount of money in your posession (%d).",
					cost, sender.getMoney());
		}
		
		if (amount > maxAmount) {
			return String.format("Hire: You are attempting to hire too many troops. The maximum %s allowed to be hired at %s is %d",
					tokens[1], targetCity.getName(), maxAmount);
		}
		
		boolean res;
		
		if (tokens[1].equals("infantry")) {
			res = targetCity.hireInfantry(amount);
		}
		else if (tokens[1].equals("cavalry")) {
			res = targetCity.hireCavalry(amount);
		}
		else {
			res = targetCity.hireArtillery(amount);
		}
		
		if (!res) {
			return "Hire: could not spawn new forces around " + tokens[2];
		}
		
		sender.setMoney(sender.getMoney() - cost);
		
		String desc = String.format("Recruited %d %s in %s%n", amount, tokens[1], targetCity.getName()); 
		map.getTurnLog().addEntry(new TurnLog.LogEntry(sender, "**RECRUITMENT - " + sender.getName().toUpperCase() + "**",
				desc, TurnLog.Type.RECRUITMENT));
		
		return desc;
	}
	
	@Override
	public String getFormat() {
		return RAW_FORMAT;
	}
	
	@Override
	public String getSynopsis() { 
		return "hire troops at a city";
	}
}
