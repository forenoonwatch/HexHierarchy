package strat.game;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import strat.bot.BotUtils;
import strat.bot.DiscordBot;
import strat.commands.Response;
import strat.game.relationships.Alliance;
import strat.game.relationships.PeaceTreaty;
import strat.game.relationships.Relationship;
import strat.game.relationships.TradeAgreement;
import sx.blah.discord.api.internal.json.objects.EmbedObject;

public class GameManager {
	public static final String DEFAULT_FILE_NAME = "hre.game";
	public static final String MOVEMENT_LOG_FILE = "movelog.dat";
	
	public static final double COMPLETION_PERCENTAGE = 0.75;
	
	private Game game;
	private GameRenderer gameRenderer;
	private Timer timer;
	private Timer autosaveTimer;
	
	private HashSet<Nation> turnsCompleted;
	private ArrayList<Relationship> relationshipRequests;
	
	public GameManager(Game game) throws IOException {
		this.game = game;
		
		gameRenderer = new GameRenderer(800, 800);
		timer = new Timer(Duration.ofHours(12));
		autosaveTimer = new Timer(Duration.ofMinutes(30));
		
		turnsCompleted = new HashSet<>();
		relationshipRequests = new ArrayList<>();
		
		gameRenderer.renderSingleImages(game);
		gameRenderer.renderPoliticalImages(game);
	}
	
	public void startTimer() {
		for (;;) {
			if (timer.isDurationPassed()) {
				saveGame(DEFAULT_FILE_NAME + ".bak");
				advanceTurn();
				save();
			}
			
			if (autosaveTimer.isDurationPassed()) {
				saveGame(DEFAULT_FILE_NAME + ".bak");
				System.out.println("Autosaved");
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
				.append(" has ended. The current standings are as follows:\n\n:crown: ");
		
		for (java.util.Map.Entry<Nation, Integer> e : holdings) {
			sb.append(e.getKey().getName()).append(": ").append(e.getValue()).append(" regions.\n");
		}
		
		EmbedObject eo = new EmbedObject();
		eo.title = String.format("Conclusion of turn %d (%s to %s)", game.getCurrentTurn() - 1, lastDate, game.getCurrentDate());
		eo.description = sb.toString();
		eo.color = Response.DEFAULT_COLOR;
		
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
		
		for (LogEntry le : game.getTurnLog().getBattleEntries()) {
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
		
		for (LogEntry le : game.getTurnLog().getCommonEntries()) {
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
		
		gameRenderer.renderPoliticalImages(game);
		
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
	
	public void logDiplomacy(LogEntry entry) {
		EmbedObject eo = new EmbedObject();
		eo.title = entry.title;
		eo.description = entry.description;
		eo.color = entry.nation.getRGB();
		
		BotUtils.sendEmbed(DiscordBot.getDiplomacyChannel(), eo);
	}
	
	public void addPendingRelationship(Relationship r) {
		relationshipRequests.add(r);
	}
	
	public Alliance findPendingAllianceBetween(Nation a, Nation b) {
		for (Relationship r : relationshipRequests) {
			if (r instanceof Alliance && r.hasNation(a) && r.hasNation(b)) {
				return (Alliance)r;
			}
		}
		
		return null;
	}
	
	public TradeAgreement findPendingTradeBetween(Nation a, Nation b) {
		for (Relationship r : relationshipRequests) {
			if (r instanceof TradeAgreement && r.hasNation(a) && r.hasNation(b)) {
				return (TradeAgreement)r;
			}
		}
		
		return null;
	}
	
	public PeaceTreaty findPendingPeaceBetween(Nation a, Nation b) {
		for (Relationship r : relationshipRequests) {
			if (r instanceof PeaceTreaty && r.hasNation(a) && r.hasNation(b)) {
				return (PeaceTreaty)r;
			}
		}
		
		return null;
	}
	
	public boolean acceptRelationship(Relationship r) {
		if (relationshipRequests.remove(r)) {
			Relationship similar = game.findSimilarRelationship(r);
			
			if (similar != null) {
				for (Nation n : r.getNations()) {
					if (similar.addNation(n) && r instanceof Alliance) {
						Alliance al = (Alliance)r;
						logDiplomacy(new LogEntry(n, ":handshake: **NATION JOINS ALLIANCE**",
								String.format("%s joins %s", n.getName(), al.getName()), LogEntry.Type.ALLIANCE_JOINED));
					}
				}
			}
			else {
				game.addRelationship(r);
				
				ArrayList<Nation> a = new ArrayList<>();
				r.getNations().forEach(n -> a.add(n));
				
				if (r instanceof Alliance) {
					Alliance al = (Alliance)r;
					logDiplomacy(new LogEntry(a.get(0), String.format(":handshake: **ALLIANCE FORMED - %s**", al.getName()),
							String.format("Alliance formed between the nations of %s and %s", a.get(0).getName(), a.get(1).getName()),
							LogEntry.Type.ALLIANCE_FORMED));
				}
				else if (r instanceof TradeAgreement) {
					logDiplomacy(new LogEntry(a.get(0), ":scales: **TRADE AGREEMENT SIGNED**",
							String.format("Trade agreement signed between the nations of %s and %s", a.get(0).getName(), a.get(1).getName()),
							LogEntry.Type.TRADE_AGREEMENT));
				}
				else if (r instanceof PeaceTreaty) {
					logDiplomacy(new LogEntry(a.get(0), ":dove: **PEACE TREATY SIGNED**",
							String.format("Peace treaty signed between the nations of %s and %s", a.get(0).getName(), a.get(1).getName()),
							LogEntry.Type.PEACE_TREATY));
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	public boolean declineRelationship(Relationship r) {
		return relationshipRequests.remove(r);
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
		gameRenderer.renderPoliticalImages(game);
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
