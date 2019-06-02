package strat.editor;

import java.io.IOException;

import strat.bot.Webhook;
import strat.game.GameRules;
import sx.blah.discord.api.internal.json.objects.EmbedObject;

public class GameInfoGenerator {
	// #format-test
	//public static final String WEBHOOK = "https://discordapp.com/api/webhooks/583146707047546880/oJ99RZ2_O92hQatYBrTSEpTvMAvEAhub_bIslOu_1_BFUxehN4bDsTA_zjqyNgKAy0OD";
	// #game-info
	public static final String WEBHOOK = "https://discordapp.com/api/webhooks/584491680334610462/Hvs3519w9-ppjVixsnTdlp3nNmPiEXKohgV8SovzTGLXf0QCvnTK6OYDrcwgEPHI34y9";
	// #welcome
	//public static final String WEBHOOK = "https://discordapp.com/api/webhooks/584496786253676574/lvi4QaP90SSdDbuYDhyZzwiWExNxxmM_24kaNsa3MqL-9egI8frKp2C2CEgVeEJw8kKy";
	
	public static void main(String[] args) throws IOException {
		Webhook w = new Webhook(WEBHOOK);
		
		//sendEmbed(w, getWelcome());
		genRules(w);
	}
	
	static void genRules(Webhook w) {
		EmbedObject[] rules = {getInitialGameInfo(), getTurnInfo(), getBuildingInfo(),
				getEspionageInfo(),
				getTradeInfo(), getAllianceInfo(), getWarInfo(),
				getArmyInfo(), getBattleAndSiegeInfo(), getArmyMovementInfo(),
				getPriceList()};
		
		for (EmbedObject e : rules) {
			sendEmbed(w, e);
			
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	static void sendEmbed(Webhook w, EmbedObject eo) {
		w.setTitle(eo.title);
		w.setColor(eo.color);
		w.setDescription(eo.description);
		
		if (eo.image != null) {
			w.setImage(new Webhook.Image(eo.image.url, eo.image.width, eo.image.height));
		}
		else {
			w.setImage(null);
		}
		
		if (eo.thumbnail != null) {
			w.setThumbnail(new Webhook.Image(eo.thumbnail.url, eo.thumbnail.width, eo.thumbnail.height));
		}
		else {
			w.setThumbnail(null);
		}
		
		w.send();
	}
	
	static EmbedObject getWelcome() {
		EmbedObject e = new EmbedObject();
		e.title = "HexHierarchy Discord Strategy Game";
		e.description = "Welcome to the HexHierarchy Game Discord Server!\n\nHexHierarchy is a project dedicated to creating"
				+ " a fun, social experience through a military strategic boardgame played entirely over Discord. "
				+ "The game takes inspiration from various sources, including Total War, Civilization, Risk, as "
				+ "well as the now defunct Modern-Feudal project that preceded this.\n\nHead over to <#582342162268815382> to start reading the rules,"
				+ "or just head to <#473942834399936512> and start talking with other players.\n"
				+ "\nIf you would like to join the game with your own nation, post a correctly formatted nation request in <#582579700191199243>"
				+ "\n\n**DISCORD INVITE**\nhttp://discord.gg/AxHa8pJ";
		e.color = 0x44bd32;
		
		EmbedObject.ThumbnailObject to = new EmbedObject.ThumbnailObject();
		to.url = "https://cdn.discordapp.com/attachments/548382890296082433/582297151321669678/hex-strat.png";
		to.width = 256;
		to.height = 256;
		e.thumbnail = to;
		
		return e;
	}
	
	static EmbedObject getInitialGameInfo() {
		EmbedObject e = new EmbedObject();
		e.title = "**THE GAME**";
		e.description = "The object of the game is to conquer as many regions as you can within the game's duration. To conquer a region, you must besiege its capital and defeat its garrison with one of your armies.\r\n";
		e.color = 0x44bd32;
		
		return e;
	}
	
	static EmbedObject getTurnInfo() {
		EmbedObject e = new EmbedObject();
		e.title = "**TURNS**";
		e.description = "Every day at noon and midnight EST (7 AM/PM GMT), the game progresses 1 turn. During each turn you can purchase upgrades for your cities, raise new troops, and move your armies into position. Every turn you also receive money related to the level of Markets in your cities.";
		e.color = 0x44bd32;
		
		return e;
	}
	
	static EmbedObject getBuildingInfo() {
		EmbedObject e = new EmbedObject();
		e.title = "**BUILDINGS**";
		e.description = "There are 5 types of buildings in each city, each with its own purpose. "
				+ "Buildings in a city can be upgraded, but each building caps at `" + GameRules.getRulei("buildingCap") + "` upgrades.\r\n" + 
				String.format("Market: Produces income at `%d` per level per turn\r\n", GameRules.getRulei("marketProfit")) + 
				String.format("Fort: Fortifies the garrison of the city by `%d` infantry, `%d` cavalry, and `%d` infantry per level\r\n",
						GameRules.getRulei("armiesPerFortLevel") * GameRules.getRulei("infantryWeight"),
						GameRules.getRulei("armiesPerFortLevel") * GameRules.getRulei("cavalryWeight"),
						GameRules.getRulei("armiesPerFortLevel") * GameRules.getRulei("artilleryWeight")) + 
				String.format("Barracks: Allows `%d` infantry to be hired at the city per turn per level\r\n",
						GameRules.getRulei("recruitmentCap") * GameRules.getRulei("infantryWeight")) + 
				String.format("Stables: Allows `%d` cavalry to be hired at the city per turn per level\r\n",
						GameRules.getRulei("recruitmentCap") * GameRules.getRulei("cavalryWeight")) + 
				String.format("Foundry: Allows `%d` artillery to be hired at the city per turn per level\r\n",
						GameRules.getRulei("recruitmentCap") * GameRules.getRulei("artilleryWeight")) +
				"\r\nThe garrison of a city does not automatically replenish, but can be replenished for a flat cost of `" +
				GameRules.getRulei("replenishmentCost") + "` per unit regardless of class.";
		e.color = 0x44bd32;
		
		return e;
	}
	
	static EmbedObject getEspionageInfo() {
		EmbedObject e = new EmbedObject();
		e.title = "**ESPIONAGE**";
		e.description = "By privately messaging the bot, a user can spy on an enemy city, revealing detailed information. "
				+ "Using a spy costs `" + GameRules.getRulei("spyCost") + "`, and its success is determined by an internal diceroll weighted by "
				+ "the distance between your closest city and the target. If your spy should fail, notice of the "
				+ "espionage is logged publicly at the end of the turn.";
		e.color = 0x44bd32;
		
		return e;
	}
	
	static EmbedObject getTradeInfo() {
		EmbedObject e = new EmbedObject();
		e.title = "**TRADE AGREEMENTS**";
		e.description = "Any nation can request a trade agreement with any other nation, "
				+ "which will provide both nations a profit of `" + GameRules.getRulei("tradeProfit") + "` per turn.";
		e.color = 0x44bd32;
		
		return e;
	}
	
	static EmbedObject getAllianceInfo() {
		EmbedObject e = new EmbedObject();
		e.title = "**ALLIANCES**";
		e.description = "Any nation can `create` an alliance with another nation or request to `join` one, "
				+ "provided they are not already in an alliance themselves. All nations in an alliance will show under the alliance's name "
				+ "and color on the political map view. When war is declared on a member of an alliance, all members are also declared war upon,"
				+ " but when a member is an aggressor, their fellow members do not automatically join.";
		e.color = 0x44bd32;
		
		return e;
	}
	
	static EmbedObject getWarInfo() {
		EmbedObject e = new EmbedObject();
		e.title = "**WAR**";
		e.description = "A nation cannot attack another nation without first declaring war on it."
				+ " Once war has been declared, the only way to cease aggressions is by requesting a "
				+ "peace treaty with the other party. War cannot be declared if you have an alliance or a"
				+ " trade agreement still present with the target, and they must first be cancelled manually.";
		e.color = 0x44bd32;
		
		return e;
	}
	
	static EmbedObject getArmyInfo() {
		EmbedObject e = new EmbedObject();
		e.title = "**ARMIES**";
		e.description = "Every army is comprised of infantry, cavalry, and artillery. Each army gets `" + GameRules.getRulei("movesPerTurn") + "` hexes of movement per turn.\r\n" + 
				"\r\n" + 
				"At the end of every turn the intersection is checked between opposing armies, and if their moves that turn cross any of the same tiles, they become engaged in battle. Otherwise, they move to their final position at the start of the next turn. If they are not intercepted and their final position was an enemy city, the army besieges it.\r\n" + 
				"\r\n" + 
				"If an army ends its turn on the tile of another friendly army, they will combine forces at the start of the next turn.\r\n" +
				"Armies have upkeep costs, which are as follows:\r\n" + 
				String.format("Infantry: %.1f per unit per turn\r\n" + 
						"Cavalry: %.1f per unit per turn\r\n" + 
						"Artillery: %.1f per unit per turn",
						GameRules.getRuled("upkeepCost") / GameRules.getRuled("infantryWeight"),
						GameRules.getRuled("upkeepCost") / GameRules.getRuled("cavalryWeight"),
						GameRules.getRuled("upkeepCost") / GameRules.getRuled("artilleryWeight"));
		e.color = 0x44bd32;
		
		return e;
	}
	
	static EmbedObject getBattleAndSiegeInfo() {
		EmbedObject e = new EmbedObject();
		e.title = "**BATTLES AND SIEGES**";
		e.description = "Battles and sieges are automatically resolved at the end of every turn. The opposing armies take turns attacking each other with a random class of unit until one is completely out of troops. Each attack in a battle is targeted at the most effective troop class:\r\n" + 
				"Infantry -> Cavalry\r\n" + 
				"Cavalry -> Artillery\r\n" + 
				"Artillery -> Infantry\r\n" + 
				"\r\n" + 
				"The effectiveness of each battle is calculated by the advantage, which is the difference in troops between one set of units and another. The greater difference in troops, the more damage you do and the less you receive. This is not the raw difference however, and every `"
				+ GameRules.getRulei("infantryWeight") + "` infantry is worth `" + GameRules.getRulei("cavalryWeight") + "` cavalry and `" + GameRules.getRulei("artilleryWeight") + "` artillery.\r\n" + 
				"\r\n" + 
				"Sieges are similar to battles, except instead of battling an opposing army, your army fights the garrison of the city and *all the armies standing within it*. ";
		e.color = 0x44bd32;
		
		return e;
	}
	
	static EmbedObject getArmyMovementInfo() {
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
	
	static EmbedObject getPriceList() {
		EmbedObject e = new EmbedObject();
		e.title = "**PRICE LIST**";
		e.description = String.format("**Recruitment**:\r\n" + 
				"Infantry: %d/unit\r\n" + 
				"Cavalry: %d/unit\r\n" + 
				"Artillery: %d/unit\r\n" + 
				"\r\n" + 
				"**Buildings**:\r\n" + 
				"Fort: %d/level\r\n" + 
				"Market: %d/level\r\n" + 
				"Foundry: %d/level\r\n" + 
				"Stables: %d/level\r\n" + 
				"Barracks: %d/level\r\n" + 
				"\r\n" + 
				"Spy: %d\r\n" + 
				"\r\n" + 
				"Garrison Replenishment: %d/unit", GameRules.getRulei("infantryCost"), GameRules.getRulei("cavalryCost"),
				GameRules.getRulei("artilleryCost"), GameRules.getRulei("fortCost"), GameRules.getRulei("marketCost"),
				GameRules.getRulei("foundryCost"), GameRules.getRulei("stablesCost"), GameRules.getRulei("barracksCost"),
				GameRules.getRulei("spyCost"), GameRules.getRulei("replenishmentCost"));
		e.color = 0x44bd32;
		
		return e;
	}
}
