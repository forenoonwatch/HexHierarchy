package strat.game.commands;

import strat.bot.BotUtils;
import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.GameManager;
import strat.game.Nation;
import strat.game.relationships.TradeAgreement;

public class TradeWith implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		if (tokens.length != 2) {
			return new Response(String.format("TradeWith: Invalid number of arguments: %d", tokens.length - 1));
		}
		
		long userID = BotUtils.parseUserID(tokens[1]);
		
		if (userID == 0L || userID == senderID) {
			return new Response("TradeWith: Invalid user.");
		}
		
		Nation sender = gameManager.getNationByUser(senderID);
		Nation target = gameManager.getNationByUser(userID);
		
		if (target == null) {
			return new Response("TradeWith: Invalid user.");
		}
		
		if (gameManager.findPendingTradeBetween(sender, target) != null) {
			return new Response(String.format("TradeWith: Already sent a trade request to %s.", target.getName()));
		}
		
		if (gameManager.getGame().findTradeBetween(sender, target) != null) {
			return new Response(String.format("TradeWith: You are already trading with %s.", target.getName()));
		}
		
		if (gameManager.getGame().findWarBetween(sender, target) != null) {
			return new Response(String.format("Cannot trade with %s as you are at war.", target.getName()));
		}
		
		TradeAgreement ta = new TradeAgreement(sender);
		ta.addNation(sender);
		ta.addNation(target);
		
		gameManager.addPendingRelationship(ta);
		
		return new Response(String.format("Successfully requested to trade with %s", target.getName()));
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.NATION;
	}

	@Override
	public String getName() {
		return "tradewith";
	}

	@Override
	public String getUsage() {
		return "user(ping)";
	}

	@Override
	public String getInfo() {
		return "requests a trade agreement with the user's nation";
	}

}
