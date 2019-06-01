package strat.game;

public class LogEntry {
	public Nation nation;
	public String title;
	public String description;
	public Type type;
	
	public LogEntry(Nation nation, String title, String description, Type type) {
		this.nation = nation;
		this.title = title;
		this.description = description;
		this.type = type;
	}
	
	public static enum Type {
		BATTLE,
		CONSTRUCTION,
		RECRUITMENT,
		PAYMENT,
		SPY_CAUGHT,
		ALLIANCE_FORMED,
		ALLIANCE_JOINED,
		ALLIANCE_LEFT,
		TRADE_AGREEMENT,
		TRADE_LEFT,
		WAR,
		PEACE_TREATY
	}
}