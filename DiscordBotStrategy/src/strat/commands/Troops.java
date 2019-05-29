package strat.commands;

import strat.bot.BotUtils;
import strat.bot.DiscordBot;
import strat.game.Army;
import strat.game.Map;
import strat.game.Nation;

public class Troops implements Command {

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
			
			if (found != null) {
				String info = getArmyInfo(map, found, false);
				BotUtils.sendLongMessage(DiscordBot.getDMFromNation(sender), info);
				return null;
			}
			else {
				return "Troops: Could not find given nation.";
			}
		}
		else {
			String info = getArmyInfo(map, sender, true);
			BotUtils.sendLongMessage(DiscordBot.getDMFromNation(sender), info);
			return null;
		}
	}
	
	@Override
	public String getFormat() {
		return "troops [nation]";
	}
	
	@Override
	public String getSynopsis() { 
		return "show troop info for yourself or a selected nation";
	}
	
	private static String getArmyInfo(Map map, Nation n, boolean showDetailedInfo) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("**ARMIES - %s**\n\n", n.getName()));
		
		for (Army a : map.getArmies()) {
			if (a.getOwnerID() == n.getNationID()) {
				if (showDetailedInfo) {
					sb.append(String.format("**%s Army %d**%nCurrent Region: %s%nMovement Remaining: %d%n",
							n.getName(), a.getArmyID(), map.getRegion(a.getHexagon().getRegionID()).getName(),
							a.getRemainingMoves()));
					sb.append(String.format("Infantry: %d%nCavalry: %d%nArtillery: %d%n",
							a.getUnits("infantry"), a.getUnits("cavalry"), a.getUnits("artillery")));
				}
				else {
					sb.append(String.format("%s Army %d%n", n.getName(), a.getArmyID()));
				}
			}
		}
		
		return sb.toString();
	}
}
