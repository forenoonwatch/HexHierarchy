package strat.commands;

import strat.bot.BotUtils;
import strat.bot.DiscordBot;
import strat.bot.ImageManager;
import strat.game.Nation;

public class ChangeName implements AdminCommand {

	@Override
	public void execute(DiscordBot bot, String message, String[] tokens) {
		if (tokens.length < 3) {
			return;
		}
		
		long userID = 0L;
		
		try {
			if (tokens[1].matches("<@\\d+>")) {
				userID = Long.parseLong(tokens[1].substring(2, tokens[1].length() - 1));
			}
			else {
				userID = Long.parseLong(tokens[1]);
			}
		}
		catch (NumberFormatException e) {
			return;
		}
		
		Nation n = DiscordBot.getNationByUser(userID);
		
		if (n == null) {
			return;
		}
		
		StringBuilder nationName = new StringBuilder();
		
		for (int i = 0; i < message.length(); ++i) {
			if (message.charAt(i) == '"') {
				do {
					++i;
					
					if (message.charAt(i) != '"') {
						nationName.append(message.charAt(i));
					}
				}
				while (i < message.length() && message.charAt(i) != '"');
				
				message = message.substring(i + 1).toLowerCase();
				
				break;
			}
		}
		
		BotUtils.sendMessage(DiscordBot.getGameChannel(), String.format("Set name of \"%s\" to \"%s\"",
				n.getName(), nationName.toString()));
		
		n.setName(nationName.toString());
		ImageManager.update();
	}

}
