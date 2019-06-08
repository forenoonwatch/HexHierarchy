package strat.game.relationships;

import strat.game.Game;
import strat.game.Nation;

public class Alliance extends Relationship {
	private String name;
	private int rgb;
	
	public Alliance(Nation sender, String name, int rgb, boolean isRequest) {
		super(sender, isRequest);
		this.name = name;
		this.rgb = rgb;
	}
	
	public Alliance(Game game, String serializedData) {
		super(null, false);
		
		String[] data = serializedData.split(",");
		
		name = data[1];
		rgb = Integer.valueOf(data[2], 16);
		
		setRequest(Boolean.valueOf(data[3]));
		
		for (int i = 4; i < data.length; ++i) {
			addNation(game.getNation(Integer.parseInt(data[i])));
		}
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setRGB(int rgb) {
		this.rgb = rgb;
	}
	
	public String getName() {
		return name;
	}
	
	public int getRGB() {
		return rgb;
	}
	
	@Override
	public String serialize() {
		return String.format("Alliance,%s,%X,%s", name, rgb & 0xFFFFFF, toString());
	}
}
