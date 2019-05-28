package strat.game;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Army extends MapObject {
	public static final int MOVES_PER_TURN = 3;
	
	private int ownerID;
	private int armyID;
	
	private int numInfantry;
	private int numCavalry;
	private int numArtillery;
	
	private ArrayList<Hexagon> pendingMoves;
	private int remainingMoves;
	
	private boolean fighting;
	
	public Army(Map map, int ownerID) {
		this(map, ownerID, 0, 0, 0);
	}
	
	public Army(Map map, int ownerID, int armyID, int q, int r) {
		super(map, q, r);
		
		this.ownerID = ownerID;
		this.armyID = armyID;
		
		numInfantry = 0;
		numCavalry = 0;
		numArtillery = 0;
		
		pendingMoves = new ArrayList<>();
		remainingMoves = MOVES_PER_TURN;
		
		fighting = false;
	}
	
	public Army(Map map, String serializedData) {
		super(map);
		
		String[] data = serializedData.split(",");
		
		ownerID = Integer.parseInt(data[1]);
		armyID = Integer.parseInt(data[2]);
		
		setQ(Integer.parseInt(data[3]));
		setR(Integer.parseInt(data[4]));
		
		numInfantry = Integer.parseInt(data[5]);
		numCavalry = Integer.parseInt(data[6]);
		numArtillery = Integer.parseInt(data[7]);
		
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
		numInfantry += a.numInfantry;
		numCavalry += a.numCavalry;
		numArtillery += a.numArtillery;
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
			
			City c = getMap().getCityAt(q, r);
			
			if (c != null) {
				if (c.getOwnerID() == ownerID) {
					additionalInfo[0] = String.format("Army %d has stopped outside the allied city of %s.", armyID, c.getName());
					return i;
				}
				else {
					additionalInfo[0] = String.format("Army %d has besieged the city of %s.", armyID, c.getName());
					remainingMoves = 0;
					pendingMoves.add(h);
					return i + 1;
				}
			}
			
			Army a = getMap().getArmyAt(q, r);
			
			if (a != null && a.getOwnerID() == ownerID) {
				remainingMoves = 0;
				a.remainingMoves = 0;
				pendingMoves.add(h);
				
				additionalInfo[0] = String.format("Army %d will merge into army %d.", armyID, a.armyID);
				return i + 1;
			}
			
			--remainingMoves;
			pendingMoves.add(h);
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
	
	public void setUnits(int unitID, int value) {
		switch (unitID) {
			case 0:
				numInfantry = value;
				break;
			case 1:
				numCavalry = value;
				break;
			case 2:
				numArtillery = value;
				break;
		}
	}
	
	public void setInfantry(int numInfantry) {
		this.numInfantry = numInfantry;
	}
	
	public void setCavalry(int numCavalry) {
		this.numCavalry = numCavalry;
	}
	
	public void setArtillery(int numArtillery) {
		this.numArtillery = numArtillery;
	}
	
	public int getInfantry() {
		return numInfantry;
	}
	
	public int getCavalry() {
		return numCavalry;
	}
	
	public int getArtillery() {
		return numArtillery;
	}
	
	public int getOwnerID() {
		return ownerID;
	}
	
	public int getArmyID() {
		return armyID;
	}
	
	public Nation getNation() {
		return getMap().getNation(ownerID);
	}
	
	public int getUnits(int unitID) {
		switch (unitID) {
			case 0:
				return numInfantry;
			case 1:
				return numCavalry;
			case 2:
				return numArtillery;
			default:
				return 0;
		}
	}
	
	public int getRemainingMoves() {
		return remainingMoves;
	}
	
	public boolean isFighting() {
		return fighting;
	}
	
	public boolean isAlive() {
		return numInfantry > 0 || numCavalry > 0 || numArtillery > 0;
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
	
	public Nation getOwner() {
		return getMap().getNation(ownerID);
	}

	@Override
	public String serialize() {
		return String.format("Army,%d,%d,%d,%d,%d,%d,%d", ownerID, armyID,
				getQ(), getR(), numInfantry, numCavalry, numArtillery);
	}
}
