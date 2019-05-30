package strat.game.commands;

import strat.bot.BotUtils;
import strat.bot.DiscordBot;
import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.City;
import strat.game.GameManager;
import strat.game.GameRules;
import strat.game.Map;
import strat.game.Nation;
import sx.blah.discord.handle.obj.IUser;

public class Regions implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		IUser user = DiscordBot.getUserByID(senderID);
		Nation sender = gameManager.getNationByUser(senderID);
		String info = getInfoForNation(gameManager.getGame().getMap(), sender);
		
		BotUtils.sendLongMessage(user.getOrCreatePMChannel(), info);
		return null;
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
				int profit = c.getBuildingLevel("market") * GameRules.getRulei("marketProfit");
				
				sb.append(String.format("%nRegion:\t\t%s%nCapital:\t%s%n", c.getRegion().getName(), c.getName()));
				sb.append(String.format("Fort Level:\t%d (%d/%d infantry, %d/%d cavalry, %d/%d artillery)%n", 
						c.getBuildingLevel("fort"), c.getGarrison().getUnits("infantry"), c.getGarrisonCapacity("infantry"),
						c.getGarrison().getUnits("cavalry"), c.getGarrisonCapacity("cavalry"),
						c.getGarrison().getUnits("artillery"), c.getGarrisonCapacity("artillery")));
				sb.append(String.format("Market Level:\t%d (+%d/turn)%n", c.getBuildingLevel("market"),
						profit));
				sb.append(String.format("Barracks Level:\t%d%nStables Level:\t%d%nFoundry Level:\t%d%n",
						c.getBuildingLevel("barracks"), c.getBuildingLevel("stables"), c.getBuildingLevel("foundry")));
			}
		}
		
		return sb.toString();
	}
}
