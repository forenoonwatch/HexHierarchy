package strat.game.commands.admin;

import java.io.IOException;

import strat.commands.Command;
import strat.commands.PermissionLevel;
import strat.commands.Response;
import strat.game.Game;
import strat.game.GameManager;

public class Load implements Command {

	@Override
	public Response execute(GameManager gameManager, long senderID, String rawMessage, String lowerMessage,
			String[] tokens) {
		if (tokens.length < 2) {
			return null;
		}
		
		Game game = null;
		
		try {
			game = new Game();
			game.load(tokens[1]);
		}
		catch (IOException e) {
			return new Response("Load: Cannot load game file " + tokens[1]);
		}
		
		gameManager.setGame(game);
		
		return new Response("Successfully loaded game file" + tokens[1]);
	}

	@Override
	public PermissionLevel getPermissionLevel() {
		return PermissionLevel.ADMIN;
	}

	@Override
	public String getName() {
		return "load";
	}

	@Override
	public String getUsage() {
		return "map_file";
	}

	@Override
	public String getInfo() {
		return "loads the given map file into the game";
	}

}
