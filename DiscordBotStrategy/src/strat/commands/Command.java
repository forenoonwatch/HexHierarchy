package strat.commands;

import strat.game.Map;
import strat.game.Nation;

public interface Command {
	public String execute(Map map, Nation sender, String[] tokens);
	public String getFormat();
	public String getSynopsis();
}
