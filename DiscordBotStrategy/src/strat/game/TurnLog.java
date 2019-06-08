package strat.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TurnLog {
	private List<LogEntry> commonEntries;
	private List<LogEntry> battleEntries;
	private List<LogEntry> diplomaticEntries;
	
	public TurnLog() {
		commonEntries = Collections.synchronizedList(new ArrayList<>());
		battleEntries = Collections.synchronizedList(new ArrayList<>());
		diplomaticEntries = Collections.synchronizedList(new ArrayList<>());
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
	
	public List<LogEntry> getCommonEntries() {
		return commonEntries;
	}
	
	public List<LogEntry> getBattleEntries() {
		return battleEntries;
	}
	
	public List<LogEntry> getDiplomaticEntries() {
		return diplomaticEntries;
	}
}
