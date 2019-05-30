package strat.game.commands;

import strat.bot.BotUtils;
import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.GameManager;
import strat.game.Nation;
import strat.game.TurnLog;

public class Pay implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		if (tokens.length < 3) {
			return new Response(String.format("Pay: Incorrect number of arguments: %d\nFormat: %s %s",
					tokens.length - 1, getName(), getUsage()));
		}
		
		long userID = BotUtils.parseUserID(tokens[1]);
		
		if (userID == 0L) {
			return new Response("Pay: Must provide a valid user to pay.");
		}
		
		Nation n = gameManager.getNationByUser(userID);
		Nation sender = gameManager.getNationByUser(senderID);
		
		if (n == null || n == sender) {
			return new Response("Pay: invalid user.");
		}
		
		int cost = 0;
		
		try {
			cost = Integer.parseInt(tokens[2]);
		}
		catch (NumberFormatException e) {
			return new Response("Pay: must provide a valid amount to pay.");
		}
		
		if (cost <= 0) {
			return new Response("Pay: must provide a valid amount to pay");
		}
		else if (cost > sender.getMoney()) {
			return new Response("Pay: amount too high. Check info for your nation's money.");
		}
		
		sender.setMoney(sender.getMoney() - cost);
		n.setMoney(n.getMoney() + cost);
		
		String desc = String.format("Transferred %d:moneybag: to %s%n", cost, n.getName());
		gameManager.getGame().getTurnLog().addEntry(new TurnLog.LogEntry(sender, "**PAYMENT - " + sender.getName().toUpperCase() + "**",
				desc, TurnLog.Type.PAYMENT));
		
		return new Response(String.format("Pay: Successfully transferred %d:moneybag: to %s", cost, n.getName()));
	}
	
	@Override
	public String getName() {
		return "pay";
	}
	
	@Override
	public String getUsage() {
		return "user(ping) amount";
	}

	@Override
	public String getInfo() {
		return "pays the nation associated with the user";
	}
	
	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.NATION;
	}

}
