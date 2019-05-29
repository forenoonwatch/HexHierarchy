package strat.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class Map {
	private Game game;
	
	private HashMap<Integer, HashMap<Integer, Hexagon>> map;
	private HashMap<Integer, Region> regions;
	private ArrayList<City> cities;
	
	private double offsetX;
	private double offsetY;
	private double radius;
	
	public Map(Game game) {
		this.game = game;
		
		map = new HashMap<>();
		regions = new HashMap<>();
		cities = new ArrayList<>();
		
		regions.put(0, Region.NO_REGION);
		
		offsetX = 0;
		offsetY = 0;
		radius = 0;
	}
	
	public void setRadiusAutoOffset(double radius) {
		this.radius = radius;
		
		int minX = 0, minY = 0, maxX = 0, maxY = 0;
		int[] pos = new int[2];
		
		for (HashMap<Integer, Hexagon> ah : map.values()) {
			for (Hexagon h : ah.values()) {
				h.toPixels(0, 0, radius, pos);
				
				if (pos[0] < minX) {
					minX = pos[0];
				}
				
				if (pos[0] > maxX) {
					maxX = pos[0];
				}
				
				if (pos[1] < minY) {
					minY = pos[1];
				}
				
				if (pos[1] > maxY) {
					minY = pos[1];
				}
			}
		}
		
		offsetX = (double)(maxX - minX) * 0.5;
		offsetY = (double)(maxY - minY) * 0.5;
	}
	
	public void forEach(Consumer<Hexagon> consumer) {
		for (HashMap<Integer, Hexagon> ah : map.values()) {
			for (Hexagon h : ah.values()) {
				consumer.accept(h);
			}
		}
	}
	
	public Hexagon getHexagonAt(int pixelX, int pixelY) {
		int[] pos = new int[2];
		calcHexPosition(pixelX, pixelY, pos);
		
		return get(pos[0], pos[1]);
	}
	
	public void calcHexPosition(int pixelX, int pixelY, int[] out) {
		double q = -2.0 * (offsetX - pixelX) / (3 * radius);
		double r = -(-(offsetX - pixelX) + Math.sqrt(3) * (offsetY - pixelY)) / (3 * radius);
		double y = -q - r;
		
		double rx = Math.round(q);
		double ry = Math.round(y);
		double rz = Math.round(r);
		
		double xDiff = Math.abs(rx - q);
		double yDiff = Math.abs(ry - y);
		double zDiff = Math.abs(rz - r);
		
		if (xDiff > yDiff && xDiff > zDiff) {
			rx = -ry - rz;
		}
		else if (yDiff > zDiff) {
			ry = -rx - rz;
		}
		else {
			rz = -rx - ry;
		}
		
		out[0] = (int)(rx);
		out[1] = (int)(rz);
	}
	
	public void add(Hexagon h) {
		if (h != null) {
			set(h.getQ(), h.getR(), h);
		}
	}
	
	public void set(int q, int r, Hexagon h) {
		HashMap<Integer, Hexagon> ah = map.get(r);
		
		if (ah == null) {
			ah = new HashMap<>();
			map.put(r, ah);
		}
		
		ah.put(q, h);
	}
	
	public Hexagon get(int q, int r) {
		HashMap<Integer, Hexagon> ah = map.get(r);
		
		if (ah == null) {
			return null;
		}
		
		return ah.get(q);
	}
	
	public Hexagon get(int x, int y, int z) {
		return get(x, z);
	}
	
	public Hexagon remove(int q, int r) {
		HashMap<Integer, Hexagon> ah = map.get(r);
		
		if (ah == null) {
			return null;
		}
		
		return ah.remove(q);
	}
	
	public void setOffsetX(double offsetX) {
		this.offsetX = offsetX;
	}
	
	public void setOffsetY(double offsetY) {
		this.offsetY = offsetY;
	}
	
	public void setRadius(double radius) {
		this.radius = radius;
	}
	
	public void addCity(City c) {
		cities.add(c);
	}
	
	public void addRegion(Region r) {
		regions.put(r.getRegionID(), r);
	}
	
	public void removeCity(City c) {
		cities.remove(c);
	}
	
	public HashMap<Integer, Region> getRegions() {
		return regions;
	}
	
	public Region getRegion(int regionID) {
		return regions.get(regionID);
	}
	
	public City getCityAt(int q, int r) {
		for (City c : cities) {
			if (c.getQ() == q && c.getR() == r) {
				return c;
			}
		}
		
		return null;
	}
	
	public ArrayList<City> getCities() {
		return cities;
	}
	
	public Game getGame() {
		return game;
	}
	
	public double getOffsetX() {
		return offsetX;
	}
	
	public double getOffsetY() {
		return offsetY;
	}
	
	public double getRadius() {
		return radius;
	}
}
