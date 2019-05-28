package strat.commands;

import strat.bot.BotUtils;
import strat.bot.DiscordBot;
import strat.bot.ImageManager;
import strat.game.Nation;

public class ChangeColor implements AdminCommand {

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
		
		int color;
		
		try {
			color = Integer.valueOf(tokens[2], 16);
		}
		catch (NumberFormatException e) {
			return;
		}
		
		BotUtils.sendMessage(DiscordBot.getGameChannel(), String.format("Set color of \"%s\" to %X",
				n.getName(), color));
		
		n.setRGB(color);
		ImageManager.update();
	}

}
