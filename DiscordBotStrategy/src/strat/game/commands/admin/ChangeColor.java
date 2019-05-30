package strat.game.commands.admin;

import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.GameManager;
import strat.game.Nation;

public class ChangeColor implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		if (tokens.length < 3) {
			return null;
		}
		
		int nationID;
		
		try {
			nationID = Integer.parseInt(tokens[1]);
		}
		catch (NumberFormatException e) {
			return null;
		}
		
		Nation n = gameManager.getGame().getNation(nationID);
		
		if (n == null) {
			return null;
		}
		
		int color;
		
		try {
			color = Integer.valueOf(tokens[2], 16);
		}
		catch (NumberFormatException e) {
			return null;
		}
		
		n.setRGB(color);
		gameManager.getRenderer().renderPoliticalView(gameManager.getGame());
		
		return new Response(String.format("Set color of \"%s\" to %X",
				n.getName(), color));
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.ADMIN;
	}

	@Override
	public String getName() {
		return "changecolor";
	}

	@Override
	public String getUsage() {
		return "nationID 0xRGB";
	}

	@Override
	public String getInfo() {
		return "changes the color of the given nation";
	}

}
