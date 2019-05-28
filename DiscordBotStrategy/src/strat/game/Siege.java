package strat.game;

public class Siege {
	public static final int ARMIES_PER_FORT_LEVEL = 2;
	
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
		int numInfantry = defender.getFortLevel() * ARMIES_PER_FORT_LEVEL * Battle.INFANTRY_WEIGHT;
		int numCavalry = defender.getFortLevel() * ARMIES_PER_FORT_LEVEL * Battle.CAVALRY_WEIGHT;
		int numArtillery = defender.getFortLevel() * ARMIES_PER_FORT_LEVEL * Battle.ARTILLERY_WEIGHT;
		
		def.setInfantry(numInfantry);
		def.setCavalry(numCavalry);
		def.setArtillery(numArtillery);
		
		int numArmies = 1;
		
		for (Army a : defender.getMap().getArmies()) {
			if (a.getOwnerID() == defender.getOwnerID() && a.getQ() == defender.getQ()
					&& a.getR() == defender.getR()) {
				def.add(a);
				++numArmies;
			}
		}
		
		Army winner = new Battle(attacker, def, defender.getHexagon()).calcWinner();
		
		int infLosses = numInfantry - def.getInfantry();
		int cavLosses = numCavalry - def.getCavalry();
		int artyLosses = numArtillery - def.getArtillery();
		
		int avgInfLoss = infLosses / numArmies;
		int avgCavLoss = cavLosses / numArmies;
		int avgArtyLoss = artyLosses / numArmies;
		
		for (int i = 0; i < Hexagon.DIRECTIONS.length; ++i) {
			Army a = defender.getMap().getArmyAt(Hexagon.DIRECTIONS[i].getQ(),
					Hexagon.DIRECTIONS[i].getR());
			
			if (a != null && a.getOwnerID() == defender.getOwnerID()) {
				int il = Math.min(avgInfLoss, a.getInfantry());
				int cl = Math.min(avgCavLoss, a.getCavalry());
				int al = Math.min(avgArtyLoss, a.getArtillery());
				
				a.setInfantry(a.getInfantry() - il);
				a.setCavalry(a.getCavalry() - cl);
				a.setArtillery(a.getArtillery() - al);
				
				infLosses -= il;
				cavLosses -= cl;
				artyLosses -= al;
			}
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
				winner.getOwner().getName(), winner.getInfantry(), winner.getCavalry(), winner.getArtillery());
		
		defender.getMap().getTurnLog().addEntry(new TurnLog.LogEntry(winner.getOwner(), title, desc, TurnLog.Type.BATTLE));
	}
	
	public Army getAttacker() {
		return attacker;
	}
	
	public City getDefender() {
		return defender;
	}
}
