package strat.game.commands;

import strat.bot.DiscordBot;
import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.commands.ResponseType;
import strat.game.City;
import strat.game.Game;
import strat.game.GameManager;
import strat.game.Nation;
import sx.blah.discord.handle.obj.IUser;

public class Info implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		if (tokens.length > 1) {
			Nation found = gameManager.matchNationByName(lowerMessage.substring(tokens[0].length()));
			
			if (found != null) {
				IUser user = DiscordBot.getUserByID(found.getOwner());
				return new Response(getInfoForNation(gameManager.getGame(), found, user, false));
			}
			else {
				return new Response("Info: Could not find given nation.");
			}
		}
		else {
			Nation sender = gameManager.getNationByUser(senderID);
			IUser user = DiscordBot.getUserByID(senderID);
			String info = getInfoForNation(gameManager.getGame(), sender, user, true);
			
			return new Response(ResponseType.PRIVATE, info);
		}
	}
	
	@Override
	public String getName() {
		return "info";
	}
	
	@Override
	public String getUsage() {
		return "[nation]";
	}
	
	@Override
	public String getInfo() { 
		return "show information for yourself or a selected nation";
	}
	
	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.NATION;
	}
	
	private static String getInfoForNation(Game game, Nation n, IUser user, boolean showMoney) {
		int numLands = 0;
		int profitPerTurn = game.calcGrossProfitForNation(n);
		
		for (City c : game.getMap().getCities()) {
			if (c.getOwnerID() == n.getNationID()) {
				++numLands;
			}
		}
		
		if (showMoney) {
			return String.format("**%s**%nMoney:\t\t%d (+%d/turn)%nRegions held:\t%d%n", n.getName(),
					n.getMoney(), profitPerTurn, numLands);
		}
		
		return String.format("**%s**%nLeader:\t%s%nRegions held:\t%d%n", n.getName(),
				user == null ? "No Ruler" : DiscordBot.getFormattedName(user), numLands);
	}
}
