package strat.game.commands;

import strat.bot.BotUtils;
import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.GameManager;
import strat.game.Nation;
import strat.game.relationships.PeaceTreaty;

public class RequestPeace implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		if (tokens.length != 2) {
			return new Response("RequestPeace: Invalid number of arguments: " + (tokens.length - 1));
		}
		
		long userID = BotUtils.parseUserID(tokens[1]);
		
		if (userID == 0L || userID == senderID) {
			return new Response("RequestPeace: Invalid user.");
		}
		
		Nation target = gameManager.getNationByUser(userID);
		
		if (target == null) {
			return new Response("RequestPeace: Invalid user.");
		}
		
		Nation sender = gameManager.getNationByUser(senderID);
		
		if (gameManager.getGame().findWarBetween(sender, target) == null) {
			return new Response(String.format("You are not at war with %s.", target.getName()));
		}
		
		if (gameManager.findPendingPeaceBetween(sender, target) != null) {
			return new Response(String.format("Already sent a peace treat request to %s.", target.getName()));
		}
		
		PeaceTreaty p = new PeaceTreaty(sender);
		p.addNation(sender);
		p.addNation(target);
		
		gameManager.addPendingRelationship(p);
		
		return new Response(String.format("Requested peace with %s.", target.getName()));
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.NATION;
	}

	@Override
	public String getName() {
		return "requestpeace";
	}

	@Override
	public String getUsage() {
		return "user(ping)";
	}

	@Override
	public String getInfo() {
		return "requests peace with the given user";
	}

}
