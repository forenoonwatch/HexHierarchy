package strat.debug;

import java.io.IOException;

import strat.bot.DiscordBot;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.internal.json.objects.EmbedObject;

public class BotEditor {
	public static void main(String[] args) throws IOException {
		
		IDiscordClient client = new ClientBuilder().withToken(DiscordBot.TOKEN).build();
		//client.getDispatcher().registerListener(this);
		client.login();
		
		try {
			Thread.sleep(3000);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Image img = Image.forFile(new File("hex-strat.png"));
		//client.changeAvatar(img);
		
		EmbedObject e = new EmbedObject();
		//e.title = "HexHierarchy Discord Strategy Game";
		e.color = 0x44bd32;
		///EmbedObject e = getRules6();
		e.title = "**DISCORD INVITE**";
		e.description = "http://discord.gg/AxHa8pJ";
		
		
		//http://discord.gg/AxHa8pJ
		client.getChannelByID(473943462073597983L).sendMessage(e); // #welcome
		//client.getChannelByID(582342162268815382L).sendMessage(e); // #rules
	}
	
	static EmbedObject getWelcome() {
		EmbedObject e = new EmbedObject();
		e.title = "HexHierarchy Discord Strategy Game";
		e.description = "Welcome to the HexHierarchy Game Discord Server!\n\nHexHierarchy is a project dedicated to creating"
				+ " a fun, social experience through a military strategic boardgame played entirely over Discord. "
				+ "The game takes inspiration from various sources, including Total War, Civilization, Risk, as "
				+ "well as the now defunct Modern-Feudal project that preceded this.\n\nHead over to <#582342162268815382> to start reading the rules, "
				+ "or just head to <#473942834399936512> and talk to a moderator to see how you can get involved in the game.";
		e.color = 0x44bd32;
		
		EmbedObject.ThumbnailObject to = new EmbedObject.ThumbnailObject();
		to.url = "https://cdn.discordapp.com/attachments/548382890296082433/582297151321669678/hex-strat.png";
		to.width = 256;
		to.height = 256;
		e.thumbnail = to;
		
		return e;
	}
	
	static EmbedObject getRules1() {
		EmbedObject e = new EmbedObject();
		e.title = "**THE GAME**";
		e.description = "The object of the game is to conquer as many regions as you can within the game's duration. To conquer a region, you must besiege its capital and defeat its garrison with one of your armies.\r\n";
		e.color = 0x44bd32;
		
		return e;
	}
	
	static EmbedObject getRules2() {
		EmbedObject e = new EmbedObject();
		e.title = "**TURNS**";
		e.description = "Every day at midnight EST (7 GMT), the game progresses 1 turn. During each turn you can purchase upgrades for your cities, raise new troops, and move your armies into position. Every turn you also receive money related to the level of Markets in your cities.";
		e.color = 0x44bd32;
		
		return e;
	}
	
	static EmbedObject getRules3() {
		EmbedObject e = new EmbedObject();
		e.title = "**BUILDINGS**";
		e.description = "There are 5 types of buildings in each city, each with its own purpose.\r\n" + 
				"Market: Produces income at `100` per level per turn\r\n" + 
				"Fort: Fortifies the garrison of the city by `50` infantry, `20` cavalry, and `5` infantry per level\r\n" + 
				"Barracks: Allows `10` infantry to be hired at the city per turn per level\r\n" + 
				"Stables: Allows `4` cavalry to be hired at the city per turn per level\r\n" + 
				"Foundry: Allows `1` artillery to be hired at the city per turn per level\r\n";
		e.color = 0x44bd32;
		
		return e;
	}
	
	static EmbedObject getRules4() {
		EmbedObject e = new EmbedObject();
		e.title = "**ARMIES**";
		e.description = "Every army is comprised of infantry, cavalry, and artillery. Each army gets `3` hexes of movement per turn.\r\n" + 
				"\r\n" + 
				"At the end of every turn the intersection is checked between opposing armies, and if their moves that turn cross any of the same tiles, they become engaged in battle. Otherwise, they move to their final position at the start of the next turn. If they are not intercepted and their final position was an enemy city, the army besieges it.\r\n" + 
				"\r\n" + 
				"If an army attempts to move onto the cell of another friendly army, they will combine forces at the start of the next turn.";
		e.color = 0x44bd32;
		
		return e;
	}
	
	static EmbedObject getRules5() {
		EmbedObject e = new EmbedObject();
		e.title = "**BATTLES AND SIEGES**";
		e.description = "Battles and sieges are automatically resolved at the end of every turn. The opposing armies take turns attacking each other with a random class of unit until one is completely out of troops. Each attack in a battle is targeted at the most effective troop class:\r\n" + 
				"Infantry -> Cavalry\r\n" + 
				"Cavalry -> Artillery\r\n" + 
				"Artillery -> Infantry\r\n" + 
				"\r\n" + 
				"The effectiveness of each battle is calculated by the advantage, which is the difference in troops between one set of units and another. The greater difference in troops, the more damage you do and the less you receive. This is not the raw difference however, and every `10` infantry is worth `4` cavalry and `1` artillery.\r\n" + 
				"\r\n" + 
				"Sieges are similar to battles, except instead of battling an opposing army, your army fights the garrison of the city and *all the armies standing next to it*. ";
		e.color = 0x44bd32;
		
		return e;
	}
	
	static EmbedObject getRules6() {
		EmbedObject e = new EmbedObject();
		e.title = "**MOVEMENT**";
		e.description = "Moving an army is done with the command `movearmy army_number direction distance`. The direction is a value between 1 and 6 giving the desired direction relative to the current path position of the selected army. Use the command `viewarmies` to get a view of the current paths for all of your forces.";
		e.color = 0x44bd32;
		
		EmbedObject.ImageObject io = new EmbedObject.ImageObject();
		io.url = "https://cdn.discordapp.com/attachments/560267546717323284/582358243238608898/movement-help.png";
		io.width = 512;
		io.height = 512;
		e.image = io;
		
		return e;
	}
}
