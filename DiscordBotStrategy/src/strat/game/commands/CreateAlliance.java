package strat.game.commands;

import strat.bot.BotUtils;
import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.GameManager;
import strat.game.Nation;
import strat.game.relationships.Alliance;
import strat.util.Util;

public class CreateAlliance implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		if (tokens.length < 4) {
			return new Response(String.format("CreateAlliance: Invalid number of arguments: %d%n%s %s",
					tokens.length - 1, getName(), getUsage()));
		}
		
		Nation sender = gameManager.getNationByUser(senderID);
		
		/*if (gameManager.getGame().getAllianceForNation(sender) != null) {
			return new Response("CreateAlliance: You are already in an alliance.");
		}*/
		
		long userID = BotUtils.parseUserID(tokens[1]);
		
		if (userID == 0L || userID == senderID) {
			return new Response("CreateAlliance: Invalid user.");
		}
		
		Nation target = gameManager.getNationByUser(userID);
		
		if (target == null) {
			return new Response("CreateAlliance: Invalid user.");
		}
		
		if (gameManager.getGame().getAllianceForNation(target) != null) {
			return new Response("CreateAlliance: user is already in an alliance"); 
		}
		
		if (gameManager.findPendingAllianceBetween(sender, target) != null) {
			return new Response(String.format("CreateAlliance: Already requested alliance with %s", target.getName()));
		}
		
		if (gameManager.getGame().findWarBetween(sender, target) != null) {
			return new Response(String.format("CreateAlliance: Cannot ally %s as you are at war.", target.getName()));
		}
		
		int rgb;
		
		try {
			rgb = Integer.valueOf(tokens[2], 16);
		}
		catch (NumberFormatException e) {
			return new Response("CreateAlliance: malformed alliance color");
		}
		
		String properName = Util.getQuotedSubstring(rawMessage);
		
		if (properName == null) {
			return new Response("CreateAlliance: Malformed alliance name.");
		}
		
		Alliance a = new Alliance(sender, properName, rgb, true);
		a.addNation(sender);
		a.addNation(target);
		
		gameManager.addPendingRelationship(a);
		
		return new Response(String.format("Successfully requested %s to join %s",
				target.getName(), properName));
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.NATION;
	}

	@Override
	public String getName() {
		return "createalliance";
	}

	@Override
	public String getUsage() {
		return "user(ping) alliance_color \"alliance_name\"";
	}

	@Override
	public String getInfo() {
		return "requests to create an alliance between you and the target";
	}

}
