package strat.commands;

import strat.bot.DiscordBot;
import strat.game.Map;
import strat.game.Nation;

public class Complete implements Command {

	@Override
	public String execute(Map map, Nation sender, String[] tokens) {
		if (DiscordBot.setTurnCompleted(sender)) {
			if (DiscordBot.isTurnComplete()) {
				DiscordBot.getInstance().advanceTurn();
			}
			
			return sender.getName() + " has completed their turns.";
		}
		
		return "You have already marked yourself as complete!";
	}

	@Override
	public String getFormat() {
		return "complete";
	}

	@Override
	public String getSynopsis() {
		return "marks you as having completed the turn. If everyone is complete, it advances the turn.";
	}

}
