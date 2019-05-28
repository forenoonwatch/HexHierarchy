package strat.commands;

import strat.bot.BotUtils;
import strat.bot.DiscordBot;
import strat.game.City;
import strat.game.Map;
import strat.game.Nation;
import sx.blah.discord.handle.obj.IUser;

public class Info implements Command {

	@Override
	public String execute(Map map, Nation sender, String[] tokens) {
		if (tokens.length > 1) {
			int maxMatched = 0;
			Nation found = null;
			
			for (Nation n : map.getNations()) {
				String nameL = n.getName().toLowerCase();
				int matched = 0;
				
				for (int i = 1; i < tokens.length; ++i) {
					if (nameL.contains(tokens[i])) {
						++matched;
					}
				}
				
				if (matched > maxMatched) {
					maxMatched = matched;
					found = n;
				}
			}
			
			IUser user = DiscordBot.getUserFromNation(found);
			
			if (found != null) {
				return getInfoForNation(map, found, user, false);
			}
			else {
				return "Info: Could not find given nation.";
			}
		}
		else {
			IUser user = DiscordBot.getUserFromNation(sender);
			String info = getInfoForNation(map, sender, user, true);
			
			BotUtils.sendMessage(user.getOrCreatePMChannel(), info);
			return null;
		}
	}
	
	@Override
	public String getFormat() {
		return "info [nation]";
	}
	
	@Override
	public String getSynopsis() { 
		return "show information for yourself or a selected nation";
	}
	
	private static String getInfoForNation(Map map, Nation n, IUser user, boolean showMoney) {
		int numLands = 0;
		int profitPerTurn = 0;
		
		for (City c : map.getCities()) {
			if (c.getOwnerID() == n.getNationID()) {
				++numLands;
				int profit = c.getMarketLevel() * City.MARKET_PROFIT;
				
				profitPerTurn += profit;
			}
		}
		
		if (showMoney) {
			return String.format("**%s**%nMoney:\t\t%d (+%d/turn)%nRegions held:\t%d%n", n.getName(),
					n.getMoney(), profitPerTurn, numLands);
		}
		
		return String.format("**%s**%nLeader:\t%s%nRegions held:\t%d%n", n.getName(),
				user == null ? "No Ruler" : DiscordBot.getFormattedName(user), numLands);
	}
}
