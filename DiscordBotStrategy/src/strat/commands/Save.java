package strat.commands;

import strat.bot.BotUtils;
import strat.bot.DiscordBot;

public class Save implements AdminCommand {

	@Override
	public void execute(DiscordBot bot, String message, String[] tokens) {
		if (tokens.length > 1) {
			bot.save(tokens[1]);
			BotUtils.sendMessage(DiscordBot.getGameChannel(), "Successfully saved game to " + tokens[1]);
		}
		else {
			bot.save();
			BotUtils.sendMessage(DiscordBot.getGameChannel(), "Successfully saved game");
		}
	}

}
