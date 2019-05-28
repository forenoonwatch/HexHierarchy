package strat.game;

import java.awt.Color;

public class Nation implements ISerializable {
	public static final int STARTING_MONEY = 300;
	
	public static final Nation NO_NATION = new Nation(0, "Unclaimed", Color.WHITE);
	
	private int nationID;
	private String name;
	private int rgb;
	private Color color;
	private int money;
	private int spawnedArmies;
	
	public Nation(int nationID, String name, Color color) {
		this.nationID = nationID;
		this.name = name;
		this.color = color;
		rgb = color.getRGB() & 0xFFFFFF;
		money = STARTING_MONEY;
		spawnedArmies = 0;
	}
	
	public Nation(String serializedData) {
		String[] data = serializedData.split(",");
		
		nationID = Integer.parseInt(data[1]);
		name = data[2];
		rgb = Integer.valueOf(data[3], 16).intValue();
		color = new Color(rgb);
		
		money = Integer.parseInt(data[4]);
		spawnedArmies = Integer.parseInt(data[5]);
	}
	
	public void setMoney(int money) {
		this.money = money;
	}
	
	public void setSpawnedArmies(int spawnedArmies) {
		this.spawnedArmies = spawnedArmies;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setRGB(int rgb) {
		this.color = new Color(rgb);
		this.rgb = rgb & 0xFFFFFF;
	}
	
	public int getNationID() {
		return nationID;
	}
	
	public String getName() {
		return name;
	}
	
	public int getRGB() {
		return rgb;
	}
	
	public Color getColor() {
		return color;
	}
	
	public int getMoney() {
		return money;
	}
	
	public int getSpawnedArmies() {
		return spawnedArmies;
	}

	@Override
	public String serialize() {
		return String.format("Nation,%d,%s,%X,%d,%d", nationID, name, rgb & 0xFFFFFF, money, spawnedArmies);
	}
}
