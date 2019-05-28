package strat.game;

import java.awt.Color;
import java.awt.Graphics;

public class City extends MapObject {
	public static final Color CITY_COLOR = new Color(0xA77D59);
	
	public static final int MARKET_PROFIT = 100;
	public static final int RECRUITMENT_CAP = 3;
	public static final int BUILDING_CAP = 5;
	
	public static final int INFANTRY_COST = 5;
	public static final int CAVALRY_COST = 13;
	public static final int ARTILLERY_COST = 20;
	
	public static final int FORT_COST = 100;
	public static final int MARKET_COST = 150;
	public static final int FOUNDRY_COST = 300;
	public static final int STABLES_COST = 300;
	public static final int BARRACKS_COST = 300;
	
	private int regionID;
	private String name;
	
	private int foundryLevel;
	private int barracksLevel;
	private int stablesLevel;
	private int fortLevel;
	private int marketLevel;
	
	private int infantryCapacity;
	private int cavalryCapacity;
	private int artilleryCapacity;
	
	public City(Map map, int regionID, String name, int q, int r) {
		super(map, q, r);
		
		this.regionID = regionID;
		this.name = name;
		
		foundryLevel = 1;
		barracksLevel = 1;
		stablesLevel = 1;
		fortLevel = 1;
		marketLevel = 1;
		
		resetCapacity();
	}
	
	public City(Map map, String serializedData) {
		super(map);
		
		String[] data = serializedData.split(",");
		
		regionID = Integer.parseInt(data[1]);
		
		setQ(Integer.parseInt(data[2]));
		setR(Integer.parseInt(data[3]));
		
		name = data[4];
		
		foundryLevel = Integer.parseInt(data[5]);
		barracksLevel = Integer.parseInt(data[6]);
		stablesLevel = Integer.parseInt(data[7]);
		fortLevel = Integer.parseInt(data[8]);
		marketLevel = Integer.parseInt(data[9]);
		
		resetCapacity();
	}

	@Override
	public void render(Graphics g) {
		render(g, true);
	}
	
	public void render(Graphics g, boolean showText) {
		int[] pos = new int[2];
		getPixelPosition(pos);
		
		g.setColor(CITY_COLOR);
		g.fillRect((int)(pos[0] - 0.5 * getMap().getRadius()),
				(int)(pos[1] - 0.5 * getMap().getRadius()), (int)getMap().getRadius(),
				(int)getMap().getRadius());
		
		g.setColor(Color.BLACK);
		g.drawRect((int)(pos[0] - 0.5 * getMap().getRadius()),
				(int)(pos[1] - 0.5 * getMap().getRadius()), (int)getMap().getRadius(),
				(int)getMap().getRadius());
		
		if (showText) {
			g.setColor(Color.BLACK);
			
			int offs = -g.getFont().getSize() / 2;
			
			if (name.equals("Regensburg")) {
				offs = -3 * offs;
			}
			
			g.drawString(name, pos[0] - name.length() * g.getFont().getSize() / 4, pos[1] + offs);
		}
	}
	
	public void resetCapacity() {
		infantryCapacity = Battle.INFANTRY_WEIGHT * City.RECRUITMENT_CAP * barracksLevel;
		cavalryCapacity = Battle.CAVALRY_WEIGHT * City.RECRUITMENT_CAP * stablesLevel;
		artilleryCapacity = Battle.ARTILLERY_WEIGHT * City.RECRUITMENT_CAP * foundryLevel;
	}
	
	public boolean hireInfantry(int numInfantry) {
		Army a = getHiredArmy();
		
		if (a == null) {
			return false;
		}
		
		infantryCapacity -= numInfantry;
		a.setInfantry(a.getInfantry() + numInfantry);
		
		return true;
	}
	
	public boolean hireCavalry(int numCavalry) {
		Army a = getHiredArmy();
		
		if (a == null) {
			return false;
		}
		
		cavalryCapacity -= numCavalry;
		a.setCavalry(a.getCavalry() + numCavalry);
		
		return true;
	}
	
	public boolean hireArtillery(int numArtillery) {
		Army a = getHiredArmy();
		
		if (a == null) {
			return false;
		}
		
		artilleryCapacity -= numArtillery;
		a.setArtillery(a.getArtillery() + numArtillery);
		
		return true;
	}
	
	private Army getHiredArmy() {
		Hexagon h = getNextSpawnPosition();
		
		if (h == null) {
			return null;
		}
		
		Army a = getMap().getArmyAt(h.getQ(), h.getR());
		
		if (a == null) {
			Nation owner = getOwner();
			owner.setSpawnedArmies(owner.getSpawnedArmies() + 1);
			a = new Army(getMap(), getOwnerID(),
					owner.getSpawnedArmies(), h.getQ(), h.getR());
			
			getMap().addArmy(a);
		}
		
		return a;
	}
	
	public void setFortLevel(int fortLevel) {
		this.fortLevel = fortLevel;
	}
	
	public void setMarketLevel(int marketLevel) {
		this.marketLevel = marketLevel;
	}
	
	public void setBarracksLevel(int barracksLevel) {
		this.barracksLevel = barracksLevel;
	}
	
	public void setStablesLevel(int stablesLevel) {
		this.stablesLevel = stablesLevel;
	}
	
	public void setFoundryLevel(int foundryLevel) {
		this.foundryLevel = foundryLevel;
	}
	
	public int getRegionID() {
		return regionID;
	}
	
	public Region getRegion() {
		return getMap().getRegion(regionID);
	}
	
	public int getOwnerID() {
		return getRegion().getOwnerID();
	}
	
	public Nation getOwner() {
		return getMap().getNation(getOwnerID());
	}
	
	public String getName() {
		return name;
	}
	
	public int getFortLevel() {
		return fortLevel;
	}
	
	public int getMarketLevel() {
		return marketLevel;
	}
	
	public int getBarracksLevel() {
		return barracksLevel;
	}
	
	public int getStablesLevel() {
		return stablesLevel;
	}
	
	public int getFoundryLevel() {
		return foundryLevel;
	}
	
	public int getInfantryCapacity() {
		return infantryCapacity;
	}
	
	public int getCavalryCapacity() {
		return cavalryCapacity;
	}
	
	public int getArtilleryCapacity() {
		return artilleryCapacity;
	}

	@Override
	public String serialize() {
		return String.format("City,%d,%d,%d,%s,%d,%d,%d,%d,%d", regionID, getQ(), getR(), name,
				foundryLevel, barracksLevel, stablesLevel, fortLevel, marketLevel);
	}
	
	private Hexagon getNextSpawnPosition() {
		for (int i = 0; i < Hexagon.DIRECTIONS.length; ++i) {
			Hexagon h = getMap().get(getQ() + Hexagon.DIRECTIONS[i].getQ(),
					getR() + Hexagon.DIRECTIONS[i].getR());
			
			if (h != null) {
				return h;
			}
		}
		
		return null;
	}
}
