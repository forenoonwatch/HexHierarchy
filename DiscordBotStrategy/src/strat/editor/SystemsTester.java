package strat.editor;

import java.awt.Color;

import strat.game.Army;
import strat.game.Battle;
import strat.game.Game;
import strat.game.Hexagon;
import strat.game.LogEntry;
import strat.game.Nation;

public class SystemsTester {
	public static void main(String[] args) {
		Game game = new Game();
		game.getMap().add(new Hexagon(0, 0));
		
		Nation n1 = new Nation(1, "Nation A", Color.RED, 0L);
		Nation n2 = new Nation(2, "Nation B", Color.BLUE, 0L);
		
		game.addNation(n1);
		game.addNation(n2);
		
		Army at = new Army(game.getMap(), n1.getNationID(), 1, 0, 0);
		Army de = new Army(game.getMap(), n2.getNationID(), 1, 0, 0);
		
		game.addArmy(de);
		game.addArmy(at);
		
		for (int i = 0; i < 1; ++i) {
			at.setUnits("infantry", 625);
			at.setUnits("cavalry", 20);
			at.setUnits("artillery", 17);
			
			de.setUnits("infantry", 237 + 53);
			de.setUnits("cavalry", 9 + 10);
			de.setUnits("artillery", 45 + 30);
			
			Battle b = new Battle(at, de, game.getMap().get(0, 0));
			b.resolve();
		}
		
		for (LogEntry le : game.getTurnLog().getBattleEntries()) {
			System.out.print(le.title);
			System.out.println(le.description);
			System.out.println();
		}
	}
}
