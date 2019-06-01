package strat.game.commands;

import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.commands.ResponseType;
import strat.game.City;
import strat.game.GameManager;
import strat.game.Map;
import strat.game.Nation;

public class Regions implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		Nation sender = gameManager.getNationByUser(senderID);
		String info = getInfoForNation(gameManager.getGame().getMap(), sender);
		
		return new Response(ResponseType.PRIVATE, sender.getName(), info, sender.getRGB());
	}
	
	@Override
	public String getName() {
		return "regions";
	}
	
	@Override
	public String getUsage() {
		return "";
	}
	
	@Override
	public String getInfo() { 
		return "privately messages region information about your regions and the cities within";
	}
	
	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.NATION;
	}
	
	private static String getInfoForNation(Map map, Nation n) {
		StringBuilder sb = new StringBuilder();
		
		for (City c : map.getCities()) {
			if (c.getOwnerID() == n.getNationID()) {
				sb.append(String.format("%nRegion:\t\t%s%nCapital:\t%s%n", c.getRegion().getName(), c.getName()));
				sb.append(c.getInfo());
			}
		}
		
		return sb.toString();
	}
}
