package strat.game;

import java.util.HashMap;

public class Siege {
	private Army attacker;
	private City defender;
	
	public Siege(Army attacker, City defender) {
		this.attacker = attacker;
		this.defender = defender;
		
		//attacker.setFighting(true);
	}
	
	public void resolve() {
		if (attacker.getOwnerID() == defender.getOwnerID()) {
			return;
		}
		
		String title = String.format(":european_castle: **SIEGE OF %s, %s**%n%n",
				defender.getName().toUpperCase(), attacker.getGame().getCurrentDate().toUpperCase());
		String oldDefender = defender.getOwner().getName();
		int oldDefenderID = defender.getOwnerID();
		
		StringBuilder desc = new StringBuilder();
		
		Army def = new Army(defender.getMap(), defender.getOwnerID());
		HashMap<String, Integer> numUnits = new HashMap<>();
		
		for (String unit : GameRules.getUnitTypes()) {
			def.setUnits(unit, defender.getGarrison().getUnits(unit));
		}
		
		int numArmies = 1;
		
		for (Army a : defender.getGame().getArmies()) {
			if (a.getOwnerID() == defender.getOwnerID() && a.getQ() == defender.getQ()
					&& a.getR() == defender.getR()) {
				def.add(a);
				++numArmies;
			}
		}
		
		for (String unit : GameRules.getUnitTypes()) {
			numUnits.put(unit, def.getUnits(unit));
		}
		
		desc.append(String.format("%s Army %d (%d, %d, %d)%n", attacker.getOwner().getName(), attacker.getArmyNumber(),
				attacker.getUnits("infantry"), attacker.getUnits("cavalry"), attacker.getUnits("artillery")));
		desc.append("vs.\n");
		desc.append(String.format("%s (%d, %d, %d)%n%n", defender.getName(),
				def.getUnits("infantry"), def.getUnits("cavalry"), def.getUnits("artillery")));
		
		Army winner = new Battle(attacker, def, defender.getHexagon()).calcWinner();
		
		HashMap<String, Integer> losses = new HashMap<>();
		HashMap<String, Integer> avgLosses = new HashMap<>();
		
		for (String unit : GameRules.getUnitTypes()) {
			losses.put(unit, numUnits.get(unit) - def.getUnits(unit));
			avgLosses.put(unit, losses.get(unit) / numArmies);
		}
		
		resolveDefenderLosses(oldDefenderID, losses, avgLosses);
		
		if (winner == attacker) {
			defender.getRegion().setOwnerID(attacker.getOwnerID());
			attacker.setQ(defender.getQ());
			attacker.setR(defender.getR());
		}
		else {
			attacker.getGame().removeArmy(attacker);
		}
		
		desc.append(String.format("%s vs %s%n%s is victorious with %d infantry, %d cavalry, and %d artillery remaining.%n",
				attacker.getOwner().getName(), oldDefender,
				winner == attacker ? attacker.getOwner().getName() : oldDefender,
				winner.getUnits("infantry"), winner.getUnits("cavalry"), winner.getUnits("artillery")));
		
		defender.getGame().getTurnLog().addEntry(new LogEntry(winner.getOwner(), title, desc.toString(), LogEntry.Type.BATTLE));
	}
	
	public Army getAttacker() {
		return attacker;
	}
	
	public City getDefender() {
		return defender;
	}
	
	private void resolveDefenderLosses(int oldDefenderID,
			HashMap<String, Integer> losses, HashMap<String, Integer> avgLosses) {
		for (Army a : defender.getGame().getArmies()) {
			if (a.getOwnerID() == oldDefenderID && a.getQ() == defender.getQ()
					&& a.getR() == defender.getR()) {
				for (String unit : GameRules.getUnitTypes()) {
					int loss = Math.min(avgLosses.get(unit), a.getUnits(unit));
					a.setUnits(unit, a.getUnits(unit) - loss);
					losses.put(unit, losses.get(unit) - loss);
				}
			}
		}
		
		for (String unit : GameRules.getUnitTypes()) {
			defender.getGarrison().setUnits(unit, Math.max(defender.getGarrison().getUnits(unit) - losses.get(unit), 0));
		}
	}
}
