package strat.commands;

import strat.bot.BotUtils;
import strat.bot.DiscordBot;
import strat.game.Battle;
import strat.game.City;
import strat.game.Map;
import strat.game.Nation;
import strat.game.Siege;
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
				int numInfantry = c.getFortLevel() * Siege.ARMIES_PER_FORT_LEVEL * Battle.INFANTRY_WEIGHT;
				int numCavalry = c.getFortLevel() * Siege.ARMIES_PER_FORT_LEVEL * Battle.CAVALRY_WEIGHT;
				int numArtillery = c.getFortLevel() * Siege.ARMIES_PER_FORT_LEVEL * Battle.ARTILLERY_WEIGHT;
				int profit = c.getMarketLevel() * City.MARKET_PROFIT;
				
				sb.append(String.format("%nRegion:\t\t%s%nCapital:\t%s%n", c.getRegion().getName(), c.getName()));
				sb.append(String.format("Fort Level:\t%d (%d infantry, %d cavalry, %d artillery)%n", 
						c.getFortLevel(), numInfantry, numCavalry, numArtillery));
				sb.append(String.format("Market Level:\t%d (+%d/turn)%n", c.getMarketLevel(),
						profit));
				sb.append(String.format("Barracks Level:\t%d%nStables Level:\t%d%nFoundry Level:\t%d%n",
						c.getBarracksLevel(), c.getStablesLevel(), c.getFoundryLevel()));
			}
		}
		
		return sb.toString();
	}
}
