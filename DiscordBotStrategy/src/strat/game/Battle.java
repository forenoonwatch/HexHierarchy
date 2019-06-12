package strat.game;

public class Battle {
	/*public static final int INFANTRY_WEIGHT = 10;
	public static final int CAVALRY_WEIGHT = 4;
	public static final int ARTILLERY_WEIGHT = 1;
	
	public static final int[] WEIGHTS = {INFANTRY_WEIGHT, CAVALRY_WEIGHT, ARTILLERY_WEIGHT};
	
	public static final double ATTACK_SCALE = 3;*/
	
	private Army attacker;
	private Army defender;
	private Hexagon location;
	
	public Battle(Army attacker, Army defender, Hexagon location) {
		this.attacker = attacker;
		this.defender = defender;
		this.location = location;
		
		attacker.setFighting(true);
		defender.setFighting(true);
	}
	
	public void resolve() {
		String title = String.format(":crossed_swords: **BATTLE OF %s, %s**%n%n",
				attacker.getMap().getRegion(location.getRegionID()).getName().toUpperCase(),
				attacker.getGame().getCurrentDate().toUpperCase());
		
		StringBuilder desc = new StringBuilder();
		
		desc.append(String.format("%s Army %d (%d, %d, %d)%n", attacker.getOwner().getName(), attacker.getArmyNumber(),
				attacker.getUnits("infantry"), attacker.getUnits("cavalry"), attacker.getUnits("artillery")));
		desc.append("vs.\n");
		desc.append(String.format("%s Army %d (%d, %d, %d)%n%n", defender.getOwner().getName(), defender.getArmyNumber(),
				defender.getUnits("infantry"), defender.getUnits("cavalry"), defender.getUnits("artillery")));
		
		Army winner = calcWinner();
		Army loser = attacker == winner ? defender : attacker;
		
		loser.getGame().removeArmy(loser);
		winner.setQ(location.getQ());
		winner.setR(location.getR());
		
		desc.append(String.format("%s vs %s%n%s is victorious with %d infantry, %d cavalry, and %d artillery remaining.%n",
				attacker.getOwner().getName(), defender.getOwner().getName(),
				winner.getOwner().getName(), winner.getUnits("infantry"), winner.getUnits("cavalry"),
				winner.getUnits("artillery")));
		
		winner.getGame().getTurnLog().addEntry(new LogEntry(winner.getOwner(), title, desc.toString(), LogEntry.Type.BATTLE));
	}
	
	public Army calcWinner() {
		//for (int i = 0; i < 5; ++i) {
		while (attacker.isAlive() && defender.isAlive()) {
			calcAttack(attacker, defender);
		}
		
		return attacker.isAlive() ? attacker : defender;
	}
	
	public Army getAttacker() {
		return attacker;
	}
	
	public Army getDefender() {
		return defender;
	}
	
	private void calcAttack(Army a, Army b) {
		double[] dmgAToB = new double[3];
		double[] dmgBToA = new double[3];
		
		double aSum = 0, bSum = 0;
		
		for (String unit : GameRules.getUnitTypes()) {
			aSum += (double)a.getUnits(unit) / GameRules.getRuled(unit + "Weight");
			bSum += (double)b.getUnits(unit) / GameRules.getRuled(unit + "Weight");
		}
		
		if (bSum != 0.0 && aSum / bSum > GameRules.getRuled("attackMaxAdvantage")) {
			aSum = GameRules.getRuled("attackMaxAdvantage");
			bSum = 1.0;
		}
		else if (aSum != 0.0 && bSum / aSum > GameRules.getRuled("attackMaxAdvantage")) {
			aSum = 1.0;
			bSum = GameRules.getRuled("attackMaxAdvantage");
		}
		
		calcDamageMatrix(a, b, dmgAToB, bSum == 0 ? aSum : aSum / bSum);
		calcDamageMatrix(b, a, dmgBToA, aSum == 0 ? bSum : bSum / aSum);
		
		//System.out.printf("A takes (%.2f, %.2f, %.2f) damage%n", dmgBToA[0], dmgBToA[1], dmgBToA[2]);
		//System.out.printf("B takes (%.2f, %.2f, %.2f) damage%n", dmgAToB[0], dmgAToB[1], dmgAToB[2]);
		
		for (int i = 0; i < GameRules.getUnitTypes().size(); ++i) {
			a.setUnits(i, Math.max((int)Math.ceil(a.getUnits(i) - dmgBToA[i] * calcRNG()), 0));
			b.setUnits(i, Math.max((int)Math.ceil(b.getUnits(i) - dmgAToB[i] * calcRNG()), 0));
		}
		
		//System.out.printf("A(%d, %d, %d) B(%d, %d, %d)%n",
		//		a.getUnits("infantry"), a.getUnits("cavalry"), a.getUnits("artillery"),
		//		b.getUnits("infantry"), b.getUnits("cavalry"), b.getUnits("artillery"));
	}
	
	private double calcRNG() {
		return 0.5 + 0.5 * Math.random();
	}
	
	private void calcDamageMatrix(Army a, Army b, double[] out, double adv) {
		for (int i = 0; i < GameRules.getUnitTypes().size(); ++i) {
			String unit = GameRules.getUnitTypes().get(i);
			
			if (a.getUnits(unit) == 0) {
				continue;
			}
			
			calcRawPower(b, unit, out);
		}
		
		for (int i = 0; i < GameRules.getUnitTypes().size(); ++i) {
			out[i] *= adv;
		}
	}
	
	private void calcRawPower(Army a, String attackUnit, double[] out) {
		String comp = GameRules.getAttackCompUnit(attackUnit);
		
		if (a.getUnits(comp) > 0) {
			out[GameRules.getUnitIndex(comp)] += GameRules.getRuled("attackAdvantage") * GameRules.getRuled(comp + "Weight");
		}
		else if (a.getUnits(attackUnit) > 0) {
			out[GameRules.getUnitIndex(attackUnit)] += GameRules.getRuled(attackUnit + "Weight");
		}
		
		comp = GameRules.getAttackCompUnit(comp);
		
		if (a.getUnits(comp) > 0) {
			out[GameRules.getUnitIndex(comp)] += GameRules.getRuled(comp + "Weight") / GameRules.getRuled("attackAdvantage");
		}
	}
}
