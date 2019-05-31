package strat.bot;
import java.io.File;
import java.io.FileNotFoundException;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.RequestBuffer;

public class BotUtils {
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
    	if (message.length() <= 2000) {
			BotUtils.sendMessage(channel, message);
		}
		else {
			for (int i = 0; i < message.length(); i += 2000) {
				BotUtils.sendMessage(channel,
						message.substring(i, Math.min(i + 2000, message.length())));
				
				try {
					Thread.sleep(500);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
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
    	else {
    		try {
    			return Long.parseLong(token);
    		}
    		catch (NumberFormatException e) {
    			return 0L;
    		}
    	}
    }
}