package strat.game;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;

public class City extends MapObject {
	public static final Color CITY_COLOR = new Color(0xA77D59);
	
	/*public static final int MARKET_PROFIT = 100;
	public static final int RECRUITMENT_CAP = 3;
	public static final int BUILDING_CAP = 5;
	
	public static final int INFANTRY_COST = 5;
	public static final int CAVALRY_COST = 13;
	public static final int ARTILLERY_COST = 20;
	
	public static final int FORT_COST = 100;
	public static final int MARKET_COST = 150;
	public static final int FOUNDRY_COST = 300;
	public static final int STABLES_COST = 300;
	public static final int BARRACKS_COST = 300;*/
	
	private int regionID;
	private String name;
	
	private HashMap<String, Integer> buildings;
	private HashMap<String, Integer> recruitCapacity;
	
	private Army garrison;
	
	private City(Map map) {
		super(map);
		
		buildings = new HashMap<>();
		recruitCapacity = new HashMap<>();
		
		garrison = new Army(map, getOwnerID());
	}
	
	public City(Map map, int regionID, String name, int q, int r) {
		this(map);
		setQ(q);
		setR(r);
		
		this.regionID = regionID;
		this.name = name;
		
		for (String building : GameRules.getBuildingTypes()) {
			buildings.put(building, 1);
		}
		
		resetCapacity();
	}
	
	public City(Map map, String serializedData) {
		this(map);
		
		String[] data = serializedData.split(",");
		
		regionID = Integer.parseInt(data[1]);
		
		setQ(Integer.parseInt(data[2]));
		setR(Integer.parseInt(data[3]));
		
		name = data[4];
		
		buildings.put("foundry", Integer.parseInt(data[5]));
		buildings.put("barracks", Integer.parseInt(data[6]));
		buildings.put("stables", Integer.parseInt(data[7]));
		buildings.put("fort", Integer.parseInt(data[8]));
		buildings.put("market", Integer.parseInt(data[9]));
		
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
		for (String unit : GameRules.getUnitTypes()) {
			recruitCapacity.put(unit, GameRules.getRulei(unit + "Weight")
					* GameRules.getRulei("recruitmentCap") * buildings.get(GameRules.getBuildingByUnit(unit)));
		}
	}
	
	public boolean hireUnits(String unitType, int numUnits) {
		Army a = getHiredArmy();
		
		if (a == null) {
			return false;
		}
		
		recruitCapacity.put(unitType, recruitCapacity.get(unitType) - numUnits);
		a.setUnits(unitType, a.getUnits(unitType) + numUnits);
		
		return true;
	}
	
	private Army getHiredArmy() {
		Army out = null;
		
		for (Army a : getMap().getArmies()) {
			if (a.getOwnerID() == getOwnerID() && a.getQ() == getQ() && a.getR() == getR()) {
				out = a;
				break;
			}
		}
		
		if (out == null) {
			Nation owner = getOwner();
			owner.setSpawnedArmies(owner.getSpawnedArmies() + 1);
			out = new Army(getMap(), getOwnerID(),
					owner.getSpawnedArmies(), getQ(), getR());
			
			getMap().addArmy(out);
		}
		
		return out;
	}
	
	public void setBuildingLevel(String building, int level) {
		buildings.put(building, level);
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
	
	public int getBuildingLevel(String building) {
		return buildings.get(building);
	}
	
	public int getUnitCapacity(String unit) {
		return recruitCapacity.get(unit);
	}
	
	public Army getGarrison() {
		garrison.setOwnerID(getOwnerID());
		return garrison;
	}
	
	public int getGarrisonUnits() {
		int sum = 0;
		
		for (String unit : GameRules.getUnitTypes()) {
			sum += garrison.getUnits(unit);
		}
		
		return sum;
	}
	
	public int getGarrisonCapacity(String unit) {
		return getBuildingLevel("fort")
				* GameRules.getRulei("armiesPerFortLevel") * GameRules.getRulei(unit + "Weight");
	}
	
	public int getGarrisonCapacity() {
		int sum = 0;
		
		for (String unit : GameRules.getUnitTypes()) {
			sum += getGarrisonCapacity(unit);
		}
		
		return sum;
	}

	@Override
	public String serialize() {
		return String.format("City,%d,%d,%d,%s,%d,%d,%d,%d,%d", regionID, getQ(), getR(), name,
				buildings.get("foundry"), buildings.get("barracks"), buildings.get("stables"),
				buildings.get("fort"), buildings.get("market"));
	}
}
