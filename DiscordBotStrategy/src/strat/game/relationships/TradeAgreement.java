package strat.game.relationships;

import strat.game.Game;

public class TradeAgreement extends Relationship {

	public TradeAgreement() {}
	
	public TradeAgreement(Game game, String serializedData) {
		String[] data = serializedData.split(",");
		
		for (int i = 1; i < data.length; ++i) {
			addNation(game.getNation(Integer.parseInt(data[i])));
		}
	}
	
	@Override
	public String serialize() {
		return "TradeAgreement," + toString();
	}

}
