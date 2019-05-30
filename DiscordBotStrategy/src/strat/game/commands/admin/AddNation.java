package strat.game.commands.admin;

import java.awt.Color;

import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.City;
import strat.game.GameManager;
import strat.game.Nation;

public class AddNation implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
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
		
		tokens = rawMessage.trim().split("\\s");
		
		if (tokens.length < 2) {
			return null;
		}
		
		int rgb;
		
		try {
			rgb = Integer.valueOf(tokens[0], 16);
		}
		catch (NumberFormatException e) {
			return null;
		}
		
		City targetCity = gameManager.findFirstCity(tokens[1]);
		
		if (targetCity == null) {
			return null;
		}
		
		Nation n = new Nation(gameManager.getGame().getNations().size() + 1, nationName.toString(), new Color(rgb), 0L);
		gameManager.getGame().addNation(n);
		targetCity.getRegion().setOwnerID(n.getNationID());
		
		gameManager.getRenderer().renderPoliticalImage(gameManager.getGame());
		
		return new Response(String.format("Successfully created nation %s with capital %s", n.getName(), targetCity.getName()));
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.ADMIN;
	}

	@Override
	public String getName() {
		return "addnation";
	}

	@Override
	public String getUsage() {
		return "addnation \"name\" 0xRGB capital";
	}

	@Override
	public String getInfo() {
		return "add new nation to game's nation list";
	}
}
