package strat.bot;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.function.BiConsumer;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RequestBuffer;

public class BotUtils {
	public static final int MAX_MESSAGE_SIZE = 2000;
	
    public static void sendMessage(IChannel channel, String message) {
        RequestBuffer.request(() -> {
            try {
                channel.sendMessage(message);
            }
            catch (DiscordException e){
                System.err.println("Message could not be sent with error: ");
                e.printStackTrace();
            }
        });

        /*
        // The below example is written to demonstrate sending a message if you want to catch the RLE for logging purposes
        RequestBuffer.request(() -> {
            try{
                channel.sendMessage(message);
            } catch (RateLimitException e){
                System.out.println("Do some logging");
                throw e;
            }
        });
        */

    }
    
    public static void sendLongMessage(IChannel channel, String message) {
    	if (message.length() <= MAX_MESSAGE_SIZE) {
			BotUtils.sendMessage(channel, message);
		}
		else {
			splitString(message, (msg, i) -> BotUtils.sendMessage(channel, msg));
		}
    }
    
    public static void sendEmbed(IChannel channel, EmbedObject embed) {
    	RequestBuffer.request(() -> {
    		try {
                channel.sendMessage(embed);
            }
            catch (DiscordException e){
                System.err.println("Message could not be sent with error: ");
                e.printStackTrace();
            }
    	});
    }
    
    public static void sendLongEmbed(IChannel channel, EmbedObject embed) {
    	if (embed.description.length() <= MAX_MESSAGE_SIZE) {
    		BotUtils.sendEmbed(channel, embed);
    	}
    	else {
    		EmbedObject.FooterObject footer = new EmbedObject.FooterObject();
    		embed.footer = footer;
    		
    		splitString(embed.description, (msg, i) -> {
    			embed.description = msg;
    			footer.text = i.toString();
    			
    			BotUtils.sendEmbed(channel, embed);
    		});
    	}
    }
    
    public static void sendFile(IChannel channel, File file) {
    	RequestBuffer.request(() -> {
            try {
                channel.sendFile(file);
            }
            catch (DiscordException | FileNotFoundException | NullPointerException e){
                System.err.println("Message could not be sent with error: ");
                e.printStackTrace();
            }
        });
    }
    
    public static long parseUserID(String token) {
    	if (token.matches("<@\\d+>")) {
    		try {
    			return Long.parseLong(token.substring(2, token.length() - 1));
    		}
    		catch (NumberFormatException e) {
    			return 0L;
    		}
    	}
    	else if (token.matches("<@!\\d+>")) {
    		try {
    			return Long.parseLong(token.substring(3, token.length() - 1));
    		}
    		catch (NumberFormatException e) {
    			return 0L;
    		}
    	}
    	else {
    		try {
    			return Long.parseLong(token);
    		}
    		catch (NumberFormatException e) {
    			return 0L;
    		}
    	}
    }
    
    private static void splitString(String message, BiConsumer<String, Integer> consumer) {
    	String[] lines = message.split("\n");
		ArrayList<String> lineBuffer = new ArrayList<>();
		int charCount = 0;
		int pageCount = 0;
		
		for (int i = 0; i < lines.length; ++i) {
			if (charCount + lines[i].length() > MAX_MESSAGE_SIZE || i == lines.length - 1) {
				StringBuilder out = new StringBuilder();
				
				for (String s : lineBuffer) {
					out.append(s).append('\n');
				}
				
				lineBuffer.clear();
				++pageCount;
				
				consumer.accept(out.toString(), pageCount);
				
				try {
					Thread.sleep(500);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				charCount = lines[i].length();
				lineBuffer.add(lines[i]);
				
				if (i == lines.length - 1) {
					++pageCount;
					consumer.accept(lines[i], pageCount);
				}
			}
			else if (charCount + lines[i].length() <= MAX_MESSAGE_SIZE) {
				charCount += lines[i].length();
				lineBuffer.add(lines[i]);
			}
		}
    }
}