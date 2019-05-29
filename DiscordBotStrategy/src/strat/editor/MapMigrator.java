package strat.editor;

import java.io.IOException;

import strat.game.Army;
import strat.game.City;
import strat.game.GameRules;
import strat.game.Hexagon;
import strat.game.Map;
import strat.game.Nation;

public class MapMigrator {
	public static void main(String[] args) throws IOException {
		Map dataMap = Map.readFromFile("hre.game");
		Map worldMap = Map.readFromFile("larger-hre.game");
		
		for (Nation n : dataMap.getNations()) {
			worldMap.addNation(n);
		}
		
		int numCities = 0;
		
		for (City inCity : dataMap.getCities()) {
			for (City outCity : worldMap.getCities()) {
				if (inCity.getName().equals(outCity.getName())) {
					++numCities;
					
					for (String building : GameRules.getBuildingTypes()) {
						outCity.setBuildingLevel(building, inCity.getBuildingLevel(building));
					}
				}
			}
		}
		
		System.out.println("Migrated " + numCities + " cities");
		
		int numArmies = 0;
		
		for (Army inArmy : dataMap.getArmies()) {
			Hexagon h = worldMap.get(inArmy.getQ(), inArmy.getR());
			
			if (h != null) {
				if (worldMap.getCityAt(inArmy.getQ(), inArmy.getR()) != null) {
					for (int i = 0; i < Hexagon.DIRECTIONS.length; ++i) {
						int q = inArmy.getQ() + Hexagon.DIRECTIONS[i].getQ();
						int r = inArmy.getR() + Hexagon.DIRECTIONS[i].getR();
						h = worldMap.get(inArmy.getQ(), inArmy.getR());
						
						if (h != null && worldMap.getCityAt(q, r) == null) {
							inArmy.setQ(q);
							inArmy.setR(r);
							worldMap.addArmy(inArmy);
							++numArmies;
							
							break;
						}
					}
				}
				else {
					++numArmies;
					worldMap.addArmy(inArmy);
				}
			}
		}
		
		System.out.println("Migrated " + numArmies + " armies");
		
		worldMap.writeToFile("migrated-hre.game");
	}
}
