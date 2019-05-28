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
	
	public String resolve() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("**SIEGE OF %s**%n%n", defender.getName().toUpperCase()));
		
		Army def = new Army(defender.getMap(), defender.getOwnerID());
		int numInfantry = defender.getFortLevel() * ARMIES_PER_FORT_LEVEL * Battle.INFANTRY_WEIGHT;
		int numCavalry = defender.getFortLevel() * ARMIES_PER_FORT_LEVEL * Battle.CAVALRY_WEIGHT;
		int numArtillery = defender.getFortLevel() * ARMIES_PER_FORT_LEVEL * Battle.ARTILLERY_WEIGHT;
		
		def.setInfantry(numInfantry);
		def.setCavalry(numCavalry);
		def.setArtillery(numArtillery);
		
		int numArmies = 1;
		
		for (int i = 0; i < Hexagon.DIRECTIONS.length; ++i) {
			Army a = defender.getMap().getArmyAt(Hexagon.DIRECTIONS[i].getQ(),
					Hexagon.DIRECTIONS[i].getR());
			
			if (a != null && a.getOwnerID() == defender.getOwnerID()) {
				def.add(a);
				++numArmies;
			}
		}
		
		Army winner = new Battle(attacker, def, defender.getHexagon()).calcWinner(sb);
		
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
		
		sb.append(String.format("%s is victorious with %d infantry, %d cavalry, and %d artillery remaining.%n",
				winner.getNation().getName(), winner.getInfantry(), winner.getCavalry(), winner.getArtillery()));
		
		return sb.toString();
	}
	
	public Army getAttacker() {
		return attacker;
	}
	
	public City getDefender() {
		return defender;
	}
}
