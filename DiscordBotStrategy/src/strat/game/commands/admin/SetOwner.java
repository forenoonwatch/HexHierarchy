package strat.game.commands.admin;

import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.GameManager;
import strat.game.Nation;
import strat.game.Region;

public class SetOwner implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		if (tokens.length < 3) {
			return null;
		}
		
		Region r;
		
		try {
			r = gameManager.getGame().getMap().getRegion(Integer.parseInt(tokens[1]));
		}
		catch (NumberFormatException e) {
			return null;
		}
		
		Nation n;
		
		try {
			n = gameManager.getGame().getNation(Integer.parseInt(tokens[2]));
		}
		catch (NumberFormatException e) {
			return null;
		}
		
		r.setOwnerID(n.getNationID());
		
		return new Response("Successfully set owner of " + r.getName() + " to " + n.getName());
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.ADMIN;
	}

	@Override
	public String getName() {
		return "setowner";
	}

	@Override
	public String getUsage() {
		return "regionID nationID";
	}

	@Override
	public String getInfo() {
		return "sets the owner of the given region to the given owner";
	}

}
