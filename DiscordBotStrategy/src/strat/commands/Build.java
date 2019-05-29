package strat.commands;

import strat.game.City;
import strat.game.GameRules;
import strat.game.Map;
import strat.game.Nation;
import strat.game.TurnLog;

public class Build implements Command {
	public static final String RAW_FORMAT = "build [fort|market|barracks|stables|foundry] city";
	public static final String FORMAT = "\nFormat: " + RAW_FORMAT;
	
	@Override
	public String execute(Map map, Nation sender, String[] tokens) {
		if (tokens.length != 3) {
			return "Build: Invalid number of arguments: " + (tokens.length - 1) + FORMAT;
		}
		
		if (!isValid(tokens[1])) {
			return "Build: Can only build fort, market, barracks, stables, or foundry" + FORMAT;
		}
		
		City targetCity = null;
		
		for (City c : map.getCities()) {
			if (c.getName().toLowerCase().equals(tokens[2])) {
				targetCity = c;
			}
		}
		
		if (targetCity == null) {
			return "Build: " + tokens[2] + " is not a valid city.";
		}
		else if (targetCity.getOwnerID() != sender.getNationID()) {
			return "Build: Can only build at cities you own.";
		}
		
		int cost = GameRules.getRulei(tokens[1] + "Cost");
		
		if (cost > sender.getMoney()) {
			return String.format("Build: The cost of your purchase (%d) is greater than the amount of money in your posession (%d).",
					cost, sender.getMoney());
		}
		
		if (targetCity.getBuildingLevel(tokens[1]) >= GameRules.getRulei("buildingCap")) {
			return String.format("Cannot construct %s in %s. Building cap (%d) reached.",
					tokens[1], targetCity.getName(), GameRules.getRulei("buildingCap"));
		}
		else {
			targetCity.setBuildingLevel(tokens[1], targetCity.getBuildingLevel(tokens[1]) + 1);
		}
		
		sender.setMoney(sender.getMoney() - cost);
		
		String desc = String.format("Constructed %s in %s%n", tokens[1], targetCity.getName());
		map.getTurnLog().addEntry(new TurnLog.LogEntry(sender, "**CONSTRUCTION - " + sender.getName().toUpperCase() + "**",
				desc, TurnLog.Type.CONSTRUCTION));
		
		return desc;
	}
	
	@Override
	public String getFormat() {
		return RAW_FORMAT;
	}
	
	@Override
	public String getSynopsis() { 
		return "build a building upgrade at a city";
	}
	
	private static boolean isValid(String str) {
		for (String building : GameRules.getBuildingTypes()) {
			if (str.equals(building)) {
				return true;
			}
		}
		
		return false;
	}
}
