package strat.game.commands.admin;

import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.GameManager;
import strat.game.Nation;

public class ChangeName implements Command {

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
		
		StringBuilder nationName = new StringBuilder();
		
		for (int i = 0; i < rawMessage.length(); ++i) {
			if (rawMessage.charAt(i) == '"') {
				do {
					++i;
					
					if (rawMessage.charAt(i) != '"') {
						nationName.append(rawMessage.charAt(i));
					}
				}
				while (i < rawMessage.length() && rawMessage.charAt(i) != '"');
				
				rawMessage = rawMessage.substring(i + 1).toLowerCase();
				
				break;
			}
		}
		
		n.setName(nationName.toString());
		gameManager.getRenderer().renderPoliticalImage(gameManager.getGame());
		
		return new Response(String.format("Set name of \"%s\" to \"%s\"",
				n.getName(), nationName.toString()));
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.ADMIN;
	}

	@Override
	public String getName() {
		return "changename";
	}

	@Override
	public String getUsage() {
		return "nationID \"name\"";
	}

	@Override
	public String getInfo() {
		return "changes the name of the nation to the given name";
	}

}
