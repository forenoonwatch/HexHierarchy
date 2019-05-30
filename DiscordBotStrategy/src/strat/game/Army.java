package strat.game;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;

public class Army extends MapObject {
	public static final int MOVES_PER_TURN = 3;
	
	private int armyID;
	
	private HashMap<String, Integer> numUnits;
	
	private ArrayList<Hexagon> pendingMoves;
	private int remainingMoves;
	
	private boolean fighting;
	
	public Army(Map map, int ownerID) {
		this(map, ownerID, 0, 0, 0);
	}
	
	public Army(Map map, int ownerID, int armyID, int q, int r) {
		super(map, q, r, ownerID);
		
		this.armyID = armyID;
		
		numUnits = new HashMap<>();
		
		for (String unit : GameRules.getUnitTypes()) {
			numUnits.put(unit, 0);
		}
		
		pendingMoves = new ArrayList<>();
		remainingMoves = MOVES_PER_TURN;
		
		fighting = false;
	}
	
	public Army(Map map, String serializedData) {
		super(map);
		
		String[] data = serializedData.split(",");
		
		setOwnerID(Integer.parseInt(data[1]));
		armyID = Integer.parseInt(data[2]);
		
		setQ(Integer.parseInt(data[3]));
		setR(Integer.parseInt(data[4]));
		
		numUnits = new HashMap<>();
		
		numUnits.put("infantry", Integer.parseInt(data[5]));
		numUnits.put("cavalry", Integer.parseInt(data[6]));
		numUnits.put("artillery", Integer.parseInt(data[7]));
		
		pendingMoves = new ArrayList<>();
		remainingMoves = MOVES_PER_TURN;
		
		fighting = false;
	}

	@Override
	public void render(Graphics g) {
		int[] pos = new int[2];
		getPixelPosition(pos);
		
		g.setColor(getOwner().getColor());
		g.fillOval((int)(pos[0] - 0.5 * getMap().getRadius()),
				(int)(pos[1] - 0.5 * getMap().getRadius()), (int)getMap().getRadius(),
				(int)getMap().getRadius());
		
		g.setColor(Color.BLACK);
		g.drawOval((int)(pos[0] - 0.5 * getMap().getRadius()),
				(int)(pos[1] - 0.5 * getMap().getRadius()), (int)getMap().getRadius(),
				(int)getMap().getRadius());
		
		int offs = -g.getFont().getSize() / 2;
		String str = Integer.toString(armyID);
		
		g.drawString(str, pos[0] - str.length() * g.getFont().getSize() / 4, pos[1] - offs);
	}
	
	public void resetForTurn() {
		fighting = false;
		remainingMoves = MOVES_PER_TURN;
		pendingMoves.clear();
	}
	
	public void add(Army a) {
		for (java.util.Map.Entry<String, Integer> unit : a.numUnits.entrySet()) {
			numUnits.put(unit.getKey(), numUnits.get(unit.getKey()) + unit.getValue());
		}
	}
	
	public int move(int direction, int distance, String[] additionalInfo) {
		Hexagon dir = Hexagon.DIRECTIONS[direction];
		int q = getLastMove().getQ();
		int r = getLastMove().getR();
		
		for (int i = 0; i < distance; ++i) {
			q += dir.getQ();
			r += dir.getR();
			
			Hexagon h = getMap().get(q, r);
			
			if (h == null) {
				additionalInfo[0] = "Army " + armyID + " has reached the map edge.";
				return i;
			}
			
			if (distance == 1 && ( pendingMoves.size() == 1 && h.equals(getHexagon()))
					|| (pendingMoves.size() >= 2 && h.equals(pendingMoves.get(pendingMoves.size() - 2)) ) ) {
				++remainingMoves;
				pendingMoves.remove(pendingMoves.size() - 1);
			}
			else {
				--remainingMoves;
				pendingMoves.add(h);
			}
		}
		
		additionalInfo[0] = String.format("Army %d has finished moving.", armyID);
		return distance;
	}
	
	public Hexagon findIntersection(Army a) {
		if (pendingMoves.size() == 0 && a.pendingMoves.size() == 0) {
			return null;
		}
		
		if (pendingMoves.size() == 0) {
			for (Hexagon h : a.pendingMoves) {
				if (h.equals(getHexagon())) {
					return h;
				}
			}
			
			return null;
		}
		else if (a.pendingMoves.size() == 0) {
			for (Hexagon h : pendingMoves) {
				if (h.equals(a.getHexagon())) {
					return h;
				}
			}
			
			return null;
		}
		
		for (Hexagon h1 : pendingMoves) {
			for (Hexagon h2 : a.pendingMoves) {
				if (h1.equals(h2)) {
					return h1;
				}
			}
		}
		
		return null;
	}
	
	public void setRemainingMoves(int remainingMoves) {
		this.remainingMoves = remainingMoves;
	}
	
	public void setFighting(boolean fighting) {
		this.fighting = fighting;
	}
	
	public void setUnits(String unit, int value) {
		numUnits.put(unit, value);
	}
	
	public void setUnits(int unitID, int value) {
		setUnits(GameRules.getUnitTypes().get(unitID), value);
	}
	
	public int getArmyID() {
		return armyID;
	}
	
	public int getUnits(String unit) {
		return numUnits.get(unit);
	}
	
	public int getUnits(int unitID) {
		return getUnits(GameRules.getUnitTypes().get(unitID));
	}
	
	public int getRemainingMoves() {
		return remainingMoves;
	}
	
	public boolean isFighting() {
		return fighting;
	}
	
	public boolean isAlive() {
		for (Integer i : numUnits.values()) {
			if (i > 0) {
				return true;
			}
		}
		
		return false;
	}
	
	public ArrayList<Hexagon> getPendingMoves() {
		return pendingMoves;
	}
	
	public Hexagon getLastMove() {
		if (pendingMoves.size() == 0) {
			return getHexagon();
		}
		
		return pendingMoves.get(pendingMoves.size() - 1);
	}

	@Override
	public String serialize() {
		return String.format("Army,%d,%d,%d,%d,%d,%d,%d", getOwnerID(), armyID,
				getQ(), getR(), numUnits.get("infantry"), numUnits.get("cavalry"), numUnits.get("artillery"));
	}
}
