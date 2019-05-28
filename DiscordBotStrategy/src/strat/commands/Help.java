package strat.commands;

import strat.game.Map;
import strat.game.Nation;

public class Help implements Command {
	
	@Override
	public String execute(Map map, Nation sender, String[] tokens) {
		return tokens[0];
	}

	@Override
	public String getFormat() {
		return "help";
	}

	@Override
	public String getSynopsis() {
		return "shows a list of commands";
	}

}
