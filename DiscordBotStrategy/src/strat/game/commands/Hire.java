package strat.game.commands;

import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.City;
import strat.game.GameManager;
import strat.game.GameRules;
import strat.game.LogEntry;
import strat.game.Nation;

public class Hire implements Command {
	public static final String RAW_FORMAT = "hire ";
	public static final String FORMAT = "\nFormat: " + RAW_FORMAT;
	
	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		if (tokens.length != 4) {
			return new Response(String.format("Hire: Invalid number of arguments: %d%nFormat: %s %s",
					tokens.length - 1, getName(), getUsage()));
		}
		
		if (!GameRules.isValidUnitType(tokens[1])) {
			return new Response(String.format("Hire: Must hire either infantry, cavalry, or artillery%nFormat: %s %s",
					getName(), getUsage()));
		}
		
		City targetCity = gameManager.findFirstCity(tokens[2]);
		Nation sender = gameManager.getNationByUser(senderID);
		
		if (targetCity == null) {
			return new Response("Hire: " + tokens[2] + " is not a valid city.");
		}
		else if (targetCity.getOwnerID() != sender.getNationID()) {
			return new Response("Hire: Can only hire at cities you own.");
		}
		
		int amount;
		
		try {
			amount = Integer.parseInt(tokens[3]);
		}
		catch (NumberFormatException e) {
			return new Response("Hire: Invalid amount " + tokens[3]);
		}
		
		if (amount <= 0) {
			return new Response("Hire: Invalid amount " + tokens[3]);
		}
		
		int cost = amount * GameRules.getRulei(tokens[1] + "Cost");
		int maxAmount = targetCity.getRecruitCapacity(tokens[1]);
		
		if (cost > sender.getMoney()) {
			return new Response(String.format("Hire: The cost of your purchase (%d) is greater than the amount of money in your posession (%d).",
					cost, sender.getMoney()));
		}
		
		if (amount > maxAmount) {
			return new Response(String.format("Hire: You are attempting to hire too many troops. The maximum %s allowed to be hired at %s is %d",
					tokens[1], targetCity.getName(), maxAmount));
		}
		
		if (!targetCity.hireUnits(tokens[1], amount)) {
			return new Response("Hire: could not spawn new forces around " + tokens[2]);
		}
		
		sender.setMoney(sender.getMoney() - cost);
		
		String desc = String.format("Recruited %d %s %s in %s%n", amount, tokens[1],
				getUnitEmoji(tokens[1]), targetCity.getName()); 
		gameManager.getGame().getTurnLog().addEntry(new LogEntry(sender, ":postal_horn: **RECRUITMENT - " + sender.getName().toUpperCase() + "**",
				desc, LogEntry.Type.RECRUITMENT));
		
		return new Response(desc);
	}
	
	@Override
	public String getName() {
		return "hire";
	}
	
	@Override
	public String getUsage() {
		return "[infantry|cavalry|artillery] city amount";
	}
	
	@Override
	public String getInfo() { 
		return "hire troops at a city";
	}
	
	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.NATION;
	}
	
	private static String getUnitEmoji(String unit) {
		switch (unit) {
			case "infantry":
				return ":guardsman:";
			case "cavalry":
				return ":horse:";
			case "artillery":
				return ":bomb:";
			default:
				return ":bust_in_silhouette:";
		}
	}
}
