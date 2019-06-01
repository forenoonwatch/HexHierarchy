package strat.game.commands;

import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.commands.ResponseType;
import strat.game.Game;
import strat.game.GameManager;
import strat.game.Nation;
import strat.game.relationships.Alliance;

public class Allies implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		if (tokens.length > 1) {
			Nation found = gameManager.matchNationByName(lowerMessage.substring(tokens[0].length()));
			
			if (found == null) {
				return new Response("Allies: Could not find given nation.");
			}
			
			return getAlliesForNation(gameManager.getGame(), found);
		}
		else {
			return getAlliesForNation(gameManager.getGame(), gameManager.getNationByUser(senderID));
		}
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.NATION;
	}

	@Override
	public String getName() {
		return "allies";
	}

	@Override
	public String getUsage() {
		return "[nation]";
	}

	@Override
	public String getInfo() {
		return "gets the current allied nations of you or a given nation";
	}
	
	private static Response getAlliesForNation(Game game, Nation sender) {
		Alliance a = game.getAllianceForNation(sender);
		
		if (a == null) {
			return new Response(sender.getName() + " does not have any allies.");
		}
		
		StringBuilder desc = new StringBuilder();
		
		desc.append("*").append(a.getName()).append("*\n\n");
		
		for (Nation n : a.getNations()) {
			if (n != sender) {
				desc.append(n.getName()).append('\n');
			}
		}
		
		return new Response(ResponseType.PUBLIC, String.format(":handshake: **%s - ALLIES**", sender.getName().toUpperCase()),
				desc.toString(), a.getRGB());
	}
}
