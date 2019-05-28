package strat.commands;

import strat.bot.DiscordBot;

public interface AdminCommand {
	public void execute(DiscordBot bot, String message, String[] tokens);
}
