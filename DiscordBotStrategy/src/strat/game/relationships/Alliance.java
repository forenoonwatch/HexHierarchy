package strat.game.relationships;

import strat.game.Game;

public class Alliance extends Relationship {
	private String name;
	private int rgb;
	
	public Alliance(String name, int rgb) {
		this.name = name;
		this.rgb = rgb;
	}
	
	public Alliance(Game game, String serializedData) {
		String[] data = serializedData.split(",");
		
		name = data[1];
		rgb = Integer.valueOf(data[2], 16);
		
		for (int i = 3; i < data.length; ++i) {
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
		return String.format("Alliance,%s,%s,%X", name, rgb & 0xFFFFFF, toString());
	}
}
