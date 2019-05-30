package strat.game.commands.admin;

import strat.bot.BotUtils;
import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.GameManager;
import strat.game.Nation;

public class SetNation implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		if (tokens.length < 2) {
			return null;
		}
		
		Nation nation = null;
		
		try {
			nation = gameManager.getGame().getNation(Integer.parseInt(tokens[1]));
		}
		catch (NumberFormatException e) {
			return null;
		}
		
		if (nation == null) {
			return null;
		}
		
		if (tokens.length >= 3) {
			long userID = BotUtils.parseUserID(tokens[2]);
			
			if (userID == 0L) {
				return new Response("Invalid user ID");
			}
			
			nation.setOwner(userID);
			
			return new Response("Successfully added user to nation " + nation.getName());
		}
		else {
			nation.setOwner(0L);
			return new Response("Successfully removed user from nation");
		}
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.ADMIN;
	}

	@Override
	public String getName() {
		return "setnation";
	}

	@Override
	public String getUsage() {
		return "nationID userID";
	}

	@Override
	public String getInfo() {
		return "sets/unsets the owner of the nation";
	}
}
