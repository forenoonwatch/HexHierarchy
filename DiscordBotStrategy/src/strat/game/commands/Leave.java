package strat.game.commands;

import strat.bot.BotUtils;
import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.GameManager;
import strat.game.LogEntry;
import strat.game.Nation;
import strat.game.relationships.Alliance;
import strat.game.relationships.Relationship;

public class Leave implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		if (tokens.length != 3) {
			return new Response(String.format("Leave: Invalid number of arguments: %d%n%s %s",
					tokens.length - 1, getName(), getUsage()));
		}
		
		if (!isValid(tokens[1])) {
			return new Response(String.format("Leave: Must specify either alliance or trade.%n%s %s",
					getName(), getUsage()));
		}
		
		long userID = BotUtils.parseUserID(tokens[2]);
		
		if (userID == 0L || userID == senderID) {
			return new Response("Leave: Invalid user.");
		}
		
		Nation sender = gameManager.getNationByUser(senderID);
		Nation target = gameManager.getNationByUser(userID);
		
		if (target == null) {
			return new Response("Leave: Invalid user.");
		}
		
		Relationship r = null;
		
		switch (tokens[1]) {
			case "alliance":
				r = gameManager.getGame().findAllianceBetween(sender, target);
				break;
			case "trade":
				r = gameManager.getGame().findTradeBetween(sender, target);
				break;
			default:
				return null;
		}
		
		if (r == null) {
			return new Response(String.format("No existing %s with %s to leave.",
					tokens[1], target.getName()));
		}
		
		r.removeNation(sender);
		
		boolean remove = r.getNations().size() <= 1;
		
		if (remove) {
			gameManager.getGame().removeRelationship(r);
		}
		
		switch (tokens[1]) {
			case "alliance":
			{
				Alliance al = (Alliance)r;
				
				if (remove) {
					gameManager.logDiplomacy(new LogEntry(sender, ":broken_heart: **ALLIANCE DESTROYED**",
							String.format("%s has been completely shattered. No nations remain within.", al.getName()),
							LogEntry.Type.ALLIANCE_LEFT));
				}
				else {
					gameManager.logDiplomacy(new LogEntry(sender, ":broken_heart: **ALLIANCE BROKEN**",
							String.format("%s has left %s", sender.getName(), al.getName()), LogEntry.Type.ALLIANCE_LEFT));
				}
			}
				break;
			case "trade":
			{
				gameManager.logDiplomacy(new LogEntry(sender, ":scales: **TRADE AGREEMENT BROKEN**",
						String.format("%s has broken their trade agreement with %s", sender.getName(), target.getName()),
						LogEntry.Type.TRADE_LEFT));
			}
				break;
		}
		
		return new Response(String.format("Successfully left %s with %s", tokens[1], target.getName()));
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.NATION;
	}

	@Override
	public String getName() {
		return "leave";
	}

	@Override
	public String getUsage() {
		return "[alliance|trade] user(ping)";
	}

	@Override
	public String getInfo() {
		return "leaves an alliance or trade agreement with the given user";
	}
	
	private static boolean isValid(String token) {
		return token.equals("alliance") || token.equals("trade");
	}
}
