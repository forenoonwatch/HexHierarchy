package strat.bot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import strat.game.Army;
import strat.game.Hexagon;
import strat.game.Map;

public final class MovementLog {
	public static void save(Map map, String fileName) throws IOException {
		File file = new File(fileName);
		
		if (!file.exists()) {
			file.createNewFile();
		}
		
		try (PrintWriter pw = new PrintWriter(file)) {
			for (Army a : map.getArmies()) {
				if (!a.getPendingMoves().isEmpty()) {
					pw.printf("Army,%d,%d%n", a.getOwnerID(), a.getArmyID());
					
					for (Hexagon h : a.getPendingMoves()) {
						pw.printf("%d,%d%n", h.getQ(), h.getR());
					}
				}
			}
		}
		catch (IOException e) {
			throw e;
		}
	}
	
	public static void load(Map map, String fileName) throws IOException {
		try (Scanner sc = new Scanner(new File(fileName))) {
			Army currArmy = null;
			
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] tokens = line.split(",");
				
				if (line.startsWith("Army")) {
					currArmy = map.getArmy(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
				}
				else if (currArmy != null) {
					currArmy.getPendingMoves().add(map.get(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1])));
					currArmy.setRemainingMoves(currArmy.getRemainingMoves() - 1);
				}
			}
		}
		catch (FileNotFoundException e) {
			return;
		}
	}
}
