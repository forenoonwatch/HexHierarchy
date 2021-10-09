package strat.game;

import java.awt.Graphics;

public abstract class MapObject implements ISerializable {
	private int q;
	private int r;
	
	private Map map;
	
	public MapObject(Map map) {
		this(map, 0, 0);
	}
	
	public MapObject(Map map, int q, int r) {
		this.map = map;
		this.q = q;
		this.r = r;
	}
	
	public abstract void render(Graphics g);
	
	public void setQ(int q) {
		this.q = q;
	}
	
	public void setR(int r) {
		this.r = r;
	}
	
	public Map getMap() {
		return map;
	}
	
	public int getQ() {
		return q;
	}
	
	public int getR() {
		return r;
	}
	
	public Hexagon getHexagon() {
		return getMap().get(getQ(), getR());
	}
	
	public void getPixelPosition(int[] out) {
		getHexagon().toPixels(getMap().getOffsetX(), getMap().getOffsetY(), getMap().getRadius(), out);
	}
}
