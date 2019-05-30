package strat.game.commands;

import strat.bot.BotUtils;
import strat.bot.DiscordBot;
import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.GameManager;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;

public class ViewArmies implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		IUser user = DiscordBot.getUserByID(senderID);
		
		if (user == null) {
			return null;
		}
		
		IChannel channel = user.getOrCreatePMChannel();
		
		if (channel == null) {
			return new Response("ViewArmies: Unable to slide into DMs.");
		}
		
		BotUtils.sendFile(channel,
				gameManager.getRenderer().renderArmyImage(gameManager.getGame(), gameManager.getNationByUser(senderID)));
		return null;
	}
	
	@Override
	public String getName() {
		return "viewarmies";
	}
	
	@Override
	public String getUsage() {
		return "";
	}
	
	@Override
	public String getInfo() { 
		return "privately messages a diagram of your armies and their paths";
	}
	
	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.NATION;
	}
}
