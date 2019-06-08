package strat.game;

import java.awt.Color;
import java.awt.Graphics;

public class Hexagon implements ISerializable {
	public static final Hexagon[] DIRECTIONS = {new Hexagon(1, -1, 0), new Hexagon(1, 0, -1),
			new Hexagon(0, 1, -1), new Hexagon(-1, 1, 0), new Hexagon(-1, 0, 1), new Hexagon(0, -1, 1)};
	
	public static final Hexagon[] DIAGONALS = {new Hexagon(2, -1, -1), new Hexagon(1, 1, -2),
			new Hexagon(-1, 2, -1), new Hexagon(-2, 1, 1), new Hexagon(-1, -1, 2), new Hexagon(1, -2, 1)};
	
	private int x;
	private int y;
	private int z;
	
	private int regionID;
	
	private boolean water;
	
	public Hexagon(int q, int r) {
		this(q, -q - r, r);
	}
	
	public Hexagon(int q, int r, boolean water) {
		this(q, -q - r, r, 0, water);
	}
	
	public Hexagon(int x, int y, int z) {
		this(x, y, z, 0, false);
	}
	
	public Hexagon(int x, int y, int z, int regionID, boolean water) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.regionID = regionID;
		
		this.water = water;
	}
	
	public Hexagon(String serializedData) {
		String[] data = serializedData.split(",");
		
		x = Integer.parseInt(data[0]);
		z = Integer.parseInt(data[1]);
		y = -x - z;
		
		regionID = Integer.parseInt(data[2]);
		
		water = Boolean.valueOf(data[3]);
	}
	
	public void render(Graphics g, Color color, double offsetX, double offsetY, double radius, boolean outline) {
		int[] xPts = new int[6];
		int[] yPts = new int[6];
		
		double xPos = offsetX + getQ() * 1.5 * radius;
		double yPos = offsetY + getQ() * 0.5 * Math.sqrt(3) * radius + getR() * Math.sqrt(3) * radius;
		
		for (int i = 0; i < 6; ++i) {
			xPts[i] = (int)(radius * Math.cos(Math.PI * i / 3.0) + xPos + 0.5);
			yPts[i] = (int)(radius * Math.sin(Math.PI * i / 3.0) + yPos + 0.5);
		}
		
		g.setColor(color);
		g.fillPolygon(xPts, yPts, 6);
		
		if (outline) {
			g.setColor(Color.BLACK);
			g.drawPolygon(xPts, yPts, 6);
		}
		//g.drawString(String.format("%d %d", getQ(), getR()), (int)(xPos - 0.5 * radius + 0.5), (int)(yPos + 0.5));
	}
	
	public void render(Graphics g, Color color, double offsetX, double offsetY, double radius) {
		render(g, color, offsetX, offsetY, radius, true);
	}
	
	public Hexagon add(Hexagon hex) {
		return new Hexagon(x + hex.x, y + hex.y, z + hex.z);
	}
	
	public Hexagon mul(int k) {
		return new Hexagon(k * x, k * y, k * z);
	}
	
	public int distanceFromCenter() {
		return Math.max(Math.max(Math.abs(x), Math.abs(y)), Math.abs(z));
	}
	
	public int distanceFrom(Hexagon hex) {
		return Math.max(Math.max(Math.abs(x - hex.x), Math.abs(y - hex.y)), Math.abs(z - hex.z));
	}
	
	public void toPixels(double offsetX, double offsetY, double radius, int[] out) {
		double xPos = offsetX + getQ() * 1.5 * radius;
		double yPos = offsetY + getQ() * 0.5 * Math.sqrt(3) * radius + getR() * Math.sqrt(3) * radius;
		
		out[0] = (int)(xPos + 0.5);
		out[1] = (int)(yPos + 0.5);
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Hexagon)) {
			return false;
		}
		
		Hexagon h = (Hexagon)o;
		return x == h.x && y == h.y && z == h.z;
	}
	
	public void setRegionID(int regionID) {
		this.regionID = regionID;
	}
	
	public void setWater(boolean water) {
		this.water = water;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	public int getQ() {
		return x;
	}
	
	public int getR() {
		return z;
	}
	
	public int getRegionID() {
		return regionID;
	}
	
	public boolean isWater() {
		return water;
	}

	@Override
	public String serialize() {
		return String.format("%d,%d,%d,%s", getQ(), getR(), regionID, water);
	}
}
