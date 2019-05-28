package strat.commands;

import java.awt.Color;

import strat.bot.BotUtils;
import strat.bot.DiscordBot;
import strat.bot.ImageManager;
import strat.game.City;
import strat.game.Nation;

public class AddNation implements AdminCommand {

	@Override
	public void execute(DiscordBot bot, String message, String[] tokens) {
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
		
		tokens = message.trim().split("\\s");
		
		if (tokens.length < 2) {
			return;
		}
		
		int rgb;
		
		try {
			rgb = Integer.valueOf(tokens[0], 16);
		}
		catch (NumberFormatException e) {
			return;
		}
		
		City targetCity = null;
		
		for (City c : bot.getMap().getCities()) {
			if (c.getName().toLowerCase().equals(tokens[1])) {
				targetCity = c;
			}
		}
		
		if (targetCity == null) {
			return;
		}
		
		Nation n = new Nation(bot.getMap().getNations().size() + 1, nationName.toString(), new Color(rgb));
		bot.getMap().addNation(n);
		targetCity.getRegion().setOwnerID(n.getNationID());
		
		ImageManager.update();
		
		BotUtils.sendMessage(DiscordBot.getGameChannel(),
				String.format("Successfully created nation %s with capital %s", n.getName(), targetCity.getName()));
	}

}
