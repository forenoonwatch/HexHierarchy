package strat.commands;

import java.util.HashMap;

import strat.bot.DiscordBot;
import strat.game.Map;
import strat.game.Nation;

public class CommandRegistry {
	private HashMap<String, Command> commands;
	private HashMap<String, AdminCommand> adminCommands;
	
	private Map map;
	private String prefix;
	
	public CommandRegistry(Map map, String prefix) {
		this.map = map;
		this.prefix = prefix;
		
		commands = new HashMap<>();
		adminCommands = new HashMap<>();
		
		initCommands();
	}
	
	public void register(String name, Command cmd) {
		commands.put(name, cmd);
	}
	
	public void registerAdmin(String name, AdminCommand cmd) {
		adminCommands.put(name, cmd);
	}
	
	public String runCommand(Nation sender, String text) {
		String[] tokens = text.split("\\s");
		
		if (tokens.length > 0 && tokens[0].startsWith(prefix)) {
			Command cmd = commands.get(tokens[0].substring(prefix.length()));
			
			if (cmd != null) {
				if (cmd instanceof Help) {
					return cmd.execute(map, sender, new String[] {getHelpString()});
				}
				else {
					return cmd.execute(map, sender, tokens);
				}
			}
		}
		
		return null;
	}
	
	public void runAdminCommand(DiscordBot bot, String text) {
		String[] tokens = text.toLowerCase().split("\\s");
		
		if (tokens.length > 0 && tokens[0].startsWith(prefix)) {
			AdminCommand cmd = adminCommands.get(tokens[0].substring(prefix.length()));
			
			if (cmd != null) {
				cmd.execute(bot, text, tokens);
			}
		}
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public String getHelpString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("**COMMANDS**\n\n");
		
		for (Command c : commands.values()) {
			sb.append(c.getFormat()).append(" - ").append(c.getSynopsis()).append('\n');
		}
		
		return sb.toString();
	}
	
	private void initCommands() {
		register("help", new Help());
		register("movearmy", new MoveArmy());
		register("hire", new Hire());
		register("troops", new Troops());
		register("build", new Build());
		register("info", new Info());
		register("regions", new Regions());
		register("viewregions", new ViewRegions());
		register("viewcities", new ViewCities());
		register("viewpolitical", new ViewPolitical());
		register("viewarmies", new ViewArmies());
		register("pay", new Pay());
		register("complete", new Complete());
		register("replenish", new Replenish());
		
		registerAdmin("setnation", new SetNation());
		registerAdmin("save", new Save());
		registerAdmin("advanceturn", new AdvanceTurn());
		registerAdmin("addnation", new AddNation());
		registerAdmin("changename", new ChangeName());
		registerAdmin("changecolor", new ChangeColor());
	}
}
