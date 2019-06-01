package strat.game.commands;

import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.commands.ResponseType;
import strat.game.Game;
import strat.game.GameManager;
import strat.game.Nation;
import strat.game.relationships.War;

public class Wars implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		if (tokens.length > 1) {
			Nation found = gameManager.matchNationByName(lowerMessage.substring(tokens[0].length()));
			
			if (found == null) {
				return new Response("Wars: Could not find given nation.");
			}
			
			return getWarsForNation(gameManager.getGame(), found);
		}
		else {
			return getWarsForNation(gameManager.getGame(), gameManager.getNationByUser(senderID));
		}
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.NATION;
	}

	@Override
	public String getName() {
		return "wars";
	}

	@Override
	public String getUsage() {
		return "[nation]";
	}

	@Override
	public String getInfo() {
		return "gets the current wars of you or a given nation";
	}
	
	private static Response getWarsForNation(Game game, Nation sender) {
		StringBuilder desc = new StringBuilder();
		
		for (War w : game.getWars()) {
			if (w.hasNation(sender)) {
				String other = "No Nation";
				
				for (Nation n : w.getNations()) {
					if (n != sender) {
						other = n.getName();
						break;
					}
				}
				
				desc.append(other).append(" - ").append(w.getNumTurns()).append(" turns\n");
			}
		}
		
		return new Response(ResponseType.PUBLIC, String.format(":crossed_swords: **%s - WARS**", sender.getName().toUpperCase()),
				desc.toString(), sender.getRGB());
	}
}
