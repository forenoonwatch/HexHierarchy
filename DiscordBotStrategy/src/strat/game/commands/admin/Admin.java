package strat.game.commands.admin;

import strat.bot.BotUtils;
import strat.bot.DiscordBot;
import strat.commands.AdminRegistry;
import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.GameManager;
import sx.blah.discord.handle.obj.IUser;

public class Admin implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		if (tokens.length != 2) {
			return new Response("Admin: Invalid number of arguments: " + (tokens.length - 1));
		}
		
		long userID = BotUtils.parseUserID(tokens[1]);
		
		if (userID == 0L || userID == senderID) {
			return new Response("Admin: Invalid user.");
		}
		
		IUser user = DiscordBot.getUserByID(userID);
		
		if (user == null) {
			return new Response("Admin: User is not a valid Discord account.");
		}
		
		if (AdminRegistry.isAdmin(userID)) {
			return new Response("Admin: User is already an admin.");
		}
		
		AdminRegistry.add(userID, PermissionLevel.ADMIN);
		
		return new Response("Successfully added " + DiscordBot.getFormattedName(user) + " as an admin.");
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.OWNER;
	}

	@Override
	public String getName() {
		return "admin";
	}

	@Override
	public String getUsage() {
		return "user(ping)";
	}

	@Override
	public String getInfo() {
		return "admins the user";
	}

}
