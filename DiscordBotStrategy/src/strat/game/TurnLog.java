package strat.game;

import java.util.ArrayList;

public class TurnLog {
	private ArrayList<LogEntry> commonEntries;
	private ArrayList<LogEntry> battleEntries;
	private ArrayList<LogEntry> diplomaticEntries;
	
	public TurnLog() {
		commonEntries = new ArrayList<>();
		battleEntries = new ArrayList<>();
		diplomaticEntries = new ArrayList<>();
	}
	
	public void addEntry(LogEntry entry) {
		switch (entry.type) {
			case BATTLE:
				battleEntries.add(entry);
				break;
			case ALLIANCE_FORMED:
			case ALLIANCE_JOINED:
			case ALLIANCE_LEFT:
			case TRADE_AGREEMENT:
			case TRADE_LEFT:
			case WAR:
			case PEACE_TREATY:
				diplomaticEntries.add(entry);
				break;
			default:
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
		diplomaticEntries.clear();
	}
	
	public ArrayList<LogEntry> getCommonEntries() {
		return commonEntries;
	}
	
	public ArrayList<LogEntry> getBattleEntries() {
		return battleEntries;
	}
	
	public ArrayList<LogEntry> getDiplomaticEntries() {
		return diplomaticEntries;
	}
}
