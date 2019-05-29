package strat.game;

import java.util.HashMap;

public class Siege {
	//public static final int ARMIES_PER_FORT_LEVEL = 2;
	
	private Army attacker;
	private City defender;
	
	public Siege(Army attacker, City defender) {
		this.attacker = attacker;
		this.defender = defender;
		
		attacker.setFighting(true);
	}
	
	public void resolve() {
		String title = String.format("**SIEGE OF %s**%n%n", defender.getName().toUpperCase());
		
		Army def = new Army(defender.getMap(), defender.getOwnerID());
		HashMap<String, Integer> numUnits = new HashMap<>();
		
		for (String unit : GameRules.getUnitTypes()) {
			numUnits.put(unit, defender.getGarrison().getUnits(unit));
			def.setUnits(unit, numUnits.get(unit));
		}
		
		int numArmies = 1;
		
		for (Army a : defender.getMap().getArmies()) {
			if (a.getOwnerID() == defender.getOwnerID() && a.getQ() == defender.getQ()
					&& a.getR() == defender.getR()) {
				def.add(a);
				++numArmies;
			}
		}
		
		Army winner = new Battle(attacker, def, defender.getHexagon()).calcWinner();
		
		HashMap<String, Integer> losses = new HashMap<>();
		HashMap<String, Integer> avgLosses = new HashMap<>();
		
		for (String unit : GameRules.getUnitTypes()) {
			losses.put(unit, numUnits.get(unit) - def.getUnits(unit));
			avgLosses.put(unit, losses.get(unit) / numArmies);
		}
		
		for (Army a : defender.getMap().getArmies()) {
			if (a.getOwnerID() == defender.getOwnerID() && a.getQ() == defender.getQ()
					&& a.getR() == defender.getR()) {
				for (String unit : GameRules.getUnitTypes()) {
					int loss = Math.min(avgLosses.get(unit), a.getUnits(unit));
					a.setUnits(unit, a.getUnits(unit) - loss);
					losses.put(unit, losses.get(unit) - loss);
				}
			}
		}
		
		for (String unit : GameRules.getUnitTypes()) {
			defender.getGarrison().setUnits(unit, losses.get(unit));
		}
		
		if (winner == attacker) {
			defender.getRegion().setOwnerID(attacker.getOwnerID());
			
			if (attacker.getPendingMoves().size() >= 2) {
				Hexagon h = attacker.getPendingMoves().get(attacker.getPendingMoves().size() - 2);
				attacker.setQ(h.getQ());
				attacker.setR(h.getR());
			}
		}
		else {
			attacker.getMap().removeArmy(attacker);
		}
		
		String desc = String.format("%s vs %s%n%s is victorious with %d infantry, %d cavalry, and %d artillery remaining.%n",
				attacker.getOwner().getName(), defender.getOwner().getName(),
				winner.getOwner().getName(), winner.getUnits("infantry"), winner.getUnits("cavalry"),
				winner.getUnits("artillery"));
		
		defender.getMap().getTurnLog().addEntry(new TurnLog.LogEntry(winner.getOwner(), title, desc, TurnLog.Type.BATTLE));
	}
	
	public Army getAttacker() {
		return attacker;
	}
	
	public City getDefender() {
		return defender;
	}
}
