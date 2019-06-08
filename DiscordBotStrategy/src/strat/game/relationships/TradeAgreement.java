package strat.game.relationships;

import strat.game.Game;
import strat.game.Nation;

public class TradeAgreement extends Relationship {

	public TradeAgreement(Nation sender, boolean isRequest) {
		super(sender, isRequest);
	}
	
	public TradeAgreement(Game game, String serializedData) {
		super(null, false);
		
		String[] data = serializedData.split(",");
		
		setRequest(Boolean.valueOf(data[1]));
		
		for (int i = 2; i < data.length; ++i) {
			addNation(game.getNation(Integer.parseInt(data[i])));
		}
	}
	
	@Override
	public String serialize() {
		return "TradeAgreement," + toString();
	}

}
