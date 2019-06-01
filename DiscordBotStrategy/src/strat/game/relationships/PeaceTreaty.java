package strat.game.relationships;

import strat.game.Game;
import strat.game.Nation;

public class PeaceTreaty extends Relationship {

	public PeaceTreaty(Nation sender) {
		super(sender);
	}
	
	public PeaceTreaty(Game game, String serializedData) {
		super(null);
		
		String[] data = serializedData.split(",");
		
		for (int i = 1; i < data.length; ++i) {
			addNation(game.getNation(Integer.parseInt(data[i])));
		}
	}
	
	@Override
	public String serialize() {
		return "PeaceTreaty," + toString();
	}

}
