package strat.commands;

import strat.bot.DiscordBot;

public class AdvanceTurn implements AdminCommand {

	@Override
	public void execute(DiscordBot bot, String message, String[] tokens) {
		bot.advanceTurn();
	}

}
