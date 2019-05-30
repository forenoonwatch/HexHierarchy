package strat.game;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import strat.bot.BotUtils;
import strat.bot.DiscordBot;
import sx.blah.discord.api.internal.json.objects.EmbedObject;

public class GameManager {
	public static final String DEFAULT_FILE_NAME = "hre.game";
	public static final String MOVEMENT_LOG_FILE = "movelog.dat";
	
	public static final double COMPLETION_PERCENTAGE = 0.75;
	
	private Game game;
	private GameRenderer gameRenderer;
	private Timer timer;
	
	private HashSet<Nation> turnsCompleted;
	
	public GameManager(Game game) throws IOException {
		this.game = game;
		
		gameRenderer = new GameRenderer(800, 800);
		timer = new Timer(Duration.ofHours(12));
		
		turnsCompleted = new HashSet<>();
		
		gameRenderer.renderSingleImages(game);
		gameRenderer.renderPoliticalImage(game);
	}
	
	public void startTimer() {
		for (;;) {
			if (timer.shouldAdvanceTurn()) {
				advanceTurn();
				save();
			}
			
			try {
				Thread.sleep(60000);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void advanceTurn() {
		String lastDate = game.getCurrentDate();
		
		game.endTurn();
		
		ArrayList<java.util.Map.Entry<Nation, Integer>> holdings = new ArrayList<>();
		sortHoldings(holdings);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("<@&582267448661639176>! Turn ").append(game.getCurrentTurn() - 1)
				.append(" has ended. The current standings are as follows:\n\n");
		
		for (java.util.Map.Entry<Nation, Integer> e : holdings) {
			sb.append(e.getKey().getName()).append(": ").append(e.getValue()).append(" regions.\n");
		}
		
		EmbedObject eo = new EmbedObject();
		eo.title = String.format("Conclusion of turn %d (%s to %s)", game.getCurrentTurn() - 1, lastDate, game.getCurrentDate());
		eo.description = sb.toString();
		
		EmbedObject.ThumbnailObject to = new EmbedObject.ThumbnailObject();
		to.url = "https://cdn.discordapp.com/attachments/548382890296082433/582297151321669678/hex-strat.png";
		to.width = 256;
		to.height = 256;
		eo.thumbnail = to;
		
		BotUtils.sendEmbed(DiscordBot.getTurnChannel(), eo);
		
		try {
			Thread.sleep(5000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		eo.thumbnail = null;
		
		for (TurnLog.LogEntry le : game.getTurnLog().getBattleEntries()) {
			eo.title = le.title;
			eo.description = le.description;
			eo.color = le.nation.getRGB();
			
			BotUtils.sendEmbed(DiscordBot.getBattleChannel(), eo);
			
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		for (TurnLog.LogEntry le : game.getTurnLog().getCommonEntries()) {
			eo.title = le.title;
			eo.description = le.description;
			eo.color = le.nation.getRGB();
			
			BotUtils.sendEmbed(DiscordBot.getActionChannel(), eo);
			
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		gameRenderer.renderPoliticalImage(game);
		
		resetTurnsCompleted();
		game.clearLog();
	}
	
	public void save() {
		saveGame(DEFAULT_FILE_NAME);
	}
	
	public void saveGame(String fileName) {
		try {
			game.save(fileName);
		}
		catch (IOException e) {
			BotUtils.sendMessage(DiscordBot.getTurnChannel(), "Could not save map to file: " + fileName);
		}
	}
	
	public boolean setTurnCompleted(Nation n) {
		boolean tc = turnsCompleted.add(n);
		
		if (isTurnComplete()) {
			advanceTurn();
			save();
		}
		
		return tc;
	}
	
	public BufferedImage renderView(int viewID) {
		if (game == null) {
			return null;
		}
		
		switch (viewID) {
			case 0:
				return gameRenderer.renderCities(game);
			case 1:
				return gameRenderer.renderRegions(game);
			case 2:
				return gameRenderer.renderArmyView(game, Nation.NO_NATION);
			case 3:
				return gameRenderer.renderPoliticalView(game);
			case 4:
				return gameRenderer.renderEditorView(game);
			default:
				return null;
		}
	}
	
	public City findFirstCity(String lowerName) {
		if (game == null) {
			return null;
		}
		
		return game.getMap().findFirstCity(lowerName);
	}
	
	public Nation getNationByUser(long userID) {
		if (game == null) {
			return null;
		}
		
		return game.getNationByUser(userID);
	}
	
	public Nation matchNationByName(String lowerName) {
		if (game == null) {
			return null;
		}
		
		return game.matchNationByName(lowerName);
	}
	
	public int getNumOwnedNations() {
		if (game == null) {
			return 0;
		}
		
		int count = 0;
		
		for (Nation n : game.getNations().values()) {
			if (n.getOwner() != 0L) {
				++count;
			}
		}
		
		return count;
	}
	
	public int getNumRequiredToComplete() {
		return (int)(COMPLETION_PERCENTAGE * getNumOwnedNations());
	}
	
	public void setGame(Game game) {
		this.game = game;
		
		turnsCompleted.clear();
		
		gameRenderer.renderSingleImages(game);
		gameRenderer.renderPoliticalImage(game);
	}
	
	public Game getGame() {
		return game;
	}
	
	public GameRenderer getRenderer() {
		return gameRenderer;
	}
	
	private void sortHoldings(ArrayList<java.util.Map.Entry<Nation, Integer>> holdings) {
		HashMap<Nation, Integer> count = new HashMap<>();
		
		for (Nation n : game.getNations().values()) {
			count.put(n, 0);
		}
		
		for (Region r : game.getMap().getRegions().values()) {
			Nation n = game.getNation(r.getOwnerID());
			count.put(n, count.get(n) + 1);
		}
		
		for (java.util.Map.Entry<Nation, Integer> e : count.entrySet()) {
			holdings.add(e);
		}
		
		holdings.sort((a, b) -> b.getValue() - a.getValue());
	}
	
	private void resetTurnsCompleted() {
		turnsCompleted.clear();
	}
	
	private boolean isTurnComplete() {
		return turnsCompleted.size() >= getNumRequiredToComplete();
	}
}
