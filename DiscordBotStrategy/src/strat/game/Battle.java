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
		String title = String.format("**BATTLE OF %s, %s**%n%n",
				attacker.getMap().getRegion(location.getRegionID()).getName().toUpperCase(),
				attacker.getMap().getCurrentDate().toUpperCase());
		
		Army winner = calcWinner();
		Army loser = attacker == winner ? defender : attacker;
		
		loser.getMap().removeArmy(loser);
		winner.setQ(location.getQ());
		winner.setR(location.getR());
		
		String desc = String.format("%s vs %s%n%s is victorious with %d infantry, %d cavalry, and %d artillery remaining.%n",
				attacker.getOwner().getName(), defender.getOwner().getName(),
				winner.getOwner().getName(), winner.getUnits("infantry"), winner.getUnits("cavalry"),
				winner.getUnits("artillery"));
		
		winner.getMap().getTurnLog().addEntry(new TurnLog.LogEntry(winner.getOwner(), title, desc, TurnLog.Type.BATTLE));
	}
	
	public Army calcWinner() {
		while (attacker.isAlive() && defender.isAlive()) {
			calcAttack(attacker, defender);
			
			if (!attacker.isAlive() || !defender.isAlive()) {
				break;
			}
			
			calcAttack(defender, attacker);
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
		int atk;
		
		do {
			atk = (int)(Math.random() * 3);
		}
		while (!hasUnit(a, atk));
		
		
		int targ = 0;
		
		switch (atk) {
			case 0: // Infantry volley
				targ = 1;
				
				if (!hasUnit(b, 1)) {
					targ = hasUnit(b, 0) ? 0 : 2;
				}
				break;
			case 1: // Cavalry charge
				targ = 2;
				
				if (!hasUnit(b, 2)) {
					targ = hasUnit(b, 1) ? 1 : 0;
				}
				break;
			case 2: // Artillery barrage
				targ = 0;
				
				if (!hasUnit(b, 0)) {
					targ = hasUnit(b, 2) ? 2 : 1;
				}
				break;
			default:
				System.out.println(atk);
		}
		
		int[] effectiveness = new int[2];
		calcEffectiveness(atk, targ, effectiveness);
		
		int atkWeight = GameRules.getRulei(GameRules.getUnitTypes().get(atk) + "Weight");
		int targWeight = GameRules.getRulei(GameRules.getUnitTypes().get(targ) + "Weight");
		
		double advAB = ((double)a.getUnits(atk) / (double)atkWeight)
				/ ((double)b.getUnits(targ) / (double)targWeight);
		//System.out.printf("Advantage: %.3f%n", advAB * ATTACK_SCALE);
		int dmgToA = (int)(GameRules.getRuled("attackScale") / advAB * effectiveness[1] * Math.random())
				* atkWeight;
		int dmgToB = (int)(GameRules.getRuled("attackScale") * advAB * effectiveness[0] * Math.random())
				* targWeight;
		
		dmgToA = Math.min(dmgToA, a.getUnits(atk));
		dmgToB = Math.min(dmgToB, b.getUnits(targ));
		
		a.setUnits(atk, Math.max(a.getUnits(atk) - dmgToA, 0));
		b.setUnits(targ, Math.max(b.getUnits(targ) - dmgToB, 0));
		
		//if (sb != null) {
			//sb.append(String.format("%s launches %s.%n%s takes %d %s losses. %s takes %d %s losses.%n",
			//		a.getNation().getName(), getUnitAttackName(atk), a.getNation().getName(), dmgToA,
			//		getUnitName(atk), b.getNation().getName(), dmgToB, getUnitName(targ)));
		//}
	}
	
	private static boolean hasUnit(Army a, int unitID) {
		return a.getUnits(unitID) > 0;
	}
	
	private static void calcEffectiveness(int atk, int def, int[] out) {
		if (atk == def) {
			out[0] = 1;
			out[1] = 1;
			return;
		}
		
		switch (atk) {
			case 0:
				switch (def) {
					case 1:
						out[0] = 2;
						out[1] = 1;
						break;
					case 2:
						out[0] = 1;
						out[1] = 2;
						break;
				}
				break;
			case 1:
				switch (def) {
					case 0:
						out[0] = 1;
						out[1] = 2;
						break;
					case 2:
						out[0] = 2;
						out[1] = 1;
						break;
				}
				break;
			case 2:
				switch (def) {
					case 0:
						out[0] = 2;
						out[1] = 1;
						break;
					case 1:
						out[0] = 1;
						out[1] = 2;
						break;
				}
				break;
		}
	}
	
	@SuppressWarnings("unused")
	private static String getUnitAttackName(int unitID) {
		switch (unitID) {
			case 0:
				return "an infantry volley";
			case 1:
				return "a cavalry charge";
			case 2:
				return "an artillery barrage";
			default:
				return "ERROR";
		}
	}
	
	@SuppressWarnings("unused")
	private static String getUnitName(int unitID) {
		switch (unitID) {
			case 0:
				return "infantry";
			case 1:
				return "cavalry";
			case 2:
				return "artillery";
			default:
				return "ERROR";
		}
	}
}
