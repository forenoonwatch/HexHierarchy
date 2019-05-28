package strat.bot;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import strat.game.Map;
import strat.game.Nation;

public class NationRegistry {
	private HashMap<Long, Nation> nationMap;
	private Map map;
	private HashSet<Nation> turnsCompleted;
	
	public NationRegistry(Map map) {
		this.map = map;
		
		nationMap = new HashMap<>();
		turnsCompleted = new HashSet<>();
	}
	
	public void load(String fileName) throws IOException {
		try (Scanner i = new Scanner(new File(fileName))) {
			while (i.hasNextLine()) {
				String[] data = i.nextLine().split(",");
				long user = Long.parseLong(data[0]);
				Nation n = map.getNation(Integer.parseInt(data[1]));
				
				addUser(user, n);
			}
		}
		catch (IOException e) {
			throw e;
		}
	}
	
	public void save(String fileName) throws IOException {
		try (PrintWriter o = new PrintWriter(new File(fileName))) {
			for (java.util.Map.Entry<Long, Nation> e : nationMap.entrySet()) {
				o.print(e.getKey());
				o.print(",");
				o.print(e.getValue().getNationID());
				o.println();
			}
		}
		catch (IOException e) {
			throw e;
		}
	}
	
	public void addUser(long user, Nation nation) {
		nationMap.put(user, nation);
	}
	
	public void removeUser(long user) {
		nationMap.remove(user);
	}
	
	public Nation getNation(long user) {
		return nationMap.get(user);
	}
	
	public long getUserForNation(int nationID) {
		for (java.util.Map.Entry<Long, Nation> e : nationMap.entrySet()) {
			if (e.getValue().getNationID() == nationID) {
				return e.getKey();
			}
		}
		
		return 0L;
	}
	
	public void resetTurnsCompleted() {
		turnsCompleted.clear();
	}
	
	public boolean setTurnCompleted(Nation n) {
		return turnsCompleted.add(n);
	}
	
	public boolean isTurnComplete() {
		return turnsCompleted.size() >= nationMap.size() - 1;
	}
}
