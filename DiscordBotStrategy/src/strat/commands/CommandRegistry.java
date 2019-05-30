package strat.commands;

import java.util.HashMap;

import strat.game.AdminRegistry;
import strat.game.GameManager;
import strat.game.commands.*;
import strat.game.commands.admin.*;

public class CommandRegistry {
	
	private static String prefix = "?";
	private static HashMap<String, Command> commands = new HashMap<>();
	
	public static void init() {
		addCommand(new Help());
		addCommand(new Build());
		addCommand(new Hire());
		addCommand(new Info());
		addCommand(new MoveArmy());
		addCommand(new Pay());
		addCommand(new Regions());
		addCommand(new Replenish());
		addCommand(new Troops());
		addCommand(new ViewArmies());
		addCommand(new ViewCities());
		addCommand(new ViewPolitical());
		addCommand(new ViewRegions());
		addCommand(new UndoMove());
		addCommand(new Spy());
		addCommand(new Split());
		
		addCommand(new AddNation());
		addCommand(new AdvanceTurn());
		addCommand(new ChangeColor());
		addCommand(new ChangeName());
		addCommand(new SetNation());
		addCommand(new SetOwner());
	}
	
	public static Response executeCommand(GameManager gameManager, String message,
			long senderID, InputLevel sourceLevel) {
		String lowerMessage = message.toLowerCase();
		String[] tokens = lowerMessage.split("\\s");
		
		PermissionLevel permissionLevel = PermissionLevel.ALL;
		
		if (AdminRegistry.isAdmin(senderID)) {
			permissionLevel = PermissionLevel.ADMIN;
		}
		else if (gameManager.getNationByUser(senderID) != null) {
			permissionLevel = PermissionLevel.NATION;
		}
		
		if (tokens.length > 0 && tokens[0].startsWith(prefix)) {
			Command cmd = commands.get(tokens[0].substring(prefix.length()));
			
			if (cmd != null && sourceLevel.ordinal() <= cmd.getInputLevel().ordinal()
					&& permissionLevel.ordinal() >= cmd.getPermissionLevel().ordinal()) {
				return cmd.execute(gameManager, senderID, message, lowerMessage, tokens);
			}
		}
		
		return null;
	}
	
	public static String getHelpString(PermissionLevel pl) {
		StringBuilder sb = new StringBuilder();
		
		for (Command cmd : commands.values()) {
			if (cmd.getPermissionLevel().ordinal() <= pl.ordinal()) {
				sb.append(String.format("%s %s - %s%n", cmd.getName(), cmd.getUsage(), cmd.getInfo()));
			}
		}
		
		return sb.toString();
	}
	
	public static void setPrefix(String prefix) {
		CommandRegistry.prefix = prefix;
	}
	
	public static String getPrefix() {
		return prefix;
	}
	
	private static void addCommand(Command cmd) {
		commands.put(cmd.getName(), cmd);
	}
}