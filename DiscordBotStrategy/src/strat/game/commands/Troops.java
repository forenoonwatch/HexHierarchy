package strat.game.commands;

import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.commands.ResponseType;
import strat.game.Army;
import strat.game.Game;
import strat.game.GameManager;
import strat.game.Nation;

public class Troops implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		Nation sender = gameManager.getNationByUser(senderID);
		
		if (tokens.length > 1) {
			Nation found = gameManager.matchNationByName(lowerMessage);
			
			if (found != null) {
				return getArmyInfo(gameManager.getGame(), found, false);
			}
			else {
				return new Response("Troops: Could not find given nation.");
			}
		}
		else {
			return getArmyInfo(gameManager.getGame(), sender, true);
		}
	}
	
	@Override
	public String getName() {
		return "troops";
	}
	
	@Override
	public String getUsage() {
		return "[nation]";
	}
	
	@Override
	public String getInfo() { 
		return "show troop info for yourself or a selected nation";
	}
	
	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.NATION;
	}
	
	private static Response getArmyInfo(Game game, Nation n, boolean showDetailedInfo) {
		StringBuilder sb = new StringBuilder();
		
		for (Army a : game.getArmies()) {
			if (a.getOwnerID() == n.getNationID()) {
				if (showDetailedInfo) {
					sb.append(String.format("**%s Army %d**%nCurrent Region: %s%nMovement Remaining: %d%n",
							n.getName(), a.getArmyID(), game.getMap().getRegion(a.getHexagon().getRegionID()).getName(),
							a.getRemainingMoves()));
					sb.append(String.format("Infantry: %d%nCavalry: %d%nArtillery: %d%n",
							a.getUnits("infantry"), a.getUnits("cavalry"), a.getUnits("artillery")));
				}
				else {
					sb.append(String.format("%s Army %d%n", n.getName(), a.getArmyID()));
				}
			}
		}
		
		return new Response(ResponseType.PRIVATE, String.format("**ARMIES - %s**\n\n", n.getName().toUpperCase()), sb.toString(),
				n.getRGB());
	}
}
