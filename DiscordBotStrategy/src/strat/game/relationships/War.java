package strat.game.relationships;

import strat.game.Game;
import strat.game.Nation;

public class War extends Relationship {
	
	private int numTurns;
	
	public War(Nation sender) {
		super(sender);
		numTurns = 0;
	}
	
	public War(Game game, String serializedData) {
		super(null);
		
		String[] data = serializedData.split(",");
		
		numTurns = Integer.parseInt(data[1]);
		
		for (int i = 2; i < data.length; ++i) {
			addNation(game.getNation(Integer.parseInt(data[i])));
		}
	}
	
	public void setNumTurns(int numTurns) {
		this.numTurns = numTurns;
	}
	
	public int getNumTurns() {
		return numTurns;
	}
	
	@Override
	public String serialize() {
		return String.format("War,%d,%s", numTurns, toString());
	}

}
