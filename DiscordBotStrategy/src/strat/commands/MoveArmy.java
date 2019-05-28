package strat.commands;

import strat.game.Army;
import strat.game.Map;
import strat.game.Nation;

public class MoveArmy implements Command {
	public static final String FORMAT = "\nFormat: movearmy army_number direction(1-6) [distance]";
	
	@Override
	public String execute(Map map, Nation sender, String[] tokens) {
		if (tokens.length < 4) {
			return "Move Army: Invalid number of arguments: " + (tokens.length - 1) + FORMAT;
		}
		
		try {
			int ai = Integer.parseInt(tokens[1]);
			int dir = Integer.parseInt(tokens[2]);
			int dist = Integer.parseInt(tokens[3]);
			
			Army army = map.getArmy(sender.getNationID(), ai);
			
			if (army == null) {
				return "Move Army: Invalid army number" + FORMAT;
			}
			
			if (dir < 1 || dir > 6) {
				return "Move Army: Invalid direction, directions must be 1-6" + FORMAT;
			}
			
			if (dist > army.getRemainingMoves()) {
				return String.format("Move Army: Invalid move distance, army %d has %d moves remaining%s",
						ai, army.getRemainingMoves(), FORMAT);
			}
			
			String[] additionalInfo = new String[1];
			int res = army.move(dir - 1, dist, additionalInfo);
			
			return String.format("Sucessfully moved army %d %d tiles.%n%s", army.getArmyID(), res, additionalInfo[0]);
		}
		catch (NumberFormatException e) {
			return "Move Army: Malformed argument" + FORMAT;
		}
	}

	@Override
	public String getFormat() {
		return "movearmy army_number direction(0-6) distance";
	}

	@Override
	public String getSynopsis() {
		return "moves the selected army the given distance in the given direction";
	}

}
