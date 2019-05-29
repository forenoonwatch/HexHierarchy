package strat.commands;

import strat.bot.BotUtils;
import strat.bot.DiscordBot;
import strat.game.City;
import strat.game.GameRules;
import strat.game.Map;
import strat.game.Nation;
import sx.blah.discord.handle.obj.IUser;

public class Regions implements Command {

	@Override
	public String execute(Map map, Nation sender, String[] tokens) {
		IUser user = DiscordBot.getUserFromNation(sender);
		String info = getInfoForNation(map, sender);
		
		BotUtils.sendLongMessage(user.getOrCreatePMChannel(), info);
		return null;
	}
	
	@Override
	public String getFormat() {
		return "regions";
	}
	
	@Override
	public String getSynopsis() { 
		return "privately messages region information about your regions and the cities within";
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
