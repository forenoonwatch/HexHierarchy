package strat.commands;

import strat.bot.BotUtils;
import strat.bot.DiscordBot;
import strat.game.Nation;

public class SetNation implements AdminCommand {

	@Override
	public void execute(DiscordBot bot, String message, String[] tokens) {
		if (tokens.length < 2) {
			return;
		}
		
		int natID;
		long userID = 0L;
		
		try {
			natID = Integer.parseInt(tokens[1]);
			
			if (tokens.length >= 3) {
				if (tokens[2].matches("<@\\d+>")) {
					userID = Long.parseLong(tokens[2].substring(2, tokens[2].length() - 1));
				}
				else {
					userID = Long.parseLong(tokens[2]);
				}
			}
		}
		catch (NumberFormatException e) {
			return;
		}
		
		if (userID == 0L) {
			userID = bot.getNationRegistry().getUserForNation(natID);
			
			if (userID != 0L) {
				bot.getNationRegistry().removeUser(userID);
				BotUtils.sendMessage(DiscordBot.getGameChannel(), "Successfully removed user from nation");
			}
		}
		else {
			Nation n = bot.getMap().getNation(natID);
			
			if (n != null) {
				bot.getNationRegistry().addUser(userID, n);
				BotUtils.sendMessage(DiscordBot.getGameChannel(), "Successfully added user to nation " + n.getName());
			}
		}
	}
}
