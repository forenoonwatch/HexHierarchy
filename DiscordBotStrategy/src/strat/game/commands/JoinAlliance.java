package strat.game.commands;

import strat.bot.BotUtils;
import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.GameManager;
import strat.game.Nation;
import strat.game.relationships.Alliance;

public class JoinAlliance implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		if (tokens.length != 2) {
			return new Response(String.format("JoinAlliance: Invalid number of arguments: %d%n%s %s",
					tokens.length - 1, getName(), getUsage()));
		}
		
		Nation sender = gameManager.getNationByUser(senderID);
		
		if (gameManager.getGame().getAllianceForNation(sender) != null) {
			return new Response("JoinAlliance: You are already in an alliance.");
		}
		
		long userID = BotUtils.parseUserID(tokens[1]);
		
		if (userID == 0L || userID == senderID) {
			return new Response("JoinAlliance: Invalid user.");
		}
		
		Nation target = gameManager.getNationByUser(userID);
		
		if (target == null) {
			return new Response("JoinAlliance: Invalid user.");
		}
		
		Alliance targetA = gameManager.getGame().getAllianceForNation(target);
		
		if (targetA == null) {
			return new Response("JoinAlliance: user is not in an alliance.");
		}
		
		if (gameManager.findPendingAllianceBetween(sender, target) != null) {
			return new Response(String.format("JoinAlliance: Already requested alliance with %s", target.getName()));
		}
		
		Alliance a = new Alliance(targetA.getName(), targetA.getRGB());
		a.addNation(sender);
		a.addNation(target);
		
		gameManager.addPendingRelationship(a);
		
		return new Response(String.format("Successfully requested to join %s from %s",
				targetA.getName(), target.getName()));
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.NATION;
	}

	@Override
	public String getName() {
		return "joinalliance";
	}

	@Override
	public String getUsage() {
		return "user(ping)";
	}

	@Override
	public String getInfo() {
		return "requests to join a user's alliance";
	}

}
