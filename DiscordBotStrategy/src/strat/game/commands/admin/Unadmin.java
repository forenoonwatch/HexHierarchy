package strat.game.commands.admin;

import strat.bot.BotUtils;
import strat.bot.DiscordBot;
import strat.commands.AdminRegistry;
import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.GameManager;
import sx.blah.discord.handle.obj.IUser;

public class Unadmin implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		if (tokens.length != 2) {
			return new Response("Unadmin: Invalid number of arguments: " + (tokens.length - 1));
		}
		
		long userID = BotUtils.parseUserID(tokens[1]);
		
		if (userID == 0L || userID == senderID) {
			return new Response("Unadmin: Invalid user.");
		}
		
		IUser user = DiscordBot.getUserByID(userID);
		
		if (user == null) {
			return new Response("Unadmin: User is not a valid Discord account.");
		}
		
		if (!AdminRegistry.isAdmin(userID)) {
			return new Response("Unadmin: User is not an admin.");
		}
		else if (AdminRegistry.getPermissionLevel(userID) == PermissionLevel.OWNER) {
			return new Response("Unadmin: Cannot unadmin an owner.");
		}
		
		AdminRegistry.remove(userID);
		
		return new Response("Successfully removed admin from " + DiscordBot.getFormattedName(user) + ".");
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.OWNER;
	}

	@Override
	public String getName() {
		return "unadmin";
	}

	@Override
	public String getUsage() {
		return "user(ping)";
	}

	@Override
	public String getInfo() {
		return "unadmins the user";
	}

}
