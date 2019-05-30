package strat.game.commands;

import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.City;
import strat.game.GameManager;
import strat.game.GameRules;
import strat.game.Nation;
import strat.game.TurnLog;

public class Build implements Command {
	public static final String RAW_FORMAT = "build ";
	public static final String FORMAT = "\nFormat: " + RAW_FORMAT;
	
	@Override
	public Response execute(GameManager gameManager, long senderID,
			String rawMessage, String lowerMessage, String[] tokens) {
		if (tokens.length != 3) {
			return new Response(String.format("Build: Invalid number of arguments: %d%nFormat: %s %s",
					tokens.length - 1, getName(), getUsage()));
		}
		
		if (!GameRules.isValidBuilding(tokens[1])) {
			return new Response(String.format("Build: Can only build fort, market, barracks, stables, or foundry%nFormat: %s - %s",
					getName(), getUsage()));
		}
		
		City targetCity = gameManager.findFirstCity(tokens[2]);
		Nation sender = gameManager.getNationByUser(senderID);
		
		if (targetCity == null) {
			return new Response("Build: " + tokens[2] + " is not a valid city.");
		}
		else if (targetCity.getOwnerID() != sender.getNationID()) {
			return new Response("Build: Can only build at cities you own.");
		}
		
		int cost = GameRules.getRulei(tokens[1] + "Cost");
		
		if (cost > sender.getMoney()) {
			return new Response(String.format("Build: The cost of your purchase (%d) is greater than the amount of money in your posession (%d).",
					cost, sender.getMoney()));
		}
		
		if (targetCity.getBuildingLevel(tokens[1]) >= GameRules.getRulei("buildingCap")) {
			return new Response(String.format("Cannot construct %s in %s. Building cap (%d) reached.",
					tokens[1], targetCity.getName(), GameRules.getRulei("buildingCap")));
		}
		else {
			targetCity.setBuildingLevel(tokens[1], targetCity.getBuildingLevel(tokens[1]) + 1);
		}
		
		sender.setMoney(sender.getMoney() - cost);
		
		String desc = String.format("Constructed %s in %s%n", tokens[1], targetCity.getName());
		gameManager.getGame().getTurnLog().addEntry(new TurnLog.LogEntry(sender, "**CONSTRUCTION - " + sender.getName().toUpperCase() + "**",
				desc, TurnLog.Type.CONSTRUCTION));
		
		return new Response(desc);
	}
	
	@Override
	public String getName() {
		return "build";
	}
	
	@Override
	public String getUsage() {
		return "[fort|market|barracks|stables|foundry] city";
	}
	
	@Override
	public String getInfo() { 
		return "build a building upgrade at a city";
	}
	
	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.NATION;
	}
}
