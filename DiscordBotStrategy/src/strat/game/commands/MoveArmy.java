package strat.game.commands;

import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.Army;
import strat.game.GameManager;
import strat.game.Nation;

public class MoveArmy implements Command {
	public static final String FORMAT = "\nFormat: movearmy army_number direction(1-6) [distance]";
	
	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		if (tokens.length < 4) {
			return new Response(String.format("Move Army: Invalid number of arguments: %d%nFormat: %s - %s",
					tokens.length - 1, getName(), getUsage()));
		}
		
		try {
			int ai = Integer.parseInt(tokens[1]);
			int dir = Integer.parseInt(tokens[2]);
			int dist = Integer.parseInt(tokens[3]);
			
			Nation sender = gameManager.getNationByUser(senderID);
			
			Army army = gameManager.getGame().getArmy(sender.getNationID(), ai);
			
			if (army == null) {
				return new Response(String.format("Move Army: Invalid army number%nFormat: %s - %s", getName(), getUsage()));
			}
			
			if (dir < 1 || dir > 6) {
				return new Response("Move Army: Invalid direction, directions must be 1-6");
			}
			
			if (dist > army.getRemainingMoves()) {
				return new Response(String.format("Move Army: Invalid move distance, army %d has %d moves remaining%nFormat: %s - %s",
						ai, army.getRemainingMoves(), getName(), getUsage()));
			}
			
			String[] additionalInfo = new String[1];
			int res = army.move(dir - 1, dist, additionalInfo);
			
			return new Response(String.format("Sucessfully moved army %d %d tiles.%n%s", army.getArmyID(), res, additionalInfo[0]));
		}
		catch (NumberFormatException e) {
			return new Response(String.format("Move Army: Malformed argument%nFormat: %s - %s", getName(), getUsage()));
		}
	}

	@Override
	public String getName() {
		return "movearmy";
	}
	
	@Override
	public String getUsage() {
		return "army_number direction(0-6) distance";
	}

	@Override
	public String getInfo() {
		return "moves the selected army the given distance in the given direction";
	}
	
	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.NATION;
	}
}
