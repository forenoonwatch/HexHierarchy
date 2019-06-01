package strat.bot;

import java.io.IOException;

import strat.commands.CommandRegistry;
import strat.commands.InputLevel;
import strat.commands.Response;
import strat.commands.ResponseType;
import strat.game.Game;
import strat.game.GameManager;
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
import sx.blah.discord.util.DiscordException;

//ID: 437030179001597962
public class DiscordBot {
	public static final String TOKEN = "NDM3MDMwMTc5MDAxNTk3OTYy.DbwJOw.CHs1YJ_qPZDYtnmpgFkX1ozjc7A";
	public static final String VERSION = "0.4.4";
	
	public static final long SERVER_ID = 473942834395742239L;
	public static final long TURN_CHANNEL_ID = 582266844153643029L;
	public static final long BATTLE_CHANNEL_ID = 582269433129730048L;
	public static final long ACTION_CHANNEL_ID = 582266965771419726L;
	public static final long GAME_CHANNEL_ID = 582267327467487242L;
	public static final long DIPLOMACY_CHANNEL_ID = 584416141460373528L;
	
	private static DiscordBot instance = new DiscordBot();
	
	private IDiscordClient client;
	
	private static IChannel gameChannel = null;
	private static IChannel turnChannel = null;
	private static IChannel battleChannel = null;
	private static IChannel actionChannel = null;
	private static IChannel diplomacyChannel = null;
	
	private GameManager gameManager;
	
	public DiscordBot() {
		try {
			client = new ClientBuilder().withToken(TOKEN).build();
			client.getDispatcher().registerListener(this);
			client.login();
		}
		catch (Exception e ) {
			System.out.println("CAUGHT BIG BUG");
			e.printStackTrace();
		}
		
		try {
			Game game = new Game();
			game.load(GameManager.DEFAULT_FILE_NAME);
			
			gameManager = new GameManager(game);
		}
		catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		try {
			Thread.sleep(5000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		client.changePresence(StatusType.ONLINE, ActivityType.PLAYING, "Version " + VERSION);
	}
	
	@EventSubscriber
    public void onMessageReceived(MessageReceivedEvent event) {
		Response r = null;
		
		if (event.getGuild() == null) {
			r = CommandRegistry.executeCommand(gameManager, event.getMessage().getContent(),
					event.getAuthor().getLongID(), InputLevel.DM_CHANNEL);
		}
		else if (event.getGuild().getLongID() == SERVER_ID
				&& event.getChannel().getLongID() == GAME_CHANNEL_ID) {
			r = CommandRegistry.executeCommand(gameManager, event.getMessage().getContent(),
					event.getAuthor().getLongID(), InputLevel.GAME_CHANNEL);
		}
		
		if (r != null) {
			try {
				IChannel target = r.type == ResponseType.PRIVATE ? event.getAuthor().getOrCreatePMChannel() : event.getChannel();
				
				if (target != null) {
					if (r.title == null) {
						BotUtils.sendLongMessage(target, r.content);
					}
					else {
						EmbedObject eo = new EmbedObject();
						eo.title = r.title;
						eo.description = r.content;
						eo.color = r.color;
						
						BotUtils.sendLongEmbed(target, eo);
					}
				}
			}
			catch (DiscordException e) {
				System.out.println("Could not created DM channel with: " + event.getMessage().getAuthor().getName());
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
			diplomacyChannel = event.getGuild().getChannelByID(DIPLOMACY_CHANNEL_ID);
		}
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
	
	public static IChannel getActionChannel() {
		return actionChannel;
	}
	
	public static IChannel getTurnChannel() {
		return turnChannel;
	}
	
	public static IChannel getBattleChannel() {
		return battleChannel;
	}
	
	public static IChannel getDiplomacyChannel() {
		return diplomacyChannel;
	}
	
	public static IUser getUserByID(long userID) {
		return instance.client.getUserByID(userID);
	}
	
	public static DiscordBot getInstance() {
		return instance;
	}
	
	public static void main(String[] args) {
		CommandRegistry.init();
		instance.gameManager.startTimer();
	}
}
