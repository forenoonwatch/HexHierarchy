package strat.game;

import java.awt.Color;

public class Region implements ISerializable {
	public static final Region NO_REGION = new Region(0, "World");
	
	private int regionID;
	private String name;
	private int ownerID;
	private int rgb;
	private Color color;
	
	public Region(int regionID, String name) {
		this(regionID, name, 0, Color.WHITE);
	}
	
	public Region(int regionID, String name, int ownerID, Color color) {
		this.regionID = regionID;
		this.name = name;
		this.ownerID = ownerID;
		this.color = color;
		this.rgb = color.getRGB();
	}
	
	public Region(String serializedData) {
		String[] data = serializedData.split(",");
		
		regionID = Integer.parseInt(data[1]);
		name = data[2];
		ownerID = Integer.parseInt(data[3]);
		rgb = Integer.valueOf(data[4], 16).intValue();
		color = new Color(rgb);
	}
	
	public void setOwnerID(int ownerID) {
		this.ownerID = ownerID;
	}
	
	public int getRegionID() {
		return regionID;
	}
	
	public String getName() {
		return name;
	}
	
	public int getOwnerID() {
		return ownerID;
	}
	
	public Color getColor() {
		return color;
	}
	
	public int getRGB() {
		return rgb;
	}
	
	@Override
	public String serialize() {
		return String.format("Region,%d,%s,%d,%X", regionID, name,
				ownerID, rgb);
	}
}
