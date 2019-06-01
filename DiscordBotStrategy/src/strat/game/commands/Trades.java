package strat.game.commands;

import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.commands.ResponseType;
import strat.game.Game;
import strat.game.GameManager;
import strat.game.Nation;
import strat.game.relationships.TradeAgreement;

public class Trades implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		if (tokens.length > 1) {
			Nation found = gameManager.matchNationByName(lowerMessage.substring(tokens[0].length()));
			
			if (found == null) {
				return new Response("Trades: Could not find given nation.");
			}
			
			return getTradePartnerInfo(gameManager.getGame(), found);
		}
		else {
			return getTradePartnerInfo(gameManager.getGame(), gameManager.getNationByUser(senderID));
		}
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.NATION;
	}

	@Override
	public String getName() {
		return "trades";
	}

	@Override
	public String getUsage() {
		return "[nation]";
	}

	@Override
	public String getInfo() {
		return "gets the current trade agreements of you or another nation";
	}

	private static Response getTradePartnerInfo(Game game, Nation sender) {
		StringBuilder desc = new StringBuilder();
		
		for (TradeAgreement t : game.getTradeAgreements()) {
			if (t.hasNation(sender)) {
				String other = "No Nation";
				
				for (Nation n : t.getNations()) {
					if (n != sender) {
						other = n.getName();
						break;
					}
				}
				
				desc.append(other).append('\n');
			}
		}
		
		return new Response(ResponseType.PUBLIC, String.format(":scales: **%s - TRADE AGREEMENTS**", sender.getName().toUpperCase()),
				desc.toString(), sender.getRGB());
	}
}
