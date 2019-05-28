package strat.game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Scanner;

public class Map {
	private HashMap<Integer, HashMap<Integer, Hexagon>> map;
	private HashMap<Integer, Region> regions;
	private HashMap<Integer, Nation> nations;
	
	private ArrayList<City> cities;
	private ArrayList<Army> armies;
	
	private ArrayList<Battle> battles;
	private ArrayList<Siege> sieges;
	
	private double offsetX;
	private double offsetY;
	private double radius;
	
	private int currentTurn;
	
	private GregorianCalendar currentDate;
	private int daysPerTurn;
	
	private TurnLog turnLog;
	
	public Map(double offsetX, double offsetY, double radius, int currentTurn, GregorianCalendar currentDate, int daysPerTurn) {
		map = new HashMap<>();
		regions = new HashMap<>();
		nations = new HashMap<>();
		
		cities = new ArrayList<>();
		armies = new ArrayList<>();
		
		battles = new ArrayList<>();
		sieges = new ArrayList<>();
		
		regions.put(0, Region.NO_REGION);
		nations.put(0, Nation.NO_NATION);
		
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.radius = radius;
		this.currentTurn = currentTurn;
		
		this.currentDate = currentDate;
		this.daysPerTurn = daysPerTurn;
		
		turnLog = new TurnLog();
	}
	
	public void renderCities(Graphics g) {
		for (HashMap<Integer, Hexagon> ah : map.values()) {
			for (Hexagon h : ah.values()) {
				Region r = regions.get(h.getRegionID());
				h.render(g, r.getColor(), offsetX, offsetY, radius, false);
			}
		}
		
		for (City c : cities) {
			c.render(g);
		}
	}
	
	public void renderRegions(Graphics g) {
		int[] pos = new int[2];
		HashMap<Integer, Integer> sx = new HashMap<>(), sy = new HashMap<>();
		HashMap<Integer, Integer> counts = new HashMap<>();
		
		for (Region r : regions.values()) {
			sx.put(r.getRegionID(), 0);
			sy.put(r.getRegionID(), 0);
			counts.put(r.getRegionID(), 0);
		}
		
		for (HashMap<Integer, Hexagon> ah : map.values()) {
			for (Hexagon h : ah.values()) {
				h.toPixels(offsetX, offsetY, radius, pos);
				Region r = regions.get(h.getRegionID());
				h.render(g, r.getColor(), offsetX, offsetY, radius, false);
				
				sx.put(h.getRegionID(), sx.get(h.getRegionID()) + pos[0]);
				sy.put(h.getRegionID(), sy.get(h.getRegionID()) + pos[1]);
				counts.put(h.getRegionID(), counts.get(h.getRegionID()) + 1);
			}
		}
		
		g.setColor(Color.BLACK);
		
		for (Region r : regions.values()) {
			if (counts.get(r.getRegionID()) == 0) {
				continue;
			}
			
			int px = sx.get(r.getRegionID()) / counts.get(r.getRegionID()) - r.getName().length() * g.getFont().getSize() / 4;
			int py = sy.get(r.getRegionID()) / counts.get(r.getRegionID());
			
			g.drawString(r.getName(), px, py);
		}
	}
	
	public void renderArmyView(Graphics g, Nation n) {
		for (HashMap<Integer, Hexagon> ah : map.values()) {
			for (Hexagon h : ah.values()) {
				Region r = regions.get(h.getRegionID());
				h.render(g, r.getColor(), offsetX, offsetY, radius, true);
			}
		}
		
		for (Army a : armies) {
			if (a.getOwnerID() == n.getNationID()) {
				for (int i = 0, l = a.getPendingMoves().size(); i < l; ++i) {
					Hexagon h = a.getPendingMoves().get(i);
					h.render(g, new Color(0, i * 128 / l + 128, 0), offsetX, offsetY, radius, false);
				}
			}
		}
		
		for (City c : cities) {
			c.render(g, false);
		}
		
		for (Army a : armies) {
			if (a.getOwnerID() == n.getNationID()) {
				a.getHexagon().render(g, new Color(0, 120, 0), offsetX, offsetY, radius, false);
			}
			
			a.render(g);
		}
	}
	
	public void renderPoliticalView(Graphics g) {
		int[] pos = new int[2];
		HashMap<Integer, Integer> sx = new HashMap<>(), sy = new HashMap<>();
		HashMap<Integer, Integer> counts = new HashMap<>();
		
		for (Nation n : nations.values()) {
			sx.put(n.getNationID(), 0);
			sy.put(n.getNationID(), 0);
			counts.put(n.getNationID(), 0);
		}
		
		for (HashMap<Integer, Hexagon> ah : map.values()) {
			for (Hexagon h : ah.values()) {
				h.toPixels(offsetX, offsetY, radius, pos);
				Nation n = getNation(getRegion(h.getRegionID()).getOwnerID());
				h.render(g, n.getColor(), offsetX, offsetY, radius, false);
				
				sx.put(n.getNationID(), sx.get(n.getNationID()) + pos[0]);
				sy.put(n.getNationID(), sy.get(n.getNationID()) + pos[1]);
				counts.put(n.getNationID(), counts.get(n.getNationID()) + 1);
			}
		}
		
		for (City c : cities) {
			c.render(g, false);
		}
		
		g.setColor(Color.BLACK);
		
		for (Nation n : nations.values()) {
			if (counts.get(n.getNationID()) == 0) {
				continue;
			}
			
			int px = sx.get(n.getNationID()) / counts.get(n.getNationID()) - n.getName().length() * g.getFont().getSize() / 4;
			int py = sy.get(n.getNationID()) / counts.get(n.getNationID());
			
			g.drawString(n.getName(), px, py);
		}
	}
	
	public void renderEditorView(Graphics g) {
		for (HashMap<Integer, Hexagon> ah : map.values()) {
			for (Hexagon h : ah.values()) {
				h.render(g, Color.WHITE, offsetX, offsetY, radius, true);
			}
		}
	}
	
	public void writeToFile(String fileName) throws IOException {
		try (PrintWriter o = new PrintWriter(new File(fileName))) {
			o.printf("%f%n%f%n%f%n%d%n", offsetX, offsetY, radius, currentTurn);
			o.printf("%d%n%d%n%d%n%d%n", currentDate.get(GregorianCalendar.YEAR), currentDate.get(GregorianCalendar.MONTH),
					currentDate.get(GregorianCalendar.DATE), daysPerTurn);
			
			for (Nation n : nations.values()) {
				if (n != Nation.NO_NATION) {
					o.println(n.serialize());
				}
			}
			
			for (Region r : regions.values()) {
				if (r != Region.NO_REGION) {
					o.println(r.serialize());
				}
			}
			
			for (City c : cities) {
				o.println(c.serialize());
			}
			
			for (Army a : armies) {
				o.println(a.serialize());
			}
			
			for (HashMap<Integer, Hexagon> ah : map.values()) {
				for (Hexagon h : ah.values()) {
					o.println(h.serialize());
				}
			}
		}
		catch (IOException e) {
			throw e;
		}
	}
	
	public static Map readFromFile(String fileName) throws IOException {
		try (Scanner i = new Scanner(new File(fileName))) {
			double offsetX = i.nextDouble();
			double offsetY = i.nextDouble();
			double radius = i.nextDouble();
			int currentTurn = i.nextInt();
			
			int year = i.nextInt();
			int month = i.nextInt();
			int date = i.nextInt();
			int daysPerTurn = i.nextInt();
			
			i.nextLine();
			
			Map out = new Map(offsetX, offsetY, radius, currentTurn, new GregorianCalendar(year, month, date),
					daysPerTurn);
			
			while (i.hasNextLine()) {
				String line = i.nextLine();
				
				if (line.startsWith("#")) {
					continue;
				}
				
				out.loadSerialized(line);
			}
			
			return out;
		}
		catch (IOException e) {
			throw e;
		}
	}
	
	public void readFromImage(BufferedImage image) {
		final double rootRad = Math.sqrt(3) * 0.5 * radius;
		int[] pos = new int[2];
		
		for (int hexY = 0; hexY < image.getHeight() / rootRad; ++hexY) {
			double y = hexY * rootRad;
			
			for (int hexX = 0; hexX < image.getWidth() / (2 * radius) - 1; ++hexX) {
				double x = (hexY % 2 == 0 ? 0 : 1 * radius) + 2 * radius * hexX;
				
				int c = image.getRGB((int)(x + 0.5), (int)(y + 0.5));
				
				if ((c & 0xFF) > 250) {
					calcHexPosition((int)(x + 0.5), (int)(y + 0.5), pos);
					add(new Hexagon(pos[0], pos[1]));
				}
			}
		}
	}
	
	public void resolveArmyIntersections() {
		ArrayList<Army> armiesToRemove = new ArrayList<>();
		
		for (int i = 0; i < armies.size(); ++i) {
			for (int j = i + 1; j < armies.size(); ++j) {
				Army ai = armies.get(i);
				Army aj = armies.get(j);
				
				if (ai.getHexagon().distanceFrom(aj.getHexagon()) > 2 * Army.MOVES_PER_TURN) {
					continue;
				}
				
				Hexagon sec = ai.findIntersection(aj);
				
				if (sec != null) {
					if (ai.getOwnerID() != aj.getOwnerID() // if two enemy armies intersect where there is no city
							&& getCityAt(sec.getQ(), sec.getR()) == null) {
						if (ai.getPendingMoves().size() == 0) {
							battles.add(new Battle(aj, ai, sec));
						}
						else if (aj.getPendingMoves().size() == 0) {
							battles.add(new Battle(ai, aj, sec));
						}
						else {
							if (Math.random() < 0.5) {
								battles.add(new Battle(ai, aj, sec));
							}
							else {
								battles.add(new Battle(aj, ai, sec));
							}
						}
					}
				}
			}
		}
		
		for (Army a : armiesToRemove) {
			if (!a.isFighting()) {
				armies.remove(a);
			}
		}
	}
	
	public void resolveArmyMovement() {
		for (Army a : armies) {
			if (!a.isFighting() && a.getPendingMoves().size() > 0) {
				Hexagon h = a.getLastMove();
				City c = getCityAt(h.getQ(), h.getR());
				
				if (c != null && c.getOwnerID() != a.getOwnerID()) {
					sieges.add(new Siege(a, c));
				}
				else {
					a.setQ(h.getQ());
					a.setR(h.getR());
				}
			}
		}
	}
	
	public void resolveArmyMerging() {
		ArrayList<Army> armiesToRemove = new ArrayList<>();
		
		for (int i = 0; i < armies.size(); ++i) {
			Army ai = armies.get(i);
			
			for (int j = i + 1; j < armies.size(); ++j) {
				Army aj = armies.get(j);
				
				if (ai.getOwnerID() == aj.getOwnerID() && !ai.isFighting()
						&& !aj.isFighting() && ai.getQ() == aj.getQ() && ai.getR() == aj.getR()) {
					// add ai to aj
					aj.add(ai);
					armiesToRemove.add(ai);
				}
			}
		}
		
		for (Army a : armiesToRemove) {
			armies.remove(a);
		}
	}
	
	public void resolveCombat() {
		for (Battle b : battles) {
			b.resolve();
		}
		
		for (Siege s : sieges) {
			s.resolve();
		}
	}
	
	public void updateMoney() {
		for (City c : cities) {
			Nation o = c.getOwner();
			o.setMoney(o.getMoney() + c.getMarketLevel() * City.MARKET_PROFIT);
		}
	}
	
	public void endTurn() {
		++currentTurn;
		currentDate.add(GregorianCalendar.DATE, daysPerTurn);
		
		resolveArmyIntersections();
		resolveArmyMovement();
		resolveArmyMerging();
		resolveCombat();
		
		battles.clear();
		sieges.clear();
		
		for (Army a : armies) {
			a.resetForTurn();
		}
		
		for (City c : cities) {
			c.resetCapacity();
		}
		
		updateMoney();
	}
	
	public void clearLog() {
		turnLog.clear();
	}
	
	public Hexagon select(int pixelX, int pixelY) {
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
	
	public void addArmy(Army a) {
		armies.add(a);
	}
	
	public void addCity(City c) {
		cities.add(c);
	}
	
	public void addNation(Nation n) {
		nations.put(n.getNationID(), n);
	}
	
	public void removeCity(City c) {
		cities.remove(c);
	}
	
	public void removeArmy(Army a) {
		armies.remove(a);
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
	
	public HashMap<Integer, Region> getRegions() {
		return regions;
	}
	
	public Region getRegion(int regionID) {
		return regions.get(regionID);
	}
	
	public Nation getNation(int nationID) {
		return nations.get(nationID);
	}
	
	public Nation getNationByName(String name) {
		for (Nation n : nations.values()) {
			if (n.getName().equals(name)) {
				return n;
			}
		}
		
		return null;
	}
	
	public Army getArmy(int nationID, int armyID) {
		for (Army a : armies) {
			if (a.getOwnerID() == nationID && a.getArmyID() == armyID) {
				return a;
			}
		}
		
		return null;
	}
	
	public Army getArmyAt(int q, int r) {
		for (Army a : armies) {
			if (a.getQ() == q && a.getR() == r) {
				return a;
			}
		}
		
		return null;
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
	
	public ArrayList<Army> getArmies() {
		return armies;
	}
	
	public Collection<Nation> getNations() {
		return nations.values();
	}
	
	public int getCurrentTurn() {
		return currentTurn;
	}
	
	public String getCurrentDate() {
		return Timer.formatDate(currentDate);
	}
	
	public TurnLog getTurnLog() {
		return turnLog;
	}
	
	private void loadSerialized(String serializedData) {
		if (serializedData.startsWith("Nation")) {
			Nation n = new Nation(serializedData);
			nations.put(n.getNationID(), n);
		}
		else if (serializedData.startsWith("Region")) {
			Region r = new Region(serializedData);
			regions.put(r.getRegionID(), r);
		}
		else if (serializedData.startsWith("City")) {
			cities.add(new City(this, serializedData));
		}
		else if (serializedData.startsWith("Army")) {
			armies.add(new Army(this, serializedData));
		}
		else {
			add(new Hexagon(serializedData));
		}
	}
}
