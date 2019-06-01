package strat.game;

import java.util.ArrayList;
import java.util.HashMap;

public final class GameRules {
	private static final HashMap<String, Double> RULES = new HashMap<>();
	private static final ArrayList<String> UNIT_TYPES = new ArrayList<>();
	private static final ArrayList<String> BUILDING_TYPES = new ArrayList<>();
	
	private static boolean initialized = false;
	
	public static int getRulei(String rule) {
		return getRule(rule).intValue();
	}
	
	public static double getRuled(String rule) {
		return getRule(rule).doubleValue();
	}
	
	public static ArrayList<String> getBuildingTypes() {
		if (!initialized) {
			init();
		}
		
		return BUILDING_TYPES;
	}
	
	public static ArrayList<String> getUnitTypes() {
		if (!initialized) {
			init();
		}
		
		return UNIT_TYPES;
	}
	
	public static String getBuildingByUnit(String unit) {
		switch (unit) {
			case "cavalry":
				return "stables";
			case "artillery":
				return "foundry";
			default:
				return "barracks";
		}
	}
	
	private static Double getRule(String rule) {
		if (!initialized) {
			init();
		}
		
		return RULES.get(rule);
	}
	
	public static boolean isValidBuilding(String str) {
		for (String building : GameRules.getBuildingTypes()) {
			if (str.equals(building)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isValidUnitType(String str) {
		for (String unit : GameRules.getUnitTypes()) {
			if (str.equals(unit)) {
				return true;
			}
		}
		
		return false;
	}
	
	private static void init() {
		initialized = true;
		
		RULES.put("movesPerTurn", 3.0);
		
		RULES.put("infantryWeight", 10.0);
		RULES.put("cavalryWeight", 4.0);
		RULES.put("artilleryWeight", 1.0);
		RULES.put("attackScale", 3.0);
		RULES.put("armiesPerFortLevel", 2.0);
		
		RULES.put("startingMoney", 300.0);
		RULES.put("marketProfit", 100.0);
		RULES.put("tradeProfit", 100.0);
		
		RULES.put("recruitmentCap", 5.0);
		RULES.put("buildingCap", 5.0);
		
		RULES.put("infantryCost", 5.0);
		RULES.put("cavalryCost", 13.0);
		RULES.put("artilleryCost", 20.0);
		RULES.put("replenishmentCost", 5.0);
		RULES.put("upkeepCost", 4.0);
		
		RULES.put("fortCost", 100.0);
		RULES.put("marketCost", 150.0);
		RULES.put("barracksCost", 300.0);
		RULES.put("stablesCost", 300.0);
		RULES.put("foundryCost", 300.0);
		
		RULES.put("spyScalar", 0.2);
		RULES.put("spyCost", 100.0);
		
		UNIT_TYPES.add("infantry");
		UNIT_TYPES.add("cavalry");
		UNIT_TYPES.add("artillery");
		
		BUILDING_TYPES.add("fort");
		BUILDING_TYPES.add("market");
		BUILDING_TYPES.add("barracks");
		BUILDING_TYPES.add("stables");
		BUILDING_TYPES.add("foundry");
	}
}
