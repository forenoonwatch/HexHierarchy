package strat.game.commands;

import strat.commands.Command;
import strat.commands.InputLevel;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.commands.ResponseType;
import strat.game.City;
import strat.game.GameManager;
import strat.game.GameRules;
import strat.game.LogEntry;
import strat.game.Nation;

public class Spy implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		if (tokens.length < 2) {
			return new Response("Spy: You must provide a city.");
		}
		
		City targetCity = gameManager.findFirstCity(tokens[1]);
		Nation sender = gameManager.getNationByUser(senderID);
		
		if (targetCity == null || sender == null) {
			return new Response("Spy: Invalid city.");
		}
		else if (targetCity.getOwnerID() == sender.getNationID()) {
			return new Response("Spy: Cannot spy on your own cities.");
		}
		
		int minDist = Integer.MAX_VALUE;
		boolean found = false;
		
		for (City c : gameManager.getGame().getMap().getCities()) {
			if (c.getOwnerID() == sender.getNationID()) {
				int dist = c.getHexagon().distanceFrom(targetCity.getHexagon());
				
				if (dist < minDist) {
					found = true;
					minDist = dist;
				}
			}
		}
		
		if (!found) {
			return new Response("Spy: Cannot send out spies with no cities.");
		}
		
		double chance = GameRules.getRuled("spyScalar");
		chance = Math.exp(chance - chance * minDist);
		
		if (Math.random() < chance) {
			return getCityInfo(targetCity);
		}
		else {
			gameManager.getGame().getTurnLog().addEntry(new LogEntry(sender,
					String.format(":spy: **SPY CAUGHT - %s**", targetCity.getName().toUpperCase()),
					String.format("A spy from %s was caught trying to enter into %s!", sender.getName(), targetCity.getName()),
					LogEntry.Type.SPY_CAUGHT));
			
			return new Response(ResponseType.PRIVATE, null,
					"Spy action failed! Your spy has been caught, this incident will be publicized at the end of the turn.", 0);
		}
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.NATION;
	}
	
	@Override
	public InputLevel getInputLevel() {
		return InputLevel.DM_CHANNEL;
	}

	@Override
	public String getName() {
		return "spy";
	}

	@Override
	public String getUsage() {
		return "city";
	}

	@Override
	public String getInfo() {
		return "sends a spy to the given city with a chance of being discovered";
	}
	
	private static Response getCityInfo(City c) {
		StringBuilder sb = new StringBuilder();
		
		int profit = c.getBuildingLevel("market") * GameRules.getRulei("marketProfit");
		
		sb.append(String.format("Fort Level:\t%d (%d/%d infantry, %d/%d cavalry, %d/%d artillery)%n", 
				c.getBuildingLevel("fort"), c.getGarrison().getUnits("infantry"), c.getGarrisonCapacity("infantry"),
				c.getGarrison().getUnits("cavalry"), c.getGarrisonCapacity("cavalry"),
				c.getGarrison().getUnits("artillery"), c.getGarrisonCapacity("artillery")));
		sb.append(String.format("Market Level:\t%d (+%d/turn)%n", c.getBuildingLevel("market"),
				profit));
		sb.append(String.format("Barracks Level:\t%d (%d recruits available)%n",
				c.getBuildingLevel("barracks"), c.getRecruitCapacity("infantry")));
		sb.append(String.format("Stables Level:\t%d (%d recruits available)%n",
				c.getBuildingLevel("stables"), c.getRecruitCapacity("cavalry")));
		sb.append(String.format("Foundry Level:\t%d (%d recruits available)%n",
				c.getBuildingLevel("foundry"), c.getRecruitCapacity("artillery")));
		
		return new Response(ResponseType.PRIVATE, String.format("**%s, %s**", c.getName(), c.getRegion().getName()), sb.toString(),
				c.getOwner().getRGB());
	}
}
