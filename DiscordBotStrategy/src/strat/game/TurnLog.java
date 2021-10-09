package strat.game;

import java.util.ArrayList;

public class TurnLog {
	private ArrayList<LogEntry> commonEntries;
	private ArrayList<LogEntry> battleEntries;
	
	public TurnLog() {
		commonEntries = new ArrayList<>();
		battleEntries = new ArrayList<>();
	}
	
	public void addEntry(LogEntry entry) {
		if (entry.type == LogEntry.Type.BATTLE) {
			battleEntries.add(entry);
		}
		else {
			for (LogEntry le : commonEntries) {
				if (le.nation == entry.nation && le.type == entry.type) {
					le.description += entry.description;
					return;
				}
			}
			
			commonEntries.add(entry);
		}
	}
	
	public void clear() {
		commonEntries.clear();
		battleEntries.clear();
	}
	
	public ArrayList<LogEntry> getCommonEntries() {
		return commonEntries;
	}
	
	public ArrayList<LogEntry> getBattleEntries() {
		return battleEntries;
	}
}
