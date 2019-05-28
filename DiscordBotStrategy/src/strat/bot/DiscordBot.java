package strat.bot;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;

import strat.commands.CommandRegistry;
import strat.game.Map;
import strat.game.Nation;
import strat.game.Region;
import strat.game.Timer;
import strat.game.TurnLog;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.GuildCreateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.StatusType;

//ID: 437030179001597962
public class DiscordBot {
	public static final String TOKEN = "NDM3MDMwMTc5MDAxNTk3OTYy.DbwJOw.CHs1YJ_qPZDYtnmpgFkX1ozjc7A";
	public static final String VERSION = "0.3.0";
	
	public static final String DEFAULT_FILE_NAME = "hre.game";
	public static final String NATION_REGISTRY_FILE = "natreg.dat";
	public static final String ADMIN_REGISTRY_FILE = "admins.dat";
	public static final String MOVEMENT_LOG_FILE = "movelog.dat";
	
	public static final long SERVER_ID = 473942834395742239L;
	public static final long TURN_CHANNEL_ID = 582266844153643029L;
	public static final long BATTLE_CHANNEL_ID = 582269433129730048L;
	public static final long ACTION_CHANNEL_ID = 582266965771419726L;
	public static final long GAME_CHANNEL_ID = 582267327467487242L;
	
	private static DiscordBot instance = new DiscordBot();
	
	private IDiscordClient client;
	
	private Map map;
	private Timer timer;
	
	private CommandRegistry commands;
	private NationRegistry nationRegistry;
	private AdminRegistry adminRegistry;
	
	private static IChannel gameChannel = null;
	private static IChannel turnChannel = null;
	private static IChannel battleChannel = null;
	private static IChannel actionChannel = null;
	
	public DiscordBot() {
		client = new ClientBuilder().withToken(TOKEN).build();
		client.getDispatcher().registerListener(this);
		client.login();
		
		try {
			map = Map.readFromFile(DEFAULT_FILE_NAME);
			System.out.println("read map");
			commands = new CommandRegistry(map, "?");
			System.out.println("read cmds");
			nationRegistry = new NationRegistry(map);
			System.out.println("read natreg");
			adminRegistry = new AdminRegistry();
			System.out.println("read admreg");
			
			nationRegistry.load(NATION_REGISTRY_FILE);
			adminRegistry.load(ADMIN_REGISTRY_FILE);
			
			MovementLog.load(map, MOVEMENT_LOG_FILE);
			System.out.println("Read mvmtlog");
			
			ImageManager.init(map, 425, 684); // HRE original: (410, 468)
		}
		catch (IOException e) {
			map = null;
			commands = null;
			nationRegistry = null;
			adminRegistry = null;
		}
		
		timer = new Timer(Duration.ofHours(24));
		
		if (commands == null) {
			System.out.println("COMMANDS ARE NULL");
		}
		
		try {
			Thread.sleep(5000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		client.changePresence(StatusType.ONLINE, ActivityType.PLAYING, "Version " + VERSION);
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
	
	@EventSubscriber
    public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getGuild() == null) {
			//BotUtils.sendMessage(event.getChannel(), event.getMessage().getContent());
			System.out.println(event.getAuthor().getName() + ": " + event.getMessage().getContent());
		}
		else if (event.getGuild().getLongID() == SERVER_ID
				&& event.getChannel().getLongID() == GAME_CHANNEL_ID) {
			String content = event.getMessage().getContent();
			String contentLower = content.toLowerCase();
			
			if (contentLower.startsWith(commands.getPrefix())) {
				Nation n = nationRegistry.getNation(event.getAuthor().getLongID());
				
				if (n != null) {
					String res = commands.runCommand(n, contentLower);
					
					if (res != null) {
						if (res.length() <= 2000) {
							BotUtils.sendMessage(gameChannel, res);
						}
						else {
							for (int i = 0; i < res.length(); i += 2000) {
								BotUtils.sendMessage(gameChannel,
										res.substring(i, Math.min(i + 2000, res.length())));
								
								try {
									Thread.sleep(500);
								}
								catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
				
				if (adminRegistry.isAdmin(event.getAuthor().getLongID())) {
					commands.runAdminCommand(this, content);
				}
			}
		}
	}
	
	@EventSubscriber
	public void onGuildCreated(GuildCreateEvent event) {
		if (event.getGuild().getLongID() == SERVER_ID) {
			gameChannel = event.getGuild().getChannelByID(GAME_CHANNEL_ID);
			turnChannel = event.getGuild().getChannelByID(TURN_CHANNEL_ID);
			battleChannel = event.getGuild().getChannelByID(BATTLE_CHANNEL_ID);
			actionChannel = event.getGuild().getChannelByID(ACTION_CHANNEL_ID);
			
			if (map != null) {
				BotUtils.sendMessage(turnChannel,
						"Bot initialized. Successfully loaded game file " + DEFAULT_FILE_NAME);
			}
			else {
				BotUtils.sendMessage(turnChannel,
						"Bot initialized. Failed to load game file " + DEFAULT_FILE_NAME);
			}
		}
	}
	
	public void advanceTurn() {
		String lastDate = map.getCurrentDate();
		
		map.endTurn();
		
		ArrayList<java.util.Map.Entry<Nation, Integer>> holdings = new ArrayList<>();
		sortHoldings(holdings);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("Turn ").append(map.getCurrentTurn() - 1).append(" has ended. The current standings are as follows:\n\n");
		
		for (java.util.Map.Entry<Nation, Integer> e : holdings) {
			sb.append(e.getKey().getName()).append(": ").append(e.getValue()).append(" regions.\n");
		}
		
		EmbedObject eo = new EmbedObject();
		eo.title = String.format("Conclusion of turn %d (%s to %s)", map.getCurrentTurn() - 1, lastDate, map.getCurrentDate());
		eo.description = sb.toString();
		
		EmbedObject.ThumbnailObject to = new EmbedObject.ThumbnailObject();
		to.url = "https://cdn.discordapp.com/attachments/548382890296082433/582297151321669678/hex-strat.png";
		to.width = 256;
		to.height = 256;
		eo.thumbnail = to;
		
		BotUtils.sendEmbed(turnChannel, eo);
		
		try {
			Thread.sleep(5000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		eo.thumbnail = null;
		
		for (TurnLog.LogEntry le : map.getTurnLog().getBattleEntries()) {
			eo.title = le.title;
			eo.description = le.description;
			eo.color = le.nation.getRGB();
			
			BotUtils.sendEmbed(battleChannel, eo);
			
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		for (TurnLog.LogEntry le : map.getTurnLog().getCommonEntries()) {
			eo.title = le.title;
			eo.description = le.description;
			eo.color = le.nation.getRGB();
			
			BotUtils.sendEmbed(actionChannel, eo);
			
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		ImageManager.update();
		
		nationRegistry.resetTurnsCompleted();
		map.clearLog();
	}
	
	public void save(String mapFile) {
		saveMap(mapFile);
		
		try {
			nationRegistry.save(NATION_REGISTRY_FILE);
		}
		catch (IOException e) {
			BotUtils.sendMessage(turnChannel, "Could not save nation registry file");
		}
		
		try {
			adminRegistry.save(ADMIN_REGISTRY_FILE);
		}
		catch (IOException e) {
			BotUtils.sendMessage(turnChannel, "Could not save admin registry file");
		}
		
		try {
			MovementLog.save(map, MOVEMENT_LOG_FILE);
		}
		catch (IOException e) {
			BotUtils.sendMessage(turnChannel, "Could not save movement log file");
		}
	}
	
	public void save() {
		save(DEFAULT_FILE_NAME);
	}
	
	public void saveMap(String fileName) {
		try {
			map.writeToFile(fileName);
		}
		catch (IOException e) {
			BotUtils.sendMessage(turnChannel, "Could not save map to file: " + fileName);
		}
	}
	
	public Map getMap() {
		return map;
	}
	
	public NationRegistry getNationRegistry() {
		return nationRegistry;
	}
	
	public AdminRegistry getAdminRegistry() {
		return adminRegistry;
	}
	
	private void sortHoldings(ArrayList<java.util.Map.Entry<Nation, Integer>> holdings) {
		HashMap<Nation, Integer> count = new HashMap<>();
		
		for (Nation n : map.getNations()) {
			count.put(n, 0);
		}
		
		for (Region r : map.getRegions().values()) {
			Nation n = map.getNation(r.getOwnerID());
			count.put(n, count.get(n) + 1);
		}
		
		for (java.util.Map.Entry<Nation, Integer> e : count.entrySet()) {
			holdings.add(e);
		}
		
		holdings.sort((a, b) -> b.getValue() - a.getValue());
	}
	
	private void logActionInternal(String logType, String info, int color) {
		EmbedObject e = new EmbedObject();
		e.title = String.format("Turn %d - %s", map.getCurrentTurn(), logType);
		e.description = info;
		e.color = color;
		
		BotUtils.sendEmbed(actionChannel, e);
	}
	
	public static void logAction(String logType, String info, int color) {
		instance.logActionInternal(logType, info, color);
	}
	
	public static boolean setTurnCompleted(Nation n) {
		return instance.nationRegistry.setTurnCompleted(n);
	}
	
	public static boolean isTurnComplete() {
		return instance.nationRegistry.isTurnComplete();
	}
	
	public static Nation getNationByUser(long userID) {
		return instance.nationRegistry.getNation(userID);
	}
	
	public static String getFormattedName(IUser u) {
		String displayName = u.getDisplayName(instance.client.getGuildByID(SERVER_ID));
		
		if (!displayName.equals(u.getName())) {
			return String.format("%s (%s)", displayName, u.getName());
		}
		
		return displayName;
	}
	
	public static IChannel getGameChannel() {
		return gameChannel;
	}
	
	public static IUser getUserFromNation(Nation n) {
		long userID = instance.nationRegistry.getUserForNation(n.getNationID());
		
		if (userID == 0L) {
			return null;
		}
		
		return instance.client.getUserByID(userID);
	}
	
	public static IChannel getDMFromNation(Nation n) {
		IUser u = getUserFromNation(n);
		
		if (u == null) {
			return null;
		}
		
		return u.getOrCreatePMChannel();
	}
	
	public static DiscordBot getInstance() {
		return instance;
	}
	
	public static void main(String[] args) {
		instance.startTimer();
	}
}
