package strat.game;

public class LogEntry implements ISerializable {
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
	
	public LogEntry(Game game, String serializedData) {
		String[] data = serializedData.split(",");
		
		nation = game.getNation(Integer.parseInt(data[1]));
		title = decodeString(data[2]);
		description = decodeString(data[3]);
		type = Type.valueOf(data[4]);
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
		PEACE_TREATY,
		ATTRITION
	}

	@Override
	public String serialize() {
		return String.format("LogEntry,%d,%s,%s,%s", nation.getNationID(), encodeString(title),
				encodeString(description), type.toString());
	}
	
	private static String encodeString(String str) {
		return str.replaceAll(",", "<comma>").replaceAll("\r", "").replaceAll("\n", "\\n");
	}
	
	private static String decodeString(String str) {
		return str.replaceAll("<comma>", ",").replaceAll("\\n", "\n");
	}
}