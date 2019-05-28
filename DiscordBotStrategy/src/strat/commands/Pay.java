package strat.commands;

import strat.bot.DiscordBot;
import strat.game.Map;
import strat.game.Nation;

public class Pay implements Command {

	@Override
	public String execute(Map map, Nation sender, String[] tokens) {
		if (tokens.length < 3) {
			return "Pay: Incorrect number of arguments.\nFormat: " + getFormat();
		}
		
		long userID = 0L;
		
		if (tokens[1].matches("<@\\d+>")) {
			try {
				userID = Long.parseLong(tokens[1].substring(2, tokens[1].length() - 1));
			}
			catch (NumberFormatException e) {
				userID = 0L;
			}
		}
		else {
			try {
				userID = Long.parseLong(tokens[1]);
			}
			catch (NumberFormatException e) {
				userID = 0L;
			}
		}
		
		if (userID == 0L) {
			return "Pay: Must provide a valid user to pay.";
		}
		
		Nation n = DiscordBot.getNationByUser(userID);
		
		if (n == null || n == sender) {
			return "Pay: invalid user.";
		}
		
		int cost = 0;
		
		try {
			cost = Integer.parseInt(tokens[2]);
		}
		catch (NumberFormatException e) {
			return "Pay: must provide a valid amount to pay.";
		}
		
		if (cost <= 0) {
			return "Pay: must provide a valid amount to pay";
		}
		else if (cost > sender.getMoney()) {
			return "Pay: amount too high. Check info for your nation's money.";
		}
		
		sender.setMoney(sender.getMoney() - cost);
		n.setMoney(n.getMoney() + cost);
		
		return String.format("Pay: Successfully transferred %d to %s", cost, n.getName());
	}

	@Override
	public String getFormat() {
		return "pay user(ping) amount";
	}

	@Override
	public String getSynopsis() {
		return "pays the nation associated with the user";
	}

}
