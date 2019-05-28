package strat.commands;

import strat.game.City;
import strat.game.Map;
import strat.game.Nation;

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
		
		int cost = getCost(tokens[1]);
		
		if (cost > sender.getMoney()) {
			return String.format("Build: The cost of your purchase (%d) is greater than the amount of money in your posession (%d).",
					cost, sender.getMoney());
		}
		
		boolean exceededCap = false;
		
		if (tokens[1].equals("fort")) {
			exceededCap = targetCity.getFortLevel() >= City.BUILDING_CAP;
			
			if (!exceededCap) {
				targetCity.setFortLevel(targetCity.getFortLevel() + 1);
			}
		}
		else if (tokens[1].equals("market")) {
			exceededCap = targetCity.getMarketLevel() >= City.BUILDING_CAP;
			
			if (!exceededCap) {
				targetCity.setMarketLevel(targetCity.getMarketLevel() + 1);
			}
		}
		else if (tokens[1].equals("barracks")) {
			exceededCap = targetCity.getBarracksLevel() >= City.BUILDING_CAP;
			
			if (!exceededCap) {
				targetCity.setBarracksLevel(targetCity.getBarracksLevel() + 1);
			}
		}
		else if (tokens[1].equals("stables")) {
			exceededCap = targetCity.getStablesLevel() >= City.BUILDING_CAP;
			
			if (!exceededCap) {
				targetCity.setStablesLevel(targetCity.getStablesLevel() + 1);
			}
		}
		else {
			exceededCap = targetCity.getFoundryLevel() >= City.BUILDING_CAP;
			
			if (!exceededCap) {
				targetCity.setFoundryLevel(targetCity.getFoundryLevel() + 1);
			}
		}
		
		if (exceededCap) {
			return String.format("Cannot construct %s in %s. Building cap (%d) reached.",
					tokens[1], targetCity.getName(), City.BUILDING_CAP);
		}
		
		sender.setMoney(sender.getMoney() - cost);
		
		return "Constructed " + tokens[1] + " in " + targetCity.getName();
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
		return str.equals("fort") || str.equals("market") || str.equals("barracks")
				|| str.equals("stables") || str.equals("foundry");
	}
	
	private static int getCost(String str) {
		if (str.equals("fort")) {
			return City.FORT_COST;
		}
		else if (str.equals("market")) {
			return City.MARKET_COST;
		}
		else if (str.equals("barracks")) {
			return City.BARRACKS_COST;
		}
		else if (str.equals("stables")) {
			return City.STABLES_COST;
		}
		else {
			return City.FOUNDRY_COST;
		}
	}
}
