package strat.game.commands;

import strat.bot.BotUtils;
import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.GameManager;
import strat.game.Nation;
import strat.game.relationships.Relationship;

public class Decline implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		if (tokens.length < 3) {
			return new Response(String.format("Decline: Invalid number of arguments: %d%n%s %s",
					tokens.length - 1, getName(), getUsage()));
		}
		
		if (!isValid(tokens[1])) {
			return new Response(String.format("Decline: Must specify either alliance or trade%n%s %s",
					getName(), getUsage()));
		}
		
		long userID = BotUtils.parseUserID(tokens[2]);
		
		if (userID == 0L) {
			return new Response("Decline: Invalid user.");
		}
		
		Nation sender = gameManager.getNationByUser(senderID);
		Nation target = gameManager.getNationByUser(userID);
		
		if (target == null) {
			return new Response("Decline: Invalid user.");
		}
		
		Relationship r = null;
		
		switch (tokens[1]) {
			case "alliance":
				r = gameManager.findPendingAllianceBetween(sender, target);
				break;
			case "trade":
				r = gameManager.findPendingTradeBetween(sender, target);
				break;
			case "peace":
				r = gameManager.findPendingPeaceBetween(sender, target);;
			default:
				return null;
		}
		
		if (r == null) {
			return new Response(String.format("No pending %s with %s to decline.",
					tokens[1], target.getName()));
		}
		
		if (r.getSender() == sender) {
			return new Response("Decline: Cannot decline your own " + tokens[1] + ".");
		}
		
		gameManager.declineRelationship(r);
		
		return new Response(String.format("Declined %s with %s.", tokens[1], target.getName()));
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.NATION;
	}

	@Override
	public String getName() {
		return "decline";
	}

	@Override
	public String getUsage() {
		return "[alliance|trade|peace] user(ping)";
	}

	@Override
	public String getInfo() {
		return "decline an alliance, trade partnership, or peace treaty with a user";
	}
	
	private static boolean isValid(String token) {
		return token.equals("alliance") || token.equals("trade") || token.equals("peace");
	}
}
