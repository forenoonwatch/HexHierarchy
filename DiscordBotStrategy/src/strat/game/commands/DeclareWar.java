package strat.game.commands;

import strat.bot.BotUtils;
import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.GameManager;
import strat.game.LogEntry;
import strat.game.Nation;
import strat.game.relationships.War;

public class DeclareWar implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		if (tokens.length != 2) {
			return new Response("War: Invalid number of arguments: " + (tokens.length - 1));
		}
		
		long userID = BotUtils.parseUserID(tokens[1]);
		
		if (userID == 0L || userID == senderID) {
			return new Response("War: Invalid user.");
		}
		
		Nation target = gameManager.getNationByUser(userID);
		
		if (target == null) {
			return new Response("War: Invalid user.");
		}
		
		Nation sender = gameManager.getNationByUser(senderID);
		
		if (gameManager.getGame().findAllianceBetween(sender, target) != null) {
			return new Response(String.format("You must break your alliance with %s before declaring war.", target.getName()));
		}
		
		if (gameManager.getGame().findTradeBetween(sender, target) != null) {
			return new Response(String.format("You must break your trade agreement with %s before declaring war.", target.getName()));
		}
		
		if (gameManager.getGame().findWarBetween(sender, target) != null) {
			return new Response(String.format("You are already at war with %s.", target.getName()));
		}
		
		War w = new War(sender);
		w.addNation(sender);
		w.addNation(target);
		
		gameManager.getGame().addWar(w);
		
		gameManager.logDiplomacy(new LogEntry(sender, ":crossed_swords: **WAR DECLARED**",
				String.format("%s has declared war on %s", sender.getName(), target.getName()), LogEntry.Type.WAR));
		
		return new Response(String.format("You have declared war on %s!", target.getName()));
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.NATION;
	}

	@Override
	public String getName() {
		return "war";
	}

	@Override
	public String getUsage() {
		return "user(ping)";
	}

	@Override
	public String getInfo() {
		return "declares war on the nation of the given user";
	}

}
