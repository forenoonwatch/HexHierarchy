package strat.commands;

import strat.game.GameManager;

public interface Command {
	public Response execute(GameManager gameManager, long senderID,
			String rawMessage, String lowerMessage, String[] tokens);
	
	public PermissionLevel getPermissionLevel();
	
	public String getName();
	public String getUsage();
	public String getInfo();
	
	default public InputLevel getInputLevel() {
		return InputLevel.GAME_CHANNEL;
	}
}
