package strat.game;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Scanner;

import strat.game.relationships.Alliance;
import strat.game.relationships.PeaceTreaty;
import strat.game.relationships.Relationship;
import strat.game.relationships.TradeAgreement;
import strat.game.relationships.War;
import strat.util.Util;

public class Game {
	private Map map;
	
	private HashMap<Integer, Nation> nations;
	
	private ArrayList<Army> armies;
	private ArrayList<Battle> battles;
	private ArrayList<Siege> sieges;
	
	private ArrayList<Alliance> alliances;
	private ArrayList<TradeAgreement> tradeAgreements;
	private ArrayList<War> wars;
	
	private int currentTurn;
	private int daysPerTurn;
	private GregorianCalendar currentDate;
	
	private TurnLog turnLog;
	
	private double defaultRenderRadius;
	
	public Game() {
		map = new Map(this);
		
		nations = new HashMap<>();
		nations.put(0, Nation.NO_NATION);
		
		armies = new ArrayList<>();
		battles = new ArrayList<>();
		sieges = new ArrayList<>();
		
		alliances = new ArrayList<>();
		tradeAgreements = new ArrayList<>();
		wars = new ArrayList<>();
		
		currentTurn = 0;
		daysPerTurn = 1;
		currentDate = (GregorianCalendar)GregorianCalendar.getInstance();
		
		turnLog = new TurnLog();
		
		defaultRenderRadius = 1.0;
	}
	
	public void load(String fileName) throws IOException {
		try (Scanner i = new Scanner(new File(fileName))) {
			defaultRenderRadius = i.nextDouble();
			currentTurn = i.nextInt();
			
			int year = i.nextInt();
			int month = i.nextInt();
			int date = i.nextInt();
			
			currentDate = new GregorianCalendar(year, month, date);
			daysPerTurn = i.nextInt();
			
			i.nextLine();
			
			while (i.hasNextLine()) {
				String line = i.nextLine();
				
				if (line.startsWith("#")) {
					continue;
				}
				
				loadSerialized(line);
			}
			
			map.setRadiusAutoOffset(defaultRenderRadius);
			
			MovementLog.load(this, GameManager.MOVEMENT_LOG_FILE);
		}
		catch (IOException e) {
			throw e;
		}
	}
	
	public void save(String fileName) throws IOException {
		try (PrintWriter o = new PrintWriter(new File(fileName))) {
			o.printf("%.1f%n%d%n", defaultRenderRadius, currentTurn);
			o.printf("%d%n%d%n%d%n%d%n", currentDate.get(GregorianCalendar.YEAR), currentDate.get(GregorianCalendar.MONTH),
					currentDate.get(GregorianCalendar.DATE), daysPerTurn);
			
			for (Nation n : nations.values()) {
				if (n != Nation.NO_NATION) {
					o.println(n.serialize());
				}
			}
			
			for (Region r : map.getRegions().values()) {
				if (r != Region.NO_REGION) {
					o.println(r.serialize());
				}
			}
			
			for (City c : map.getCities()) {
				o.println(c.serialize());
			}
			
			for (Army a : armies) {
				o.println(a.serialize());
			}
			
			for (Alliance a : alliances) {
				o.println(a.serialize());
			}
			
			for (TradeAgreement t : tradeAgreements) {
				o.println(t.serialize());
			}
			
			for (War w : wars) {
				o.println(w.serialize());
			}
			
			map.forEach(h -> o.println(h.serialize()));
			
			MovementLog.save(this, GameManager.MOVEMENT_LOG_FILE);
		}
		catch (IOException e) {
			throw e;
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
		
		for (City c : map.getCities()) {
			c.resetCapacity();
		}
		
		for (War w : wars) {
			w.setNumTurns(w.getNumTurns() + 1);
		}
		
		updateMoney();
	}
	
	public void clearLog() {
		turnLog.clear();
	}
	
	public Map getMap() {
		return map;
	}
	
	public void addArmy(Army a) {
		armies.add(a);
	}
	
	public boolean removeArmy(Army a) {
		return armies.remove(a);
	}
	
	public void addNation(Nation n) {
		nations.put(n.getNationID(), n);
	}
	
	public Nation getNation(int nationID) {
		return nations.get(nationID);
	}
	
	public Nation getNationByUser(long userID) {
		for (Nation n : nations.values()) {
			if (n.getOwner() == userID) {
				return n;
			}
		}
		
		return null;
	}
	
	public Nation matchNationByName(String name) {
		return Util.findBestMatch(nations.values(), name);
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
	
	public void addAlliance(Alliance a) {
		alliances.add(a);
	}
	
	public boolean removeAlliance(Alliance a) {
		return alliances.remove(a);
	}
	
	public void addTradeAgreement(TradeAgreement t) {
		tradeAgreements.add(t);
	}
	
	public boolean removeTradeAgreement(TradeAgreement t) {
		return tradeAgreements.remove(t);
	}
	
	public void addWar(War w) {
		wars.add(w);
	}
	
	public boolean removeWar(War w) {
		return wars.remove(w);
	}
	
	public Alliance getAllianceForNation(Nation n) {
		for (Alliance a : alliances) {
			if (a.hasNation(n)) {
				return a;
			}
		}
		
		return null;
	}
	
	public Relationship findSimilarRelationship(Relationship r) {
		if (r instanceof Alliance) {
			Alliance a = (Alliance)r;
			return findSimilarAlliance(a.getName(), a.getRGB());
		}
		
		return null;
	}
	
	public Alliance findSimilarAlliance(String name, int rgb) {
		for (Alliance a : alliances) {
			if (a.getName().equals(name) && a.getRGB() == rgb) {
				return a;
			}
		}
		
		return null;
	}
	
	public Alliance findAllianceBetween(Nation na, Nation nb) {
		for (Alliance a : alliances) {
			if (a.hasNation(na) && a.hasNation(nb)) {
				return a;
			}
		}
		
		return null;
	}
	
	public TradeAgreement findTradeBetween(Nation a, Nation b) {
		for (TradeAgreement t : tradeAgreements) {
			if (t.hasNation(a) && t.hasNation(b)) {
				return t;
			}
		}
		
		return null;
	}
	
	public War findWarBetween(Nation a, Nation b) {
		for (War w : wars) {
			if (w.hasNation(a) && w.hasNation(b)) {
				return w;
			}
		}
		
		return null;
	}
	
	public void addRelationship(Relationship r) {
		if (r instanceof Alliance) {
			addAlliance((Alliance)r);
		}
		else if (r instanceof TradeAgreement) {
			addTradeAgreement((TradeAgreement)r);
		}
		else if (r instanceof PeaceTreaty) {
			ArrayList<Nation> ns = new ArrayList<>();
			r.getNations().forEach(n -> ns.add(n));
			War w = findWarBetween(ns.get(0), ns.get(1));
			
			if (w != null) {
				wars.remove(w);
			}
		}
	}
	
	public void removeRelationship(Relationship r) {
		if (r instanceof Alliance) {
			removeAlliance((Alliance)r);
		}
		else if (r instanceof TradeAgreement) {
			removeTradeAgreement((TradeAgreement)r);
		}
		else if (r instanceof War) {
			removeWar((War)r);
		}
	}
	
	public int calcGrossProfitForNation(Nation n) {
		int profit = 0;
		
		for (City c : map.getCities()) {
			if (c.getOwnerID() == n.getNationID()) {
				profit += c.getBuildingLevel("market") * GameRules.getRulei("marketProfit");
			}
		}
		
		for (TradeAgreement t : tradeAgreements) {
			if (t.hasNation(n)) {
				profit += GameRules.getRulei("tradeProfit");
			}
		}
		
		return profit;
	}
	
	public ArrayList<Alliance> getAlliances() {
		return alliances;
	}
	
	public ArrayList<TradeAgreement> getTradeAgreements() {
		return tradeAgreements;
	}
	
	public ArrayList<War> getWars() {
		return wars;
	}
	
	public HashMap<Integer, Nation> getNations() {
		return nations;
	}
	
	public ArrayList<Army> getArmies() {
		return armies;
	}
	
	public int getCurrentTurn() {
		return currentTurn;
	}
	
	public int getDaysPerTurn() {
		return daysPerTurn;
	}
	
	public String getCurrentDate() {
		return Timer.formatDate(currentDate);
	}
	
	public TurnLog getTurnLog() {
		return turnLog;
	}
	
	public double getDefaultRenderRadius() {
		return defaultRenderRadius;
	}
	
	private boolean canFight(Army a, Army b) {
		return a.getOwnerID() != b.getOwnerID() && findWarBetween(a.getOwner(), b.getOwner()) != null;
	}
	
	private boolean canBesiege(Army a, City c) {
		return a.getOwnerID() != c.getOwnerID() && findWarBetween(a.getOwner(), c.getOwner()) != null;
	}
	
	private void resolveArmyIntersections() {
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
					if (canFight(ai, aj) // if two enemy armies intersect where there is no city
							&& map.getCityAt(sec.getQ(), sec.getR()) == null) {
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
	
	private void resolveArmyMovement() {
		for (Army a : armies) {
			if (!a.isFighting() && a.getPendingMoves().size() > 0) {
				Hexagon h = a.getLastMove();
				City c = map.getCityAt(h.getQ(), h.getR());
				
				if (c != null && canBesiege(a, c)) {
					sieges.add(new Siege(a, c));
				}
				else {
					a.setQ(h.getQ());
					a.setR(h.getR());
				}
			}
		}
	}
	
	private void resolveArmyMerging() {
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
					
					for (String unit : GameRules.getUnitTypes()) {
						ai.setUnits(unit, 0);
					}
				}
			}
		}
		
		for (Army a : armiesToRemove) {
			armies.remove(a);
		}
	}
	
	private void resolveCombat() {
		for (Battle b : battles) {
			b.resolve();
		}
		
		for (Siege s : sieges) {
			s.resolve();
		}
	}
	
	private void updateMoney() {
		for (City c : map.getCities()) {
			Nation o = c.getOwner();
			o.setMoney(o.getMoney() + c.getBuildingLevel("market") * GameRules.getRulei("marketProfit"));
		}
		
		for (TradeAgreement t : tradeAgreements) {
			for (Nation n : t.getNations()) {
				n.setMoney(n.getMoney() + GameRules.getRulei("tradeProfit"));
			}
		}
	}
	
	private void loadSerialized(String serializedData) {
		if (serializedData.startsWith("Nation")) {
			Nation n = new Nation(serializedData);
			nations.put(n.getNationID(), n);
		}
		else if (serializedData.startsWith("Region")) {
			map.addRegion(new Region(serializedData));
		}
		else if (serializedData.startsWith("City")) {
			map.addCity(new City(map, serializedData));
		}
		else if (serializedData.startsWith("Army")) {
			armies.add(new Army(map, serializedData));
		}
		else if (serializedData.startsWith("Alliance")) {
			alliances.add(new Alliance(this, serializedData));
		}
		else if (serializedData.startsWith("TradeAgreement")) {
			tradeAgreements.add(new TradeAgreement(this, serializedData));
		}
		else if (serializedData.startsWith("War")) {
			wars.add(new War(this, serializedData));
		}
		else {
			map.add(new Hexagon(serializedData));
		}
	}
}
